package org.finra.rmcs.config;

import java.util.Properties;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.fidelius.FideliusClient;
import org.finra.rmcs.constants.Constants;
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
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {"org.finra.rmcs.repo"},
    entityManagerFactoryRef = "rmcsEntityManagerFactory",
    transactionManagerRef = "rmcsTransactionManager")
@EntityScan("org.finra.rmcs")
@ComponentScan(basePackages = "org.finra")
public class JpaConfig {
  private final String url;

  private final String user;

  private final String key;

  private final String hibernateBatchSize;

  public JpaConfig(
      @Value("${spring.datasource.url}") String url,
      @Value("${spring.datasource.username}") String user,
      @Value("${spring.datasource.password}") String key,
      @Value("${hibernate.jdbc.batch_size:1000}") String hibernateBatchSize) {
    this.url = url;
    this.user = user;
    this.key = key;
    this.hibernateBatchSize = hibernateBatchSize;
  }

  @Configuration
  public class DataSourceConfiguration {

    @Bean
    @SneakyThrows
    public DataSource dataSource() {
      FideliusClient fideliusClient = new FideliusClient();
      String credPassword =
          fideliusClient.getCredential(
              key, Constants.APPLICATION_NAME, System.getenv(Constants.ACTIVE_PROFILE), "", null);
      if (StringUtils.isBlank(credPassword)) {
        log.info("Failed to retrieve password of {} from Fidelius", key);
      } else {
        log.info("Successfully retrieved password of {} from Fidelius", key);
      }
      Class.forName(Constants.POSTGRESQL_DRIVER);
      DriverManagerDataSource dataSource = new DriverManagerDataSource(url, user, credPassword);
      dataSource.setDriverClassName(Constants.POSTGRESQL_DRIVER);
      return dataSource;
    }
  }

  @Configuration
  public class EntityManagerFactoryConfiguration {

    private final DataSource dataSource;

    public EntityManagerFactoryConfiguration(DataSource dataSource) {
      this.dataSource = dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean rmcsEntityManagerFactory() {
      LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
      em.setDataSource(dataSource);
      em.setPackagesToScan(Constants.ENTITY_PACKAGE);
      em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
      em.setJpaProperties(additionalProperties());
      return em;
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

  @Configuration
  @EnableTransactionManagement
  public class TransactionManagerConfiguration implements TransactionManagementConfigurer {
    private final EntityManagerFactory entityManagerFactory;

    public TransactionManagerConfiguration(
        @Qualifier("rmcsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
      this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
      return rmcsTransactionManager();
    }

    @Bean
    public PlatformTransactionManager rmcsTransactionManager() {
      JpaTransactionManager transactionManager = new JpaTransactionManager();
      transactionManager.setEntityManagerFactory(entityManagerFactory);
      return transactionManager;
    }
  }
}
