package org.orgsync.spring.config;

import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.event.DomainEventPublisher;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.service.OrgSyncCompanyGroupService;
import org.orgsync.core.service.OrgSyncCompanyService;
import org.orgsync.core.service.OrgSyncDepartmentService;
import org.orgsync.core.service.OrgSyncIntegrationService;
import org.orgsync.core.service.OrgSyncOrganizationCodeService;
import org.orgsync.core.service.OrgSyncMemberService;
import org.orgsync.core.service.OrgSyncMultiLanguageService;
import org.orgsync.core.service.OrgSyncUserGroupCodeUserService;
import org.orgsync.core.service.OrgSyncUserService;
import org.orgsync.core.service.OrgSyncLogSeqService;
import org.orgsync.core.transaction.TransactionRunner;
import org.orgsync.spring.event.SpringDomainEventPublisher;
import org.orgsync.spring.transaction.SpringTransactionRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Core Spring configuration that wires the sync engine and supporting components.
 */
@Configuration
public class OrgSyncConfiguration {

    @Bean
    public DomainEventPublisher domainEventPublisher(ApplicationEventPublisher publisher) {
        return new SpringDomainEventPublisher(publisher);
    }

    @Bean
    public TransactionRunner transactionRunner(PlatformTransactionManager transactionManager) {
        return new SpringTransactionRunner(new TransactionTemplate(transactionManager));
    }

    @Bean
    public SyncEngine syncEngine(OrgChartClient defaultOrgChartClient,
                                 OrgSyncLogSeqService LogSeqService,
                                 LockManager lockManager,
                                 TransactionRunner transactionRunner,
                                 OrgSyncOrganizationCodeService organizationCodeService,
                                 OrgSyncDepartmentService departmentService,
                                 OrgSyncUserService userService,
                                 OrgSyncMemberService memberService,
                                 OrgSyncIntegrationService integrationService,
                                 OrgSyncCompanyGroupService companyGroupService,
                                 OrgSyncCompanyService companyService,
                                 OrgSyncUserGroupCodeUserService userGroupCodeUserService,
                                 OrgSyncMultiLanguageService multiLanguageService,
                                 ObjectMapper objectMapper) {
        return new SyncEngine(defaultOrgChartClient, LogSeqService, lockManager, transactionRunner,
            organizationCodeService, departmentService, userService, memberService,
            integrationService, companyGroupService, companyService, userGroupCodeUserService,
            multiLanguageService, objectMapper);
    }
}
