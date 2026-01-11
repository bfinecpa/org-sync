package org.orgsync.bootsample;

import java.util.List;
import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.ProvisionSequenceDto;
import org.orgsync.core.dto.snapshotDto.SnapshotDto;
import org.orgsync.core.dto.type.DomainType;
import org.orgsync.core.dto.type.LogType;

public class SampleOrgChartClient implements OrgChartClient {

    private static final List<LogInfoDto> FIRST_BATCH = List.of(
        new LogInfoDto(DomainType.DEPARTMENT, 10L, "id", 10L, LogType.CREATE),
        new LogInfoDto(DomainType.DEPARTMENT, 10L, "name", "Engineering", LogType.CREATE),
        new LogInfoDto(DomainType.DEPARTMENT, 10L, "sortOrder", 1, LogType.CREATE),
        new LogInfoDto(DomainType.DEPARTMENT, 10L, "status", "ACTIVE", LogType.CREATE),
        new LogInfoDto(DomainType.DEPARTMENT, 10L, "departmentPath", "/Engineering", LogType.CREATE),
        new LogInfoDto(DomainType.USER, 100L, "id", 100L, LogType.CREATE),
        new LogInfoDto(DomainType.USER, 100L, "name", "Sample User", LogType.CREATE),
        new LogInfoDto(DomainType.USER, 100L, "loginId", "sample.user", LogType.CREATE),
        new LogInfoDto(DomainType.USER, 100L, "locale", "ko_KR", LogType.CREATE),
        new LogInfoDto(DomainType.RELATION_MEMBER, 1000L, "id", 1000L, LogType.CREATE),
        new LogInfoDto(DomainType.RELATION_MEMBER, 1000L, "user", 100L, LogType.CREATE),
        new LogInfoDto(DomainType.RELATION_MEMBER, 1000L, "department", 10L, LogType.CREATE),
        new LogInfoDto(DomainType.RELATION_MEMBER, 1000L, "memberType", "TEAM_MEMBER", LogType.CREATE),
        new LogInfoDto(DomainType.RELATION_MEMBER, 1000L, "sortOrder", 1, LogType.CREATE),
        new LogInfoDto(DomainType.RELATION_MEMBER, 1000L, "departmentOrder", 1, LogType.CREATE)
    );

    private static final List<LogInfoDto> SECOND_BATCH = List.of(
        new LogInfoDto(DomainType.USER, 100L, "name", "Updated User", LogType.UPDATE),
        new LogInfoDto(DomainType.RELATION_MEMBER, 1000L, "id", 1000L, LogType.DELETE)
    );

    @Override
    public ProvisionSequenceDto fetchChanges(String companyUuid, Long logSeq) {
        if (logSeq == null || logSeq < 0) {
            return new ProvisionSequenceDto(false, List.of(), 1L, true, FIRST_BATCH);
        }
        if (logSeq == 1L) {
            return new ProvisionSequenceDto(false, List.of(), 2L, false, SECOND_BATCH);
        }
        return new ProvisionSequenceDto(false, List.of(), logSeq, false, List.of());
    }

    @Override
    public SnapshotDto fetchSnapshot(String companyUuid, Long snapshotId) {
        throw new UnsupportedOperationException("Snapshots are not used in the sample client.");
    }
}
