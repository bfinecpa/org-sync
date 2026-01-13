package org.orgsync.core.dto.snapshotDto;

import java.util.List;

public record SnapshotDto(
    Long logSeq,
    List<CompanyGroupSnapshotDto> companyGroupSnapshot,
    List<IntegrationSnapshotDto> integrationSnapshot,
    List<OrganizationCodeSnapshotDto> organizationCodeSnapshot,
    List<UserSnapshotDto> userSnapshot,
    List<DepartmentSnapshotDto> departmentSnapshot,
    List<TreeSnapshotDto> relationSnapshot
) {
}
