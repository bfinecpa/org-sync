package org.orgsync.core.service;

import org.orgsync.core.dto.domainDto.CompanyGroupDto;

public interface OrgSyncCompanyGroupService {

    default CompanyGroupDto findById(Long id) {
        return null;
    }

    default void create(CompanyGroupDto companyGroupDto) {}

    default void update(CompanyGroupDto companyGroupDto) {}

    default void delete(Long companyGroupId) {}

    default CompanyGroupDto findByCompanyId(Long id) {return null;}
}
