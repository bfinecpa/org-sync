package org.orgsync.core.dto.snapshotDto;

import java.util.List;

public class SnapshotDto {

    private Long logSeq;

    private List<CompanyGroupSnapshotDto> companyGroupSnapshot;

    private List<IntegrationSnapshotDto> integrationSnapshot;

    private List<OrganizationCodeSnapshotDto> organizationCodeSnapshot;

    private List<UserSnapshotDto> userSnapshot;

    private List<DepartmentSnapshotDto> departmentSnapshot;

    private List<TreeSnapshotDto> relationSnapshot;

    public Long getLogSeq() {
        return logSeq;
    }

    public List<CompanyGroupSnapshotDto> getCompanyGroupSnapshot() {
        return companyGroupSnapshot;
    }

    public List<IntegrationSnapshotDto> getIntegrationSnapshot() {
        return integrationSnapshot;
    }

    public List<OrganizationCodeSnapshotDto> getOrganizationCodeSnapshot() {
        return organizationCodeSnapshot;
    }

    public List<UserSnapshotDto> getUserSnapshot() {
        return userSnapshot;
    }

    public List<DepartmentSnapshotDto> getDepartmentSnapshot() {
        return departmentSnapshot;
    }

    public List<TreeSnapshotDto> getRelationSnapshot() {
        return relationSnapshot;
    }

    public SnapshotDto() {
    }

    public SnapshotDto(Long logSeq, List<CompanyGroupSnapshotDto> companyGroupSnapshot,
        List<IntegrationSnapshotDto> integrationSnapshot, List<OrganizationCodeSnapshotDto> organizationCodeSnapshot,
        List<UserSnapshotDto> userSnapshot, List<DepartmentSnapshotDto> departmentSnapshot,
        List<TreeSnapshotDto> relationSnapshot) {
        this.logSeq = logSeq;
        this.companyGroupSnapshot = companyGroupSnapshot;
        this.integrationSnapshot = integrationSnapshot;
        this.organizationCodeSnapshot = organizationCodeSnapshot;
        this.userSnapshot = userSnapshot;
        this.departmentSnapshot = departmentSnapshot;
        this.relationSnapshot = relationSnapshot;
    }
}
