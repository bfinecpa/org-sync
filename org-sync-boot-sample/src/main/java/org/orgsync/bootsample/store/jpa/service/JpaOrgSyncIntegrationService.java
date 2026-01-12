package org.orgsync.bootsample.store.jpa.service;

import java.util.List;
import java.util.stream.Collectors;
import org.orgsync.bootsample.store.jpa.entity.IntegrationEntity;
import org.orgsync.bootsample.store.jpa.repository.IntegrationRepository;
import org.orgsync.core.dto.domainDto.IntegrationDto;
import org.orgsync.core.service.OrgSyncIntegrationService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaOrgSyncIntegrationService implements OrgSyncIntegrationService {

    private final IntegrationRepository integrationRepository;

    public JpaOrgSyncIntegrationService(IntegrationRepository integrationRepository) {
        this.integrationRepository = integrationRepository;
    }

    @Override
    public IntegrationDto findById(Long id) {
        return integrationRepository.findById(id)
            .map(this::toDto)
            .orElse(null);
    }

    @Override
    public void create(IntegrationDto integrationDto) {
        if (integrationDto == null) {
            return;
        }
        integrationRepository.save(new IntegrationEntity(integrationDto.getId()));
    }

    @Override
    public void update(IntegrationDto integrationDto) {
        create(integrationDto);
    }

    @Override
    public void delete(Long id) {
        integrationRepository.deleteById(id);
    }

    @Override
    public List<IntegrationDto> findByCompanyId(Long id) {
        return integrationRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private IntegrationDto toDto(IntegrationEntity entity) {
        return new IntegrationDto(entity.getId());
    }
}
