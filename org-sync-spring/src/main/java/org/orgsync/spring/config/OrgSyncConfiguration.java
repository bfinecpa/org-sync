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
import org.orgsync.core.service.OrgSyncUserService;
import org.orgsync.core.service.OrgSyncLogSeqService;
import org.orgsync.spring.event.SpringDomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Core Spring configuration that wires the sync engine and supporting components.
 */
@Configuration
public class OrgSyncConfiguration {

    @Bean
    public DomainEventPublisher domainEventPublisher(org.springframework.context.ApplicationEventPublisher publisher) {
        return new SpringDomainEventPublisher(publisher);
    }

    @Bean
    public OrgChartClient defaultOrgChartClient() {
        return (companyId, sinceCursor) -> {
            throw new UnsupportedOperationException("OrgChartClient is not configured");
        };
    }

    @Bean
    public SyncEngine syncEngine(OrgChartClient defaultOrgChartClient,
                                 OrgSyncLogSeqService LogSeqService,
                                 DomainEventPublisher eventPublisher,
                                 LockManager lockManager,
                                 OrgSyncOrganizationCodeService organizationCodeService,
                                 OrgSyncDepartmentService departmentService,
                                 OrgSyncUserService userService,
                                 OrgSyncMemberService memberService,
                                 OrgSyncIntegrationService integrationService,
                                 OrgSyncCompanyGroupService companyGroupService,
                                 OrgSyncCompanyService companyService) {
        return new SyncEngine(defaultOrgChartClient, LogSeqService, eventPublisher, lockManager,
            organizationCodeService, departmentService, userService, memberService,
            integrationService, companyGroupService, companyService);
    }
}
