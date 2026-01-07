package org.orgsync.core.repository;

import org.orgsync.core.dto.CompanyGroupDto;

public interface CompanyGroupRepository {

    void create(String companyUuid, CompanyGroupDto companyGroupDto);

    void update(String companyUuid, Long companyGroupId, String fieldName, Object updatedValue);

    void delete(String companyUuid, Long companyGroupId);
}
