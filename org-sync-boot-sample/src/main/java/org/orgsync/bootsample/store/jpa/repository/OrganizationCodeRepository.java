package org.orgsync.bootsample.store.jpa.repository;

import java.util.List;
import org.orgsync.bootsample.store.jpa.entity.OrganizationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationCodeRepository extends JpaRepository<OrganizationCodeEntity, Long> {

    List<OrganizationCodeEntity> findByCompanyId(Long companyId);
}
