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

public record DepartmentSnapshotDto(
    Long deptId,
    String name,
    Map<MultiLanguageType, String> multiLanguageMap,
    int sortOrder,
    String code,
    String alias,
    String email,
    Boolean isDeleted,
    String departmentPath,
    String status
) {

    public List<MultiLanguageDto> toMultiLanguageDtos() {
        List<MultiLanguageDto> multiLanguageDtos = new ArrayList<>();
        for (Entry<MultiLanguageType, String> entry : multiLanguageMap.entrySet()) {
            multiLanguageDtos.add(
                new MultiLanguageDto(this.deptId, TargetDomain.DEPARTMENT, entry.getKey(), entry.getValue()));
        }
        return multiLanguageDtos;
    }

    public DepartmentDto toDepartmentDto(Long companyId) {
        DepartmentStatus departmentStatus = status == null ? null : DepartmentStatus.valueOf(status);
        return new DepartmentDto(
            deptId,
            companyId,
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
