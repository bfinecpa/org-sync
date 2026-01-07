package org.orgsync.core.engine;

import java.util.Objects;

/**
 * Container for domain-specific delta callbacks.
 */
public final class SyncDomainCallbacks {

    private final OrganizationCodeDeltaCallback organizationCodeCallback;
    private final DepartmentDeltaCallback departmentCallback;
    private final UserDeltaCallback userCallback;
    private final RelationMemberDeltaCallback relationMemberCallback;
    private final IntegrationDeltaCallback integrationCallback;
    private final CompanyGroupDeltaCallback companyGroupCallback;
    private final CompanyDeltaCallback companyCallback;

    private SyncDomainCallbacks(Builder builder) {
        this.organizationCodeCallback = Objects.requireNonNull(builder.organizationCodeCallback,
            "organizationCodeCallback");
        this.departmentCallback = Objects.requireNonNull(builder.departmentCallback, "departmentCallback");
        this.userCallback = Objects.requireNonNull(builder.userCallback, "userCallback");
        this.relationMemberCallback = Objects.requireNonNull(builder.relationMemberCallback,
            "relationMemberCallback");
        this.integrationCallback = Objects.requireNonNull(builder.integrationCallback, "integrationCallback");
        this.companyGroupCallback = Objects.requireNonNull(builder.companyGroupCallback, "companyGroupCallback");
        this.companyCallback = Objects.requireNonNull(builder.companyCallback, "companyCallback");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SyncDomainCallbacks noop() {
        return builder().build();
    }

    public OrganizationCodeDeltaCallback organizationCode() {
        return organizationCodeCallback;
    }

    public DepartmentDeltaCallback department() {
        return departmentCallback;
    }

    public UserDeltaCallback user() {
        return userCallback;
    }

    public RelationMemberDeltaCallback relationMember() {
        return relationMemberCallback;
    }

    public IntegrationDeltaCallback integration() {
        return integrationCallback;
    }

    public CompanyGroupDeltaCallback companyGroup() {
        return companyGroupCallback;
    }

    public CompanyDeltaCallback company() {
        return companyCallback;
    }

    public static final class Builder {
        private OrganizationCodeDeltaCallback organizationCodeCallback = OrganizationCodeDeltaCallback.noop();
        private DepartmentDeltaCallback departmentCallback = DepartmentDeltaCallback.noop();
        private UserDeltaCallback userCallback = UserDeltaCallback.noop();
        private RelationMemberDeltaCallback relationMemberCallback = RelationMemberDeltaCallback.noop();
        private IntegrationDeltaCallback integrationCallback = IntegrationDeltaCallback.noop();
        private CompanyGroupDeltaCallback companyGroupCallback = CompanyGroupDeltaCallback.noop();
        private CompanyDeltaCallback companyCallback = CompanyDeltaCallback.noop();

        private Builder() {
        }

        public Builder organizationCode(OrganizationCodeDeltaCallback callback) {
            this.organizationCodeCallback = Objects.requireNonNull(callback, "callback");
            return this;
        }

        public Builder department(DepartmentDeltaCallback callback) {
            this.departmentCallback = Objects.requireNonNull(callback, "callback");
            return this;
        }

        public Builder user(UserDeltaCallback callback) {
            this.userCallback = Objects.requireNonNull(callback, "callback");
            return this;
        }

        public Builder relationMember(RelationMemberDeltaCallback callback) {
            this.relationMemberCallback = Objects.requireNonNull(callback, "callback");
            return this;
        }

        public Builder integration(IntegrationDeltaCallback callback) {
            this.integrationCallback = Objects.requireNonNull(callback, "callback");
            return this;
        }

        public Builder companyGroup(CompanyGroupDeltaCallback callback) {
            this.companyGroupCallback = Objects.requireNonNull(callback, "callback");
            return this;
        }

        public Builder company(CompanyDeltaCallback callback) {
            this.companyCallback = Objects.requireNonNull(callback, "callback");
            return this;
        }

        public SyncDomainCallbacks build() {
            return new SyncDomainCallbacks(this);
        }
    }
}
