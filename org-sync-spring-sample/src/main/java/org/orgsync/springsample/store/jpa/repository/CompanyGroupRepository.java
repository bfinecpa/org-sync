package org.orgsync.springsample.store.jpa.repository;

import org.orgsync.springsample.store.jpa.entity.CompanyGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyGroupRepository extends JpaRepository<CompanyGroupEntity, Long> {
}
