package org.finra.rmcs.service.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.dto.BusinessObjectData;
import org.finra.rmcs.dto.DmNotification;
import org.finra.rmcs.service.DmFileService;
import org.finra.rmcs.utils.Util;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class DmFileServiceImpl implements DmFileService {

  private RestTemplate restTemplate;
  OAuth2RestTemplate oAuth2RestTemplate;
  private String dmEndPoint;
  private String dmFileNameSpace;

  public DmFileServiceImpl(@Value("${dm.endpoint}") String dmEndPoint,
      @Value("${dm.file.namespace}") String dmFileNameSpace, @NonNull @Qualifier("issoRestTemplate") OAuth2RestTemplate oAuth2RestTemplate, @NonNull @Qualifier("restTemplate")RestTemplate restTemplate){
    this.restTemplate = restTemplate;
    this.oAuth2RestTemplate = oAuth2RestTemplate;
    this.dmEndPoint = dmEndPoint;
    this.dmFileNameSpace = dmFileNameSpace;
  }

  @Override
  public BusinessObjectData getDmFileInformation(String correlationId, DmNotification notificationMessage) {
    log.info("start getDMFileInformation with correlationId:{}", correlationId);
    try{
      String url = Util.constructDmUrl(this.dmEndPoint, this.dmFileNameSpace, notificationMessage);
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(oAuth2RestTemplate.getAccessToken().toString());
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<String> httpEntity =
          new HttpEntity<String>(headers);
      ResponseEntity<BusinessObjectData> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, BusinessObjectData.class);

      log.info("successfully retrieved the file information for correlationId:{}", correlationId);
      return response.getBody();
    } catch(Exception ex){
      log.error("failed to retrieve the file information from DM for correlationId:{} message:{}, exception:{}", correlationId, notificationMessage, ex);
      return null;
    }
  }


}
