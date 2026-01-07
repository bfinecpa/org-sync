package org.orgsync.core.repository;

import org.orgsync.core.dto.DepartmentDto;

public interface DepartmentRepository {

    void create(String companyUuid, DepartmentDto departmentDto);

    void update(String companyUuid, Long departmentId, String fieldName, Object updatedValue);

    void delete(String companyUuid, Long departmentId);
}
