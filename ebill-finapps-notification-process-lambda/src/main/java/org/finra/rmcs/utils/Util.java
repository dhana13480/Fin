package org.finra.rmcs.utils;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.FileTypeEnum;
import org.finra.rmcs.dto.BusinessObjectData;
import org.finra.rmcs.dto.DmNotification;
import org.finra.rmcs.dto.StorageUnit;
import org.finra.rmcs.entity.InvoiceDetailFileEntity;
import org.finra.rmcs.entity.InvoiceSummaryFileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Util {

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
  @SneakyThrows
  public static boolean isDryRun(SQSEvent sqsEvent) {
    boolean isDryRun = false;
    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode messageBodyNode = mapper.readTree(sqsRecord.getBody());
    JsonNode messageNode = messageBodyNode.get(Constants.SQS_MESSAGE);
    String healthPayload = messageNode.textValue()
        .replaceAll(Constants.TEXT_ONLY_PATTERN, "");

    if (healthPayload.equalsIgnoreCase(Constants.DRY_RUN)) {
      isDryRun = true;
    }
    return isDryRun;
  }
  @SneakyThrows
  public static boolean isHealthCheck(SQSEvent sqsEvent) {
    boolean isHealthFlag = false;

    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode messageBodyNode = mapper.readTree(sqsRecord.getBody());
    JsonNode messageNode = messageBodyNode.get(Constants.SQS_MESSAGE);
    String healthPayload = messageNode.textValue()
        .replaceAll(Constants.TEXT_ONLY_PATTERN, "");

    if (healthPayload.equalsIgnoreCase(Constants.HEALTH_PAYLOAD)) {
      isHealthFlag = true;
    }
    return isHealthFlag;
  }

  public static SQSEvent getSqsEvent(String queueMessage) {
    SQSEvent sqsEvent = new SQSEvent();
    SQSMessage sqsMessage = new SQSMessage();
    sqsMessage.setBody(queueMessage);
    List<SQSMessage> events = new ArrayList<>();
    events.add(sqsMessage);
    sqsEvent.setRecords(events);

    return sqsEvent;
  }

  public static void populateReturnMap(
      String status, String details, Map<String, Object> returnMap) {
    returnMap.put(Constants.COMPLETION_STATUS, status);
    returnMap.put(Constants.COMPLETION_DETAILS, details);
  }

  public static DmNotification getNotificationMessage(SQSEvent sqsEvent)
      throws JsonProcessingException {
    SQSEvent.SQSMessage sqsRecord = sqsEvent.getRecords().get(0);
    JsonNode msgBodyNode = objectMapper.readTree(sqsRecord.getBody());
    String payload = msgBodyNode.get(Constants.SQS_EVENT_MESSAGE).asText();
    return objectMapper.readValue(payload, DmNotification.class);
  }

  public static boolean isPdfFileTypeNotification(DmNotification dmNotification){
    if(dmNotification.getNewBusinessObjectDataStatus().equals(Constants.VALID_BUSINESS_OBJECT_STATUS) &&
         dmNotification.getBusinessObjectDataKey().getBusinessObjectDataVersion() !=null &&
            dmNotification.getBusinessObjectDataKey().getBusinessObjectFormatFileType().equalsIgnoreCase(FileTypeEnum.PDF.name())
    ) {
      log.info("PDF Notification to be persisted");
      return true;
    }
    log.info("Notification not to be persisted");
    return false;
  }

  public static boolean isValidGZorZipFileTypeNotification(DmNotification dmNotification){
    if(
        dmNotification.getNewBusinessObjectDataStatus().equals(Constants.VALID_BUSINESS_OBJECT_STATUS) &&
            dmNotification.getBusinessObjectDataKey().getBusinessObjectDataVersion() !=null &&
            isFileTypeGzOrZip(dmNotification.getBusinessObjectDataKey().getBusinessObjectFormatFileType())
    ) {
      log.info("{}} Notification to be persisted", dmNotification.getBusinessObjectDataKey().getBusinessObjectFormatFileType());
      return true;
    }
    log.info("Notification not to be persisted");
    return false;
  }

  public static boolean isFileTypeGzOrZip(String fileType) {
    if(FileTypeEnum.GZ.name().equalsIgnoreCase(fileType) || FileTypeEnum.ZIP.name().equalsIgnoreCase(fileType))
    {
      log.info("GZ or ZIP file notification");
      return true;
    }
    log.info("Not GZ or ZIP file notification");
    return false;
  }

  public static InvoiceSummaryFileEntity convertDmNotificationMessageToInvoiceSummaryFileEntity(
          InvoiceSummaryFileEntity invoiceSummaryFileEntity, DmNotification notificationMessage, BusinessObjectData dmFileInformation) {
    log.info("start convertDmNotificationMessageToInvoiceSummaryFileEntity");
    if(null!= invoiceSummaryFileEntity){
      invoiceSummaryFileEntity.setVersion(notificationMessage.getBusinessObjectDataKey().getBusinessObjectDataVersion());
      invoiceSummaryFileEntity.setUpdatedDate(notificationMessage.getEventDate());
     invoiceSummaryFileEntity.setUpdatedBy(Constants.LAMBDA_SERVICE_NAME);
    }else{
      invoiceSummaryFileEntity = new InvoiceSummaryFileEntity();
      invoiceSummaryFileEntity.setInvoiceNumber(notificationMessage.getBusinessObjectDataKey().getPartitionValue());
      invoiceSummaryFileEntity.setVersion(notificationMessage.getBusinessObjectDataKey().getBusinessObjectDataVersion());
      invoiceSummaryFileEntity.setCreatedDate(notificationMessage.getEventDate());
      invoiceSummaryFileEntity.setCreatedBy(Constants.LAMBDA_SERVICE_NAME);
      invoiceSummaryFileEntity.setUpdatedDate(notificationMessage.getEventDate());
      invoiceSummaryFileEntity.setUpdatedBy(Constants.LAMBDA_SERVICE_NAME);
    }
    if (null != dmFileInformation) {
      Map<String, String> fileInfoMap = getFileNameFromDmFileInformation(dmFileInformation);
      invoiceSummaryFileEntity.setFileName(fileInfoMap.get(Constants.FILE_NAME));
      invoiceSummaryFileEntity.setFilePath(fileInfoMap.get(Constants.FILE_PATH));
    }
    log.info("completed convertDmNotificationMessageToInvoiceSummaryFileEntity with return object:{}", invoiceSummaryFileEntity);
    return invoiceSummaryFileEntity;
  }

  public static Map<String, String> getFileNameFromDmFileInformation(BusinessObjectData dmFileInformation) {
    Map<String, String> fileInfoMap = new HashMap<>();
    if(null!=dmFileInformation.getStorageUnits() && !dmFileInformation.getStorageUnits().isEmpty()){
      StorageUnit storageUnit = dmFileInformation.getStorageUnits().get(0);
      if(null!=storageUnit.getStorageFiles() && !storageUnit.getStorageFiles().isEmpty()){
        String filePath = storageUnit.getStorageFiles().get(0).getFilePath();
        String fileName = FilenameUtils.getName(filePath);
        fileInfoMap.put(Constants.FILE_NAME,fileName);
        fileInfoMap.put(Constants.FILE_PATH,filePath);
      }
    }else{
      fileInfoMap.put(Constants.FILE_NAME,"");
      fileInfoMap.put(Constants.FILE_PATH,"");
    }
    return fileInfoMap;
  }


  public static InvoiceDetailFileEntity convertDmNotificationMessageToInvoiceDetailsFileEntity(
          InvoiceDetailFileEntity invoiceDetailFileEntity, DmNotification notificationMessage, BusinessObjectData dmFileInformation) {
    log.info("start convertDmNotificationMessageToInvoiceDetailsFileEntity");
    String fileName = generateFileName(notificationMessage);
    log.info("generated fileName: {}", fileName);
    if(null!= invoiceDetailFileEntity){
      invoiceDetailFileEntity.setVersion(notificationMessage.getBusinessObjectDataKey().getBusinessObjectDataVersion());
      invoiceDetailFileEntity.setUpdatedDate(notificationMessage.getEventDate());
      invoiceDetailFileEntity.setUpdatedBy(Constants.LAMBDA_SERVICE_NAME);
    }else{
      invoiceDetailFileEntity = new InvoiceDetailFileEntity();
      invoiceDetailFileEntity.setInvoiceNumber(notificationMessage.getBusinessObjectDataKey().getPartitionValue());
      invoiceDetailFileEntity.setFileType(notificationMessage.getBusinessObjectDataKey().getBusinessObjectFormatFileType());
      invoiceDetailFileEntity.setBuCode(notificationMessage.getBusinessObjectDataKey().getBusinessObjectDefinitionName());
      invoiceDetailFileEntity.setSubPartition(getSubPartitionValue(notificationMessage));
      invoiceDetailFileEntity.setVersion(notificationMessage.getBusinessObjectDataKey().getBusinessObjectDataVersion());
      invoiceDetailFileEntity.setCreatedDate(notificationMessage.getEventDate());
      invoiceDetailFileEntity.setCreatedBy(Constants.LAMBDA_SERVICE_NAME);
      invoiceDetailFileEntity.setUpdatedDate(notificationMessage.getEventDate());
      invoiceDetailFileEntity.setUpdatedBy(Constants.LAMBDA_SERVICE_NAME);
    }

    if (null != dmFileInformation) {
      Map<String, String> fileInfoMap = getFileNameFromDmFileInformation(dmFileInformation);
      invoiceDetailFileEntity.setFileName(fileInfoMap.get(Constants.FILE_NAME));
      invoiceDetailFileEntity.setFilepath(fileInfoMap.get(Constants.FILE_PATH));
    }
    log.info("completed convertDmNotificationMessageToInvoiceDetailsFileEntity with return object:{}", invoiceDetailFileEntity);
    return invoiceDetailFileEntity;
  }

  private static String generateFileName(DmNotification notificationMessage) {
    log.info("start generateFileName");
    StringBuilder fileName = new StringBuilder();
    fileName.append(notificationMessage.getBusinessObjectDataKey().getBusinessObjectDefinitionName()).append("-")
        .append(notificationMessage.getBusinessObjectDataKey().getPartitionValue());
    String subPartitionValue = getSubPartitionValue(notificationMessage);
    log.info("subPartitionValue:{}", subPartitionValue);
    if(null!= subPartitionValue){
      fileName.append(Constants.DASH).append(subPartitionValue);
    }
    fileName.append(Constants.DOT+Constants.CSV+Constants.DOT).append(notificationMessage.getBusinessObjectDataKey().getBusinessObjectFormatFileType().toLowerCase());
    log.info("end generateFileName");
    return fileName.toString();
  }

  public static String getSubPartitionValue(DmNotification notificationMessage){
    if(null!= notificationMessage.getBusinessObjectDataKey().getSubPartitionValues() && !notificationMessage.getBusinessObjectDataKey().getSubPartitionValues().isEmpty()){
      return notificationMessage.getBusinessObjectDataKey().getSubPartitionValues().get(0);
    }else{
      return null;
    }
  }

  public static String constructDmUrl(String dmEndPoint, String dmFileNameSpace, DmNotification notificationMessage) {
    StringBuilder url = new StringBuilder(dmEndPoint).append("/businessObjectData/namespaces/").append(dmFileNameSpace)
        .append("/businessObjectDefinitionNames/").append(notificationMessage.getBusinessObjectDataKey().getBusinessObjectDefinitionName())
        .append("/businessObjectFormatUsages/").append(notificationMessage.getBusinessObjectDataKey().getBusinessObjectFormatUsage())
        .append("/businessObjectFormatFileTypes/").append(notificationMessage.getBusinessObjectDataKey().getBusinessObjectFormatFileType())
        .append("?partitionValue=").append(notificationMessage.getBusinessObjectDataKey().getPartitionValue());
    if(null!=notificationMessage.getBusinessObjectDataKey().getSubPartitionValues() && !notificationMessage.getBusinessObjectDataKey().getSubPartitionValues().isEmpty()){
      url.append("&subPartitionValues=").append(notificationMessage.getBusinessObjectDataKey().getSubPartitionValues().get(0));
    }
    url.append("&businessObjectDataVersion=").append(notificationMessage.getBusinessObjectDataKey().getBusinessObjectDataVersion());
    log.info("constructed DM url:{}", url.toString());
    return url.toString();
  }

}
