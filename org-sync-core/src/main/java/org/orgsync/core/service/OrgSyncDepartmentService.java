package org.orgsync.core.service;

import java.util.List;
import org.orgsync.core.dto.domainDto.DepartmentDto;

public interface OrgSyncDepartmentService {

    default DepartmentDto findById(Long id) {return null;}

    default void create(DepartmentDto departmentDto) {}

    default void update(DepartmentDto departmentDto) {}

    default void delete(Long departmentId) {}

    default List<DepartmentDto> findByCompanyId(Long id) {
        return null;
    }

    default void updateParentId(DepartmentDto departmentDto) {}
}
