package org.orgsync.bootsample.store.jpa.repository;

import org.orgsync.bootsample.store.jpa.entity.CompanyGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyGroupRepository extends JpaRepository<CompanyGroupEntity, Long> {
}
