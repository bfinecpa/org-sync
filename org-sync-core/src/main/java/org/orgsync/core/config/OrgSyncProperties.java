package org.orgsync.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class OrgSyncProperties {

    @JsonProperty("org-sync")
    private OrgSyncRoot root;

    public OrgSyncRoot getRoot() {
        return root;
    }

    public Optional<DomainSpec> organizationCodeSpec() {
        if (root == null || root.getSpec() == null || root.getSpec().getDomain() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(root.getSpec().getDomain().getOrganizationCode());
    }

    public Optional<DomainSpec> departmentSpec() {
        if (root == null || root.getSpec() == null || root.getSpec().getDomain() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(root.getSpec().getDomain().getDepartment());
    }

    public Optional<DomainSpec> userSpec() {
        if (root == null || root.getSpec() == null || root.getSpec().getDomain() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(root.getSpec().getDomain().getUser());
    }

    public Optional<DomainSpec> relationMemberSpec() {
        if (root == null || root.getSpec() == null || root.getSpec().getDomain() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(root.getSpec().getDomain().getRelationMember());
    }

    public Optional<DomainSpec> integrationSpec() {
        if (root == null || root.getSpec() == null || root.getSpec().getDomain() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(root.getSpec().getDomain().getIntegration());
    }

    public Optional<DomainSpec> companyGroupSpec() {
        if (root == null || root.getSpec() == null || root.getSpec().getDomain() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(root.getSpec().getDomain().getCompanyGroup());
    }

    public Optional<DomainSpec> companySpec() {
        if (root == null || root.getSpec() == null || root.getSpec().getDomain() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(root.getSpec().getDomain().getCompany());
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
        private DomainSpec organizationCode;
        @JsonProperty("department")
        private DomainSpec department;
        @JsonProperty("user")
        private DomainSpec user;
        @JsonProperty("relation-member")
        private DomainSpec relationMember;
        @JsonProperty("integration")
        private DomainSpec integration;
        @JsonProperty("company-group")
        private DomainSpec companyGroup;
        @JsonProperty("company")
        private DomainSpec company;

        public DomainSpec getOrganizationCode() {
            return organizationCode;
        }

        public void setOrganizationCode(DomainSpec organizationCode) {
            this.organizationCode = organizationCode;
        }

        public DomainSpec getDepartment() {
            return department;
        }

        public void setDepartment(DomainSpec department) {
            this.department = department;
        }

        public DomainSpec getUser() {
            return user;
        }

        public void setUser(DomainSpec user) {
            this.user = user;
        }

        public DomainSpec getRelationMember() {
            return relationMember;
        }

        public void setRelationMember(DomainSpec relationMember) {
            this.relationMember = relationMember;
        }

        public DomainSpec getIntegration() {
            return integration;
        }

        public void setIntegration(DomainSpec integration) {
            this.integration = integration;
        }

        public DomainSpec getCompanyGroup() {
            return companyGroup;
        }

        public void setCompanyGroup(DomainSpec companyGroup) {
            this.companyGroup = companyGroup;
        }

        public DomainSpec getCompany() {
            return company;
        }

        public void setCompany(DomainSpec company) {
            this.company = company;
        }
    }

    public static class DomainSpec {

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
