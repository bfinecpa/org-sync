package org.orgsync.core.repository;

import org.orgsync.core.dto.OrganizationCodeDto;

public interface OrganizationCodeRepository {

    void create(String companyUuid, OrganizationCodeDto organizationCodeDto);

    void update(String companyUuid, Long organizationCodeId, String fieldName, Object updatedValue);

    void delete(String companyUuid, Long organizationCodeId);
}
