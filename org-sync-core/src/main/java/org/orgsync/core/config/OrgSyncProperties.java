package org.orgsync.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class OrgSyncProperties {

    @JsonProperty("orgsync")
    private OrgSyncRoot root;

    public OrgSyncRoot getRoot() {
        return root;
    }

    public Optional<OrganizationCodeSpec> organizationCodeSpec() {
        if (root == null || root.getSpec() == null || root.getSpec().getDomain() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(root.getSpec().getDomain().getOrganizationCode());
    }

    public static class OrgSyncRoot {

        private Spec spec;

        public Spec getSpec() {
            return spec;
        }

        public void setSpec(Spec spec) {
            this.spec = spec;
        }
    }

    public static class Spec {

        private Domain domain;

        public Domain getDomain() {
            return domain;
        }

        public void setDomain(Domain domain) {
            this.domain = domain;
        }
    }

    public static class Domain {

        @JsonProperty("organization-code")
        private OrganizationCodeSpec organizationCode;

        public OrganizationCodeSpec getOrganizationCode() {
            return organizationCode;
        }

        public void setOrganizationCode(OrganizationCodeSpec organizationCode) {
            this.organizationCode = organizationCode;
        }
    }

    public static class OrganizationCodeSpec {

        @JsonProperty("sync-enabled")
        private boolean syncEnabled;

        @JsonProperty("table-name")
        private String tableName;

        private Map<String, FieldSpec> fields = Collections.emptyMap();

        public boolean isSyncEnabled() {
            return syncEnabled;
        }

        public void setSyncEnabled(boolean syncEnabled) {
            this.syncEnabled = syncEnabled;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public Map<String, FieldSpec> getFields() {
            return fields;
        }

        public void setFields(Map<String, FieldSpec> fields) {
            this.fields = Objects.requireNonNullElse(fields, Collections.emptyMap());
        }
    }

    public static class FieldSpec {

        private boolean enabled;

        @JsonProperty("column-name")
        private String columnName;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }
    }
}
