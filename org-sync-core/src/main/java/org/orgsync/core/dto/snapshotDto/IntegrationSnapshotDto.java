package org.orgsync.core.dto.snapshotDto;

import java.util.List;

public record IntegrationSnapshotDto(
    Long id,
    List<Long> userIdList
) {
}
