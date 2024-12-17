package org.finra.rmcs.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.EwsAccessTokenResponse;
import org.finra.rmcs.dto.EwsAccountInformationResponse;
import org.finra.rmcs.entity.AlertEmailEventEntity;
import org.finra.rmcs.entity.EmailConfig;
import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.finra.rmcs.model.NotificationMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@SpringJUnitConfig
class UtilTest {

  private static final ObjectMapper mapper = new ObjectMapper();
  @InjectMocks
  Util utils;
  @Mock
  private Map<String, String> apiUserBean;
  @MockBean
  private RestTemplate restTemplate;

  @BeforeEach
  void beforeEach() {
    apiUserBean = mock(Map.class);
    when(apiUserBean.get(anyString())).thenReturn("password");

    ReflectionTestUtils.setField(utils, "ewsApiUser", "ewsApiUser");
    ReflectionTestUtils.setField(utils, "ewsTokenUrl", "ewsTokenUrl");
    ReflectionTestUtils.setField(utils, "restTemplate", restTemplate);
    ReflectionTestUtils.setField(utils, "apiUserBean", apiUserBean);
  }

  @Test
  void test_getEwsAccessTokenReturnsString() {
    // Arrange
    String expectedEwsAccessToken = "ewsAccessToken";
    EwsAccessTokenResponse ewsAccessTokenResponse = EwsAccessTokenResponse
        .builder()
        .accessToken(expectedEwsAccessToken)
        .build();

    when(restTemplate.exchange(
        anyString(),
        any(),
        any(),
        (Class<Object>) any()
    )).thenReturn(
        new ResponseEntity<>(ewsAccessTokenResponse, HttpStatus.OK)
    );

    // Action
    String actualEwsAccessToken = utils.getEwsAccessToken();

    // Assert
    assertNotNull(actualEwsAccessToken);
    assertEquals(expectedEwsAccessToken, actualEwsAccessToken);
  }

  @Test
  void test_getEwsAccessTokenWithHttpStatusNotOkThrowsException() {
    // Arrange
    String expectedEwsAccessToken = "ewsAccessToken";
    EwsAccessTokenResponse ewsAccessTokenResponse = EwsAccessTokenResponse
        .builder()
        .accessToken(expectedEwsAccessToken)
        .build();

    when(restTemplate.exchange(
        any(),
        any(),
        any(),
        (Class<Object>) any()
    )).thenReturn(
        new ResponseEntity<>(ewsAccessTokenResponse, HttpStatus.NOT_FOUND)
    );

    // Action
    // Assert
    assertThrows(RuntimeException.class, () -> {
      utils.getEwsAccessToken();
    });

  }

  @Test
  void test_getEwsAccessTokenWithNoBodyThrowsException() {
    // Arrange
    EwsAccessTokenResponse ewsAccessTokenResponse = null;

    when(restTemplate.exchange(
        any(),
        any(),
        any(),
        (Class<Object>) any()
    )).thenReturn(
        new ResponseEntity<>(ewsAccessTokenResponse, HttpStatus.OK)
    );

    // Action
    // Assert
    assertThrows(RuntimeException.class, () -> {
      utils.getEwsAccessToken();
    });
  }

  @Test
  void test_getEwsAccessTokenThrowsHttpStatusCodeExceptionException() {
    // Arrange
    when(restTemplate.exchange(
        any(),
        any(),
        any(),
        (Class<Object>) any()
    )).thenThrow(
        new HttpServerErrorException(HttpStatus.BAD_GATEWAY)
    );

    // Action
    // Assert
    assertThrows(/* TODO pjk HttpServerError*/Exception.class, () -> {
      utils.getEwsAccessToken();
    });
  }

  @Test
  void test_getEwsAccountInformationReturnsEwsAccountInformationResponse() {
    // Arrange
    String expectedEwsAccessToken = "ewsAccessToken";
    EwsAccessTokenResponse ewsAccessTokenResponse = EwsAccessTokenResponse
        .builder()
        .accessToken(expectedEwsAccessToken)
        .build();

    String ewsUser = "ewsUser";
    EwsAccountInformationResponse expectedEwsAccountInformationResponse = EwsAccountInformationResponse
        .builder()
        .build();

    when(restTemplate.exchange(
        anyString(),
        any(),
        any(),
        (Class<Object>) any()
    )).thenReturn(
        new ResponseEntity<>(ewsAccessTokenResponse, HttpStatus.OK),
        new ResponseEntity<>(expectedEwsAccountInformationResponse, HttpStatus.OK)
    );

    // Action
    EwsAccountInformationResponse actualEwsAccountInformationResponse = utils.getEwsAccountInformation(
        ewsUser);

    // Assert
    assertNotNull(actualEwsAccountInformationResponse);
    assertEquals(expectedEwsAccountInformationResponse, actualEwsAccountInformationResponse);

  }

  @Test
  void test_getEwsAccountInformationWithStatusCodeNotValidThrowsException() {
    // Arrange
    String expectedEwsAccessToken = "ewsAccessToken";
    EwsAccessTokenResponse ewsAccessTokenResponse = EwsAccessTokenResponse
        .builder()
        .accessToken(expectedEwsAccessToken)
        .build();

    String ewsUser = "ewsUser";
    EwsAccountInformationResponse expectedEwsAccountInformationResponse = EwsAccountInformationResponse
        .builder()
        .build();

    when(restTemplate.exchange(
        anyString(),
        any(),
        any(),
        (Class<Object>) any()
    )).thenReturn(
        new ResponseEntity<>(ewsAccessTokenResponse, HttpStatus.OK),
        new ResponseEntity<>(expectedEwsAccountInformationResponse, HttpStatus.BAD_GATEWAY)
    );

    // Action
    // Assert
    assertThrows(RuntimeException.class, () -> {
      utils.getEwsAccountInformation(ewsUser);
    });
  }

  @Test
  void test_getEwsAccountInformationWithNoBodyThrowsBadGatewayException() {
    // Arrange
    String expectedEwsAccessToken = "ewsAccessToken";
    EwsAccessTokenResponse ewsAccessTokenResponse = EwsAccessTokenResponse
        .builder()
        .accessToken(expectedEwsAccessToken)
        .build();

    String ewsUser = "ewsUser";
    EwsAccountInformationResponse expectedEwsAccountInformationResponse = null;

    when(restTemplate.exchange(
        anyString(),
        any(),
        any(),
        (Class<Object>) any()
    )).thenReturn(
        new ResponseEntity<>(ewsAccessTokenResponse, HttpStatus.OK),
        new ResponseEntity<>(expectedEwsAccountInformationResponse, HttpStatus.OK)
    );

    // Action
    // Assert
    assertThrows(RuntimeException.class, () -> {
      utils.getEwsAccountInformation(ewsUser);
    });
  }

  @Test
  void test_getEwsAccountInformationThrowsException() {
    // Arrange
    String expectedEwsAccessToken = "ewsAccessToken";
    EwsAccessTokenResponse ewsAccessTokenResponse = EwsAccessTokenResponse
        .builder()
        .accessToken(expectedEwsAccessToken)
        .build();

    String ewsUser = "ewsUser";
    EwsAccountInformationResponse expectedEwsAccountInformationResponse = null;

    when(restTemplate.exchange(
        anyString(),
        any(),
        any(),
        (Class<Object>) any()
    )).thenReturn(
        new ResponseEntity<>(ewsAccessTokenResponse, HttpStatus.OK)
    ).thenThrow(
        new HttpServerErrorException(HttpStatus.BAD_GATEWAY)
    );

    // Action
    // Assert
    assertThrows(HttpServerErrorException.class, () -> {
      utils.getEwsAccountInformation(ewsUser);
    });
  }

  @Test
  void getNotificationMessage_test() throws Exception {
    SQSEvent sqsEvent =
        mapper.readValue(
            this.getClass().getClassLoader().getResourceAsStream("ValidEvent.json"),
            SQSEvent.class);
    NotificationMessage actual = Util.getNotificationMessage(sqsEvent);
    Assertions.assertEquals("AFFILIATED_FIRM_TRANSFER", actual.getModule());
  }

  @Test
  void getMessageId_test() throws Exception {
    SQSEvent sqsEvent =
        mapper.readValue(
            this.getClass().getClassLoader().getResourceAsStream("ValidEvent.json"),
            SQSEvent.class);
    String actual = Util.getMessageId(sqsEvent, "1234-1ab23-sdd4-sadf");
    Assertions.assertEquals("1623e1f2-d627-52c5-b280-bc740bb89be6", actual);
  }

  @Test
  public void testGenerateBody() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("AFFILIATED_FIRM_TRANSFER_Submitted");
    String res = utils.generateBody("test", list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  public void testGenerateSubject() throws Exception {
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    alertEmailEventEntity.add(newlist);
    String res = utils.generateSubject("test", alertEmailEventEntity, null, null);
    Assertions.assertNotNull(res);
  }

  @Test
  public void testGenerateSubjectNull() {
    String res = utils.generateSubject(null, new ArrayList<>(), null, null);
    Assertions.assertEquals(Constants.EMPTY_STR, res);
  }

  @Test
  public void testGenerateBodySubmitted() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("AFFILIATED_FIRM_TRANSFER_Submitted");
    String dbBody = "FROM_ORG_ID<br/> TO_ORG_ID<br/> TRANSACTION_AMOUNT<br/>";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  public void testGenerateBodyFailed() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("AFFILIATED_FIRM_TRANSFER_FAILED");
    String dbBody = "From_org_name<br/> To_org_name<br/> transaction_date<br/>";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  public void testGenerateBodyProcessed() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("AFFILIATED_FIRM_TRANSFER_Processed");
    String dbBody = "From_org_id<br/> To_org_id<br/> transaction_amount<br/>";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  public void testGenerateBodyProcessingError() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("AFFILIATED_FIRM_TRANSFER_Processing_error");
    String dbBody = "From_org_name<br/> To_org_name<br/> transaction_id<br/>";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  public void testGenerateBodyQueued() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("AFFILIATED_FIRM_TRANSFER_Queued");
    String dbBody = "TRANSACTION_DATE<br/>transaction_id<br/>FROM_ORG_NAME <br/> FROM_ORG_ID" +
        "<br/>FROM_ORG_ID<br/> TO_ORG_ID<br/>customer_name <br/> AMOUNT<br/>";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  public void testGenerateBodyBalanceAbove() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExpectedValue("2000.02");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("CRDRG_BALANCEABOVE");
    String dbBody =
        "THRESHOLDAMOUNT<br/>BUSINESSUNITLONGDESCRIPTION<br/>ESTIMATEDBALANCE <br/> ORGID" +
            "<br/>BALANCEDATE<br/>CUSTOMERNAME<br/> ";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  public void testGenerateBodyBalanceBELOW() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExpectedValue("2000.02");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("CRDRG_BALANCEBELOW");
    String dbBody =
        "THRESHOLDAMOUNT<br/>BUSINESSUNITLONGDESCRIPTION<br/>ESTIMATEDBALANCE <br/> ORGID" +
            "<br/>BALANCEDATE<br/>CUSTOMERNAME<br/> ";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  public void testGenerateBodyDebit() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExpectedValue("2000.02");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("CRDRG_DEBIT");
    String dbBody =
        "THRESHOLDAMOUNT<br/>BUSINESSUNITLONGDESCRIPTION<br/>ESTIMATEDBALANCE <br/> ORGID" +
            "<br/>BALANCEDATE<br/>CUSTOMERNAME<br/> ";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  public void testGenerateBodyNEWINVOICE() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    AlertEmailEventEntity list1 = new AlertEmailEventEntity();
    list1.setOrgID("79");
    alertEmailEventEntityForInvoiceAlerts.add(list1);
    NotificationMessage notificationMessage = new NotificationMessage();
    notificationMessage.setStatus("NEW_INVOICE_AVAILABLE");
    List<PaymentTrackingEntity> list = new ArrayList<>();
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("NEW_INVOICE_AVAILABLE");
    String dbBody = "BUSINESSUNITLONGDESCRIPTION<br/> ORGID" +
        "<br/>CUSTOMERNAME <br/> LISTOFINVOICES<br/> ";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  public void testGenerateBodyINVOICEPASTDUE() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    AlertEmailEventEntity list1 = new AlertEmailEventEntity();
    list1.setOrgID("79");
    alertEmailEventEntityForInvoiceAlerts.add(list1);
    NotificationMessage notificationMessage = new NotificationMessage();
    notificationMessage.setStatus("INVOICE_PAST_DUE");
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExpectedValue("2000.02");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("INVOICE_PAST_DUE");
    String dbBody = "BUSINESSUNITLONGDESCRIPTION<br/> ORGID" +
        "<br/>CUSTOMERNAME<br/>LISTOFINVOICES<br/> ";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  void gettable_test() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("AFFILIATED_FIRM_TRANSFER_Submitted");
    String dbBody = "TABLE_DATA<br/>";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  void buildEmailTemplateVariableKeyValueMap_test() throws Exception {
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity paymentTrackingEntity = new PaymentTrackingEntity();
    paymentTrackingEntity.setCustomerId("123");
    paymentTrackingEntity.setCreatedDate(LocalDateTime.parse("2023-02-01T22:34:29.439917"));
    list.add(paymentTrackingEntity);
    EwsAccountInformationResponse newlist = new EwsAccountInformationResponse();
    ResponseEntity<EwsAccountInformationResponse> ewUserResp = new ResponseEntity<>(newlist,
        HttpStatusCode.valueOf(403));
    newlist.setOrgId("23");
    notificationMessage.setAmount("2000");
    notificationMessage.setTransmissionId("1234");
    notificationMessage.setStatus("true");
    notificationMessage.setPaymentNumber(new ArrayList<>(Arrays.asList("123", "123")));
    Map<String, String> res = utils.buildEmailTemplateVariableKeyValueMap(notificationMessage,
        ewUserResp, list, orgMap);
    Assertions.assertEquals(res.get(Constants.REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_AMOUNT),
        "$2,000.00");
  }

  @Test
  void generateRefundBody_test() throws Exception {
    String emailTemplateFromDb = "abc ";
    NotificationMessage refundReq = new NotificationMessage();
    refundReq.setPaymentNumber(new ArrayList<>(Arrays.asList("123", "123")));
    refundReq.setTransmissionId("1234");
    refundReq.setAmount("100000.54");
    refundReq.setStatus("true");
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity paymentTrackingEntity = new PaymentTrackingEntity();
    paymentTrackingEntity.setCustomerId("123");
    paymentTrackingEntity.setCreatedDate(LocalDateTime.parse("2023-02-01T22:34:29.439917"));
    list.add(paymentTrackingEntity);
    EwsAccountInformationResponse newlist = new EwsAccountInformationResponse();
    ResponseEntity<EwsAccountInformationResponse> response = new ResponseEntity<>(newlist,
        HttpStatusCode.valueOf(403));
    newlist.setOrgId("23");
    String res = utils.generateRefundBody(emailTemplateFromDb, refundReq, response, list, orgMap);

  }

  @Test
  void generateSubject_test() {
    String sub = "FINRA E-Bill AutoPay Payment for {{bus_un_long_ds}}:{{crd}} Failed";
    String orgId = "test";
    String invoiceType = "CRDRG";
    Assertions.assertEquals("FINRA E-Bill AutoPay Payment for CRDRG:test Failed",
        Util.generateSubject(sub, orgId, invoiceType));
  }

  @Test
  void test_parseSemiColonConcatenatedEmails() {
    // Action
    List<String> res = utils.parseSemiColonConcatenatedEmails("test@test");
    // Assert
    assertNotNull(res);
  }

  @Test
  void gettable_ReallocationInvoiceSubmitted() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("REALLOCATION_INVOICE_SUBMITTED");
    String dbBody = "TABLE_DATA<br/>";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  void gettable_ReallocationInvoiceFailed() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("REALLOCATION_INVOICE_FAILED");
    String dbBody = "TABLE_DATA<br/>";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  void gettable_ReallocationInvoiceQueued() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("REALLOCATION_INVOICE_QUEUED");
    String dbBody = "TABLE_DATA<br/>";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

  @Test
  void gettable_ReallocationInvoiceSuccess() throws Exception {
    String invoiceTypeDescription = "TRACE";
    List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts = new ArrayList<>();
    NotificationMessage notificationMessage = new NotificationMessage();
    List<PaymentTrackingEntity> list = new ArrayList<>();
    PaymentTrackingEntity payment = new PaymentTrackingEntity();
    payment.setPaymentAmount(1000.00);
    payment.setCreatedBy("abc");
    list.add(payment);
    List<AlertEmailEventEntity> alertEmailEventEntity = new ArrayList<>();
    AlertEmailEventEntity newlist = new AlertEmailEventEntity();
    newlist.setActualValue("1000");
    newlist.setExtraInfo(TestUtil.getResourceFileContents("/ExtraInfo.json"));
    alertEmailEventEntity.add(newlist);
    Map<String, String> orgMap = new HashMap<>();
    orgMap.put("79", "JP Morgan");
    EmailConfig emailConfig = new EmailConfig();
    emailConfig.setEventTypeName("REALLOCATION_INVOICE_PROCESSED");
    String dbBody = "TABLE_DATA<br/>";
    String res = utils.generateBody(dbBody, list, alertEmailEventEntity, emailConfig, orgMap,
        notificationMessage, alertEmailEventEntityForInvoiceAlerts, invoiceTypeDescription);
    Assertions.assertNotNull(res);
  }

//  @Test
//  void generateAutoPayBody_test() {
//    String body1 =
//        "As a result of your AutoPay subscription, E-Bill attempted but failed to fund the E-Bill {{bus_un_long_ds}} (CRD#: {{crd}}) for {{amount}} due to the following error:</br>\n"
//            + "    {{failure_reason}} The reference number for this transaction is {{transaction_id}}.</br>";
//
//    NotificationMessage msg = new NotificationMessage();
//    msg.setAmount("10");
//    SubscriptionEntity subscription = new SubscriptionEntity(
//          UUID.randomUUID(), "test_orgid", "testUser", UUID.randomUUID(), "testUser"
//      );
//    PaymentTrackingEntity paymentTracking = new PaymentTrackingEntity();
//    paymentTracking.setPaymentReferenceNumber("test_pt");
//    Assertions.assertEquals(
//        "As a result of your AutoPay subscription, E-Bill attempted but failed to fund the E-Bill CRDRG (CRD#: test_orgid) for 10 due to the following error:</br>\n"
//            + "     ",
//        Util.generateAutoPayBody(body1, Arrays.asList(), msg, subscription));
//    msg.setErrorMsg(Arrays.asList("test_err1","test_err2"));
//    Assertions.assertEquals(
//        "As a result of your AutoPay subscription, E-Bill attempted but failed to fund the E-Bill CRDRG (CRD#: test_orgid) for 10 due to the following error:</br>\n"
//            + "    1: test_err1 </br>2: test_err2 </br> The reference number for this transaction is test_pt.</br>",
//        Util.generateAutoPayBody(body1, Arrays.asList(paymentTracking), msg, subscription));
//  }

}


