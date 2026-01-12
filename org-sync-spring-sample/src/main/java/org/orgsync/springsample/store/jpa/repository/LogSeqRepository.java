package org.orgsync.springsample.store.jpa.repository;

import java.util.Optional;
import org.orgsync.springsample.store.jpa.entity.LogSeqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogSeqRepository extends JpaRepository<LogSeqEntity, Long> {

    Optional<LogSeqEntity> findByCompanyUuid(String companyUuid);
}
