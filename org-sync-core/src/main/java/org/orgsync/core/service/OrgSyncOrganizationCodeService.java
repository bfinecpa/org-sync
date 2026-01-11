package org.orgsync.core.service;

import java.util.List;
import org.orgsync.core.dto.domainDto.OrganizationCodeDto;

public interface OrgSyncOrganizationCodeService {

    default OrganizationCodeDto findById(Long id) {
        return null;
    }

    default void create(OrganizationCodeDto organizationCodeDto) {}

    default void update(OrganizationCodeDto organizationCodeDto) {}

    default void delete(Long domainId) {}

    default List<OrganizationCodeDto> findByCompanyId(Long id) {
        return null;
    }
}
