package org.orgsync.springsample.store.jpa.service;

import org.orgsync.springsample.store.jpa.entity.CompanyEntity;
import org.orgsync.springsample.store.jpa.repository.CompanyRepository;
import org.orgsync.core.dto.domainDto.CompanyDto;
import org.orgsync.core.service.OrgSyncCompanyService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaOrgSyncCompanyService implements OrgSyncCompanyService {

    private final CompanyRepository companyRepository;

    public JpaOrgSyncCompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public CompanyDto findByUuid(String companyUuid) {
        return companyRepository.findByUuid(companyUuid)
            .map(this::toDto)
            .orElseGet(() -> toDto(companyRepository.save(new CompanyEntity(companyUuid))));
    }

    @Override
    public void updateCompanyGroupId(CompanyDto companyDto) {
        if (companyDto == null) {
            return;
        }
        CompanyEntity entity = new CompanyEntity(companyDto.getId(), companyDto.getUuid(), companyDto.getCompanyGroupId());
        companyRepository.save(entity);
    }

    @Override
    public boolean existsByCompanyGroupId(Long companyGroupId) {
        if (companyGroupId == null) {
            return false;
        }
        return companyRepository.existsByCompanyGroupId(companyGroupId);
    }

    private CompanyDto toDto(CompanyEntity entity) {
        return new CompanyDto(entity.getId(), entity.getUuid(), entity.getCompanyGroupId());
    }
}
