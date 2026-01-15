package org.orgsync.core.service;

import java.util.List;
import org.orgsync.core.dto.domainDto.IntegrationDto;

public interface OrgSyncIntegrationService {

    default IntegrationDto findById(Long id) {return null;}

    default void create(IntegrationDto integrationDto) {}

    default void delete(Long id) {}

    default List<IntegrationDto> findByCompanyId(Long id) {return null;}
}
