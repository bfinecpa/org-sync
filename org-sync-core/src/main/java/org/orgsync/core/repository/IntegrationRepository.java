package org.orgsync.core.repository;

import org.orgsync.core.dto.IntegrationDto;

public interface IntegrationRepository {

    void create(String companyUuid, IntegrationDto integrationDto);

    void update(String companyUuid, Long integrationId, String fieldName, Object updatedValue);

    void delete(String companyUuid, Long integrationId);
}
