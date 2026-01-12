package org.orgsync.bootsample.store.jpa.repository;

import java.util.Optional;
import org.orgsync.bootsample.store.jpa.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    Optional<CompanyEntity> findByUuid(String uuid);

    boolean existsByCompanyGroupId(Long companyGroupId);
}
