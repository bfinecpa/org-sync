
# 조직도 동기화 공통 라이브러리 명세서 (v0.1)

## 0. 목적

여러 하위 서버가 **조직도 서버(원천)로부터 조직도 도메인 데이터를 동기화**하는 로직을 공통화한다.

* 동기화 트리거: RabbitMQ “회사 변경 이벤트”
* 동기화 방식: 하위 서버가 `sinceCursor`(마지막 처리 커서)로 조직도 서버에 **pull**
* 데이터 형태: **스냅샷** 또는 **변경 로그(델타)**
* 적용 방식: 하위 서버 DB에 **JDBC로 반영**
* 저장 범위: 하위 서버는 **필요 도메인/필드/레코드만 저장**
* 이벤트: 하위 서버 내부로 **도메인 생성/수정/삭제 + 필드 변경 이벤트** 발행 가능

### RabbitMQ 연동 (회사 변경 이벤트)

* 기본 큐 이름: `orgsync.org-chart.sync.queue` (환경변수/설정 `orgsync.amqp.org-chart.sync.queue`로 변경 가능)
* 교환기: `dop_user_company_sync` (환경변수/설정 `orgsync.amqp.org-chart.sync.exchange`로 변경 가능)
* 라우팅 키: `user_company.sync` (환경변수/설정 `orgsync.amqp.org-chart.sync.routing-key`로 변경 가능)

* 메시지 페이로드(JSON): `{ "companyId": "<회사 ID>" }`
* 소비 로직: 큐 수신 시 해당 `companyId`로 `SyncEngine.synchronizeCompany` 호출
* 메시지 컨버터: `Jackson2JsonMessageConverter`

### 에러 메시지 정책

* 라이브러리에서 던지는 예외 메시지는 `[org-sync]` 접두사를 포함해 로그에서 출처를 명확히 한다.
* 동일한 접두사를 모든 모듈에서 사용해 운영자가 장애 원인을 빠르게 식별할 수 있도록 한다.

---

## 1. 범위 / 비범위

### 1.1 In-Scope (라이브러리가 제공)

1. 동기화 엔진

* 커서 로드/저장
* 스냅샷/델타 처리 분기
* JDBC upsert/delete 반영
* 트랜잭션/락/멱등성 보장
  * `LockManager` 인터페이스만 의존하며, 기본 구현은 DB의 `SELECT ... FOR UPDATE` 기반 `JdbcLockManager`이다.
  * 소비 애플리케이션은 인프라에 맞춰 자체 락 구현(예: Redis 뮤텍스, 다른 DB 락)을 주입할 수 있다.

2. 저장 선택(프로젝션)

* YAML 또는 코드 DSL 기반 도메인/필드 선택
* YAML 또는 DSL 기반 테이블/컬럼 매핑
* (옵션) 레코드 필터링(조건 저장)

3. 실행 시 검증

* YAML에 선언된 테이블/컬럼이 실제 DB에 존재하는지 검증(실패 시 fail-fast)

4. 이벤트 발행(하위 서버 내부)

* EntityCreated/EntityUpdated/EntityDeleted
* FieldUpdated(선택한 필드만)
* SnapshotApplied(스냅샷 처리 완료 요약 이벤트)

5. Spring/Spring Boot 통합

* Spring용 `@Configuration` 제공
* Spring Boot starter(자동 설정) 제공 ([Home][1])

### 1.2 Out-of-Scope (초기 버전에서 제외)

* 조직도 서버의 내부 저장소/스키마 설계
* 조직도 서버의 데이터 생성/정합성 검증 로직
* 하위 서버의 “도메인 모델(JPA 엔티티)” 제공 (JDBC 기반)
* 분산 트랜잭션(2PC) 보장

---

## 2. 핵심 가정(계약)

라이브러리 구현의 성공을 위해 조직도 서버 응답은 다음을 만족해야 한다.

1. 델타(변경 로그)에는 아래를 제공
* 변경된 엔티티의 **after 값**(저장에 필요한 값)

2. 커서(cursor)는 “여기까지 처리했다”를 나타내는 값이며,

* 요청은 `sinceCursor`로 하고,
* 응답은 `nextCursor`를 준다.
* 일반적으로 `seq > sinceCursor`(exclusive) 규칙을 따른다.

3. 조직도 서버는 하위 서버의 커서가 오래되거나 로그가 유실된 경우 `needSnapshot=true`로 응답할 수 있다.

---

## 3. 도메인 범위

지원 도메인(고정):

* 회사그룹
* 겸직 정보
* 조직 코드
* 사용자
* 부서
* 조직 관계(사용자-부서)

각 도메인은 “기본 키(식별자)”를 갖는다. (예: userUuid, deptUuid 등)
3.1 도메인별 “저장 가능한 필드 카탈로그” (원천 스키마 → 하위 서버 프로젝션)

목표: 조직도 서버가 제공할 수 있는 “표준 필드(캐노니컬)”를 정의하고, 하위 서버는 YAML 또는 코드 DSL로 필요한 필드만 저장한다.

3.1.1 공통 필드(모든 도메인 권장)
필드	의미	권장 타입(Postgres)	비고
createdAt	생성 시각	timestamptz	스냅샷에서도 제공 권장
updatedAt	최종 수정 시각	timestamptz	델타/스냅샷 공통

시간은 UTC ISO-8601을 계약으로 권장.

3.1.2 도메인별 표준 필드 (캐노니컬)

아래 필드들은 “조직도 서버가 제공 가능한 최대치”를 정의한 것이고, 하위 서버 저장 여부는 YAML/DSL로 선택한다.

(A) USER (사용자)

PK 후보: userUuid(권장), 또는 userId(숫자형)

필드:

userUuid : varchar(64)

loginId : varchar(128) (옵션)

name : varchar(256)

displayName : varchar(256) (옵션)

email : varchar(320) (옵션)

mobile : varchar(32) (옵션)

orgCode : varchar(128) (조직 코드)

status : varchar(32) (예: ACTIVE/LEAVE/LOCKED)

positionName : varchar(128) (옵션)

jobTitleName : varchar(128) (옵션)

workType : varchar(32) (옵션)

sortOrder : int (옵션)

(B) DEPT (부서)

PK 후보: deptUuid

필드:

deptUuid : varchar(64)

parentDeptUuid : varchar(64) (루트면 null)

deptName : varchar(256)

deptCode : varchar(128) (옵션)

path : text (옵션, 예: /ROOT/SALES/TEAM1)

level : int (옵션)

sortOrder : int (옵션)

(C) USER_DEPT (조직 관계: 사용자-부서)

PK 후보: 복합키 (userUuid, deptUuid) 또는 별도 relUuid

필드:

userUuid : varchar(64)

deptUuid : varchar(64)

role : varchar(32) (예: MEMBER/LEADER) (옵션)

primary : boolean (주부서 여부) (옵션)

joinedAt : timestamptz (옵션)

leftAt : timestamptz (옵션)

(D) ORG_CODE (조직 코드)

PK 후보: orgCode

필드:

orgCode : varchar(128)

orgName : varchar(256) (옵션)

enabled : boolean (옵션)

(E) CONCURRENT_POSITION (겸직 정보)

PK 후보: id 또는 (userUuid, deptUuid, fromAt) 등

필드:

userUuid : varchar(64)

deptUuid : varchar(64)

fromAt : timestamptz (옵션)

toAt : timestamptz (옵션)

type : varchar(32) (옵션)

(F) COMPANY_GROUP (회사그룹)

PK 후보: groupId 또는 groupUuid

필드:

groupId : varchar(64)

groupName : varchar(256)

memberCompanyIds : text 또는 별도 매핑 테이블 권장
---

## 4. 아키텍처 / 모듈 구성

### 4.1 모듈

1. `orgsync-core`

* Spring/Boot 의존 없음
* 동기화 엔진, 스펙 빌더/YAML 파서/검증기, JDBC 적용기, 커서 저장 인터페이스, 이벤트 인터페이스(SPI)

2. `orgsync-spring`

* Spring Framework 통합
* 트랜잭션 연동, Spring 이벤트 발행 어댑터, (선택) Spring AMQP 리스너 어댑터

3. `orgsync-spring-boot-starter`

* Boot 자동 설정 제공 ([Home][1])
* `DataSource`, `PlatformTransactionManager`가 존재할 때 자동 활성화

> Boot2/Spring5와 Boot3/Spring6가 혼재라면 starter를 라인 분리하는 설계를 권장(추후 결정)

---

## 5. 외부 인터페이스(조직도 서버 / RabbitMQ)

### 5.1 RabbitMQ 트리거 이벤트(입력)

* exchange/queue/binding은 사용 서비스에서 구성 가능
* 메시지 payload 최소 예시:

```json
{
  "companyId": "C001",
  "eventId": "uuid",
  "occurredAt": "2025-12-23T10:00:00Z"
}
```

### 5.2 ACK 전략 (권장)

* 메시지 ACK는 **DB 반영 + 커서 저장 트랜잭션 커밋 이후** 수행
* Spring AMQP 사용 시 `AcknowledgeMode.MANUAL`을 권장 ([Home][2])

### 5.3 조직도 서버 Pull API (초안)

1. 변경 로그 요청

* `GET /orgsync/changes?companyId={id}&sinceCursor={cursor}&projection={...}`
* 응답:

```json
{
  "needSnapshot": false,
  "nextCursor": "123456789",
  "changes": [
    {
      "domain": "USER",
      "op": "UPDATE",
      "key": {"userUuid":"U1"},
      "changedFields": ["name","orgCode"],
      "after": {"userUuid":"U1","name":"홍길동","orgCode":"SALES-1"},
      "occurredAt": "..."
    }
  ]
}
```

2. 스냅샷 요청/응답(청크 권장)

* 응답:

```json
{
  "needSnapshot": true,
  "snapshotCursor": "200000000",
  "chunks": [
    {"domain":"USER","items":[...], "chunkNo":1, "last":false},
    {"domain":"USER","items":[...], "chunkNo":2, "last":true}
  ]
}
```

---

## 6. 하위 서버 DB 요구사항

### 6.1 커서 저장 테이블(기본 제공 DDL)

라이브러리는 기본 DDL(예: Postgres/MySQL)을 제공하거나, “사용 서비스가 생성”하도록 문서화한다.

예시(논리):

* `sync_state(company_id PK, last_cursor, last_success_at, last_snapshot_at, version, updated_at)`
* 동시성 제어를 위해 `company_id` 단일 row에 대해 락을 잡는다.

### 6.2 JDBC 적용 방식

* `JdbcTemplate` 또는 `NamedParameterJdbcTemplate` 기반
* 대량 처리는 `batchUpdate`를 기본 전략으로 사용 ([Home][3])

---

## 7. 동기화 엔진 동작 명세

### 7.1 처리 흐름(요약)

1. (트리거) 회사 이벤트 수신
2. 현재 `last_cursor` 읽기
3. 조직도 서버에 `sinceCursor=last_cursor`로 pull
4. 응답이 스냅샷이면 snapshot 적용, 아니면 changes 적용
5. DB 반영 + 커서 전진을 **단일 트랜잭션**으로 커밋
6. 커밋 성공 후:

* 메시지 ACK
* 내부 이벤트 발행

### 7.2 동시 실행 제어(기본)

**DB 행 락 + 커서 CAS**를 기본으로 한다.

* 트랜잭션 시작 후 `sync_state`의 `company_id` 행을 `SELECT ... FOR UPDATE`로 잠금 ([PostgreSQL][4])
* “내가 pull 요청에 사용한 sinceCursor”와 현재 DB last_cursor가 다르면:

  * 다른 워커가 먼저 처리한 것으로 보고 이번 실행은 중단(또는 재시작 정책 선택)

추가 안전장치(CAS):

* `UPDATE sync_state SET last_cursor=? WHERE company_id=? AND last_cursor=?`
* 영향 row=0이면 커서 경합 → 중단/재시작

DB 락용 테이블/컬럼은 설정으로 바꿀 수 있다.

* 기본: `SELECT uuid FROM company WHERE uuid=:companyUuid FOR UPDATE`
* 변경: `orgsync.lock.company.table`, `orgsync.lock.company.uuid-column`
  * ex) `orgsync.lock.company.table=tenant_company`, `orgsync.lock.company.uuid-column=company_uuid`

### 7.3 Redis 분산락(옵션)

* 목적: “중복 pull”을 줄이기 위한 single-flight 최적화
* 정합성의 최종 방어선은 **DB 커서 CAS**로 유지

설정으로 선택:

* `lock.strategy = DB_ONLY | REDIS_SINGLE_FLIGHT`

---

## 8. “필요 데이터만 저장” 명세 (YAML/DSL)

### 8.1 YAML 목표

하위 서버가 선언만으로 아래를 정의:

* 저장할 도메인 on/off
* 저장할 필드(화이트리스트)
* 테이블/컬럼 매핑
* PK(또는 유니크 키)
* upsert/patch/delete 정책
* (옵션) 레코드 필터 정책
* (옵션) 이벤트 발행 범위

### 8.2 Java Builder DSL 예시

`OrgSyncSpec` 빌더 DSL을 사용하면 YAML 없이 코드만으로 프로젝션을 정의할 수 있다.

```java
import static org.orgsync.core.spec.OrgSyncSpec.orgsyncSpec;
import static org.orgsync.core.spec.RecordFilters.prefix;
import static org.orgsync.core.spec.SqlColumnType.*;

@Bean
OrgSyncSpec orgSyncSpec() {
  return orgsyncSpec(spec -> {
    spec.state(s -> s.table("sync_state").companyIdColumn("company_id").cursorColumn("last_cursor"));
    spec.validateSchemaOnStartup(true);

    spec.domain("USER", d -> {
      d.enabled(true);
      d.table("app_user");
      d.pk("user_uuid");
      d.writeMode(WriteMode.UPSERT);
      d.deleteMode(DeleteMode.HARD_DELETE);
      d.map("userUuid", "user_uuid", VARCHAR, 64, false);
      d.map("name", "user_name", VARCHAR, 256, false);
      d.map("orgCode", "org_code", VARCHAR, 128, true);
      d.map("updatedAt", "updated_at", TIMESTAMPTZ, null, false);
      d.filter(prefix("orgCode", "SALES-"));
      d.emit(e -> e.entityEvents(true).fieldEvents("name", "orgCode"));
    });

    spec.domain("DEPT", d -> d.enabled(false));
  });
}
```

### 8.3 YAML 예시(초안)

```yaml
orgsync:
  state:
    table: sync_state
    companyIdColumn: company_id
    cursorColumn: last_cursor

  validateSchemaOnStartup: true

  domains:
    USER:
      enabled: true
      table: app_user
      pk: [user_uuid]
      writeMode: UPSERT
      deleteMode: HARD_DELETE
      columns:
        userUuid: user_uuid
        name: user_name
        orgCode: org_code
      filters:
        - type: PREFIX
          field: orgCode
          value: "SALES-"
      emit:
        entityEvents: true
        fieldEvents: [name, orgCode]

    DEPT:
      enabled: false
```

### 8.4 스키마 검증 규칙

`validateSchemaOnStartup=true`일 때:

* 각 enabled 도메인에 대해 table 존재 여부 확인
* `pk` 컬럼 존재 여부 확인
* `columns`에 선언된 컬럼 존재 여부 확인
* 실패 시 애플리케이션 기동 실패(fail-fast)

---

## 9. 이벤트 발행 명세(하위 서버 내부)

### 9.1 이벤트 타입

* `EntityCreated(domain, key, after)`
* `EntityUpdated(domain, key, changedFields, after)`
* `EntityDeleted(domain, key)`
* `FieldUpdated(domain, key, field, oldValue?, newValue)`
* `SnapshotApplied(companyId, cursor, domains)`

### 9.2 이벤트 발행 정책

* 기본: 델타 처리 시에만 Entity/Field 이벤트 발행
* 스냅샷 처리 시:

  * 기본은 `SnapshotApplied`만 발행(대량 이벤트 폭주 방지)
  * 옵션으로 스냅샷에서도 Entity 이벤트 발행 가능(주의: 성능/폭주)

---

## 10. SPI(확장 포인트) 명세

YAML로 커버하기 어려운 서비스별 로직을 코드로 플러그인 할 수 있게 한다.

### 10.1 SPI 목록(초안)

* `RowFilter`: 레코드 저장 여부 결정
* `ValueTransformer`: 필드 값 변환
* `MergeStrategy`: patch/merge 규칙(널 처리, 우선순위)
* `EventMapper`: 변경 결과 → 이벤트 생성 규칙

### 10.2 결합 방식

* Spring/Boot 환경: Spring Bean 주입이 기본
* (옵션) 순수 자바 환경을 대비하면 ServiceLoader 지원 가능(후순위)

---

## 11. Spring / Spring Boot 통합 명세

### 11.1 Boot Starter 동작

* `DataSource`가 존재하면 동기화 관련 빈 자동 등록 ([Home][1])
* `JdbcTemplate` 없으면 `DataSource`로 생성
* RabbitMQ 사용 시, 설정이 있을 때만 Listener 컨테이너 등록(옵션)

### 11.2 DataSource 선택 규칙

* 기본: `@Primary` DataSource 사용
* 옵션: `orgsync.datasourceBeanName`으로 명시 선택

---

## 12. 실패/재시도/멱등성 정책

### 12.1 멱등성 원칙

* DB 반영은 UPSERT 중심(또는 PK 기반 insert/update 분리)
* 커서 전진은 “커밋 성공 후” + CAS로 중복/경합 방어

### 12.2 재시도

* 조직도 서버 호출 실패: 재시도(지수 백오프) + 최대 횟수
* DB 반영 실패: 트랜잭션 롤백 → 메시지 NACK/requeue 또는 DLQ 정책(옵션)

### 12.3 독성 메시지 대응

* 반복 실패 시 DLQ로 라우팅(운영 설정 가이드 제공)

---

## 13. 성능 요구사항(초안)

* 스냅샷/대량 변경 시:

  * 도메인별 청크 적용
  * JDBC batchUpdate 사용 권장 ([Home][3])
* 동시성:

  * 회사 단위 직렬화가 기본(락 키=companyId)
* 메모리:

  * 대량 응답은 스트리밍/청크 단위 처리(전체 적재 금지)

---

## 14. 구현 산출물(코덱스 작업 항목)

### 14.1 Repository/Build

* 멀티모듈 Gradle(또는 Maven)
* 모듈: core / spring / boot-starter
* 샘플 앱 2개:

  * boot-sample
  * spring-sample

### 14.2 핵심 클래스(초안)

* `SyncEngine`
* `OrgChartClient` (HTTP)
* `SyncStateRepository` (JDBC)
* `JdbcApplier`
* `YamlSpecLoader` + `SpecValidator`
* `DomainEventPublisher`(인터페이스) + Spring 구현
* `LockManager`(DB 기본, Redis 옵션)

### 14.3 테스트(필수)

* 단위 테스트: YAML 파싱/검증, SQL 생성, 커서 CAS
* 통합 테스트(Testcontainers 권장):

  * DB row lock 경합 시 커서 역전 방지
  * 스냅샷 처리 후 커서 갱신
  * 델타 중복 수신에도 멱등 처리

---

## 15. MVP 우선순위(추천)

1. 델타 처리 + DB 락/CAS + JDBC UPSERT + 커서 저장
2. YAML 도메인/필드/컬럼 매핑 + 기동 검증
3. 스냅샷(청크) 처리 + SnapshotApplied 이벤트
4. 필드 이벤트(whitelist) + SPI 확장
5. Redis single-flight 옵션 + DLQ 운영 가이드

---
