package org.orgsync.bootsample;


import org.orgsync.core.engine.SyncEngine;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class OrgSyncBootSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrgSyncBootSampleApplication.class, args);
    }

    @Bean
    CommandLineRunner runSync(SyncEngine syncEngine) {
        return args -> syncEngine.synchronizeCompany("sample-company", -1L);
    }

}
