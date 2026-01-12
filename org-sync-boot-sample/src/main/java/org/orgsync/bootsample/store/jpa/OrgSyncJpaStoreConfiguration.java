package org.orgsync.bootsample.store.jpa;

import org.orgsync.bootsample.store.jpa.repository.CompanyGroupRepository;
import org.orgsync.bootsample.store.jpa.repository.CompanyRepository;
import org.orgsync.bootsample.store.jpa.repository.DepartmentRepository;
import org.orgsync.bootsample.store.jpa.repository.IntegrationRepository;
import org.orgsync.bootsample.store.jpa.repository.LogSeqRepository;
import org.orgsync.bootsample.store.jpa.repository.MemberRepository;
import org.orgsync.bootsample.store.jpa.repository.MultiLanguageRepository;
import org.orgsync.bootsample.store.jpa.repository.OrganizationCodeRepository;
import org.orgsync.bootsample.store.jpa.repository.UserGroupUserRepository;
import org.orgsync.bootsample.store.jpa.repository.UserRepository;
import org.orgsync.bootsample.store.jpa.service.JpaOrgSyncCompanyGroupService;
import org.orgsync.bootsample.store.jpa.service.JpaOrgSyncCompanyService;
import org.orgsync.bootsample.store.jpa.service.JpaOrgSyncDepartmentService;
import org.orgsync.bootsample.store.jpa.service.JpaOrgSyncIntegrationService;
import org.orgsync.bootsample.store.jpa.service.JpaOrgSyncLogSeqService;
import org.orgsync.bootsample.store.jpa.service.JpaOrgSyncMemberService;
import org.orgsync.bootsample.store.jpa.service.JpaOrgSyncMultiLanguageService;
import org.orgsync.bootsample.store.jpa.service.JpaOrgSyncOrganizationCodeService;
import org.orgsync.bootsample.store.jpa.service.JpaOrgSyncUserGroupCodeUserService;
import org.orgsync.bootsample.store.jpa.service.JpaOrgSyncUserService;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = OrgSyncJpaStoreConfiguration.class)
public class OrgSyncJpaStoreConfiguration {

    @Bean
    public OrgSyncCompanyService orgSyncCompanyService(CompanyRepository companyRepository) {
        return new JpaOrgSyncCompanyService(companyRepository);
    }

    @Bean
    public OrgSyncCompanyGroupService orgSyncCompanyGroupService(CompanyGroupRepository companyGroupRepository,
                                                                 CompanyRepository companyRepository) {
        return new JpaOrgSyncCompanyGroupService(companyGroupRepository, companyRepository);
    }

    @Bean
    public OrgSyncIntegrationService orgSyncIntegrationService(IntegrationRepository integrationRepository) {
        return new JpaOrgSyncIntegrationService(integrationRepository);
    }

    @Bean
    public OrgSyncOrganizationCodeService orgSyncOrganizationCodeService(
        OrganizationCodeRepository organizationCodeRepository) {
        return new JpaOrgSyncOrganizationCodeService(organizationCodeRepository);
    }

    @Bean
    public OrgSyncDepartmentService orgSyncDepartmentService(DepartmentRepository departmentRepository) {
        return new JpaOrgSyncDepartmentService(departmentRepository);
    }

    @Bean
    public OrgSyncUserService orgSyncUserService(UserRepository userRepository,
                                                 UserGroupUserRepository userGroupUserRepository) {
        return new JpaOrgSyncUserService(userRepository, userGroupUserRepository);
    }

    @Bean
    public OrgSyncMemberService orgSyncMemberService(MemberRepository memberRepository) {
        return new JpaOrgSyncMemberService(memberRepository);
    }

    @Bean
    public OrgSyncUserGroupCodeUserService orgSyncUserGroupCodeUserService(
        UserGroupUserRepository userGroupUserRepository) {
        return new JpaOrgSyncUserGroupCodeUserService(userGroupUserRepository);
    }

    @Bean
    public OrgSyncMultiLanguageService orgSyncMultiLanguageService(
        MultiLanguageRepository multiLanguageRepository) {
        return new JpaOrgSyncMultiLanguageService(multiLanguageRepository);
    }

    @Bean
    public OrgSyncLogSeqService orgSyncLogSeqService(LogSeqRepository logSeqRepository,
                                                     CompanyRepository companyRepository) {
        return new JpaOrgSyncLogSeqService(logSeqRepository, companyRepository);
    }
}
