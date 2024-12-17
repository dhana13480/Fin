package org.finra.rmcs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@Slf4j
public class OAuth2ClientConfig {

  private final String grantType;
  private final String user;
  private final String accessTokenUrl;
  private final String scope;
  private final Map<String, String> apiUserBean;

  public OAuth2ClientConfig(@Value("${spring.oauth2.client.grantType}") String grantType,
      @Value("${spring.api.username}") String user,
      @Value("${spring.oauth2.client.accessTokenUrl}") String accessTokenUrl,
      @Value("${spring.oauth2.client.scope}") String scope, Map<String, String> apiUserBean
  ) {
    this.grantType = grantType;
    this.user = user;
    this.accessTokenUrl = accessTokenUrl;
    this.scope = scope;
    this.apiUserBean = apiUserBean;
  }

  @Bean(name = "issoRestTemplate")
  public OAuth2RestTemplate getRestTemplate() {
    return new OAuth2RestTemplate(oAuth2Client());
  }

  @Bean
  public ClientCredentialsResourceDetails oAuth2Client() {
    ClientCredentialsResourceDetails clientCredentialsResourceDetails =
        new ClientCredentialsResourceDetails();
    clientCredentialsResourceDetails.setGrantType(grantType);
    clientCredentialsResourceDetails.setClientId(user);
    clientCredentialsResourceDetails.setClientSecret(apiUserBean.get(user));
    clientCredentialsResourceDetails.setAccessTokenUri(accessTokenUrl);
    clientCredentialsResourceDetails.setScope(Collections.singletonList(scope));

    return clientCredentialsResourceDetails;
  }

  @Bean(name = "restTemplate")
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(new ObjectMapper());
    restTemplate.getMessageConverters().add(converter);
    return restTemplate;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable());
    return http.build();
  }
}
