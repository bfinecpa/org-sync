package org.orgsync.boot;

import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.event.DomainEventPublisher;
import org.orgsync.core.jdbc.JdbcApplier;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.spec.OrgSyncSpec;
import org.orgsync.core.spec.SpecValidator;
import org.orgsync.core.spec.YamlSpecLoader;
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
import java.nio.file.Path;

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
    public SpecValidator specValidator() {
        return new SpecValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public YamlSpecLoader yamlSpecLoader() {
        return new YamlSpecLoader();
    }

    @Bean
    @ConditionalOnMissingBean
    public OrgSyncSpec orgSyncSpec(YamlSpecLoader loader) {
        return OrgSyncSpec.fromYaml(loader.load(Path.of("org-sync.yaml")));
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({DataSource.class, OrgSyncSpec.class})
    public JdbcApplier jdbcApplier(DataSource dataSource, OrgSyncSpec syncSpec) {
        return new JdbcApplier(dataSource, syncSpec);
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
