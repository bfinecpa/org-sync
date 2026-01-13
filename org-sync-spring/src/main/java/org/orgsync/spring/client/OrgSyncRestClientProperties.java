package org.orgsync.spring.client;

import java.util.Arrays;

public class OrgSyncRestClientProperties {

    public static final String DEFAULT_CHANGES_PATH = "/api/provision/common/sync/company/{companyUuid}/sequence/{logSeq}";
    public static final String DEFAULT_SNAPSHOT_PATH = "/api/provision/common/sync/company/{companyUuid}/snapshot/{snapshotId}";
    public static final String LOCAL_BASE_URL = "http://localhost:8080";
    public static final String DEFAULT_BASE_URL = "http://dop-service-gateway.dop-platform.svc.cluster.local:20719";

    private static final String LOCAL_PROFILE = "local";

    private String baseUrl;
    private String changesPath = DEFAULT_CHANGES_PATH;
    private String snapshotPath = DEFAULT_SNAPSHOT_PATH;

    public String getBaseUrl() {
        if (baseUrl != null && !baseUrl.isBlank()) {
            return baseUrl;
        }
        String baseUrl = isLocalProfileActive() ? LOCAL_BASE_URL : DEFAULT_BASE_URL;
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

    private boolean isLocalProfileActive() {
        String profiles = resolveProfiles("spring.profiles.active", "SPRING_PROFILES_ACTIVE");
        if (profiles == null || profiles.isBlank()) {
            profiles = resolveProfiles("spring.profiles.default", "SPRING_PROFILES_DEFAULT");
        }
        if (profiles == null || profiles.isBlank()) {
            return false;
        }
        return Arrays.stream(profiles.split(","))
            .map(String::trim)
            .anyMatch(profile -> profile.equalsIgnoreCase(LOCAL_PROFILE));
    }

    private String resolveProfiles(String propertyKey, String envKey) {
        String profiles = System.getProperty(propertyKey);
        if (profiles != null && !profiles.isBlank()) {
            return profiles;
        }
        return System.getenv(envKey);
    }
}
