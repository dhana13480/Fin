package org.finra.rmcs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.fidelius.FideliusClient;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.service.EwsUserApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
@ComponentScan(basePackages = Constants.ORG_FINRA)
public class EbillModeledScheduleWrapperConfig {

  private final String svcUser;
  private final String svcPasswordKey;

  public EbillModeledScheduleWrapperConfig(@Value("${spring.api.username}") String svcUser,
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


  // For external api call
  @Bean
  public RestTemplate restTemplate() {
    createCustomTrustStore();
    RestTemplate restTemplate = new RestTemplate();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(new ObjectMapper());
    restTemplate.getMessageConverters().add(converter);
    return restTemplate;
  }

  @SneakyThrows
  public static void createCustomTrustStore() {
    // locate the default truststore
    log.info("createCustomTrustStore");
    String filename =
        System.getProperty("java.home") + "/lib/security/cacerts".replace('/', File.separatorChar);
    log.info("filename:{}", filename);
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

    try (FileInputStream fis = new FileInputStream(filename)) {
      keyStore.load(fis, Constants.CERT_PW.toCharArray());
    }

    URL res = EwsUserApiService.class.getClassLoader().getResource("FINRACorpRootCA_cert.crt");
    // add Certificate to Key store
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    Certificate caCert = cf.generateCertificate(new FileInputStream(res.toURI().getPath()));
    keyStore.setCertificateEntry("ca-cert", caCert);

    // can only save to /tmp from a lambda
    String certPath =
        System.getProperty("java.io.tmpdir") + File.separatorChar + "CustomTruststore";
    log.info("certPath: {}", certPath);
    // write Key Store
    try (FileOutputStream out = new FileOutputStream(certPath)) {
      keyStore.store(out, Constants.CERT_PW.toCharArray());
    }

    // Set Certificates to System properties
    System.setProperty("javax.net.ssl.trustStore", certPath);
    System.setProperty("javax.net.ssl.trustStorePassword", Constants.CERT_PW);
  }
}
