package org.orgsync.springsample;

import org.orgsync.core.DomainEvent;
import org.orgsync.core.DomainEventPublisher;
import org.orgsync.core.LockManager;
import org.orgsync.core.OrgChartClient;
import org.orgsync.core.SyncEngine;
import org.orgsync.core.SyncResponse;
import org.orgsync.core.SyncStateRepository;
import org.orgsync.core.YamlSyncSpec;
import org.orgsync.spring.InMemoryLockManager;
import org.orgsync.spring.OrgSyncConfiguration;
import org.orgsync.spring.SpringDomainEventPublisher;
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
            engine.synchronizeCompany("spring-sample");
        }
    }

    @Configuration
    static class SampleConfig {

        @Bean
        public OrgChartClient orgChartClient() {
            return (companyId, sinceCursor) -> new SyncResponse(false, "cursor-1", Set.of("user"), Collections.emptyList());
        }

        @Bean
        public SyncStateRepository syncStateRepository() {
            return new SyncStateRepository() {
                private String cursor;

                @Override
                public Optional<String> loadCursor(String companyId) {
                    return Optional.ofNullable(cursor);
                }

                @Override
                public void saveCursor(String companyId, String nextCursor) {
                    this.cursor = nextCursor;
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
