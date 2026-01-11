package org.orgsync.bootsample;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.service.OrgSyncCompanyGroupService;
import org.orgsync.core.service.OrgSyncCompanyService;
import org.orgsync.core.service.OrgSyncDepartmentService;
import org.orgsync.core.service.OrgSyncIntegrationService;
import org.orgsync.core.service.OrgSyncLogSeqService;
import org.orgsync.core.service.OrgSyncMemberService;
import org.orgsync.core.service.OrgSyncMultiLanguageService;
import org.orgsync.core.service.OrgSyncOrganizationCodeService;
import org.orgsync.core.service.OrgSyncUserGroupCodeUserService;
import org.orgsync.core.service.OrgSyncUserService;
import org.orgsync.spring.lock.InMemoryLockManager;
import org.orgsync.spring.store.InMemoryOrgSyncStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.ResourcelessTransactionManager;


@SpringBootApplication
public class OrgSyncBootSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrgSyncBootSampleApplication.class, args);
    }

    @Bean
    CommandLineRunner runSync(SyncEngine syncEngine) {
        return args -> syncEngine.synchronizeCompany("sample-company", 0L);
    }

    @Bean
    public InMemoryOrgSyncStore orgSyncStore() {
        InMemoryOrgSyncStore store = new InMemoryOrgSyncStore();
        store.registerCompany("sample-company");
        return store;
    }

    @Bean
    public LockManager lockManager() {
        return new InMemoryLockManager();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public OrgSyncLogSeqService orgSyncLogSeqService(InMemoryOrgSyncStore store) {
        return store.logSeqService();
    }

    @Bean
    public OrgSyncOrganizationCodeService orgSyncOrganizationCodeService(InMemoryOrgSyncStore store) {
        return store.organizationCodeService();
    }

    @Bean
    public OrgSyncDepartmentService orgSyncDepartmentService(InMemoryOrgSyncStore store) {
        return store.departmentService();
    }

    @Bean
    public OrgSyncUserService orgSyncUserService(InMemoryOrgSyncStore store) {
        return store.userService();
    }

    @Bean
    public OrgSyncMemberService orgSyncMemberService(InMemoryOrgSyncStore store) {
        return store.memberService();
    }

    @Bean
    public OrgSyncIntegrationService orgSyncIntegrationService(InMemoryOrgSyncStore store) {
        return store.integrationService();
    }

    @Bean
    public OrgSyncCompanyGroupService orgSyncCompanyGroupService(InMemoryOrgSyncStore store) {
        return store.companyGroupService();
    }

    @Bean
    public OrgSyncCompanyService orgSyncCompanyService(InMemoryOrgSyncStore store) {
        return store.companyService();
    }

    @Bean
    public OrgSyncUserGroupCodeUserService orgSyncUserGroupCodeUserService(InMemoryOrgSyncStore store) {
        return store.userGroupCodeUserService();
    }

    @Bean
    public OrgSyncMultiLanguageService orgSyncMultiLanguageService(InMemoryOrgSyncStore store) {
        return store.multiLanguageService();
    }
}
