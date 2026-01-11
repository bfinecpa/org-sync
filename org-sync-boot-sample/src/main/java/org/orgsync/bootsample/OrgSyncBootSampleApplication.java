package org.orgsync.bootsample;

import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.engine.SyncResponse;
import org.orgsync.core.event.DomainEvent;
import org.orgsync.core.event.DomainEventPublisher;
import org.orgsync.core.service.OrgSyncLogSeqService;
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
        return args -> syncEngine.synchronizeCompany("sample-company", -1L);
    }

    @Bean
    OrgChartClient orgChartClient() {
        return (companyId, sinceCursor) -> new SyncResponse(false, -1L, Set.of("user"), Collections.emptyList());
    }

    @Bean
    OrgSyncLogSeqService syncLogSeqRepository() {
        return new OrgSyncLogSeqService() {
            private Long logSeq;

            @Override
            public Optional<Long> getLogSeq(String companyUuid) {
                return Optional.ofNullable(logSeq);
            }

            @Override
            public void saveCursor(String companyUuid, Long nextCursor) {
                this.logSeq = nextCursor;
            }
        };
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
