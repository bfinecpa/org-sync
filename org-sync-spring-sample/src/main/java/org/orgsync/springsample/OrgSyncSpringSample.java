package org.orgsync.springsample;

import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.engine.SyncResponse;
import org.orgsync.core.event.DomainEventPublisher;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.state.LogSeqRepository;
import org.orgsync.core.spec.YamlSyncSpec;
import org.orgsync.spring.config.OrgSyncConfiguration;
import org.orgsync.spring.event.SpringDomainEventPublisher;
import org.orgsync.spring.lock.InMemoryLockManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class OrgSyncSpringSample {

    public static void main(String[] args) {
        try (GenericApplicationContext context = new AnnotationConfigApplicationContext(SampleConfig.class, OrgSyncConfiguration.class)) {
            SyncEngine engine = context.getBean(SyncEngine.class);
            engine.synchronizeCompany("spring-sample", -1L);
        }
    }

    @Configuration
    static class SampleConfig {

        @Bean
        public OrgChartClient orgChartClient() {
            return (companyId, sinceCursor) -> new SyncResponse(false, -1L, Set.of("user"), Collections.emptyList());
        }

        @Bean
        public LogSeqRepository syncStateRepository() {
            return new LogSeqRepository() {
                private Long logSeq;

                @Override
                public Optional<Long> loadLogSeq(String companyUuid) {
                    return Optional.ofNullable(logSeq);
                }

                @Override
                public void saveCursor(String companyUuid, Long nextCursor) {
                    this.logSeq = nextCursor;
                }
            };
        }

        @Bean
        public YamlSyncSpec yamlSyncSpec() {
            return new YamlSyncSpec(Collections.emptyMap());
        }

        @Bean
        public DomainEventPublisher domainEventPublisher(org.springframework.context.ApplicationEventPublisher publisher) {
            return new SpringDomainEventPublisher(publisher);
        }

        @Bean
        public LockManager lockManager() {
            return new InMemoryLockManager();
        }

        @Bean
        public DataSource dataSource() {
            return new org.apache.commons.dbcp2.BasicDataSource();
        }
    }
}
