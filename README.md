# org-sync

조직도 서버(원천)에서 내려오는 변경 로그/스냅샷을 하위 시스템으로 동기화하기 위한 공통 라이브러리입니다. 회사 단위 락과 트랜잭션을 지원하며, Spring/Spring Boot 환경에서 쉽게 통합할 수 있습니다.

## 구성 모듈

- **org-sync-core**: 동기화 엔진, DTO, 서비스 인터페이스
- **org-sync-spring**: Spring 어댑터(REST 클라이언트, AMQP 리스너)
- **org-sync-boot-starter**: Spring Boot 자동 설정

## 요구 사항

- Java 17+
- Spring Framework 6.x 또는 Spring Boot 3.x (spring 모듈 사용 시)

## 설치 예시 (Gradle)

```groovy
dependencies {
    implementation "org.orgsync:org-sync-core:0.1.0-SNAPSHOT"
    implementation "org.orgsync:org-sync-spring:0.1.0-SNAPSHOT"
    implementation "org.orgsync:org-sync-boot-starter:0.1.0-SNAPSHOT"
}
```

> 배포 전에는 로컬 또는 사내 저장소에 publish 후 사용하세요.

## 빠른 시작 (Spring Boot)

1) **필수 서비스 구현**

동기화가 동작하려면 아래 서비스 인터페이스를 구현해야 합니다.

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

2) **LockManager 등록**

```java
@Bean
public LockManager lockManager() {
    return new InMemoryLockManager();
}
```

3) **org chart 서버 설정**

```yaml
orgsync:
  client:
    base-url: http://org-chart.example.com
```

`base-url`이 있어야 `OrgChartRestClient`가 자동 생성됩니다.

4) **RabbitMQ 이벤트 수신**

`org-sync-boot-starter`는 기본적으로 아래 큐/교환기를 사용합니다.

- Queue: `orgsync.org-chart.sync.queue`
- Exchange: `dop_user_company_sync.fanout`

발행되는 메시지 예시는 다음과 같습니다.

```json
{
  "companyUuid": "C001",
  "logSeq": 1234
}
```

## Spring (Boot 없이) 통합

`org-sync-spring`을 사용하는 경우 직접 빈을 구성해야 합니다.

- `OrgChartClient` 구현 제공
- `LockManager` 구현 제공
- `TransactionRunner` 구현 제공

Spring 기본 구성은 `OrgSyncConfiguration`에서 제공됩니다.

## 참고 문서

- `org-sync-spec.md`: 상세 동기화 스펙과 데이터 계약
- `org-sync-boot-sample`: JPA 기반 예시 구현

## 라이선스

내부 사용을 전제로 하는 사내 라이브러리입니다.
