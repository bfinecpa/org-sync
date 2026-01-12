package org.orgsync.springsample.store.jpa.service;

import java.util.Optional;
import org.orgsync.springsample.store.jpa.entity.LogSeqEntity;
import org.orgsync.springsample.store.jpa.repository.CompanyRepository;
import org.orgsync.springsample.store.jpa.repository.LogSeqRepository;
import org.orgsync.core.service.OrgSyncLogSeqService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaOrgSyncLogSeqService implements OrgSyncLogSeqService {

    private final LogSeqRepository logSeqRepository;
    private final CompanyRepository companyRepository;

    public JpaOrgSyncLogSeqService(LogSeqRepository logSeqRepository, CompanyRepository companyRepository) {
        this.logSeqRepository = logSeqRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public Optional<Long> getLogSeq(String companyUuid) {
        return logSeqRepository.findByCompanyUuid(companyUuid)
            .map(LogSeqEntity::getLogSeq);
    }

    @Override
    public void saveLogSeq(Long companyId, Long logSeq) {
        String companyUuid = companyRepository.findById(companyId)
            .map(company -> company.getUuid())
            .orElse(null);
        LogSeqEntity entity = logSeqRepository.findById(companyId)
            .orElseGet(() -> new LogSeqEntity(companyId, companyUuid, logSeq));
        if (companyUuid != null) {
            entity.setCompanyUuid(companyUuid);
        }
        entity.setLogSeq(logSeq);
        logSeqRepository.save(entity);
    }
}
