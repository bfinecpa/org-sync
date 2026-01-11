package org.orgsync.core.dto.snapshotDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.orgsync.core.dto.domainDto.DepartmentDto;
import org.orgsync.core.dto.domainDto.MultiLanguageDto;
import org.orgsync.core.dto.type.DepartmentStatus;
import org.orgsync.core.dto.type.MultiLanguageType;
import org.orgsync.core.dto.type.TargetDomain;

public class DepartmentSnapshotDto {

    private Long deptId;

    private String name;

    Map<MultiLanguageType, String> multiLanguageMap;

    private int sortOrder;

    private String code;

    private String alias;

    private String email;

    private Boolean isDeleted;

    private String departmentPath;

    private String status;

    public Long getDeptId() {
        return deptId;
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

    public String getCode() {
        return code;
    }

    public String getAlias() {
        return alias;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public String getDepartmentPath() {
        return departmentPath;
    }

    public String getStatus() {
        return status;
    }

    public List<MultiLanguageDto> toMultiLanguageDtos() {
        List<MultiLanguageDto> multiLanguageDtos = new ArrayList<>();
        for (Entry<MultiLanguageType, String> entry : multiLanguageMap.entrySet()) {
            multiLanguageDtos.add(new MultiLanguageDto(this.deptId, TargetDomain.DEPARTMENT, entry.getKey(), entry.getValue()));
        }
        return multiLanguageDtos;
    }

    public DepartmentDto toDepartmentDto() {
        DepartmentStatus departmentStatus = status == null ? null : DepartmentStatus.valueOf(status);
        return new DepartmentDto(
            deptId,
            null,
            name,
            null,
            sortOrder,
            code,
            alias,
            email,
            departmentStatus,
            departmentPath
        );
    }
}

