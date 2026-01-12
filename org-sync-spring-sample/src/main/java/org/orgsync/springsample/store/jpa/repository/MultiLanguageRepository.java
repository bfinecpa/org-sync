package org.orgsync.springsample.store.jpa.repository;

import org.orgsync.springsample.store.jpa.entity.MultiLanguageEntity;
import org.orgsync.springsample.store.jpa.entity.MultiLanguageId;
import org.orgsync.core.dto.type.TargetDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MultiLanguageRepository extends JpaRepository<MultiLanguageEntity, MultiLanguageId> {

    void deleteByIdIdAndIdTargetDomain(Long id, TargetDomain targetDomain);
}
