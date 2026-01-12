package org.orgsync.bootsample.store.jpa.repository;

import org.orgsync.bootsample.store.jpa.entity.IntegrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationRepository extends JpaRepository<IntegrationEntity, Long> {
}
