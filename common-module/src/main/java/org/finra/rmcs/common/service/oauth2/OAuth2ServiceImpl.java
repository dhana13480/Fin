package org.finra.rmcs.common.service.oauth2;

import java.net.URI;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.finra.fidelius.FideliusClient;
import org.finra.rmcs.common.constants.Constants;
import org.finra.rmcs.common.dto.OAuth2Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class OAuth2ServiceImpl implements OAuth2Service {

  private final String user;
  private final String svcPasswordKey;
  private final String accessTokenUrl;
  private final String grantType;
  private final RestTemplate restTemplate = new RestTemplate();

  @Autowired
  public OAuth2ServiceImpl(
      @Value("${spring.api.username}") String user,
      @Value("${spring.api.passwordKey}") String svcPasswordKey,
      @Value("${spring.oauth2.client.accessTokenUrl}") String accessTokenUrl,
      @Value("${spring.oauth2.client.grantType}") String grantType) {
    this.grantType = grantType;
    this.user = user;
    this.svcPasswordKey = svcPasswordKey;
    this.accessTokenUrl = accessTokenUrl;
  }

  @Override
  @SneakyThrows
  public String getAccessToken() {
    String password = getPassword(svcPasswordKey);
    URI uri = new URIBuilder(accessTokenUrl).addParameter("grant_type", grantType).build();
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(user, password);
    HttpEntity<String> httpEntity = new HttpEntity<>(headers);
    ResponseEntity<OAuth2Token> tokenResponse =
        restTemplate.exchange(uri, HttpMethod.POST, httpEntity, OAuth2Token.class);
    OAuth2Token oAuth2Token = tokenResponse.getBody();
    if (oAuth2Token == null) {
      throw new RuntimeException(
          String.format("Failed to get OAuth2 token from FIP %s", accessTokenUrl));
    }
    return oAuth2Token.getAccessToken();
  }

  @SneakyThrows
  private String getPassword(String passwordKey) {
    FideliusClient fideliusClient = new FideliusClient();
    String credPassword =
        fideliusClient.getCredential(
            passwordKey,
            Constants.RMCS,
            System.getenv(Constants.SPRING_PROFILES_ACTIVE),
            null,
            null);
    if (StringUtils.isBlank(credPassword)) {
      log.info("Failed to retrieve password of {} from Fidelius", passwordKey);
    } else {
      log.info("Successfully retrieved password of {} from Fidelius", passwordKey);
    }
    return credPassword;
  }
}
