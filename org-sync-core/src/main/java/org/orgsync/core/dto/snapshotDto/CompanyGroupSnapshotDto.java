package org.orgsync.core.dto.snapshotDto;

import java.util.List;

public record CompanyGroupSnapshotDto(
    Long id,
    List<String> companyUuidList
) {
}
