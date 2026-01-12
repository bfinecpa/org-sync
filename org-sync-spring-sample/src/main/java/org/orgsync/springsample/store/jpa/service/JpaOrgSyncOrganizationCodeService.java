package org.orgsync.springsample.store.jpa.service;

import java.util.List;
import java.util.stream.Collectors;
import org.orgsync.springsample.store.jpa.entity.OrganizationCodeEntity;
import org.orgsync.springsample.store.jpa.repository.OrganizationCodeRepository;
import org.orgsync.core.dto.domainDto.OrganizationCodeDto;
import org.orgsync.core.service.OrgSyncOrganizationCodeService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaOrgSyncOrganizationCodeService implements OrgSyncOrganizationCodeService {

    private final OrganizationCodeRepository organizationCodeRepository;

    public JpaOrgSyncOrganizationCodeService(OrganizationCodeRepository organizationCodeRepository) {
        this.organizationCodeRepository = organizationCodeRepository;
    }

    @Override
    public OrganizationCodeDto findById(Long id) {
        return organizationCodeRepository.findById(id)
            .map(this::toDto)
            .orElse(null);
    }

    @Override
    public void create(OrganizationCodeDto organizationCodeDto) {
        if (organizationCodeDto == null) {
            return;
        }
        organizationCodeRepository.save(toEntity(organizationCodeDto));
    }

    @Override
    public void update(OrganizationCodeDto organizationCodeDto) {
        create(organizationCodeDto);
    }

    @Override
    public void delete(Long domainId) {
        organizationCodeRepository.deleteById(domainId);
    }

    @Override
    public List<OrganizationCodeDto> findByCompanyId(Long id) {
        return organizationCodeRepository.findByCompanyId(id).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private OrganizationCodeEntity toEntity(OrganizationCodeDto dto) {
        return new OrganizationCodeEntity(dto.getId(), dto.getCompanyId(), dto.getCode(), dto.getType(),
            dto.getName(), dto.getSortOrder());
    }

    private OrganizationCodeDto toDto(OrganizationCodeEntity entity) {
        return new OrganizationCodeDto(entity.getId(), entity.getCompanyId(), entity.getCode(), entity.getType(),
            entity.getName(), entity.getSortOrder());
    }
}
