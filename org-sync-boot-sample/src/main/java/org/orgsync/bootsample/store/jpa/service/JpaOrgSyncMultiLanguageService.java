package org.orgsync.bootsample.store.jpa.service;

import java.util.List;
import java.util.stream.Collectors;
import org.orgsync.bootsample.store.jpa.entity.MultiLanguageEntity;
import org.orgsync.bootsample.store.jpa.entity.MultiLanguageId;
import org.orgsync.bootsample.store.jpa.repository.MultiLanguageRepository;
import org.orgsync.core.dto.domainDto.MultiLanguageDto;
import org.orgsync.core.dto.type.TargetDomain;
import org.orgsync.core.service.OrgSyncMultiLanguageService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaOrgSyncMultiLanguageService implements OrgSyncMultiLanguageService {

    private final MultiLanguageRepository multiLanguageRepository;

    public JpaOrgSyncMultiLanguageService(MultiLanguageRepository multiLanguageRepository) {
        this.multiLanguageRepository = multiLanguageRepository;
    }

    @Override
    public void create(List<MultiLanguageDto> multiLanguageDtos) {
        if (multiLanguageDtos == null) {
            return;
        }
        List<MultiLanguageEntity> entities = multiLanguageDtos.stream()
            .filter(dto -> dto.getMultiLanguageType() != null)
            .map(this::toEntity)
            .collect(Collectors.toList());
        multiLanguageRepository.saveAll(entities);
    }

    @Override
    public void delete(Long id, TargetDomain targetDomain) {
        multiLanguageRepository.deleteByIdIdAndIdTargetDomain(id, targetDomain);
    }

    @Override
    public void delete(List<MultiLanguageDto> multiLanguageDtos) {
        if (multiLanguageDtos == null) {
            return;
        }
        List<MultiLanguageId> ids = multiLanguageDtos.stream()
            .filter(dto -> dto.getMultiLanguageType() != null)
            .map(dto -> new MultiLanguageId(dto.getId(), dto.getTargetDomain(), dto.getMultiLanguageType()))
            .collect(Collectors.toList());
        multiLanguageRepository.deleteAllById(ids);
    }

    @Override
    public void update(List<MultiLanguageDto> multiLanguageDto) {
        create(multiLanguageDto);
    }

    private MultiLanguageEntity toEntity(MultiLanguageDto dto) {
        MultiLanguageId id = new MultiLanguageId(dto.getId(), dto.getTargetDomain(), dto.getMultiLanguageType());
        return new MultiLanguageEntity(id, dto.getValue());
    }
}
