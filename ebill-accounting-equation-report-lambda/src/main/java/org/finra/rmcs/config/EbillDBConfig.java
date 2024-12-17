package org.finra.rmcs.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.fidelius.FideliusClient;
import org.finra.rmcs.constants.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Slf4j
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {Constants.ORG_FINRA_RMCS_REPO},
        entityManagerFactoryRef = Constants.RMCS_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = Constants.RMCS_TRANSACTION_MANAGER)
@EntityScan(Constants.ORG_FINRA_RMCS)
public class EbillDBConfig {

    @Value("${spring.datasource.password}")
    private String fideliusKey;

    @Value("${hibernate.jdbc.batch_size:1000}")
    private String hibernateBatchSize;
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSourceProperties dataSourceProperties(){
        log.info("Setting dataSourceProperties");
        DataSourceProperties properties = new DataSourceProperties();
        properties.setPassword(getPasswordFromFidelius());
        return properties;
    }

    @Bean
    @Primary
    public DataSource dataSource(){
        log.info("Setting dataSource");
        return dataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @SneakyThrows
    private String getPasswordFromFidelius() {
        FideliusClient fideliusClient = new FideliusClient();
        String credPassword = fideliusClient.getCredential(fideliusKey, Constants.RMCS,
                System.getenv(Constants.SPRING_PROFILES_ACTIVE), "EBILL", null);

        if (StringUtils.isBlank(credPassword)) {
            log.info("Failed to retrieve password of {} from Fidelius", fideliusKey);
        } else {
            log.info("Successfully retrieved password of {} from Fidelius", fideliusKey);
        }
        return credPassword;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean rmcsEntityManagerFactory(DataSource dataSource) {
        log.info("Setting rmcsEntityManagerFactory");
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPersistenceUnitName("ebillPersistenceUnit");
        em.setPackagesToScan(Constants.ORG_FINRA_RMCS_ENTITY);
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean
    public PlatformTransactionManager rmcsTransactionManager(EntityManagerFactory rmcsEntityManagerFactory) {
        log.info("Setting rmcsTransactionManager");
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(rmcsEntityManagerFactory);
        return transactionManager;
    }

    final Properties additionalProperties() {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.put(Constants.HIBERNATE_DIALECT,
                Constants.ORG_HIBERNATE_DIALECT_POSTGRESQL_DIALECT);
        hibernateProperties.put(Constants.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE, Constants.FALSE);
        hibernateProperties.put(Constants.HIBERNATE_SHOW_SQL, Constants.FALSE);
        hibernateProperties.put(Constants.HIBERNATE_FORMAT_SQL, Constants.FALSE);
        hibernateProperties.put(Constants.HIBERNATE_JDBC_BATCH_SIZE, hibernateBatchSize);
        hibernateProperties.put(Constants.HIBERNATE_ORDER_INSERTS, Constants.TRUE);
        hibernateProperties.put(Constants.HIBERNATE_ENABLE_LAZY_LOAD_NO_TRANS, Constants.TRUE);
        hibernateProperties.put(Constants.HIBERNATE_JDBC_LOB_NON_CONTEXTUAL_CREATION, Constants.TRUE);
        return hibernateProperties;
    }


}
