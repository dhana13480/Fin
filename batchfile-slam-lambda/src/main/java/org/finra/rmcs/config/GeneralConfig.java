package org.finra.rmcs.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

@Slf4j
@Configuration
@ComponentScan("org.finra.rmcs")
public class GeneralConfig {

  @Bean(name = "snsClient")
  public SnsClient getSNSClient() {
    return SnsClient.builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build();
  }
}
