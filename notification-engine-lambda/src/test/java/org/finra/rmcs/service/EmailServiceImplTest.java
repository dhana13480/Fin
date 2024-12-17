package org.finra.rmcs.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finra.rmcs.constants.ModuleTypeEnum;
import org.finra.rmcs.dto.EwsAccountInformationResponse;
import org.finra.rmcs.dto.GatewayEmailNotificationRequest;
import org.finra.rmcs.dto.GatewayEmailNotificationResponse;
import org.finra.rmcs.dto.*;
import org.finra.rmcs.entity.AlertEmailEventEntity;
import org.finra.rmcs.entity.EmailConfig;
import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.finra.rmcs.entity.SubscriptionEntity;
import org.finra.rmcs.model.NotificationMessage;
import org.finra.rmcs.repo.*;
import org.finra.rmcs.service.impl.EmailServiceImpl;
import org.finra.rmcs.service.impl.GatewayEmailNotificationServiceImpl;
import org.finra.rmcs.utils.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;
    @Mock
    private EmailConfigRepo emailConfigRepo;
    @Mock
    private PaymentTrackingRepo paymentTrackingRepo;
    @Mock
    private AlertEmailEventRepo alertEmailEventRepo;
    @Mock
    private RevenueStreamAppConfigRepo revenueStreamAppConfigRepo;
    @Mock
    private GatewayEmailNotificationServiceImpl gatewayEmailNotificationService;
    @Mock
    private Util utils;
    @Mock
    private EwsSerrvice ewsSerrvice;

    @Mock
    private OrganizationService orgService;

    @Mock
    private EmailTrackingService emailTrackingService;

    @Mock
    private SubscriptionRepo subscriptionRepo;
    @Spy
    ObjectMapper objectMapper = new ObjectMapper();
    NotificationMessage notificationMessage;
    GatewayEmailNotificationRequest request;
    EmailResponse response;
    EmailConfig emailConfig;

    @BeforeEach
    void setUp(){
        notificationMessage = new NotificationMessage();
        notificationMessage.setPaymentNumber(new ArrayList<>(Arrays.asList("EBILL12345678")));
        notificationMessage.setEwsUser("Test");
        request = new GatewayEmailNotificationRequest();
        request.setTo(new ArrayList<>(Arrays.asList("test@finra.org")));
        request.setSubject("Test Subject");
        request.setBody("Test Body");
        GatewayEmailNotificationResponse gatewayEmailNotificationResponse = new GatewayEmailNotificationResponse();
        response = new EmailResponse();
        response.setGatewayEmailNotificationResponseList(new GatewayEmailNotificationResponse[]{gatewayEmailNotificationResponse});
    }
    @Test
    public void sendEmailNotificationAFT_test() throws Exception{
        String invoiceTypeDescription = "TRACE";
        ExtraInfo extraInfo = new ExtraInfo();
        notificationMessage.setModule("AFFILIATE_FIRM_TRANSFER");
        notificationMessage.setStatus("SUBMITTED");
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setSubject("test");
        emailConfig.setBody("test");
        emailConfig.setEventTypeName("AFFILIATED_FIRM_TRANSFER_Submitted");
        List<AlertEmailEventEntity> alertEmailEventEntity1 = new ArrayList<>();
        AlertEmailEventEntity alertEmailEventEntity = new AlertEmailEventEntity();
        alertEmailEventEntity.setId(UUID.fromString("319dea09-7ef1-460e-9d6f-0975a283c676"));
        alertEmailEventEntity.setSendTS(LocalDate.now());
        alertEmailEventEntity.setStatus(2);
        alertEmailEventEntity.setExtraInfo("{\"org_name\":\"JP Morgan Chase\"}");
        alertEmailEventEntity1.add(alertEmailEventEntity);
        PaymentTrackingEntity paymentTrackingEntity = new PaymentTrackingEntity();
        paymentTrackingEntity.setPaymentReferenceNumber("1234");
        ExtraInfo extraInfoNew = objectMapper.readValue(alertEmailEventEntity1.get(0).getExtraInfo(), ExtraInfo.class);
        extraInfoNew.setOrgName("JP Morgan Chase");
        when(emailConfigRepo.findByEventTypeName(any())).thenReturn(emailConfig);
        when(paymentTrackingRepo.findByPaymentReferenceNumberIn(any())).thenReturn(List.of(paymentTrackingEntity));
        when(alertEmailEventRepo.findAllById(any())).thenReturn(List.of(alertEmailEventEntity));
        when(revenueStreamAppConfigRepo.findbyRevenueStreamNameDesc(any())).thenReturn(invoiceTypeDescription);
        Map<String, String> orgMap = new HashMap<>();
        orgMap.put("79","JP Morgan");
        when(orgService.getOrganizationDetailsByBUAndPaymentNumber(any(),any())).thenReturn(orgMap);
        doNothing().when(emailTrackingService).saveEmailTrackingDetails(any(),any(),any());
        EwsAccountInformationResponse ewsAccountInformationResponse =
            new EwsAccountInformationResponse();
        ewsAccountInformationResponse.setPersonalInfo(PersonalInfo.builder()
            .email("test@g")
            .build());
        ResponseEntity<EwsAccountInformationResponse> response = new ResponseEntity<>(ewsAccountInformationResponse, HttpStatus.OK);
        when(ewsSerrvice.getAccountInformation(notificationMessage.getEwsUser())).thenReturn(response);
        GatewayEmailNotificationResponse[] gatewayEmailNotificationResponseList = new GatewayEmailNotificationResponse[1];
        gatewayEmailNotificationResponseList[0] = new GatewayEmailNotificationResponse();
        when (gatewayEmailNotificationService.sendGatewayEmailNotification(any(),any())).thenReturn(gatewayEmailNotificationResponseList);
        GatewayEmailNotificationRequest Expectedrequest = GatewayEmailNotificationRequest.builder()
                .eventName("AFFILIATED_FIRM_TRANSFER_Submitted")
                .subject("test")
                .body("test<br/>")
                .feedback(null)
                .to(Arrays.asList("test@g"))
                .build();
        GatewayEmailNotificationRequest Actualrequest = emailService.setNotificationRequest(emailConfig, null,Arrays.asList(paymentTrackingEntity)
                ,Arrays.asList(alertEmailEventEntity), null,notificationMessage,new HashMap<>() );

        Assertions.assertEquals(Expectedrequest,Actualrequest);
    }

    @Test
    public void sendEmailNotificationAFT_ERROR() throws Exception{
        String invoiceTypeDescription = "TRACE";
        ExtraInfo extraInfo = new ExtraInfo();
        notificationMessage.setModule("AFFILIATE_FIRM_TRANSFER");
        notificationMessage.setStatus("SUBMITTED");
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setSubject("test");
        emailConfig.setBody("test");
        emailConfig.setEventTypeName("AFFILIATED_FIRM_TRANSFER_Submitted");
        List<AlertEmailEventEntity> alertEmailEventEntity1 = new ArrayList<>();
        AlertEmailEventEntity alertEmailEventEntity = new AlertEmailEventEntity();
        alertEmailEventEntity.setId(UUID.fromString("319dea09-7ef1-460e-9d6f-0975a283c676"));
        alertEmailEventEntity.setSendTS(LocalDate.now());
        alertEmailEventEntity.setStatus(3);
        alertEmailEventEntity.setExtraInfo("{\"org_name\":\"JP Morgan Chase\"}");
        alertEmailEventEntity1.add(alertEmailEventEntity);
        PaymentTrackingEntity paymentTrackingEntity = new PaymentTrackingEntity();
        paymentTrackingEntity.setPaymentReferenceNumber("1234");
        ExtraInfo extraInfoNew = objectMapper.readValue(alertEmailEventEntity1.get(0).getExtraInfo(), ExtraInfo.class);
        extraInfoNew.setOrgName("JP Morgan Chase");
        when(emailConfigRepo.findByEventTypeName(any())).thenReturn(emailConfig);
        when(paymentTrackingRepo.findByPaymentReferenceNumberIn(any())).thenReturn(List.of(paymentTrackingEntity));
        when(alertEmailEventRepo.findAllById(any())).thenReturn(List.of(alertEmailEventEntity));
        when(revenueStreamAppConfigRepo.findbyRevenueStreamNameDesc(any())).thenReturn(invoiceTypeDescription);
        Map<String, String> orgMap = new HashMap<>();
        orgMap.put("79","JP Morgan");
        when(orgService.getOrganizationDetailsByBUAndPaymentNumber(any(),any())).thenReturn(orgMap);
        doNothing().when(emailTrackingService).saveEmailTrackingDetails(any(),any(),any());
        EwsAccountInformationResponse ewsAccountInformationResponse =
                new EwsAccountInformationResponse();
        ewsAccountInformationResponse.setPersonalInfo(PersonalInfo.builder()
                .email("test@g")
                .build());
        ResponseEntity<EwsAccountInformationResponse> response = new ResponseEntity<>(ewsAccountInformationResponse, HttpStatus.OK);
        when(ewsSerrvice.getAccountInformation(notificationMessage.getEwsUser())).thenReturn(response);
        GatewayEmailNotificationResponse[] gatewayEmailNotificationResponseList = new GatewayEmailNotificationResponse[1];
        gatewayEmailNotificationResponseList[0] = new GatewayEmailNotificationResponse();
        when (gatewayEmailNotificationService.sendGatewayEmailNotification(any(),any())).thenReturn(null);
        GatewayEmailNotificationRequest Expectedrequest = GatewayEmailNotificationRequest.builder()
                .eventName("AFFILIATED_FIRM_TRANSFER_Submitted")
                .subject("test")
                .body("test<br/>")
                .feedback(null)
                .to(Arrays.asList("test@g"))
                .build();
        GatewayEmailNotificationRequest Actualrequest = emailService.setNotificationRequest(emailConfig, null,Arrays.asList(paymentTrackingEntity)
                ,alertEmailEventEntity1, null,notificationMessage,new HashMap<>() );

        Assertions.assertEquals(Expectedrequest,Actualrequest);
    }
    @Test
    public void sendEmailNotificationInvoiceAlert_test() throws Exception{
        String invoiceTypeDescription = "TRACE";
        notificationMessage.setStatus("NEW_INVOICE_AVAILABLE");
        notificationMessage.setInvoiceType("CRDRG");
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setSubject("test");
        EmailConfig emailConfigForInvoiceAlerts = new EmailConfig();
        emailConfigForInvoiceAlerts.setEventTypeName("NEW_INVOICE_AVAILABLE");
        emailConfigForInvoiceAlerts.setBody("test");
        emailConfigForInvoiceAlerts.setFeedBack("test");
        emailConfigForInvoiceAlerts.setSubject("test");
        List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>() ;
        List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>() ;
        AlertEmailEventEntity alertEmailEventEntityForInvoiceAlerts1 = new AlertEmailEventEntity();
        alertEmailEventEntityForInvoiceAlerts1.setId(UUID.fromString("319dea09-7ef1-460e-9d6f-0975a283c676"));
        alertEmailEventEntityForInvoiceAlerts1.setExtraInfo("{\"org_name\":\"JP Morgan Chase\"}");
        alertEmailEventEntityForInvoiceAlerts1.setSendTS(LocalDate.now());
        alertEmailEventEntityForInvoiceAlerts1.setInvoiceNumber("1234");
        alertEmailEventEntityForInvoiceAlerts1.setStatus(2);
       alertEmailEventEntityForInvoiceAlerts.add(alertEmailEventEntityForInvoiceAlerts1);
        PaymentTrackingEntity paymentTrackingEntity = new PaymentTrackingEntity();
        paymentTrackingEntity.setPaymentReferenceNumber("1234");
        ExtraInfo extraInfoNew = objectMapper.readValue(alertEmailEventEntityForInvoiceAlerts.get(0).getExtraInfo(), ExtraInfo.class);
        extraInfoNew.setOrgName("JP Morgan Chase");
        when(emailConfigRepo.findByEventTypeName(any())).thenReturn(emailConfigForInvoiceAlerts);
        when(alertEmailEventRepo.findByInvoiceId(any(),any())).thenReturn(List.of(alertEmailEventEntityForInvoiceAlerts1));
        when(revenueStreamAppConfigRepo.findbyRevenueStreamNameDesc(any())).thenReturn(invoiceTypeDescription);
        doNothing().when(emailTrackingService).saveEmailTrackingDetails(any(),any(),any());
        EwsAccountInformationResponse ewsAccountInformationResponse =
                new EwsAccountInformationResponse();
        ewsAccountInformationResponse.setPersonalInfo(PersonalInfo.builder()
                .email("test@g")
                .build());
        ResponseEntity<EwsAccountInformationResponse> response = new ResponseEntity<>(ewsAccountInformationResponse, HttpStatus.OK);
        when(ewsSerrvice.getAccountInformation(notificationMessage.getEwsUser())).thenReturn(response);
        GatewayEmailNotificationResponse[] gatewayEmailNotificationResponseList = new GatewayEmailNotificationResponse[1];
        gatewayEmailNotificationResponseList[0] = new GatewayEmailNotificationResponse();
        when (gatewayEmailNotificationService.sendGatewayEmailNotification(any(),any())).thenReturn(gatewayEmailNotificationResponseList);
        emailService.sendEmailNotification(notificationMessage);
        GatewayEmailNotificationRequest Expectedrequest = GatewayEmailNotificationRequest.builder()
                .eventName("NEW_INVOICE_AVAILABLE")
                .subject("test")
                .body("test<br/>")
                .feedback("test")
                .to(Arrays.asList("test@g"))
                .build();
        emailService.sendEmailNotification(notificationMessage);
        GatewayEmailNotificationRequest Actualrequest = emailService.setNotificationRequest(null, emailConfigForInvoiceAlerts,Arrays.asList(),Arrays.asList(),
                alertEmailEventEntityForInvoiceAlerts,notificationMessage,new HashMap<>() );
        Assertions.assertEquals(Expectedrequest,Actualrequest);
    }

    @Test
    public void sendEmailNotificationInvoiceAlert_Error() throws Exception{
        String invoiceTypeDescription = "TRACE";
        notificationMessage.setStatus("NEW_INVOICE_AVAILABLE");
        notificationMessage.setInvoiceType("CRDRG");
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setSubject("test");
        EmailConfig emailConfigForInvoiceAlerts = new EmailConfig();
        emailConfigForInvoiceAlerts.setEventTypeName("NEW_INVOICE_AVAILABLE");
        emailConfigForInvoiceAlerts.setBody("test");
        emailConfigForInvoiceAlerts.setFeedBack("test");
        emailConfigForInvoiceAlerts.setSubject("test");
        List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>() ;
        List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>() ;
        AlertEmailEventEntity alertEmailEventEntityForInvoiceAlerts1 = new AlertEmailEventEntity();
        alertEmailEventEntityForInvoiceAlerts1.setId(UUID.fromString("319dea09-7ef1-460e-9d6f-0975a283c676"));
        alertEmailEventEntityForInvoiceAlerts1.setExtraInfo("{\"org_name\":\"JP Morgan Chase\"}");
        alertEmailEventEntityForInvoiceAlerts1.setSendTS(LocalDate.now());
        alertEmailEventEntityForInvoiceAlerts1.setInvoiceNumber("1234");
        alertEmailEventEntityForInvoiceAlerts1.setStatus(3);
        alertEmailEventEntityForInvoiceAlerts.add(alertEmailEventEntityForInvoiceAlerts1);
        PaymentTrackingEntity paymentTrackingEntity = new PaymentTrackingEntity();
        paymentTrackingEntity.setPaymentReferenceNumber("1234");
        ExtraInfo extraInfoNew = objectMapper.readValue(alertEmailEventEntityForInvoiceAlerts.get(0).getExtraInfo(), ExtraInfo.class);
        extraInfoNew.setOrgName("JP Morgan Chase");
        when(emailConfigRepo.findByEventTypeName(any())).thenReturn(emailConfigForInvoiceAlerts);
        when(alertEmailEventRepo.findByInvoiceId(any(),any())).thenReturn(List.of(alertEmailEventEntityForInvoiceAlerts1));
        when(revenueStreamAppConfigRepo.findbyRevenueStreamNameDesc(any())).thenReturn(invoiceTypeDescription);
        doNothing().when(emailTrackingService).saveEmailTrackingDetails(any(),any(),any());
        EwsAccountInformationResponse ewsAccountInformationResponse =
                new EwsAccountInformationResponse();
        ewsAccountInformationResponse.setPersonalInfo(PersonalInfo.builder()
                .email("test@g")
                .build());
        ResponseEntity<EwsAccountInformationResponse> response = new ResponseEntity<>(ewsAccountInformationResponse, HttpStatus.OK);
        when(ewsSerrvice.getAccountInformation(notificationMessage.getEwsUser())).thenReturn(response);
        GatewayEmailNotificationResponse[] gatewayEmailNotificationResponseList = new GatewayEmailNotificationResponse[1];
        gatewayEmailNotificationResponseList[0] = new GatewayEmailNotificationResponse();
        when (gatewayEmailNotificationService.sendGatewayEmailNotification(any(),any())).thenReturn(null);
        emailService.sendEmailNotification(notificationMessage);
        GatewayEmailNotificationRequest Expectedrequest = GatewayEmailNotificationRequest.builder()
                .eventName("NEW_INVOICE_AVAILABLE")
                .subject("test")
                .body("test<br/>")
                .feedback("test")
                .to(Arrays.asList("test@g"))
                .build();
        GatewayEmailNotificationRequest Actualrequest = emailService.setNotificationRequest(null, emailConfigForInvoiceAlerts,Arrays.asList(),Arrays.asList(),
                alertEmailEventEntityForInvoiceAlerts,notificationMessage,new HashMap<>() );

        Assertions.assertEquals(Expectedrequest,Actualrequest);

    }

    @Test
    public void sendEmailNotificationInvoiceAlert_Processed() throws Exception{
        String invoiceTypeDescription = "TRACE";
        notificationMessage.setStatus("NEW_INVOICE_AVAILABLE");
        notificationMessage.setInvoiceType("CRDRG");
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setSubject("test");
        EmailConfig emailConfigForInvoiceAlerts = new EmailConfig();
        emailConfigForInvoiceAlerts.setEventTypeName("NEW_INVOICE_AVAILABLE");
        emailConfigForInvoiceAlerts.setBody("test");
        emailConfigForInvoiceAlerts.setFeedBack("test");
        emailConfigForInvoiceAlerts.setSubject("test");
        List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>() ;
        List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>() ;
        AlertEmailEventEntity alertEmailEventEntityForInvoiceAlerts1 = new AlertEmailEventEntity();
        alertEmailEventEntityForInvoiceAlerts1.setId(UUID.fromString("319dea09-7ef1-460e-9d6f-0975a283c676"));
        alertEmailEventEntityForInvoiceAlerts1.setExtraInfo("{\"org_name\":\"JP Morgan Chase\"}");
        alertEmailEventEntityForInvoiceAlerts1.setSendTS(LocalDate.now());
        alertEmailEventEntityForInvoiceAlerts1.setInvoiceNumber("1234");
        alertEmailEventEntityForInvoiceAlerts1.setStatus(2);
        alertEmailEventEntityForInvoiceAlerts.add(alertEmailEventEntityForInvoiceAlerts1);
        PaymentTrackingEntity paymentTrackingEntity = new PaymentTrackingEntity();
        paymentTrackingEntity.setPaymentReferenceNumber("1234");
        ExtraInfo extraInfoNew = objectMapper.readValue(alertEmailEventEntityForInvoiceAlerts.get(0).getExtraInfo(), ExtraInfo.class);
        extraInfoNew.setOrgName("JP Morgan Chase");
        when(emailConfigRepo.findByEventTypeName(any())).thenReturn(emailConfigForInvoiceAlerts);
        when(alertEmailEventRepo.findByInvoiceId(any(),any())).thenReturn(List.of(alertEmailEventEntityForInvoiceAlerts1));
        when(revenueStreamAppConfigRepo.findbyRevenueStreamNameDesc(any())).thenReturn(invoiceTypeDescription);
        doNothing().when(emailTrackingService).saveEmailTrackingDetails(any(),any(),any());
        EwsAccountInformationResponse ewsAccountInformationResponse =
                new EwsAccountInformationResponse();
        ewsAccountInformationResponse.setPersonalInfo(PersonalInfo.builder()
                .email("test@g")
                .build());
        ResponseEntity<EwsAccountInformationResponse> response = new ResponseEntity<>(ewsAccountInformationResponse, HttpStatus.OK);
        when(ewsSerrvice.getAccountInformation(notificationMessage.getEwsUser())).thenReturn(response);
        GatewayEmailNotificationResponse[] gatewayEmailNotificationResponseList = new GatewayEmailNotificationResponse[1];
        gatewayEmailNotificationResponseList[0] = new GatewayEmailNotificationResponse();
        when (gatewayEmailNotificationService.sendGatewayEmailNotification(any(),any())).thenReturn(gatewayEmailNotificationResponseList);
        emailService.sendEmailNotification(notificationMessage);
        GatewayEmailNotificationRequest Expectedrequest = GatewayEmailNotificationRequest.builder()
                .eventName("NEW_INVOICE_AVAILABLE")
                .subject("test")
                .body("test<br/>")
                .feedback("test")
                .to(Arrays.asList("test@g"))
                .build();
        GatewayEmailNotificationRequest Actualrequest = emailService.setNotificationRequest(null, emailConfigForInvoiceAlerts,Arrays.asList(),Arrays.asList(),
                alertEmailEventEntityForInvoiceAlerts,notificationMessage,new HashMap<>() );

        Assertions.assertEquals(Expectedrequest,Actualrequest);

    }

    @Test
    public void sendEmailNotificationInvoiceAutoPay_test(){
        String invoiceTypeDescription = "TRACE";
        ExtraInfo extraInfo = new ExtraInfo();
        notificationMessage.setModule("INVOICE_AUTOPAY");
        notificationMessage.setStatus("SETUP");
        notificationMessage.setSubscriptionId(UUID.randomUUID().toString());
        notificationMessage.setInvoiceType("CRDRG");
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setEventTypeName("INVOICE_AUTOPAY_SETUP");
        emailConfig.setBody("test<br/>");
        emailConfig.setSubject("test<br/>");
        AlertEmailEventEntity alertEmailEventEntity = new AlertEmailEventEntity();
        alertEmailEventEntity.setId(UUID.fromString("319dea09-7ef1-460e-9d6f-0975a283c676"));
        alertEmailEventEntity.setSendTS(LocalDate.now());
        alertEmailEventEntity.setStatus(2);
        PaymentTrackingEntity paymentTrackingEntity = new PaymentTrackingEntity();
        paymentTrackingEntity.setPaymentReferenceNumber("1234");
        when(emailConfigRepo.findByEventTypeName(any())).thenReturn(emailConfig);
        when(paymentTrackingRepo.findByPaymentReferenceNumberIn(any())).thenReturn(List.of(paymentTrackingEntity));
        when(alertEmailEventRepo.findAllById(any())).thenReturn(List.of(alertEmailEventEntity));
        when(revenueStreamAppConfigRepo.findbyRevenueStreamNameDesc(any())).thenReturn(invoiceTypeDescription);
        Map<String, String> orgMap = new HashMap<>();
        orgMap.put("79","JP Morgan");
        when(orgService.getOrganizationDetailsByBUAndPaymentNumber(any(),any())).thenReturn(orgMap);
        doNothing().when(emailTrackingService).saveEmailTrackingDetails(any(),any(),any());
        EwsAccountInformationResponse ewsAccountInformationResponse =
                new EwsAccountInformationResponse();
        ewsAccountInformationResponse.setPersonalInfo(PersonalInfo.builder()
                .email("test@g")
                .build());

        ResponseEntity<EwsAccountInformationResponse> response = new ResponseEntity<>(ewsAccountInformationResponse, HttpStatus.OK);
        when(ewsSerrvice.getAccountInformation(notificationMessage.getEwsUser())).thenReturn(response);
        GatewayEmailNotificationResponse[] gatewayEmailNotificationResponseList = new GatewayEmailNotificationResponse[1];
        gatewayEmailNotificationResponseList[0] = new GatewayEmailNotificationResponse();
        when (gatewayEmailNotificationService.sendGatewayEmailNotification(any(),any())).thenReturn(gatewayEmailNotificationResponseList);
        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .orgId("test")
                .build();
        when(subscriptionRepo.findById(UUID.fromString(notificationMessage.getSubscriptionId()))).thenReturn(
                Optional.ofNullable(subscription));
        GatewayEmailNotificationRequest Expectedrequest = GatewayEmailNotificationRequest.builder()
                .eventName("INVOICE_AUTOPAY_SETUP")
                .subject("test<br/>")
                .body("test<br/>")
                .feedback(null)
                .to(Arrays.asList("test@g"))
                .build();
        emailService.sendEmailNotification(notificationMessage);
        GatewayEmailNotificationRequest Actualrequest = emailService.setNotificationRequest(emailConfig, null,Arrays.asList(),Arrays.asList(),
                null,notificationMessage,new HashMap<>() );
        Assertions.assertEquals(Expectedrequest,Actualrequest);
    }

    @Test
    public void setNotificationRequest_test() {
        String invoiceTypeDescription = "TRACE";
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setModule(ModuleTypeEnum.AUTOPAY.name());
        notificationMessage.setStatus("SETUP");
        notificationMessage.setEwsUser("test");
        notificationMessage.setInvoiceType("CRDRG");
        notificationMessage.setSubscriptionId(UUID.randomUUID().toString());
        EmailConfig emailConfigForInvoiceAlerts = new EmailConfig();
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setEventTypeName("AUTOPAY_SETUP");
        emailConfig.setBody("test");
        emailConfig.setFeedBack("test");
        emailConfig.setSubject("test");
        when(emailConfigRepo.findByEventTypeName(any())).thenReturn(emailConfig);
        //when(alertEmailEventRepo.findAllById(any())).thenReturn(List.of(alertEmailEventEntity1));
        doNothing().when(emailTrackingService).saveEmailTrackingDetails(any(),any(),any());
        EwsAccountInformationResponse ewsAccountInformationResponse =
            new EwsAccountInformationResponse();
        ewsAccountInformationResponse.setPersonalInfo(PersonalInfo.builder()
                .email("test@g")
            .build());
        ResponseEntity<EwsAccountInformationResponse> response = new ResponseEntity<>(ewsAccountInformationResponse, HttpStatus.OK);
        when(ewsSerrvice.getAccountInformation(notificationMessage.getEwsUser())).thenReturn(response);

        SubscriptionEntity subscription = SubscriptionEntity.builder()
            .orgId("test")
            .build();
        when(subscriptionRepo.findById(UUID.fromString(notificationMessage.getSubscriptionId()))).thenReturn(
            Optional.ofNullable(subscription));

        GatewayEmailNotificationRequest req = GatewayEmailNotificationRequest.builder()
            .eventName("AUTOPAY_SETUP")
            .subject("test")
            .body("test")
            .feedback("test")
            .to(Arrays.asList("test@g"))
            .build();

        Assertions.assertEquals(req, emailService.setNotificationRequest(emailConfig, emailConfigForInvoiceAlerts,Arrays.asList(),Arrays.asList(), Arrays.asList(),notificationMessage,new HashMap<>() ));

    }

    @Test
    public void setNotificationRequest_test_ENABLER_DATA_REFRESH_SUCCESS() {
        String invoiceTypeDescription = "TRACE";
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setModule("ENABLER");
        notificationMessage.setStatus("RUN_DATA_REFRESH_SUCCESS");
        notificationMessage.setEwsUser("test");
        notificationMessage.setInvoiceType("CRDRG");
        notificationMessage.setSubscriptionId(UUID.randomUUID().toString());
        EmailConfig emailConfigForInvoiceAlerts = new EmailConfig();
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setEventTypeName("ENABLER_RUN_DATA_REFRESH_SUCCESS");
        emailConfig.setTo("test");
        emailConfig.setBody("test");
        emailConfig.setFeedBack("test");
        emailConfig.setSubject("test");
        when(emailConfigRepo.findByEventTypeName(any())).thenReturn(emailConfig);
        doNothing().when(emailTrackingService).saveEmailTrackingDetails(any(),any(),any());
        GatewayEmailNotificationResponse[] gatewayEmailNotificationResponseList = new GatewayEmailNotificationResponse[1];
        gatewayEmailNotificationResponseList[0] = new GatewayEmailNotificationResponse();
        when (gatewayEmailNotificationService.sendGatewayEmailNotification(any(),any())).thenReturn(gatewayEmailNotificationResponseList);
        emailService.sendEmailNotification(notificationMessage);

        GatewayEmailNotificationRequest req = GatewayEmailNotificationRequest.builder()
            .eventName("ENABLER_RUN_DATA_REFRESH_SUCCESS")
            .subject("test")
            .body("test")
            .feedback("test")
            .to(Arrays.asList("test"))
            .build();

        Assertions.assertEquals(req, emailService.setNotificationRequest(emailConfig, null,null,null, null,notificationMessage,null ));

    }
}
