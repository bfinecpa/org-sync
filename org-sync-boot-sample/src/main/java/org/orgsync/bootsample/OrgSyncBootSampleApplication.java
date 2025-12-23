package org.orgsync.bootsample;

import org.orgsync.core.DomainEvent;
import org.orgsync.core.DomainEventPublisher;
import org.orgsync.core.OrgChartClient;
import org.orgsync.core.SyncEngine;
import org.orgsync.core.SyncResponse;
import org.orgsync.core.SyncStateRepository;
import org.orgsync.core.YamlSyncSpec;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
public class OrgSyncBootSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrgSyncBootSampleApplication.class, args);
    }

    @Bean
    CommandLineRunner runSync(SyncEngine syncEngine) {
        return args -> syncEngine.synchronizeCompany("sample-company");
    }

    @Bean
    OrgChartClient orgChartClient() {
        return (companyId, sinceCursor) -> new SyncResponse(false, "cursor-1", Set.of("user"), Collections.emptyList());
    }

    @Bean
    SyncStateRepository syncStateRepository() {
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
    YamlSyncSpec yamlSyncSpec() {
        return new YamlSyncSpec(Collections.emptyMap());
    }

    @Bean
    DomainEventPublisher domainEventPublisher() {
        return new DomainEventPublisher() {
            @Override
            public void publishDomainEvent(DomainEvent event) {
                System.out.println("Domain event: " + event.type());
            }

            @Override
            public void publishSnapshotApplied(String companyId, String cursor, Iterable<String> domains) {
                System.out.println("Snapshot applied for " + companyId + " at " + cursor);
            }
        };
    }

    @Bean
    DataSource dataSource() {
        return new org.apache.commons.dbcp2.BasicDataSource();
    }
}
