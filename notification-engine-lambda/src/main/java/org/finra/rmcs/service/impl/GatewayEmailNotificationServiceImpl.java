package org.finra.rmcs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.fidelius.FideliusClient;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.DxtnTokenResponse;
import org.finra.rmcs.dto.GatewayEmailNotificationRequest;
import org.finra.rmcs.dto.GatewayEmailNotificationResponse;
import org.finra.rmcs.dto.Notifications;
import org.finra.rmcs.service.GatewayEmailNotificationService;
import org.finra.rmcs.utils.GatewayEmailNotificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.finra.rmcs.entity.EmailConfig;

@Service
@Slf4j
@RequiredArgsConstructor
public class GatewayEmailNotificationServiceImpl implements GatewayEmailNotificationService {

    @Autowired
    private RetryTemplate retryTemplate;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Value("${dxtn.fcn.url}")
    private String dxtnFcnUrl;

    @Value("${dxtn.token.url}")
    private String dxtnApiUrl;

    @Value("${dxtn.fcn.username}")
    private String dxtnUser;

    @Value("${spring.oauth2.client.grantType}")
    private String grantType;

    @Value("${app.internal.payment.notification.email}")
    private String nonProdEmail;

    @Override
    public GatewayEmailNotificationResponse[] sendGatewayEmailNotification(
            GatewayEmailNotificationRequest request,EmailConfig emailConfig) {

        log.info("Start send gateway email notification with request {}", request);
        try{
            HttpHeaders headers = new HttpHeaders();
            Notifications[] notificationsRequest = GatewayEmailNotificationUtil.generateNotificationsRequest(request,emailConfig,nonProdEmail );
            String bearerToken = getGatewayNotificationToken(dxtnApiUrl+ Constants.GRANT_TYPE +grantType,dxtnUser);
            headers.add(HttpHeaders.CONTENT_TYPE, Constants.ENCODING_JSON_VALUE);
            headers.add(HttpHeaders.AUTHORIZATION, Constants.BEARER + bearerToken);
            String uri = UriComponentsBuilder.fromUriString(dxtnFcnUrl).build().toUriString();
            log.info("Notification Request Json Payload : {}",objectMapper.writeValueAsString(notificationsRequest));
            HttpEntity<Notifications[]> httpEntity = new HttpEntity<>(notificationsRequest, headers);

            return retryTemplate.execute(ctx -> {
                ResponseEntity<GatewayEmailNotificationResponse[]> gatewayEmailNotificationResponse =
                        restTemplate.exchange(uri, HttpMethod.POST, httpEntity, GatewayEmailNotificationResponse[].class);
                log.info("Gateway email notification sent {}",gatewayEmailNotificationResponse.getBody());
                return gatewayEmailNotificationResponse.getBody();
            });

        }catch (Exception ex){
            log.error("Exception while sending email through gateway notification:{}", ex);
            return null;
        }
    }

    public String getGatewayNotificationToken(String dxtnApiUrl , String dxtnUser)  {
        log.info("get dxtn api access token  , URL :{}",dxtnApiUrl);
        DxtnTokenResponse response = null;
        String tokenBody = "";
        String password = "";

        try {
            FideliusClient fideliusClient = new FideliusClient();
            password =  fideliusClient.getCredential(dxtnUser, Constants.RMCS,
                    System.getenv(Constants.SPRING_PROFILES_ACTIVE), "", null);
            HttpHeaders headers = new HttpHeaders();

            headers.setBasicAuth(dxtnUser, password);
            HttpEntity<String> httpEntity = new HttpEntity<>(tokenBody, headers);

            ResponseEntity<DxtnTokenResponse> tokenResponse =
                    restTemplate.exchange(dxtnApiUrl, HttpMethod.POST, httpEntity, DxtnTokenResponse.class);
            log.info("tokenResponseL :{}",tokenResponse);
            if (tokenResponse.getStatusCode() == HttpStatus.OK) {
                log.info("received token response");
                if (null != tokenResponse.getBody()) {
                    response = tokenResponse.getBody();
                }
            } else {
                log.error("Rest Call for {} is returning : {}", dxtnApiUrl, tokenResponse.getStatusCode());
                throw new InvalidTokenException("Rest Call for: "+dxtnApiUrl+" is returning : "+tokenResponse.getStatusCode() );
            }
        } catch (Exception e) {
            log.error("Error while generating Ews API Token{}" ,e);
        }
        log.info("return the notification token");
        return response!= null ? response.getAccessToken() : null;
    }
}
