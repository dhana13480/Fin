package org.finra.rmcs.service.impl;

import com.amazonaws.regions.Regions;
import lombok.extern.slf4j.Slf4j;
import org.finra.fidelius.FideliusClient;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.EwsTokenResponse;
import org.finra.rmcs.exception.EwsTokenException;
import org.finra.rmcs.exception.TokenException;
import org.finra.rmcs.service.EwsTokenservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EwsTokenServiceImpl implements EwsTokenservice {
  @Value("${client_id}")
  String clientId;

  @Value("${client_secret}")
  String clientSecret;

  @Value("${ews_token_url}")
  String ewsTokenUrl;

  @Value("${ews_api_user}")
  String ewsApiUser;

  @Autowired
  RestTemplate restTemplate;

  private FideliusClient fideliusClient = new FideliusClient(Regions.US_EAST_1.getName());

  @Override
  public String getEwsToken() throws TokenException {
    EwsTokenResponse response = null;
    try {
      String ewsApiPassword = fideliusClient.getCredential(ewsApiUser, Constants.RMCS,
          System.getenv(Constants.SPRING_PROFILES_ACTIVE), "", null);
      HttpHeaders headers = new HttpHeaders();
      headers.setBasicAuth(ewsApiUser, ewsApiPassword);
      HttpEntity<String> httpEntity = new HttpEntity<>("", headers);

      ResponseEntity<EwsTokenResponse> tokenResponse =
          restTemplate.exchange(ewsTokenUrl, HttpMethod.POST, httpEntity, EwsTokenResponse.class);

      if (tokenResponse.getStatusCode() != HttpStatus.OK || null == tokenResponse.getBody()) {
        log.error("Rest Call for {} is returning : {}", ewsTokenUrl, tokenResponse.getStatusCode());
        throw new EwsTokenException(
            "Rest Call for: " + ewsTokenUrl + " is returning : " + tokenResponse.getStatusCode());
      }
      response = tokenResponse.getBody();
    } catch (Exception e) {
      log.error("Error while generating Ews API Token{}", e.getMessage());
      throw new TokenException("Error while generating Ews API Token", e);
    }
    return response != null ? response.getAccessToken() : null;
  }

}
