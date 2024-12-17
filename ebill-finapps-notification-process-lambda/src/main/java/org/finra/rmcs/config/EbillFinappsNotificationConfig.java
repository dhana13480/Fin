package org.finra.rmcs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.fidelius.FideliusClient;
import org.finra.rmcs.constants.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@ComponentScan(basePackages = Constants.ORG_FINRA)
public class EbillFinappsNotificationConfig {

  private final String svcUser;
  private final String svcPasswordKey;

  public EbillFinappsNotificationConfig(@Value("${spring.api.username}") String svcUser,
      @Value("${spring.api.passwordKey}") String svcPasswordKey) {
    this.svcUser = svcUser;
    this.svcPasswordKey = svcPasswordKey;
  }

  @Bean(name = "apiUserBean")
  public Map<String, String> apiUserBean() {
    Map<String, String> apiUserBean = new HashMap<>();
    apiUserBean.put(svcUser, getPassword(svcPasswordKey));
    return apiUserBean;
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();

    // `java.time.LocalDateTime` handling
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  @SneakyThrows
  private String getPassword(String passwordKey) {
    FideliusClient fideliusClient = new FideliusClient();
    String credPassword = fideliusClient.getCredential(passwordKey, Constants.AGS,
        System.getenv(Constants.SPRING_PROFILES_ACTIVE), Constants.NASDCORP, null);
    if (StringUtils.isBlank(credPassword)) {
      log.info("Failed to retrieve password of {} from Fidelius", passwordKey);
    } else {
      log.info("Successfully retrieved password of {} from Fidelius", passwordKey);
    }
    return credPassword;
  }
}
