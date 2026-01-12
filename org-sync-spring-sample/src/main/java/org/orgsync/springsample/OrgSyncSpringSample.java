package org.orgsync.springsample;

import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.lock.LockManager;
import org.orgsync.spring.config.OrgSyncConfiguration;
import org.orgsync.spring.lock.InMemoryLockManager;
import org.orgsync.springsample.store.jpa.OrgSyncJpaStoreConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;

public class OrgSyncSpringSample {

    public static void main(String[] args) {
        try (GenericApplicationContext context = new AnnotationConfigApplicationContext(SampleConfig.class, OrgSyncConfiguration.class)) {
            SyncEngine engine = context.getBean(SyncEngine.class);
            engine.synchronizeCompany("spring-sample", 0L);
        }
    }

    @Configuration
    @EnableTransactionManagement
    @EnableJpaRepositories(basePackages = "org.orgsync.springsample.store.jpa.repository")
    @Import(OrgSyncJpaStoreConfiguration.class)
    static class SampleConfig {

        @Bean
        public OrgChartClient orgChartClient() {
            return new SampleOrgChartClient();
        }

        @Bean
        public LockManager lockManager() {
            return new InMemoryLockManager();
        }

        @Bean
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUrl("jdbc:h2:mem:orgsync-spring;DB_CLOSE_DELAY=-1");
            dataSource.setUsername("sa");
            dataSource.setPassword("");
            return dataSource;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
            LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
            factoryBean.setDataSource(dataSource);
            factoryBean.setPackagesToScan("org.orgsync.springsample.store.jpa.entity");
            HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            factoryBean.setJpaVendorAdapter(vendorAdapter);
            Map<String, Object> properties = new HashMap<>();
            properties.put("hibernate.hbm2ddl.auto", "update");
            factoryBean.setJpaPropertyMap(properties);
            return factoryBean;
        }

        @Bean
        public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
