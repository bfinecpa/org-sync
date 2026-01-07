package org.orgsync.boot;

import org.orgsync.boot.config.CompanyLockProperties;
import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.event.DomainEventPublisher;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.repository.CompanyGroupRepository;
import org.orgsync.core.repository.CompanyRepository;
import org.orgsync.core.repository.DepartmentRepository;
import org.orgsync.core.repository.IntegrationRepository;
import org.orgsync.core.repository.OrganizationCodeRepository;
import org.orgsync.core.repository.RelationMemberRepository;
import org.orgsync.core.repository.UserRepository;
import org.orgsync.core.state.LogSeqRepository;
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
                                 LogSeqRepository logSeqRepository,
                                 DomainEventPublisher eventPublisher,
                                 LockManager lockManager,
                                 OrganizationCodeRepository organizationCodeRepository,
                                 DepartmentRepository departmentRepository,
                                 UserRepository userRepository,
                                 RelationMemberRepository relationMemberRepository,
                                 IntegrationRepository integrationRepository,
                                 CompanyGroupRepository companyGroupRepository,
                                 CompanyRepository companyRepository) {
        return new SyncEngine(client, logSeqRepository, eventPublisher, lockManager, organizationCodeRepository,
            departmentRepository, userRepository, relationMemberRepository, integrationRepository,
            companyGroupRepository, companyRepository);
    }
}
