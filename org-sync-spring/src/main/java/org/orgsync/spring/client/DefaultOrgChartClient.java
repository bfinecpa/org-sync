package org.orgsync.spring.client;

import org.orgsync.core.Constants;
import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.dto.ProvisionSequenceDto;
import org.springframework.web.client.RestClient;

import java.util.Objects;

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
    public ProvisionSequenceDto fetchChanges(String companyUuid, Long logSeq) {
        ProvisionSequenceDto response = restClient.get()
                .uri("/company/{companyUuid}/sequence/{logSeq}", companyUuid, normalizeCursor(logSeq))
                .retrieve()
                .body(ProvisionSequenceDto.class);

        if (response == null) {
            throw new IllegalStateException(Constants.ERROR_PREFIX + "Failed to fetch changes: response body is null");
        }

        return response;
    }

    private Long normalizeCursor(Long logSeq) {
        return logSeq == null ? -1L : logSeq;
    }
}
