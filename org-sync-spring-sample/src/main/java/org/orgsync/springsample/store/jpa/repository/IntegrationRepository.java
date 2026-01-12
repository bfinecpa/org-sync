package org.orgsync.springsample.store.jpa.repository;

import org.orgsync.springsample.store.jpa.entity.IntegrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationRepository extends JpaRepository<IntegrationEntity, Long> {
}
