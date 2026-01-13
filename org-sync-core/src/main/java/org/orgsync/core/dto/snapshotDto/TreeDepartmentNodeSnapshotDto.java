package org.orgsync.core.dto.snapshotDto;

import java.time.ZonedDateTime;
import java.util.Map;
import org.orgsync.core.dto.type.MultiLanguageType;

public record TreeDepartmentNodeSnapshotDto(
    Long id,
    String name,
    Map<MultiLanguageType, String> multiLanguageMap,
    int sortOrder,
    String email,
    Boolean isDeleted,
    ZonedDateTime deletedAt
) {
}
