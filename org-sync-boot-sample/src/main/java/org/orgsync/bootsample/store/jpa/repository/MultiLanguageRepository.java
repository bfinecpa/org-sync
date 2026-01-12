package org.orgsync.bootsample.store.jpa.repository;

import org.orgsync.bootsample.store.jpa.entity.MultiLanguageEntity;
import org.orgsync.bootsample.store.jpa.entity.MultiLanguageId;
import org.orgsync.core.dto.type.TargetDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MultiLanguageRepository extends JpaRepository<MultiLanguageEntity, MultiLanguageId> {

    void deleteByIdIdAndIdTargetDomain(Long id, TargetDomain targetDomain);
}
