package org.finra.rmcs.config;

import java.util.Properties;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.util.Util;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"org.finra.rmcs.ebillrepo","org.finra.rmcs.entity"},
        entityManagerFactoryRef = "ebillEntityManagerFactory",
        transactionManagerRef = "eBillConnectTransactionManager")
@EntityScan("org.finra.rmcs.entity")
@ComponentScan(basePackages = "org.finra.rmcs")
public class EbillJpaConfig {

    private final String url;

    private final String user;

    private final String key;

    private final String hibernateBatchSize;

    public EbillJpaConfig(
            @Value("${spring.datasource.ebill.url}") String url,
            @Value("${spring.datasource.ebill.username}") String user,
            @Value("${spring.datasource.ebill.password}") String key,
            @Value("${hibernate.jdbc.batch_size:1000}") String hibernateBatchSize) {
        this.url = url;
        this.user = user;
        this.key = key;
        this.hibernateBatchSize = hibernateBatchSize;
    }

    @Configuration
    public class DataSourceConfiguration {

        @Bean(name = "ebillDataSource")
        @SneakyThrows
        public DataSource dataSource() {
            String credPassword = Util.getPassword(key, "connect");
            Class.forName("org.postgresql.Driver");
            DriverManagerDataSource dataSource = new DriverManagerDataSource(url, user,
                    credPassword);
            dataSource.setDriverClassName("org.postgresql.Driver");
            return dataSource;
        }
    }

    @Configuration
    public class EntityManagerFactoryConfiguration {

        private final DataSource dataSource;

        public EntityManagerFactoryConfiguration(@Qualifier("ebillDataSource") DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean(name = "ebillEntityManagerFactory")
        public LocalContainerEntityManagerFactoryBean connectEntityManagerFactory() {
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(dataSource);
            em.setPackagesToScan("org.finra.rmcs");
            em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            em.setJpaProperties(additionalProperties());
            return em;
        }

        @Bean(name = "eBillConnectTransactionManager")
        public PlatformTransactionManager rmcsTransactionManager(
                @Qualifier("ebillEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(entityManagerFactory);
            return transactionManager;
        }

        final Properties additionalProperties() {
            final Properties hibernateProperties = new Properties();
            hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            hibernateProperties.put("hibernate.cache.use_second_level_cache", Constants.FALSE);
            hibernateProperties.put("hibernate.show_sql", Constants.FALSE);
            hibernateProperties.put("hibernate.format_sql", Constants.FALSE);
            hibernateProperties.put("hibernate.jdbc.batch_size", hibernateBatchSize);
            hibernateProperties.put("hibernate.order_inserts", Constants.TRUE);
            hibernateProperties.put("hibernate.enable_lazy_load_no_trans", Constants.TRUE);
            hibernateProperties.put("hibernate.jdbc.lob.non_contextual_creation", Constants.TRUE);
            return hibernateProperties;
        }
    }
}

