package org.orgsync.boot;

import org.orgsync.boot.config.CompanyLockProperties;
import org.orgsync.core.lock.LockManager;
import org.orgsync.spring.config.OrgSyncConfiguration;
import org.orgsync.spring.lock.InMemoryLockManager;
import org.orgsync.spring.lock.JdbcLockManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.client.RestClient;
import org.orgsync.core.client.OrgChartClient;
import org.orgsync.spring.client.OrgChartRestClient;
import org.orgsync.spring.client.OrgSyncRestClientProperties;

import javax.sql.DataSource;

/**
 * Spring Boot auto-configuration that exposes the sync engine and defaults.
 */
@AutoConfiguration
@Import(OrgSyncConfiguration.class)
@EnableConfigurationProperties(CompanyLockProperties.class)
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
    @ConditionalOnMissingBean(LockManager.class)
    @ConditionalOnBean({DataSource.class, PlatformTransactionManager.class})
    public LockManager lockManager(DataSource dataSource,
                                   PlatformTransactionManager transactionManager,
                                   CompanyLockProperties lockProperties) {
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        return new JdbcLockManager(new NamedParameterJdbcTemplate(dataSource), txTemplate,
                lockProperties.getTable(), lockProperties.getUuidColumn());
    }

    @Bean
    @ConditionalOnMissingBean(LockManager.class)
    public LockManager inMemoryLockManager() {
        return new InMemoryLockManager();
    }
}
