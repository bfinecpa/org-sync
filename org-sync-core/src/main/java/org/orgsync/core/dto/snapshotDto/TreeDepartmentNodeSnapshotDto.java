package org.orgsync.core.dto.snapshotDto;

import java.time.ZonedDateTime;
import java.util.Map;
import org.orgsync.core.dto.type.MultiLanguageType;

public class TreeDepartmentNodeSnapshotDto {

    private Long id;

    private String name;

    private Map<MultiLanguageType, String> multiLanguageMap;

    private int sortOrder;

    private String email;

    private Boolean isDeleted;

    private ZonedDateTime deletedAt;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<MultiLanguageType, String> getMultiLanguageMap() {
        return multiLanguageMap;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }
}
