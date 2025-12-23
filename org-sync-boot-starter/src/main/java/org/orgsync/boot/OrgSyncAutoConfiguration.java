package org.orgsync.boot;

import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.event.DomainEventPublisher;
import org.orgsync.core.jdbc.JdbcApplier;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.spec.SpecValidator;
import org.orgsync.core.spec.YamlSyncSpec;
import org.orgsync.core.state.SyncStateRepository;
import org.orgsync.spring.config.OrgSyncConfiguration;
import org.orgsync.spring.event.SpringDomainEventPublisher;
import org.orgsync.spring.lock.InMemoryLockManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

/**
 * Spring Boot auto-configuration that exposes the sync engine and defaults.
 */
@AutoConfiguration
@ConditionalOnClass(SyncEngine.class)
@Import(OrgSyncConfiguration.class)
public class OrgSyncAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LockManager lockManager() {
        return new InMemoryLockManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public DomainEventPublisher domainEventPublisher(ApplicationEventPublisher publisher) {
        return new SpringDomainEventPublisher(publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({DataSource.class, YamlSyncSpec.class})
    public JdbcApplier jdbcApplier(DataSource dataSource, YamlSyncSpec syncSpec) {
        return new JdbcApplier(dataSource, syncSpec);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpecValidator specValidator() {
        return new SpecValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public SyncEngine syncEngine(OrgChartClient client,
                                 SyncStateRepository stateRepository,
                                 JdbcApplier jdbcApplier,
                                 DomainEventPublisher eventPublisher,
                                 LockManager lockManager) {
        return new SyncEngine(client, stateRepository, jdbcApplier, eventPublisher, lockManager);
    }
}
