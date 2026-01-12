package org.orgsync.spring.client;

import java.net.URI;
import org.orgsync.core.Constants;
import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.dto.ProvisionSequenceDto;
import org.orgsync.core.dto.snapshotDto.SnapshotDto;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

public class OrgChartRestClient implements OrgChartClient {

    private final RestClient restClient;
    private final String changesPath;
    private final String snapshotPath;

    public OrgChartRestClient(RestClient restClient, OrgSyncRestClientProperties properties) {
        this(restClient, properties.getChangesPath(), properties.getSnapshotPath());
    }

    public OrgChartRestClient(RestClient restClient, String changesPath, String snapshotPath) {
        if (restClient == null) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "RestClient is required");
        }
        this.restClient = restClient;
        this.changesPath = normalizePath(changesPath, OrgSyncRestClientProperties.DEFAULT_CHANGES_PATH);
        this.snapshotPath = normalizePath(snapshotPath, OrgSyncRestClientProperties.DEFAULT_SNAPSHOT_PATH);
    }

    @Override
    public ProvisionSequenceDto fetchChanges(String companyUuid, Long logSeq) {
        if (!StringUtils.hasText(companyUuid)) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "companyUuid is required for change fetch");
        }

        ProvisionSequenceDto response = restClient.get()
            .uri(uriBuilder -> uriBuilder.path(changesPath).build(companyUuid, logSeq))
            .retrieve()
            .body(ProvisionSequenceDto.class);

        if (response == null) {
            throw new IllegalStateException(Constants.ORG_SYNC_PREFIX + "No response from org chart server");
        }

        return response;
    }

    @Override
    public SnapshotDto fetchSnapshot(String companyUuid, Long snapshotId) {
        if (!StringUtils.hasText(companyUuid)) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "companyUuid is required for snapshot fetch");
        }
        if (snapshotId == null) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "snapshotId is required for snapshot fetch");
        }

        SnapshotDto response = restClient.get()
            .uri(uriBuilder -> uriBuilder.path(snapshotPath).build(companyUuid, snapshotId))
            .retrieve()
            .body(SnapshotDto.class);

        if (response == null) {
            throw new IllegalStateException(Constants.ORG_SYNC_PREFIX + "No snapshot response from org chart server");
        }

        return response;
    }

    private String normalizePath(String candidate, String fallback) {
        String path = StringUtils.hasText(candidate) ? candidate : fallback;
        return path.startsWith("/") ? path : "/" + path;
    }
}
