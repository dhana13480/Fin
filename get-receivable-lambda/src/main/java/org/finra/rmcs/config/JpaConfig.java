package org.finra.rmcs.config;

import java.util.Properties;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {Constants.ORG_FINRA_RMCS_REPO},
    entityManagerFactoryRef = Constants.RMCS_ENTITY_MANAGER_FACTORY,
    transactionManagerRef = Constants.RMCS_TRANSACTION_MANAGER)
@EntityScan(Constants.ORG_FINRA_RMCS)
@ComponentScan(basePackages = Constants.ORG_FINRA)
public class JpaConfig {

  private final String url;

  private final String user;

  private final String key;

  private final String hibernateBatchSize;

  public JpaConfig(@Value("${spring.datasource.url}") String url,
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
      String credPassword = fideliusClient.getCredential(key, Constants.RMCS,
          System.getenv(Constants.SPRING_PROFILES_ACTIVE), "", null);
      if (StringUtils.isBlank(credPassword)) {
        log.info("Failed to retrieve password of {} from Fidelius", key);
      } else {
        log.info("Successfully retrieved password of {} from Fidelius", key);
      }
      Class.forName(Constants.ORG_POSTGRESQL_DRIVER);
      DriverManagerDataSource dataSource = new DriverManagerDataSource(url, user, credPassword);
      dataSource.setDriverClassName(Constants.ORG_POSTGRESQL_DRIVER);
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
      em.setPackagesToScan(Constants.ORG_FINRA_RMCS_ENTITY);
      em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
      em.setJpaProperties(additionalProperties());
      return em;
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

  @Configuration
  @EnableTransactionManagement
  public class TransactionManagerConfiguration implements TransactionManagementConfigurer {

    private final EntityManagerFactory entityManagerFactory;

    public TransactionManagerConfiguration(
        @Qualifier(Constants.RMCS_ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
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
