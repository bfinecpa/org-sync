package org.orgsync.springsample.store.jpa.repository;

import java.util.List;
import java.util.Optional;
import org.orgsync.springsample.store.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    List<UserEntity> findByCompanyId(Long companyId);

    Optional<UserEntity> findByCompanyIdAndIntegrationId(Long companyId, Long integrationId);

    Optional<UserEntity> findFirstByCompanyIdAndIdIn(Long companyId, List<Long> ids);

    boolean existsByIntegrationId(Long integrationId);
}
