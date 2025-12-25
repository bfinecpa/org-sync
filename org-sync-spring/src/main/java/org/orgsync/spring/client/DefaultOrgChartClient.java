package org.orgsync.spring.client;

import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.engine.SyncResponse;
import org.orgsync.core.event.DomainEvent;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default HTTP-based implementation of {@link OrgChartClient} that calls the
 * upstream org chart provision API.
 */
public class DefaultOrgChartClient implements OrgChartClient {

    private static final String DEFAULT_BASE_URL =
            "http://dop-portal-provision.dop-platform.svc.cluster.local:20719/api/provision/common/sync";

    private final RestClient restClient;

    public DefaultOrgChartClient() {
        this(RestClient.builder().baseUrl(DEFAULT_BASE_URL).build());
    }

    public DefaultOrgChartClient(String baseUrl) {
        this(RestClient.builder().baseUrl(baseUrl).build());
    }

    public DefaultOrgChartClient(RestClient restClient) {
        this.restClient = Objects.requireNonNull(restClient, "restClient");
    }

    @Override
    public SyncResponse fetchChanges(String companyId, Long logSeq) {
        ProvisionSequenceDto response = restClient.get()
                .uri("/company/{companyUuid}/sequence/{logSeq}", companyId, normalizeCursor(logSeq))
                .retrieve()
                .body(ProvisionSequenceDto.class);

        if (response == null) {
            throw new IllegalStateException("Failed to fetch changes: response body is null");
        }

        return toSyncResponse(response);
    }

    private Long normalizeCursor(Long logSeq) {
        return logSeq == null ? -1L : logSeq;
    }

    private SyncResponse toSyncResponse(ProvisionSequenceDto response) {
        Set<String> processedDomains = response.logInfoList() == null ? Collections.emptySet() :
                response.logInfoList().stream()
                        .map(LogInfoResponseDto::domain)
                        .filter(Objects::nonNull)
                        .map(String::valueOf)
                        .collect(Collectors.toUnmodifiableSet());

        List<DomainEvent> events = response.needSnapshot() || response.logInfoList() == null
                ? Collections.emptyList()
                : response.logInfoList().stream()
                .map(this::toDomainEvent)
                .toList();

        return new SyncResponse(response.needSnapshot(), response.logSeq(), processedDomains, events);
    }

    private DomainEvent toDomainEvent(LogInfoResponseDto logInfo) {
        DomainEvent.Type eventType = mapEventType(logInfo.logType());
        String domain = logInfo.domain() == null ? "unknown" : String.valueOf(logInfo.domain());
        String key = logInfo.domainId() == null ? "" : String.valueOf(logInfo.domainId());
        Map<String, Object> payload = logInfo.fieldName() == null
                ? null
                : Map.of(logInfo.fieldName(), logInfo.updatedValue());
        return new DomainEvent(eventType, domain, key, payload);
    }

    private DomainEvent.Type mapEventType(String logType) {
        if (logType == null) {
            return DomainEvent.Type.FIELD_UPDATED;
        }

        return switch (logType.toUpperCase(Locale.ROOT)) {
            case "CREATE" -> DomainEvent.Type.ENTITY_CREATED;
            case "DELETE" -> DomainEvent.Type.ENTITY_DELETED;
            case "SNAPSHOT" -> DomainEvent.Type.SNAPSHOT_APPLIED;
            case "UPDATE" -> DomainEvent.Type.ENTITY_UPDATED;
            default -> DomainEvent.Type.FIELD_UPDATED;
        };
    }
}
