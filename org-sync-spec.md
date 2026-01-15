# org-sync 스펙 문서 (재작성 v1)

## 1. 목표

org-sync는 **조직도 서버(원천)**에서 내려오는 변경 로그/스냅샷을 하위 시스템으로 동기화하기 위한 공통 라이브러리다. 동기화의 핵심은 아래와 같다.

- **동기화 트리거**: RabbitMQ 이벤트(회사 UUID + logSeq)
- **동기화 방식**: 하위 시스템이 org chart 서버로부터 **pull**
- **데이터 형태**: Delta(변경 로그) 또는 Snapshot
- **반영 방식**: 서비스 인터페이스를 통한 저장소 업데이트
- **락/트랜잭션**: 회사 단위 락 + 트랜잭션 실행 추상화

이 문서는 현재 코드(`org-sync-core`, `org-sync-spring`, `org-sync-boot-starter`)에 구현된 기능을 기준으로 재정리했다.

---

## 2. 모듈 구성

### 2.1 org-sync-core

순수 자바 모듈로 동기화 엔진과 DTO, 서비스 인터페이스를 제공한다.

- `SyncEngine`: 동기화 전체 흐름을 조율
- `SyncLogApplier`: delta 로그를 도메인별 CRUD로 반영
- `SyncSnapshotApplier`: snapshot 데이터를 비교/반영
- `OrgChartClient`: 원천 시스템 호출 인터페이스
- `OrgSync*Service`: 하위 저장소 CRUD 인터페이스
- `LockManager`: 회사 단위 동시성 제어
- `TransactionRunner`: 트랜잭션 실행 추상화

### 2.2 org-sync-spring

Spring Framework용 어댑터.

- `OrgChartRestClient`: RestClient 기반 원천 호출 구현
- `OrgSyncConfiguration`: 기본 빈 조립
- `OrgSyncAmqpConfiguration`: RabbitMQ 리스너 구성
- `InMemoryLockManager`: 테스트/로컬용 인메모리 락
- `InMemoryOrgSyncStore`: 테스트용 인메모리 저장소

### 2.3 org-sync-boot-starter

Spring Boot 자동 설정 모듈.

- `OrgSyncAutoConfiguration`: 기본 빈 등록
- `OrgSyncAmqpAutoConfiguration`: AMQP 설정 자동 구성

---

## 3. 동기화 흐름

### 3.1 주요 흐름

1. **RabbitMQ 이벤트 수신** → 회사 UUID + logSeq 확보
2. `SyncEngine.synchronizeCompany(companyUuid, logSeq)` 실행
3. LockManager로 회사 단위 락 획득
4. TransactionRunner로 트랜잭션 실행
5. 현재 저장된 logSeq 확인 후 필요 시 delta/snapshot 적용

### 3.2 logSeq 제어

- 현재 저장된 logSeq보다 **작거나 같은 값**은 무시된다.
- logSeq는 `OrgSyncLogSeqService`를 통해 저장/조회된다.

### 3.3 Snapshot 처리

- 원천 서버 응답이 `needSnapshot=true`이면 snapshot을 먼저 적용한다.
- 응답의 `snapshotIdList`를 순회하면서 `fetchSnapshot`을 호출한다.
- 스냅샷 처리 후, 마지막 logSeq 기준으로 delta를 다시 적용한다.

### 3.4 Delta 처리

- `ProvisionSequenceDto.logInfoList`를 도메인 단위로 그룹화하여 생성/수정/삭제를 반영한다.
- 응답의 `needUpdateNextLog=true`이면 `logSeq`를 다음 커서로 간주하고 재호출한다.
- 다음 커서가 이전 커서보다 커지지 않으면 즉시 오류를 발생시킨다.

---

## 4. 외부 인터페이스

### 4.1 RabbitMQ 이벤트

`org-sync-spring`은 아래 기본 설정으로 리스너를 등록한다.

- Queue: `orgsync.org-chart.sync.queue`
- Exchange: `dop_user_company_sync.fanout` (fanout 타입)

메시지는 아래 두 형태를 지원한다.

1) 직접 payload

```json
{
  "companyUuid": "C001",
  "logSeq": 1234
}
```

2) messagePayload 래핑 구조

```json
{
  "messagePayload": "{\"companyUuid\":\"C001\",\"logSeq\":1234}"
}
```

> `messagePayload`는 문자열(JSON)로 들어오는 경우를 처리하도록 구현되어 있다.

### 4.2 org chart 서버 API

`OrgChartRestClient`가 사용하는 기본 경로는 다음과 같다.

- 변경 로그: `/api/provision/common/sync/company/{companyUuid}/sequence/{logSeq}`
- 스냅샷: `/api/provision/common/sync/company/{companyUuid}/snapshot/{snapshotId}`

응답은 `ResponseWrapper<T>` 형태(`{"data": {...}}`)를 기대한다.

#### 변경 로그 응답 예시

```json
{
  "data": {
    "needSnapshot": false,
    "snapshotIdList": [],
    "logSeq": 1234,
    "needUpdateNextLog": false,
    "logInfoList": [
      {
        "domain": "USER",
        "domainId": 1001,
        "fieldName": "name",
        "updatedValue": "홍길동",
        "logType": "UPDATE"
      }
    ]
  }
}
```

#### 스냅샷 응답 예시

```json
{
  "data": {
    "logSeq": 5678,
    "companyGroupSnapshot": [],
    "integrationSnapshot": [],
    "organizationCodeSnapshot": [],
    "userSnapshot": [],
    "departmentSnapshot": [],
    "relationSnapshot": []
  }
}
```

---

## 5. 도메인 범위

현재 엔진이 지원하는 도메인 유형은 다음과 같다.

- ORGANIZATION_CODE (조직 코드)
- DEPARTMENT (부서)
- USER (사용자)
- RELATION_MEMBER (사용자-부서 관계)
- INTEGRATION (겸직/통합)
- COMPANY_GROUP (회사 그룹)

각 도메인은 delta 로그 또는 snapshot 구조에 따라 업데이트된다.

---

## 6. 저장소 인터페이스

하위 시스템은 아래 서비스 인터페이스를 구현해야 한다.

필수 구현(동기화에 직접 사용됨):

- `OrgSyncCompanyService`
- `OrgSyncLogSeqService`
- `OrgSyncOrganizationCodeService`
- `OrgSyncDepartmentService`
- `OrgSyncUserService`
- `OrgSyncMemberService`
- `OrgSyncIntegrationService`
- `OrgSyncCompanyGroupService`
- `OrgSyncUserGroupCodeUserService`
- `OrgSyncMultiLanguageService`

각 서비스는 생성/수정/삭제/조회 작업을 도메인별 DTO로 수행한다.

---

## 7. 다국어 데이터 처리

- 사용자/부서/조직코드는 다국어 맵을 가진다.
- Snapshot/Delta 모두에서 다국어 값이 포함되면 다음 규칙을 적용한다.

| 상태 | 처리 |
| --- | --- |
| 값 존재 | create 또는 update |
| 빈 값 | delete |

`multiLanguageMap`은 JSON 문자열 형태로 전달되며, `MultiLanguageType` 키를 사용한다.

---

## 8. 락/트랜잭션

- `LockManager`는 회사 단위 동시성 제어를 담당한다.
- `TransactionRunner`는 동기화 작업을 트랜잭션으로 감싼다.
- Spring 환경에서는 `SpringTransactionRunner`가 `PlatformTransactionManager` 기반으로 제공된다.

---

## 9. 에러 정책

- 공통 예외 메시지 접두사는 `[org-sync]`로 통일한다.
- logSeq가 증가하지 않는 응답이 오면 즉시 실패하도록 설계되어 있다.

---

## 10. 현재 범위에서 제외되는 기능

아래 항목은 기존 문서에 있었지만 현재 구현에는 포함되지 않는다.

- YAML/DSL 기반 스펙 정의
- JDBC 직접 upsert/스키마 검증
- 이벤트 발행(SPI) 자동 연결

이 기능들이 필요하다면 별도의 확장이 필요하다.
