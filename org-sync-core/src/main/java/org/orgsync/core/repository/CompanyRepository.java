package org.orgsync.core.repository;

import java.util.Optional;
import org.orgsync.core.dto.CompanyDto;

public interface CompanyRepository {

    Optional<Long> findCompanyIdByUuid(String companyUuid);

    void create(String companyUuid, CompanyDto companyDto);

    void update(String companyUuid, Long companyId, String fieldName, Object updatedValue);

    void delete(String companyUuid, Long companyId);
}
