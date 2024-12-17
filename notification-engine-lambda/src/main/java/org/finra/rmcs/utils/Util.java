package org.finra.rmcs.utils;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.finra.rmcs.constants.BuCodeEnum;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.EventTypeEnum;
import org.finra.rmcs.constants.ModuleTypeEnum;
import org.finra.rmcs.dto.EwsAccessTokenResponse;
import org.finra.rmcs.dto.EwsAccountInformationResponse;
import org.finra.rmcs.dto.ExtraInfo;
import org.finra.rmcs.entity.AlertEmailEventEntity;
import org.finra.rmcs.entity.EmailConfig;
import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.finra.rmcs.entity.SubscriptionEntity;
import org.finra.rmcs.model.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.PaymentStatusEnum;

@Slf4j
@Component
public class Util {
  @Autowired
  RestTemplate restTemplate;

  @Value("${aci.client.id}")
  String clientId;

  @Value("${aci.client.secret}")
  String clientSecret;

  @Value("${aci.client.grantType}")
  String grantType;

  @Value("${aci.client.tokenUrl}")
  String aciTokenUrl;

  @Value("${aci.client.x-auth-key}")
  String xAuthKey;

  @Value("${aci.client.paymentInitializeUrl}")
  String paymentInitializeUrl;

  @Value("${ews.api.user}")
  private String ewsApiUser;

  @Value("${ews.api.url}")
  private String ewsApiUrl;

  @Value("${ews.tokenUrl}")
  private String ewsTokenUrl;

  private final Map<String, String> apiUserBean;

  public Util(Map<String, String> apiUserBean) {
    this.apiUserBean = apiUserBean;
  }

  public static final String METHOD_NAME =
          new StringBuilder()
                  .append(Constants.CLASS)
                  .append("Util")
                  .append(Constants.SPACE)
                  .append(Constants.METHOD)
                  .append(Thread.currentThread().getStackTrace()[1].getMethodName())
                  .append(Constants.SPACE)
                  .toString();
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String generateSubject(String subject, String orgId, String invoiceType) {
      return subject.replace(Constants.AUTOPAY_BUS_UN_LONG_DS,invoiceType != null ?invoiceType :Constants.CRDRG ).replace(Constants.AUTOPAY_CRD, orgId);
  }

  public static String generateAutoPayBody(String body, List<PaymentTrackingEntity> paymentTrackingEntity, NotificationMessage notificationMessage,
      SubscriptionEntity subscriptionEntity, String invoiceTypeDescription) {
    body = body.replace(Constants.AUTOPAY_BUS_UN_LONG_DS, invoiceTypeDescription != null ? invoiceTypeDescription: Constants.EMPTY_STR);

    if(body.contains(Constants.AUTOPAY_CRD)) {
      body = body.replace(Constants.AUTOPAY_CRD, subscriptionEntity.getOrgId());
    }

    if(body.contains(Constants.AUTOPAY_AMOUNT)) {
      body = body.replace(Constants.AUTOPAY_AMOUNT, notificationMessage.getAmount() != null ?notificationMessage.getAmount(): "");
    }

    if(body.contains(Constants.AUTOPAY_TRANSACTION_ID)) {
      if(paymentTrackingEntity != null && paymentTrackingEntity.size() == 1) {
        body = body.replace(Constants.AUTOPAY_TRANSACTION_ID, paymentTrackingEntity.get(0).getPaymentReferenceNumber());
      } else if (!notificationMessage.getPaymentNumber().isEmpty() && notificationMessage.getPaymentNumber().size() == 1) {
        body = body.replace(Constants.AUTOPAY_TRANSACTION_ID, notificationMessage.getPaymentNumber().get(0));
      } else {
        body = body.replace(Constants.AUTOPAY_TRANSACTION_ID_REFERENCE, "");
      }
    }

    if(body.contains(Constants.AUTOPAY_FAILURE_REASON) && ModuleTypeEnum.AUTOPAY.name().equalsIgnoreCase(notificationMessage.getModule())) {
        body = body.replace(Constants.AUTOPAY_FAILURE_REASON, paymentTrackingEntity !=null ? paymentTrackingEntity.get(0).getPrcsRspnsTx() : null);
    }
    if (body.contains(Constants.TABLE_DATA)){
      body = body.replace(Constants.TABLE_DATA,populateInvoiceAutopayTable(paymentTrackingEntity));
    }

    return body;
  }
  private static String populateInvoiceAutopayTable (List<PaymentTrackingEntity> paymentTrackingEntity){
    StringBuilder html = new StringBuilder();
    html.append(Constants.HTML +
            Constants.BODY +
            Constants.TABLE +
            "<tr>");
    html.append(
            Constants.TRANSACTION_ID_TABLE +
            "<th>Failure Reason</th>");

    for (PaymentTrackingEntity paymentTracking :paymentTrackingEntity ) {
      html.append("<tr>");
      html.append("<td>").append(paymentTracking.getPaymentReferenceNumber()).append(Constants.TD);
      html.append("<td>").append(paymentTracking.getPrcsRspnsTx()).append(Constants.TD);
    }
    html.append(Constants.HTMLTAG);
    log.info(Constants.HTML_STRING, html.toString());
    return html.toString();

  }
  public final String getEwsAccessToken() {
    log.info("get ews api access token");

    try {
      String body = "";
      String ewsApiPassword = apiUserBean.get(ewsApiUser);
      log.info("ewsApiUser {}", ewsApiUser);
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setBasicAuth(ewsApiUser, ewsApiPassword);
      HttpEntity<String> httpEntity = new HttpEntity<>(body, httpHeaders);

      ResponseEntity<EwsAccessTokenResponse> ewsAccessTokenResponse =
              restTemplate.exchange(
                      ewsTokenUrl, HttpMethod.POST, httpEntity, EwsAccessTokenResponse.class);
      log.info("ewsAccessTokenResponse {}", ewsAccessTokenResponse);

      if (ewsAccessTokenResponse.getStatusCode() == HttpStatus.OK
              && null != ewsAccessTokenResponse.getBody()) {

        return ewsAccessTokenResponse.getBody().getAccessToken();
      } else {
        throw new RuntimeException(
                String.format(
                        "StatusCode[%s] Body[%s]",
                        ewsAccessTokenResponse.getStatusCode(), ewsAccessTokenResponse.getBody()));
      }
    } catch (Exception exception) {
      String message =
              String.format(
                      "getEwsAccessToken(ewsApiUser[%s] ewsTokenUrl[%s] failed. exception[%s]",
                      ewsApiUser, ewsTokenUrl, exception);
      log.error(message);

      throw exception;
    }
  }

  @SuppressWarnings("squid:S112")
  public final EwsAccountInformationResponse getEwsAccountInformation(@NonNull String ewsUser) {
    Objects.requireNonNull(ewsUser);

    String ewsAccessToken = getEwsAccessToken();
    log.info("ewsAccessToken {}", ewsAccessToken);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setBearerAuth(ewsAccessToken);
    HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
    String ewsUrl = ewsApiUrl + "/" + ewsUser;
    log.info("ewsUrl {}", ewsUrl);


    try {
      ResponseEntity<EwsAccountInformationResponse> ewsAccountInformationResponse =
              restTemplate.exchange(
                      ewsUrl, HttpMethod.GET, httpEntity, EwsAccountInformationResponse.class);

      if (ewsAccountInformationResponse.getStatusCode() == HttpStatus.OK
              && null != ewsAccountInformationResponse.getBody()) {
        return ewsAccountInformationResponse.getBody();
      } else {
        throw new RuntimeException(
                String.format(
                        "StatusCode[%s] Body[%s]",
                        ewsAccountInformationResponse.getStatusCode(),
                        ewsAccountInformationResponse.getBody()));
      }
    } catch (Exception exception) {
      String message =
              String.format(
                      "getEwsAccountInformation(ewsUser[%s] ewsApiUrl[%s] failed. exception[%s]",
                      ewsUser, ewsApiUrl, exception);
      log.error(message);

      throw exception;
    }
  }

  @SneakyThrows
  public static boolean isDryRun(SQSEvent sqsEvent) {
    boolean isDryRun = false;
    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    JsonNode msgBodyNode = objectMapper.readTree(sqsRecord.getBody());
    JsonNode messageAttributesNode = msgBodyNode.get(Constants.SQS_MESSAGE_ATTRIBUTES);
    JsonNode dryRunNode =
            messageAttributesNode != null ? messageAttributesNode.get(Constants.DRY_RUN) : null;
    if (dryRunNode != null) {
      String valueText = dryRunNode.get("Value").asText();
      isDryRun = Boolean.parseBoolean(valueText);
    }
    return isDryRun;
  }

  public static void populateReturnMap(
          String status, String details, Map<String, Object> returnMap) {
    returnMap.put(Constants.COMPLETION_STATUS, status);
    returnMap.put(Constants.COMPLETION_DETAILS, details);
  }

  @SneakyThrows
  public static String getMessageId(SQSEvent sqsEvent, String correlationId) {
    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    JsonNode msgBodyNode = objectMapper.readTree(sqsRecord.getBody());
    String type =
            msgBodyNode.get(Constants.SQS_TYPE) != null
                    ? msgBodyNode.get(Constants.SQS_TYPE).asText()
                    : null;
    String messageId =
            msgBodyNode.get(Constants.SNS_MESSAGE_ID) != null
                    ? msgBodyNode.get(Constants.SNS_MESSAGE_ID).asText()
                    : null;
    if (Constants.SQS_NOTIFICATION.equals(type) && !StringUtils.isBlank(messageId)) {
      return messageId;
    } else {
      log.info(
              "{} message : Invalid SQS Type or Message Id is not present",
              new StringBuilder()
                      .append(METHOD_NAME)
                      .append(Constants.SPACE)
                      .append(Constants.CORRELATION_ID)
                      .append(correlationId)
                      .toString());
      return null;
    }
  }

  public static NotificationMessage getNotificationMessage(SQSEvent sqsEvent)
          throws JsonProcessingException {
    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    JsonNode msgBodyNode = objectMapper.readTree(sqsRecord.getBody());
    String payload = msgBodyNode.get(Constants.SQS_EVENT_MESSAGE).asText();
    return objectMapper.readValue(payload, NotificationMessage.class);
  }

  public static String generateBody(String dbBody, List<PaymentTrackingEntity> paymentTrackingEntity, List<AlertEmailEventEntity> alertEmailEventEntity, EmailConfig emailConfig, Map<String, String> orgMap,NotificationMessage notificationMessage, List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts,String invoiceTypeDescription) {
    StringBuilder emailBodyBuilder = new StringBuilder();
    ExtraInfo extraInfo = new ExtraInfo();
    try {
      if(alertEmailEventEntity != null && !alertEmailEventEntity.isEmpty() ) {
         extraInfo = objectMapper.readValue(alertEmailEventEntity.get(0).getExtraInfo(), ExtraInfo.class);
        log.info("extraInfo {}", extraInfo);
      }else if ( alertEmailEventEntityForInvoiceAlerts !=null && !alertEmailEventEntityForInvoiceAlerts.isEmpty()){
        extraInfo = objectMapper.readValue(alertEmailEventEntityForInvoiceAlerts.get(0).getExtraInfo(), ExtraInfo.class);
        log.info("extraInfo {}", extraInfo);
      }
      log.info("Generating Email Body from database {}", dbBody);
      String[] str = dbBody.split(Constants.BREAK);
      log.info("str {}", str);
      emailBodyBuilding(str, emailBodyBuilder, extraInfo,paymentTrackingEntity, alertEmailEventEntity, emailConfig, orgMap,notificationMessage,alertEmailEventEntityForInvoiceAlerts,invoiceTypeDescription);
      log.info("Generated Email Body {}", emailBodyBuilder.toString());
    } catch (Exception e) {
      log.error("Exception occurred while generating email body :" + e.getMessage());
      return Constants.EMPTY_STR;
    }
    return emailBodyBuilder.toString();
  }

  public static String generateSubject(String dbSubject, List<AlertEmailEventEntity> alertEmailEventEntity,NotificationMessage notificationMessage,List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts) {
    try {
      log.info("Generating Email subject from database {}", dbSubject);
      return emailSubjectBuilding(dbSubject, alertEmailEventEntity,notificationMessage,alertEmailEventEntityForInvoiceAlerts);
    } catch (Exception e) {
      log.error("Exception occurred while generating email subject :" + e.getMessage());
      return Constants.EMPTY_STR;
    }

  }
  private static String emailSubjectBuilding(String dbSubject, List<AlertEmailEventEntity> alertEmailEventEntity,NotificationMessage notificationMessage,List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts) {
    try {
      if (dbSubject.contains(Constants.BUSINESS_UNIT_LONG_DESCRIPTION) && notificationMessage.getModule() != null && Constants.CRDRG.equalsIgnoreCase(notificationMessage.getModule())) {
        dbSubject = StringUtils.replace(dbSubject, Constants.BUSINESS_UNIT_LONG_DESCRIPTION, Constants.CRDRG);
      } else {
        dbSubject = StringUtils.replace(dbSubject, Constants.BUSINESSUNITLONGDESCRIPTION, notificationMessage.getInvoiceType());
      }
      if (dbSubject.contains(Constants.THRESHOLD_AMOUNT)) {
        dbSubject = StringUtils.replace(dbSubject, Constants.THRESHOLD_AMOUNT, Util.populateAmount(alertEmailEventEntity.get(0).getExpectedValue()));
      }
      if (dbSubject.contains(Constants.DAYS_CONFIGURED)) {
        dbSubject = StringUtils.replace(dbSubject, Constants.DAYS_CONFIGURED, alertEmailEventEntityForInvoiceAlerts.get(0).getAlertCategoryperUser().getThresholdValue());
      }
      if (dbSubject.contains(Constants.REFUND_TRANSACTION_ID)) {
        dbSubject = StringUtils.replace(dbSubject, Constants.REFUND_TRANSACTION_ID, notificationMessage.getPaymentNumber().get(0));
      }
      log.info("dbSubject {}", dbSubject);
      return dbSubject;
    }
    catch (Exception e) {
      log.error("Exception occurred while generating email subject :" + e.getMessage());
      return Constants.EMPTY_STR;
    }
  }

  private static void emailBodyBuilding(String[] str, StringBuilder emailBodyBuilder, ExtraInfo extraInfo, List<PaymentTrackingEntity> paymentTrackingEntity,List<AlertEmailEventEntity> alertEmailEventEntity,EmailConfig emailConfig,Map<String, String> orgMap,NotificationMessage notificationMessage,List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts, String invoiceTypeDescription) {
    for (String strValue : str) {
      if(EventTypeEnum.AFFILIATED_FIRM_TRANSFER_PROCESSED.name().equalsIgnoreCase(emailConfig.getEventTypeName())){
        populateEmailBodyForAFT(emailBodyBuilder,strValue,paymentTrackingEntity, orgMap);
      }
      else if (EventTypeEnum.AFFILIATED_FIRM_TRANSFER_FAILED.name().equalsIgnoreCase(emailConfig.getEventTypeName())) {
        populateEmailBodyForAFT(emailBodyBuilder,strValue,paymentTrackingEntity, orgMap);
      }
      else if (EventTypeEnum.AFFILIATED_FIRM_TRANSFER_SUBMITTED.name().equalsIgnoreCase(emailConfig.getEventTypeName())) {
        populateEmailBodyForAFT(emailBodyBuilder,strValue,paymentTrackingEntity, orgMap);
      }
      else if (EventTypeEnum.AFFILIATED_FIRM_TRANSFER_QUEUED.name().equalsIgnoreCase(emailConfig.getEventTypeName())) {
        populateEmailBodyForAFT(emailBodyBuilder,strValue,paymentTrackingEntity, orgMap);
      }
      else if (EventTypeEnum.CRDRG_BALANCEBELOW.name().equalsIgnoreCase(emailConfig.getEventTypeName())) {
        populateEmailBodyForAlerts(emailBodyBuilder,strValue,extraInfo,alertEmailEventEntity.get(0),invoiceTypeDescription);
      }
      else if (EventTypeEnum.CRDRG_BALANCEABOVE.name().equalsIgnoreCase(emailConfig.getEventTypeName())) {
        populateEmailBodyForAlerts(emailBodyBuilder,strValue,extraInfo,alertEmailEventEntity.get(0),invoiceTypeDescription);
      }
      else if (EventTypeEnum.CRDRG_DEBIT.name().equalsIgnoreCase(emailConfig.getEventTypeName())) {
        populateEmailBodyForDebit(emailBodyBuilder,strValue,extraInfo,alertEmailEventEntity.get(0));
      }
      else if (EventTypeEnum.NEW_INVOICE_AVAILABLE.name().equalsIgnoreCase(emailConfig.getEventTypeName())) {
        populateEmailBodyForNewInvoice(emailBodyBuilder,strValue,extraInfo,notificationMessage,alertEmailEventEntityForInvoiceAlerts,invoiceTypeDescription);
      }
      else if (EventTypeEnum.INVOICE_PAST_DUE.name().equalsIgnoreCase(emailConfig.getEventTypeName())) {
        populateEmailBodyForInvoicePastDue(emailBodyBuilder,strValue,extraInfo,notificationMessage,alertEmailEventEntityForInvoiceAlerts,invoiceTypeDescription);
      }
      else if (EventTypeEnum.REALLOCATION_INVOICE_SUBMITTED.name().equalsIgnoreCase(emailConfig.getEventTypeName())) {
        populateEmailBodyForInvoiceReallocation(emailBodyBuilder,strValue,paymentTrackingEntity,orgMap,invoiceTypeDescription);
      }
      else if (EventTypeEnum.REALLOCATION_INVOICE_FAILED.name().equalsIgnoreCase(emailConfig.getEventTypeName())) {
        populateEmailBodyForInvoiceReallocation(emailBodyBuilder,strValue,paymentTrackingEntity,orgMap,invoiceTypeDescription);
      }
      else if (EventTypeEnum.REALLOCATION_INVOICE_QUEUED.name().equalsIgnoreCase(emailConfig.getEventTypeName())) {
        populateEmailBodyForInvoiceReallocation(emailBodyBuilder,strValue,paymentTrackingEntity,orgMap,invoiceTypeDescription);
      }
      else if (EventTypeEnum.REALLOCATION_INVOICE_PROCESSED.name().equalsIgnoreCase(emailConfig.getEventTypeName())) {
        populateEmailBodyForInvoiceReallocation(emailBodyBuilder,strValue,paymentTrackingEntity,orgMap,invoiceTypeDescription);
      }
      emailBodyBuilder.append(Constants.BREAK);
    }
  }
  private static void populateEmailBodyForInvoiceReallocation(StringBuilder emailBodyBuilder,
                                                             String strValue, List<PaymentTrackingEntity> paymentTrackingEntity, Map<String, String> orgMap,String invoiceTypeDescription){
    strValue = populateTableForReallocation(strValue,paymentTrackingEntity, orgMap);
    emailBodyBuilder.append(strValue);

  }
  private static void populateEmailBodyForNewInvoice(StringBuilder emailBodyBuilder,
                                                       String strValue,ExtraInfo extraInfo,NotificationMessage notificationMessage,List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts, String invoiceTypeDescription){

    strValue = populateBusinessUnitDescription(strValue,invoiceTypeDescription);
    strValue = populateOrgIDForInvoice(strValue, alertEmailEventEntityForInvoiceAlerts.get(0));
    strValue = populateCustomerName(strValue, extraInfo);
    strValue = populateListofInvoices(strValue, notificationMessage);
    emailBodyBuilder.append(strValue);

  }
  private static void populateEmailBodyForInvoicePastDue(StringBuilder emailBodyBuilder,
                                                       String strValue,ExtraInfo extraInfo, NotificationMessage notificationMessage,List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts ,String invoiceTypeDescription){

    strValue = populateBusinessUnitDescription(strValue,invoiceTypeDescription);
    strValue = populateOrgIDForInvoice(strValue, alertEmailEventEntityForInvoiceAlerts.get(0));
    strValue = populateCustomerName(strValue, extraInfo);
   strValue = populateListofInvoices(strValue, notificationMessage);
    strValue = populateDaysConfigured(strValue, alertEmailEventEntityForInvoiceAlerts);

    emailBodyBuilder.append(strValue);

  }
  private static void populateEmailBodyForAlerts(StringBuilder emailBodyBuilder,
                                                       String strValue,ExtraInfo extraInfo, AlertEmailEventEntity alertEmailEventEntity,String invoiceTypeDescription){

    strValue = populateBusinessUnitDescription(strValue,invoiceTypeDescription);
    strValue = populateOrgID(strValue, alertEmailEventEntity);
    strValue = populateCustomerName(strValue, extraInfo);
    strValue = populateThresholdAmount(strValue, alertEmailEventEntity);
    strValue = populateEstimatedBalance(strValue, alertEmailEventEntity);
    strValue = populateBalanceDate(strValue, alertEmailEventEntity);
    emailBodyBuilder.append(strValue);
  }

  private static void populateEmailBodyForDebit(StringBuilder emailBodyBuilder,
                                                String strValue,ExtraInfo extraInfo,AlertEmailEventEntity alertEmailEventEntity){
    strValue = populateOrgID(strValue, alertEmailEventEntity);
    strValue = populateCustomerName(strValue, extraInfo);
    emailBodyBuilder.append(strValue);

  }
  private static String populateBusinessUnitDescription(String strValue,String invoiceTypeDescription) {
    if(strValue.contains(Constants.BUSINESS_UNIT_LONG_DESCRIPTION)){
      strValue = StringUtils.replace(strValue,Constants.BUSINESS_UNIT_LONG_DESCRIPTION,invoiceTypeDescription);
    }
    return strValue;
  }
  private static String populateDaysConfigured(String strValue,List<AlertEmailEventEntity> alertEmailEventEntityForInvoiceAlerts) {
    if(strValue.contains(Constants.DAYS_CONFIGURED)){
      strValue = StringUtils.replace(strValue,Constants.DAYS_CONFIGURED,alertEmailEventEntityForInvoiceAlerts.get(0).getAlertCategoryperUser().getThresholdValue());
    }
    return strValue;
  }
  private static String populateOrgIDForInvoice(String strValue, AlertEmailEventEntity alertEmailEventEntityForInvoiceAlerts) {
    if(strValue.contains(Constants.ORG_ID)){
      strValue = StringUtils.replace(strValue,Constants.ORG_ID,alertEmailEventEntityForInvoiceAlerts.getOrgID());
    }
    return strValue;
  }
  private static String populateListofInvoices(String strValue,NotificationMessage notificationMessage) {
    if(strValue.contains(Constants.LIST_OF_INVOICES)){
      strValue = StringUtils.replace(strValue,Constants.LIST_OF_INVOICES,Util.invoices(notificationMessage));
    }
    return strValue;

  }
  private static String invoices(NotificationMessage notificationMessage){
    StringBuilder htmlBuilder = new StringBuilder();
   List<String> invoices = notificationMessage.getInvoiceNumber();
    htmlBuilder.append("<ul>");
    for (String invoice : invoices) {
     htmlBuilder.append( "<li>").append(invoice).append("</li>");
    }
    htmlBuilder.append("</ul>");
    log.info("htmlBuilder.toString() :{}", htmlBuilder.toString());
    return htmlBuilder.toString();

  }
  private static String populateOrgID(String strValue, AlertEmailEventEntity alertEmailEventEntity) {
    if(strValue.contains(Constants.ORG_ID)){
      strValue = StringUtils.replace(strValue,Constants.ORG_ID,alertEmailEventEntity.getOrgID());
    }
    return strValue;
  }
  private static String populateCustomerName(String strValue, ExtraInfo extraInfo) {
    if(strValue.contains(Constants.CUSTOMER_NAME)){
      strValue = StringUtils.replace(strValue,Constants.CUSTOMER_NAME,extraInfo.getOrgName());
    }
    return strValue;
  }
  private static String populateThresholdAmount(String strValue, AlertEmailEventEntity alertEmailEventEntity) {
    if(strValue.contains(Constants.THRESHOLD_AMOUNT)){
      strValue = StringUtils.replace(strValue,Constants.THRESHOLD_AMOUNT,Util.populateAmount(alertEmailEventEntity.getExpectedValue()));
    }
    return strValue;
  }
  private static String populateEstimatedBalance(String strValue, AlertEmailEventEntity alertEmailEventEntity) {
    if(strValue.contains(Constants.ESTIMATED_BALANCE)){
      strValue = StringUtils.replace(strValue,Constants.ESTIMATED_BALANCE,Util.populateAmount(alertEmailEventEntity.getActualValue()));
    }
    return strValue;
  }
  private static String populateBalanceDate(String strValue, AlertEmailEventEntity alertEmailEventEntity) {
    if(strValue.contains(Constants.BALANCE_DATE)){
      strValue = StringUtils.replace(strValue,Constants.BALANCE_DATE,convertToEST(alertEmailEventEntity.getCreatedDate()));
    }
    return strValue;
  }
  private static void populateEmailBodyForAFT(StringBuilder emailBodyBuilder,
                                              String strValue, List<PaymentTrackingEntity> paymentTrackingEntity, Map<String, String> orgMap){
    strValue = populateTableData(strValue,paymentTrackingEntity, orgMap);
    emailBodyBuilder.append(strValue);

  }
  private static String populateTableData(String strValue, List<PaymentTrackingEntity> paymentTrackingEntity, Map<String, String> orgMap) {
    if (strValue.contains(Constants.TABLE_DATA)) {
      strValue = StringUtils.replace(strValue,Constants.TABLE_DATA,populateTable(paymentTrackingEntity, orgMap));
    }
    return strValue;
  }

  private static String populateTable ( List<PaymentTrackingEntity> paymentTrackingEntity, Map<String, String> orgMap){
    StringBuilder html = new StringBuilder();
    html.append(Constants.HTML +
            Constants.BODY +
            Constants.TABLE +
            "<tr>");
    html.append(Constants.TRANSACTION_DATE_TABLE +
            "<th>Transaction ID</th>" +
            "<th>From (Org ID)</th>" +
            "<th>To (Org ID)</th>" +
            "<th>Amount</th>" +
            "<th>Transaction Status</th>" +
            "<th>Organization</th>" +
            "</tr>");

    for (PaymentTrackingEntity paymentTracking :paymentTrackingEntity ) {
      html.append("<tr>");
      html.append("<td>").append(convertToEST(paymentTracking.getCreatedDate())).append(Constants.TD);
      html.append("<td>").append(paymentTracking.getPaymentReferenceNumber()).append(Constants.TD);
      html.append("<td>").append(orgMap.get(paymentTracking.getOrgId()).trim()+" "+"("+ (paymentTracking.getOrgId())+")").append(Constants.TD);
      String aftToOrg = orgMap.get(paymentTracking.getAftToOrgId())!=null ? orgMap.get(paymentTracking.getAftToOrgId()).trim():"";
      html.append("<td>").append(aftToOrg+" "+"("+ (paymentTracking.getAftToOrgId())+")").append(Constants.TD);
      html.append("<td>").append(Util.populateAmount(paymentTracking.getPaymentAmount().toString())).append(Constants.TD);
      html.append("<td>").append(PaymentStatusEnum.Processed.getValue().equals(paymentTracking.getPaymentStatusId().toString())
          ? Constants.SUCCESS : PaymentStatusEnum.valueOfPaymentStatus(paymentTracking.getPaymentStatusId().toString())).append(Constants.TD);

      html.append("<td>").append(paymentTracking.getOrgId()+" " +(orgMap.get(paymentTracking.getOrgId()).trim())).append(Constants.TD);
    }
    html.append(Constants.HTMLTAG);
    log.info(Constants.HTML_STRING, html.toString());
    return html.toString();

  }


  public static String generateRefundBody(String emailTemplateFromDb, NotificationMessage refundReq,
      ResponseEntity<EwsAccountInformationResponse> response, List<PaymentTrackingEntity> paymentTrackingEntity, Map<String, String> orgMap) {
    return StringSubstitutor.replace(emailTemplateFromDb,
        buildEmailTemplateVariableKeyValueMap(refundReq, response,paymentTrackingEntity,orgMap), "{", "}");
  }

  // Key value exactly match variable name in email template
  // ie: {ORG_ID} in template
  public static Map<String, String> buildEmailTemplateVariableKeyValueMap(NotificationMessage notifMsg,
      ResponseEntity<EwsAccountInformationResponse> ewUserResp,List<PaymentTrackingEntity> paymentTrackingEntity,Map<String, String> orgMap) {
    Map<String, String> map = new HashMap<>();
    map.put(Constants.REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_AMOUNT, Util.populateAmount(notifMsg.getAmount()));
    map.put(Constants.REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_CUSTOMER_NAME, orgMap.get( paymentTrackingEntity.get(0).getOrgId()));
    map.put(Constants.REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_DATE, convertToEST(paymentTrackingEntity.get(0).getCreatedDate()));
    map.put(Constants.REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_ORG_ID, ewUserResp.getBody().getOrgId());
    map.put(Constants.REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_TRANSACTION_ID,
        notifMsg.getPaymentNumber().get(0));
    map.put(Constants.REFUND_EMAIL_TEMPLATE_VARIABLE_KEY_TRANSACTION_STATUS, notifMsg.getStatus());
    return map;
  }

  public static List<String> parseSemiColonConcatenatedEmails(String emailString) {
    List<String> emails = new ArrayList<>();
    // Empty
    if(StringUtils.isBlank(emailString)) {
      return emails;
    }
    // Single email
    if(emailString.indexOf(Constants.CONST_SEMI_COLON) < 0) {
      emails.add(emailString);
    }else {
      // multi emails separated by ";" or one email with ending ";"
      Arrays.stream(emailString.split(Constants.CONST_SEMI_COLON)).forEach(emails::add);
    }
    return emails;
  }
  private static String populateAmount(String amount) {
    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
    numberFormat.setMinimumFractionDigits(2);
    numberFormat.setMaximumFractionDigits(2);
    return numberFormat.format(new BigDecimal(amount));
  }
  private static String populateTableForReallocation(String strValue, List<PaymentTrackingEntity> paymentTrackingEntity, Map<String, String> orgMap) {
    if (strValue.contains(Constants.TABLE_DATA)) {
      strValue = StringUtils.replace(strValue,Constants.TABLE_DATA,populateTableDataForReallocation(paymentTrackingEntity, orgMap));
    }
    return strValue;
  }
  private static String populateTableDataForReallocation (List<PaymentTrackingEntity> paymentTrackingEntity, Map<String, String> orgMap){
    StringBuilder html = new StringBuilder();
    html.append(Constants.HTML  +
            Constants.BODY +
            Constants.TABLE +
            "<tr>");
    html.append(Constants.TRANSACTION_DATE_TABLE +
            "<th>Transaction ID</th>" +
            "<th>From</th>" +
            "<th>To</th>" +
            "<th>Invoice Number</th>" +
            "<th>Amount</th>" +
            "<th>Transaction Status</th>" +
            "<th>Organization</th>" +
            "</tr>");


    for (PaymentTrackingEntity paymentTracking :paymentTrackingEntity ) {
      html.append("<tr>");
      html.append("<td>").append(convertToEST(paymentTracking.getCreatedDate())).append(Constants.TD);
      html.append("<td>").append(paymentTracking.getPaymentReferenceNumber()).append(Constants.TD);
      html.append("<td>").append(BuCodeEnum.valueOf(paymentTracking.getRalcnSrcBusUn()).getDescription()).append(Constants.TD);
      html.append("<td>").append(BuCodeEnum.valueOf(paymentTracking.getBusinessUnit()).getDescription()).append(Constants.TD);
      html.append("<td>").append(paymentTracking.getInvoiceId()).append(Constants.TD);
      html.append("<td>").append(Util.populateAmount(paymentTracking.getPaymentAmount().toString())).append(Constants.TD);
      html.append("<td>").append(paymentTracking.getPaymentStatusId().toString().equals(PaymentStatusEnum.Processed.getValue())
          ? Constants.SUCCESS : PaymentStatusEnum.valueOfPaymentStatus(paymentTracking.getPaymentStatusId().toString())).append(Constants.TD);

      html.append("<td>").append(paymentTracking.getOrgId()+" " +(orgMap.get(paymentTracking.getOrgId()).trim())).append(Constants.TD);
    }
    html.append(Constants.HTMLTAG);
    log.info(Constants.HTML_STRING, html.toString());
    return html.toString();

  }

  public static String convertToEST(LocalDateTime localDateTime) {
    if (Objects.isNull(localDateTime)) {
      return null;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_BILL_SUBMIT_TIME);
    ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(Constants.UTC_TIME_ZONE));
    return zonedDateTime
            .withZoneSameInstant(ZoneId.of(Constants.EST_TIME_ZONE))
            .toLocalDateTime()
            .format(formatter);
  }

}
