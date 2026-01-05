package org.orgsync.spring.config;

import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.event.DomainEventPublisher;
import org.orgsync.core.jdbc.JdbcApplier;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.state.LogSeqRepository;
import org.orgsync.spring.event.SpringDomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

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
    public JdbcApplier jdbcApplier(DataSource dataSource) {
        return new JdbcApplier(dataSource);
    }

    @Bean
    public SyncEngine syncEngine(OrgChartClient defaultOrgChartClient,
                                 LogSeqRepository logSeqRepository,
                                 JdbcApplier jdbcApplier,
                                 DomainEventPublisher eventPublisher,
                                 LockManager lockManager) {
        return new SyncEngine(defaultOrgChartClient, logSeqRepository, jdbcApplier, eventPublisher, lockManager);
    }
}
