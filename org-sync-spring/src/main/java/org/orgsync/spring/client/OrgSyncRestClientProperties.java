package org.orgsync.spring.client;

public class OrgSyncRestClientProperties {

    public static final String DEFAULT_CHANGES_PATH = "/api/provision/common/sync/company/{companyUuid}/sequence/{logSeq}";
    public static final String DEFAULT_SNAPSHOT_PATH = "/api/provision/common/sync/company/{companyUuid}/snapshot/{snapshotId}";

    private String baseUrl;
    private String changesPath = DEFAULT_CHANGES_PATH;
    private String snapshotPath = DEFAULT_SNAPSHOT_PATH;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getChangesPath() {
        return changesPath;
    }

    public void setChangesPath(String changesPath) {
        this.changesPath = changesPath;
    }

    public String getSnapshotPath() {
        return snapshotPath;
    }

    public void setSnapshotPath(String snapshotPath) {
        this.snapshotPath = snapshotPath;
    }
}
