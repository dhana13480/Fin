package org.finra.rmcs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import org.finra.rmcs.dto.EmailResponse;
import org.finra.rmcs.dto.GatewayEmailNotificationRequest;
import org.finra.rmcs.dto.GatewayEmailNotificationResponse;
import org.finra.rmcs.entity.EmailTracking;
import org.finra.rmcs.entity.PaymentEmailTracking;
import org.finra.rmcs.model.NotificationMessage;
import org.finra.rmcs.repo.EmailTrackingRepo;
import org.finra.rmcs.repo.PaymentEmailTrackingRepo;
import org.finra.rmcs.service.impl.EmailTrackingServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class EmailTrackingServiceImplTest {

  @InjectMocks
  EmailTrackingServiceImpl emailTrackingService;
  @Mock
  EmailTrackingRepo emailTrackingRepo;
  @Mock
  PaymentEmailTrackingRepo paymentEmailTrackingRepo;
  @Mock
  ObjectMapper objectMapper;

  NotificationMessage notificationMessage;
  GatewayEmailNotificationRequest request;
  EmailResponse response;

  @BeforeEach
  void setUp() {
    notificationMessage = new NotificationMessage();
    notificationMessage.setModule("AFFILIATE_FIRM_TRANSFER");
    notificationMessage.setStatus("SUBMITTED");
    notificationMessage.setPaymentNumber(new ArrayList<>(Arrays.asList("EBILL12345678")));
    notificationMessage.setEwsUser("Test");
    request = new GatewayEmailNotificationRequest();
    request.setTo(new ArrayList<>(Arrays.asList("test@finra.org")));
    request.setSubject("Test Subject");
    request.setBody("Test Body");
    GatewayEmailNotificationResponse gatewayEmailNotificationResponse = new GatewayEmailNotificationResponse();
    gatewayEmailNotificationResponse.setId("1233-123-1424-24241");
    response = new EmailResponse();
    response.setGatewayEmailNotificationResponseList(
        new GatewayEmailNotificationResponse[]{gatewayEmailNotificationResponse});
  }

  @Test
  void saveEmailTrackingDetailsTest() {
    EmailTracking emailTracking = new EmailTracking();
    emailTracking.setId(UUID.randomUUID());
    PaymentEmailTracking paymentEmailTracking = new PaymentEmailTracking();
    paymentEmailTracking.setId(UUID.randomUUID());
    Mockito.when(emailTrackingRepo.save(Mockito.any())).thenReturn(emailTracking);
    Mockito.when(paymentEmailTrackingRepo.saveAll(Mockito.any()))
        .thenReturn(new ArrayList<>(Arrays.asList(paymentEmailTracking)));
    emailTrackingService.saveEmailTrackingDetails(notificationMessage, request, response);
    Mockito.verify(emailTrackingRepo).save(Mockito.any());
  }

  @Test
  void saveEmailTrackingDetailsExceptionTest() {
    EmailTracking emailTracking = new EmailTracking();
    emailTracking.setId(UUID.randomUUID());
    PaymentEmailTracking paymentEmailTracking = new PaymentEmailTracking();
    paymentEmailTracking.setId(UUID.randomUUID());
    Mockito.when(emailTrackingRepo.save(Mockito.any())).thenReturn(emailTracking);
    Mockito.when(paymentEmailTrackingRepo.saveAll(Mockito.any())).thenThrow(new RuntimeException());
    Assertions.assertThrows(RuntimeException.class, () -> {
      emailTrackingService.saveEmailTrackingDetails(notificationMessage, request, response);
    });
  }
}
