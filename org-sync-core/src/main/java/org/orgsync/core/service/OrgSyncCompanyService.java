package org.orgsync.core.service;

import org.orgsync.core.dto.domainDto.CompanyDto;

public interface OrgSyncCompanyService {


    CompanyDto findByUuid(String companyUuid);

    default void updateCompanyGroupId(CompanyDto companyDto) {}

    default boolean existsByCompanyGroupId(Long companyGroupId) {return false;}
}
