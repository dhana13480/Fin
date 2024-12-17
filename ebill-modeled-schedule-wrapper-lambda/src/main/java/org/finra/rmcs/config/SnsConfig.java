package org.finra.rmcs.config;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.finra.rmcs.constants.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = Constants.ORG_FINRA)
public class SnsConfig {
  @Bean
  public AmazonSNS snsClient() {
    return AmazonSNSClientBuilder.defaultClient();
  }
}
