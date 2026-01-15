package org.orgsync.spring.config;

import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.logging.SyncLogger;
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
import org.orgsync.spring.logging.Slf4jSyncLogger;
import org.orgsync.spring.transaction.SpringTransactionRunner;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
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
    public TransactionRunner transactionRunner(PlatformTransactionManager transactionManager) {
        return new SpringTransactionRunner(new TransactionTemplate(transactionManager));
    }

    @Bean
    public SyncLogger syncLogger() {
        return new Slf4jSyncLogger(LoggerFactory.getLogger(SyncEngine.class));
    }

    @Bean
    public SyncEngine syncEngine(OrgChartClient defaultOrgChartClient,
                                 OrgSyncLogSeqService logSeqService,
                                 LockManager lockManager,
                                 TransactionRunner transactionRunner,
                                 ObjectProvider<OrgSyncOrganizationCodeService> organizationCodeServiceProvider,
                                 ObjectProvider<OrgSyncDepartmentService> departmentServiceProvider,
                                 ObjectProvider<OrgSyncUserService> userServiceProvider,
                                 ObjectProvider<OrgSyncMemberService> memberServiceProvider,
                                 ObjectProvider<OrgSyncIntegrationService> integrationServiceProvider,
                                 ObjectProvider<OrgSyncCompanyGroupService> companyGroupServiceProvider,
                                 OrgSyncCompanyService companyService,
                                 ObjectProvider<OrgSyncUserGroupCodeUserService> userGroupCodeUserServiceProvider,
                                 ObjectProvider<OrgSyncMultiLanguageService> multiLanguageServiceProvider,
                                 ObjectMapper objectMapper,
                                 SyncLogger syncLogger) {
        OrgSyncOrganizationCodeService organizationCodeService =
            organizationCodeServiceProvider.getIfAvailable(() -> new OrgSyncOrganizationCodeService() {});
        OrgSyncDepartmentService departmentService =
            departmentServiceProvider.getIfAvailable(() -> new OrgSyncDepartmentService() {});
        OrgSyncUserService userService =
            userServiceProvider.getIfAvailable(() -> new OrgSyncUserService() {});
        OrgSyncMemberService memberService =
            memberServiceProvider.getIfAvailable(() -> new OrgSyncMemberService() {});
        OrgSyncIntegrationService integrationService =
            integrationServiceProvider.getIfAvailable(() -> new OrgSyncIntegrationService() {});
        OrgSyncCompanyGroupService companyGroupService =
            companyGroupServiceProvider.getIfAvailable(() -> new OrgSyncCompanyGroupService() {});
        OrgSyncUserGroupCodeUserService userGroupCodeUserService =
            userGroupCodeUserServiceProvider.getIfAvailable(() -> new OrgSyncUserGroupCodeUserService() {});
        OrgSyncMultiLanguageService multiLanguageService =
            multiLanguageServiceProvider.getIfAvailable(() -> new OrgSyncMultiLanguageService() {});
        return new SyncEngine(defaultOrgChartClient, logSeqService, lockManager, transactionRunner,
            organizationCodeService, departmentService, userService, memberService,
            integrationService, companyGroupService, companyService, userGroupCodeUserService,
            multiLanguageService, objectMapper, syncLogger);
    }
}
