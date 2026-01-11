package org.orgsync.boot;

import org.orgsync.boot.config.CompanyLockProperties;
import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.event.DomainEventPublisher;
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
import org.orgsync.spring.config.OrgSyncConfiguration;
import org.orgsync.spring.event.SpringDomainEventPublisher;
import org.orgsync.spring.lock.InMemoryLockManager;
import org.orgsync.spring.lock.JdbcLockManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * Spring Boot auto-configuration that exposes the sync engine and defaults.
 */
@AutoConfiguration
@ConditionalOnClass(SyncEngine.class)
@Import(OrgSyncConfiguration.class)
@EnableConfigurationProperties(CompanyLockProperties.class)
public class OrgSyncAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LockManager.class)
    @ConditionalOnBean({DataSource.class, PlatformTransactionManager.class})
    public LockManager lockManager(DataSource dataSource,
                                   PlatformTransactionManager transactionManager,
                                   CompanyLockProperties lockProperties) {
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        return new JdbcLockManager(new NamedParameterJdbcTemplate(dataSource), txTemplate,
                lockProperties.getTable(), lockProperties.getUuidColumn());
    }

    @Bean
    @ConditionalOnMissingBean(LockManager.class)
    public LockManager inMemoryLockManager() {
        return new InMemoryLockManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public DomainEventPublisher domainEventPublisher(ApplicationEventPublisher publisher) {
        return new SpringDomainEventPublisher(publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public SyncEngine syncEngine(OrgChartClient client,
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
                                 com.fasterxml.jackson.databind.ObjectMapper objectMapper,
                                 SyncLogger syncLogger) {
        return new SyncEngine(client, LogSeqService, lockManager, transactionRunner, organizationCodeService,
            departmentService, userService, memberService, integrationService,
            companyGroupService, companyService, userGroupCodeUserService, multiLanguageService, objectMapper,
            syncLogger);
    }
}
