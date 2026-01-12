package org.orgsync.springsample.store.jpa.service;

import org.orgsync.springsample.store.jpa.entity.CompanyGroupEntity;
import org.orgsync.springsample.store.jpa.repository.CompanyGroupRepository;
import org.orgsync.springsample.store.jpa.repository.CompanyRepository;
import org.orgsync.core.dto.domainDto.CompanyGroupDto;
import org.orgsync.core.service.OrgSyncCompanyGroupService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaOrgSyncCompanyGroupService implements OrgSyncCompanyGroupService {

    private final CompanyGroupRepository companyGroupRepository;
    private final CompanyRepository companyRepository;

    public JpaOrgSyncCompanyGroupService(CompanyGroupRepository companyGroupRepository,
                                         CompanyRepository companyRepository) {
        this.companyGroupRepository = companyGroupRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public CompanyGroupDto findById(Long id) {
        return companyGroupRepository.findById(id)
            .map(this::toDto)
            .orElse(null);
    }

    @Override
    public void create(CompanyGroupDto companyGroupDto) {
        if (companyGroupDto == null) {
            return;
        }
        companyGroupRepository.save(new CompanyGroupEntity(companyGroupDto.getId()));
    }

    @Override
    public void update(CompanyGroupDto companyGroupDto) {
        create(companyGroupDto);
    }

    @Override
    public void delete(Long companyGroupId) {
        companyGroupRepository.deleteById(companyGroupId);
    }

    @Override
    public CompanyGroupDto findByCompanyId(Long id) {
        return companyRepository.findById(id)
            .map(company -> company.getCompanyGroupId())
            .flatMap(companyGroupRepository::findById)
            .map(this::toDto)
            .orElse(null);
    }

    private CompanyGroupDto toDto(CompanyGroupEntity entity) {
        return new CompanyGroupDto(entity.getId());
    }
}
