package org.orgsync.boot;

import org.orgsync.core.lock.LockManager;
import org.orgsync.core.logging.SyncLogger;
import org.orgsync.core.service.OrgSyncCompanyGroupService;
import org.orgsync.core.service.OrgSyncCompanyService;
import org.orgsync.core.service.OrgSyncDepartmentService;
import org.orgsync.core.service.OrgSyncIntegrationService;
import org.orgsync.core.service.OrgSyncOrganizationCodeService;
import org.orgsync.core.service.OrgSyncMemberService;
import org.orgsync.core.service.OrgSyncMultiLanguageService;
import org.orgsync.core.service.OrgSyncUserGroupCodeUserService;
import org.orgsync.core.service.OrgSyncUserService;
import org.orgsync.core.service.OrgSyncLogSeqService;
import org.orgsync.core.transaction.TransactionRunner;
import org.orgsync.spring.config.OrgSyncConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.client.RestClient;
import org.orgsync.core.client.OrgChartClient;
import org.orgsync.spring.client.OrgChartRestClient;
import org.orgsync.spring.client.OrgSyncRestClientProperties;

/**
 * Spring Boot auto-configuration that exposes the sync engine and defaults.
 */
@AutoConfiguration
@Import(OrgSyncConfiguration.class)
public class OrgSyncAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "orgsync.client")
    public OrgSyncRestClientProperties orgSyncRestClientProperties() {
        return new OrgSyncRestClientProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "orgsync.client", name = "base-url")
    public RestClient orgSyncRestClient(OrgSyncRestClientProperties properties,
                                        ObjectProvider<RestClient.Builder> builderProvider) {
        RestClient.Builder builder = builderProvider.getIfAvailable(RestClient::builder);
        return builder.baseUrl(properties.getBaseUrl()).build();
    }

    @Bean
    @ConditionalOnMissingBean(OrgChartClient.class)
    @ConditionalOnBean(RestClient.class)
    @ConditionalOnProperty(prefix = "orgsync.client", name = "base-url")
    public OrgChartClient orgChartClient(RestClient restClient, OrgSyncRestClientProperties properties) {
        return new OrgChartRestClient(restClient, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SyncEngine syncEngine(OrgChartClient client,
                                 OrgSyncLogSeqService LogSeqService,
                                 LockManager lockManager,
                                 TransactionRunner transactionRunner,
                                 OrgSyncOrganizationCodeService organizationCodeService,
                                 OrgSyncDepartmentService departmentService,
                                 OrgSyncUserService userService,
                                 OrgSyncMemberService memberService,
                                 OrgSyncIntegrationService integrationService,
                                 OrgSyncCompanyGroupService companyGroupService,
                                 OrgSyncCompanyService companyService,
                                 OrgSyncUserGroupCodeUserService userGroupCodeUserService,
                                 OrgSyncMultiLanguageService multiLanguageService,
                                 com.fasterxml.jackson.databind.ObjectMapper objectMapper,
                                 SyncLogger syncLogger) {
        return new SyncEngine(client, LogSeqService, lockManager, transactionRunner, organizationCodeService,
            departmentService, userService, memberService, integrationService,
            companyGroupService, companyService, userGroupCodeUserService, multiLanguageService, objectMapper,
            syncLogger);
    }
}
