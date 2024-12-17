package org.finra.rmcs.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.herd.sdk.model.BusinessObjectFormat;
import org.finra.rmcs.common.service.email.EmailServiceImpl;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.constants.HerdEntity;
import org.finra.rmcs.entity.ReceivableJsonFileEntity;
import org.finra.rmcs.exception.JsonLineCountMisMatchException;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.repository.ReceivableJsonRepository;
import org.finra.rmcs.repository.ReceivableReqRepository;
import org.finra.rmcs.service.ProcessResponseService;
import org.finra.rmcs.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProcessResponseServiceImpl implements ProcessResponseService {

  @Value("${email.error.subject}")
  private String errorEmailSubject;

  @Value("${email.error.body}")
  private String errorEmailBody;

  @Value("${email.error.from}")
  private String errorEmailFrom;

  @Value("${email.error.to}")
  private String errorEmailTo;

  @Value("${email.error.cc}")
  private String errorEmailCC;

  private final ReceivableJsonRepository receivableJsonRepository;
  private final ReceivableReqRepository receivableReqRepository;
  private final HerdServiceImpl herdService;
  private final EmailServiceImpl emailService;

  @Autowired
  public ProcessResponseServiceImpl(
      ReceivableJsonRepository receivableJsonRepository,
      ReceivableReqRepository receivableReqRepository,
      HerdServiceImpl herdService,
      EmailServiceImpl emailService) {
    this.receivableJsonRepository = receivableJsonRepository;
    this.receivableReqRepository = receivableReqRepository;
    this.herdService = herdService;
    this.emailService = emailService;
  }

  @Override
  public Map<String, String> createResponseFile(
      String type,
      String transmissionId,
      String jsonLineCount,
      ReceivableJsonFileEntity receivableJsonFileEntity,
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent) {
    BusinessObjectData businessObjectData =
        herdService.retrieveBusinessObjectData(businessObjectDataStatusChangeEvent);
    Map<String, String> responseFile = new HashMap<>();
    if (type.equalsIgnoreCase(Constants.STATUS_FATAL)) {
      return Map.of(
          Util.generateResponseFileName(Constants.STATUS_FATAL, businessObjectData),
          receivableJsonFileEntity.getMessage());
    } else {
      List<String> okResponseFile =
          receivableReqRepository.findResponsePayloadByTransmissionIdAndStatus(
              transmissionId, "COMPLETED");
      if (!okResponseFile.isEmpty()) {
        String okFileName = Util.generateResponseFileName("ok", businessObjectData);
        responseFile.put(
            okFileName,
            okResponseFile.parallelStream().collect(Collectors.joining(Constants.NEW_LINE)));
      }
      List<String> failResponseFile =
          receivableReqRepository.findResponsePayloadByTransmissionIdAndStatus(
              transmissionId, "ERROR");
      if (!failResponseFile.isEmpty()) {
        String failFileName = Util.generateResponseFileName("fail", businessObjectData);
        responseFile.put(
            failFileName,
            failResponseFile.parallelStream().collect(Collectors.joining(Constants.NEW_LINE)));
      }
      // Validate json_line_count is match with response file
      int totalCountInResponse = okResponseFile.size() + failResponseFile.size();
      if (Integer.parseInt(jsonLineCount) != totalCountInResponse) {
        throw new JsonLineCountMisMatchException(
            String.format(
                "json_line_count is not match with the response file for transmission_id: %s, expected: %s, actual: %s",
                transmissionId, jsonLineCount, totalCountInResponse));
      }

      return responseFile;
    }
  }

  @Override
  public void registerResponseFileToDM(
      String fileName,
      String file,
      HerdEntity herdEntity,
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent) {

    BusinessObjectData businessObjectData = null;
    try {
      log.info("Pre register {} to DM", fileName);
      BusinessObjectFormat businessObjectFormat =
          herdService.getBusinessObjectFormat(businessObjectDataStatusChangeEvent, herdEntity);

      businessObjectData =
          herdService.preRegisterBusinessObjectData(
              businessObjectFormat, businessObjectDataStatusChangeEvent);

      String s3Key = herdService.uploadResponseFileToS3(businessObjectData, file, fileName);

      herdService.updateBusinessObjectDataStorage(businessObjectData);

      String dmStatus =
          herdService.updateBusinessObjectDataStatus(businessObjectData, Constants.DM_STATUS_VALID);

      if (!dmStatus.equals(Constants.DM_STATUS_VALID)) {
        throw new RuntimeException(
            String.format(
                "Failed to update business object data status for businessObjectDefinitionName %s, partitionValue %s, subPartitionValue %s",
                businessObjectData.getBusinessObjectDefinitionName(),
                businessObjectData.getPartitionValue(),
                businessObjectData.getSubPartitionValues().get(0)));
      }

      log.info(
          "Updated Data Status to {} for for businessObjectDefinitionName {}, partitionValue {}, subPartitionValue {}",
          dmStatus,
          businessObjectData.getBusinessObjectDefinitionName(),
          businessObjectData.getPartitionValue(),
          businessObjectData.getSubPartitionValues().get(0));

      log.info("Registered file {} successfully to DM with the S3 path {}", fileName, s3Key);
    } catch (RuntimeException e) {
      log.error(
          "Unexpected exception while trying to register file {}: {}", fileName, e.getMessage());
      if (businessObjectData != null) {
        String updateStatus =
            herdService.updateBusinessObjectDataStatus(
                businessObjectData, Constants.DM_STATUS_INVALID);
        if (!updateStatus.equalsIgnoreCase(Constants.DM_STATUS_INVALID)) {
          throw new RuntimeException(
              String.format(
                  "Failed to invalidate business object data status for businessObjectDefinitionName %s, partitionValue %s, subPartitionValue %s",
                  businessObjectData.getBusinessObjectDefinitionName(),
                  businessObjectData.getPartitionValue(),
                  businessObjectData.getSubPartitionValues().get(0)));
        }
      }
      throw new RuntimeException(e);
    }
  }

  @Override
  public ReceivableJsonFileEntity getReceivableJsonEntityByTransmissionId(String transmissionId) {
    Optional<ReceivableJsonFileEntity> receivableJsonEntity =
        receivableJsonRepository.findByTransmissionId(transmissionId);
    if (receivableJsonEntity.isEmpty()) {
      throw new RuntimeException(
          String.format("ReceivableJsonEntity is null for transmission_id: %s", transmissionId));
    }
    return receivableJsonEntity.isPresent() ? receivableJsonEntity.get() : null;
  }

  @Override
  @SneakyThrows
  public void updateReceivableJsonEntityStatus(
      ReceivableJsonFileEntity receivableJsonFileEntity,
      Map<String, String> responsePayload,
      String status) {
    receivableJsonFileEntity.setStatus(status);
    receivableJsonFileEntity.setResponsePayload(
        new ObjectMapper().writeValueAsString(responsePayload));
    receivableJsonRepository.save(receivableJsonFileEntity);
  }

  @Override
  public void sendErrorNotificationEmail(
      Exception e, String s3Url, String snsMessageId, String transmissionId) {
    String subject;
    String body;
    if (e instanceof JsonLineCountMisMatchException) {
      subject =
          String.format(
              errorEmailSubject, System.getenv(Constants.SPRING_PROFILES_ACTIVE).toUpperCase());
      body =
          String.format(
              errorEmailBody,
              Constants.LAMBDA_NAME,
              e.getMessage(),
              transmissionId,
              snsMessageId,
              s3Url,
              Constants.NEW_LINE + Arrays.toString(e.getStackTrace()));
    } else {
      subject =
          String.format(
              "[%s] - Unexpected error occurred",
              System.getenv(Constants.SPRING_PROFILES_ACTIVE).toUpperCase());
      body =
          String.format(
              "%s happened an unexpected error occurred due to the following error, please investigate it. Thanks!%n%nerror_message: %s%n%ntransmission_id: %s%nsns_message_id: %s%ns3_url: %s%nstack trace: %n%s",
              Constants.LAMBDA_NAME,
              e.getMessage(),
              transmissionId,
              snsMessageId,
              s3Url,
              Constants.NEW_LINE + Arrays.toString(e.getStackTrace()));
    }
    emailService.sendEMail(errorEmailFrom, errorEmailTo, errorEmailCC, subject, body);
  }

  public void sendErrorNotificationEmail(
      String message, String s3Url, String snsMessageId, String transmissionId) {
    String subject =
        String.format(
            errorEmailSubject, System.getenv(Constants.SPRING_PROFILES_ACTIVE).toUpperCase());
    String body =
        String.format(
            errorEmailBody,
            Constants.LAMBDA_NAME,
            message,
            transmissionId,
            snsMessageId,
            s3Url,
            "N/A");
    emailService.sendEMail(errorEmailFrom, errorEmailTo, errorEmailCC, subject, body);
  }
}
