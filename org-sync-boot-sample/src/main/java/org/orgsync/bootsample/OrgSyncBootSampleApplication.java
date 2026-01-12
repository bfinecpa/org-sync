package org.orgsync.bootsample;


import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.lock.LockManager;
import org.orgsync.bootsample.store.jpa.OrgSyncJpaStoreConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@SpringBootApplication
@EntityScan(basePackages = "org.orgsync.bootsample.store.jpa.entity")
@EnableJpaRepositories(basePackages = "org.orgsync.bootsample.store.jpa.repository")
@Import(OrgSyncJpaStoreConfiguration.class)
public class OrgSyncBootSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrgSyncBootSampleApplication.class, args);
    }

    @Bean
    CommandLineRunner runSync(SyncEngine syncEngine) {
        return args -> syncEngine.synchronizeCompany("sample-company", 0L);
    }

    @Bean
    public LockManager lockManager() {
        return new InMemoryLockManager();
    }
}
