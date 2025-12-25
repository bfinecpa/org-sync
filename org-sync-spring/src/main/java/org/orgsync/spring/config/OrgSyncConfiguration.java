package org.orgsync.spring.config;

import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.event.DomainEventPublisher;
import org.orgsync.core.jdbc.JdbcApplier;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.spec.OrgSyncSpec;
import org.orgsync.core.spec.SpecValidator;
import org.orgsync.core.spec.YamlSpecLoader;
import org.orgsync.core.state.LogSeqRepository;
import org.orgsync.spring.event.SpringDomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.nio.file.Path;

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
    public YamlSpecLoader yamlSpecLoader() {
        return new YamlSpecLoader();
    }

    @Bean
    public OrgSyncSpec orgSyncSpec(YamlSpecLoader loader) {
        // TODO: externalize specification path to configuration
        return OrgSyncSpec.fromYaml(loader.load(Path.of("org-sync.yaml")));
    }

    @Bean
    public SpecValidator specValidator() {
        return new SpecValidator();
    }

    @Bean
    public JdbcApplier jdbcApplier(DataSource dataSource, OrgSyncSpec orgSyncSpec) {
        return new JdbcApplier(dataSource, orgSyncSpec);
    }

    @Bean
    public SyncEngine syncEngine(OrgChartClient client,
                                 LogSeqRepository stateRepository,
                                 JdbcApplier jdbcApplier,
                                 DomainEventPublisher eventPublisher,
                                 LockManager lockManager) {
        return new SyncEngine(client, stateRepository, jdbcApplier, eventPublisher, lockManager);
    }
}
