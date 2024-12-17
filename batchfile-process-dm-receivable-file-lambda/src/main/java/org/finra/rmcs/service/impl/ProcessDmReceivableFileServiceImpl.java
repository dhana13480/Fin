package org.finra.rmcs.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.entity.ReceivableJsonFileEntity;
import org.finra.rmcs.exception.ProcessDmReceivableFileException;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;
import org.finra.rmcs.model.Error;
import org.finra.rmcs.repository.ReceivableJsonFileRepository;
import org.finra.rmcs.service.ProcessDmReceivableFileService;
import org.finra.rmcs.util.Util;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ProcessDmReceivableFileServiceImpl implements ProcessDmReceivableFileService {

  private final ReceivableJsonFileRepository receivableJsonFileRepository;

  private final S3ServiceImpl s3Service;

  private final ObjectMapper objectMapper;

  @Override
  @SneakyThrows
  public void upsertEntry(
      String transmissionId,
      String s3Url,
      String revenueStream,
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent,
      Map<String, Object> returnMap) {
    Optional<ReceivableJsonFileEntity> receivableJsonEntity =
        receivableJsonFileRepository.findByTransmissionId(transmissionId);
    returnMap.put(Constants.RE_RUN_KEY, receivableJsonEntity.isPresent());
    if (receivableJsonEntity.isPresent()) {
      ReceivableJsonFileEntity existingEntry = receivableJsonEntity.get();
      existingEntry.setS3FileUrl(s3Url);
      existingEntry.setVersion(existingEntry.getVersion() + 1);
      existingEntry.setStatus(Constants.STATUS_IN_PROGRESS);
      receivableJsonFileRepository.save(existingEntry);
      return;
    }

    ReceivableJsonFileEntity newEntry = new ReceivableJsonFileEntity();
    newEntry.setId(UUID.randomUUID());
    newEntry.setS3FileUrl(s3Url);
    newEntry.setVersion(0);
    newEntry.setRevenueStream(revenueStream);
    newEntry.setDmDataStatusChangeEvent(
        objectMapper.writeValueAsString(businessObjectDataStatusChangeEvent));
    newEntry.setTransmissionId(transmissionId);
    newEntry.setStatus(Constants.STATUS_IN_PROGRESS);
    newEntry.setCreatedBy(Constants.LAMBDA_NAME);
    newEntry.setCreatedDate(
        ZonedDateTime.now(ZoneId.of(Constants.TIME_ZONE_AMERICA_NEW_YORK)).toLocalDateTime());
    receivableJsonFileRepository.save(newEntry);
  }

  @Override
  @SneakyThrows
  public void validateReceivableFile(
      String transmissionId,
      String s3Url,
      String expectedRevenueSteam,
      BusinessObjectData businessObjectData,
      Map<String, Object> returnMap) {
    String methodName =
        Constants.CLASS
            + this.getClass().getSimpleName()
            + " "
            + Constants.METHOD
            + Thread.currentThread().getStackTrace()[1].getMethodName();
    List<Error> errorList = new ArrayList<>();
    // Validate file size
    long fileSize = s3Service.getFileSize(s3Url);
    if (fileSize > Constants.FILE_SIZE_2_GB) {
      log.info("File size is greater than 2 GB for transmission_id {}", transmissionId);
      errorList.add(
          Error.builder()
              .errorCode(Constants.ERROR_CODE_RMCS_012)
              .errorMessage("file size is greater than 2 GB")
              .timeStamp(
                  ZonedDateTime.now(ZoneId.of(Constants.TIME_ZONE_AMERICA_NEW_YORK))
                      .format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
              .build());
      returnMap.put(Constants.TYPE, Constants.FATAL);
      saveErrorMessage(transmissionId, errorList);
      return;
    }

    boolean isMetaDataMatch = true;
    boolean notEmptyFile = true;

    // Validate Meta Data - revenue_stream
    String revenueStreamFromMetaData =
        Util.getMetaDataByKey(businessObjectData, Constants.META_DATA_KEY_REVENUE_STREAM);
    if (StringUtils.isBlank(revenueStreamFromMetaData)
        || !expectedRevenueSteam.equalsIgnoreCase(revenueStreamFromMetaData)) {
      log.info(
          "Revenue stream is not match for transmission_id: {}, expected: {}, actual: {}",
          transmissionId,
          expectedRevenueSteam,
          revenueStreamFromMetaData);
      isMetaDataMatch = false;
      errorList.add(
          Error.builder()
              .errorCode(Constants.ERROR_CODE_RMCS_010)
              .errorMessage(
                  String.format(
                      Constants.META_DATA_IS_NOT_MATCH, Constants.META_DATA_KEY_REVENUE_STREAM))
              .timeStamp(
                  ZonedDateTime.now(ZoneId.of(Constants.TIME_ZONE_AMERICA_NEW_YORK))
                      .format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
              .transmissionId(transmissionId)
              .build());
    }

    // Validate Meta Data - json_line_count
    String receivableS3Data = s3Service.retrieveS3Object(s3Url);
    List<String> receivableList = Arrays.asList(receivableS3Data.split(System.lineSeparator()));
    String jsonLineCountFromMetaData =
        Util.getMetaDataByKey(businessObjectData, Constants.META_DATA_KEY_JSON_LINE_COUNT);
    if (Integer.parseInt(jsonLineCountFromMetaData) == 0 && StringUtils.isBlank(receivableS3Data)) {
      receivableList = new ArrayList<>();
      notEmptyFile = false;
    }
    if (StringUtils.isBlank(jsonLineCountFromMetaData)
        || receivableList.size() != Integer.parseInt(jsonLineCountFromMetaData)) {
      log.info(
          "Row count is not match for transmission_id: {}, meta date: {}, actual: {}",
          transmissionId,
          jsonLineCountFromMetaData,
          receivableList.size());
      isMetaDataMatch = false;
      errorList.add(
          Error.builder()
              .errorCode(Constants.ERROR_CODE_RMCS_010)
              .errorMessage(
                  String.format(
                      Constants.META_DATA_IS_NOT_MATCH, Constants.META_DATA_KEY_JSON_LINE_COUNT))
              .timeStamp(
                  ZonedDateTime.now(ZoneId.of(Constants.TIME_ZONE_AMERICA_NEW_YORK))
                      .format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
              .transmissionId(transmissionId)
              .build());
    }

    // Validate json format
    boolean isJsonFormatCorrect = true;
    List<String> errorIndex = new ArrayList<>();
    for (int i = 0; i < receivableList.size(); i++) {
      try {
        objectMapper.readTree(receivableList.get(i));
      } catch (JsonProcessingException e) {
        isJsonFormatCorrect = false;
        errorIndex.add(String.valueOf(i));
      }
    }

    if (!isJsonFormatCorrect) {
      isMetaDataMatch = false;
      errorList.add(
          Error.builder()
              .errorCode(Constants.ERROR_CODE_RMCS_010)
              .errorMessage(String.format(Constants.ERROR_PARSING_JSON, errorIndex))
              .timeStamp(
                  ZonedDateTime.now(ZoneId.of(Constants.TIME_ZONE_AMERICA_NEW_YORK))
                      .format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
              .transmissionId(transmissionId)
              .build());
    }

    returnMap.put(Constants.META_DATA_KEY_JSON_LINE_COUNT, jsonLineCountFromMetaData);
    returnMap.put(
        Constants.TYPE, isMetaDataMatch && notEmptyFile ? Constants.COMPLETED : Constants.FATAL);
    if (!isMetaDataMatch) {
      log.info(
          Constants.EVENT_FILE_VALIDATION_FAILURE_LOG_FORMAT,
          Constants.LAMBDA_NAME,
          methodName,
          Constants.EVENT_FILE_VALIDATION_FAILURE,
          Constants.SOURCE_FILE_UPLOAD,
          transmissionId,
          expectedRevenueSteam,
          s3Url,
          receivableList.size(),
          errorList);
      saveErrorMessage(transmissionId, errorList);
    } else {
      log.info(
          Constants.EVENT_FILE_VALIDATION_SUCCESS_LOG_FORMAT,
          Constants.LAMBDA_NAME,
          methodName,
          Constants.EVENT_FILE_VALIDATION_SUCCESS,
          Constants.SOURCE_FILE_UPLOAD,
          transmissionId,
          expectedRevenueSteam,
          s3Url,
          receivableList.size());
    }
  }

  @SneakyThrows
  private void saveErrorMessage(String transmissionId, List<Error> errorList) {
    Optional<ReceivableJsonFileEntity> receivableJsonEntity =
        receivableJsonFileRepository.findByTransmissionId(transmissionId);
    if (!receivableJsonEntity.isPresent() || receivableJsonEntity.isEmpty()) {
      throw new ProcessDmReceivableFileException(
          String.format("Invalid Transaction as no transmission_id %s found", transmissionId));
    }
    ReceivableJsonFileEntity entry = receivableJsonEntity.get();
    entry.setMessage(objectMapper.writeValueAsString(errorList));
    entry.setStatus(Constants.STATUS_FATAL);
    receivableJsonFileRepository.save(entry);
  }

  @Override
  @SneakyThrows
  public void handleUnRetryableException(
      String transmissionId,
      String s3Url,
      String revenueStream,
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent,
      Map<String, Object> returnMap) {
    transmissionId =
        StringUtils.isBlank(transmissionId) ? UUID.randomUUID().toString() : transmissionId;
    returnMap.put(Constants.TYPE, Constants.FATAL);
    returnMap.put(Constants.TRANSMISSION_ID_KEY, transmissionId);
    Optional<ReceivableJsonFileEntity> receivableJsonEntity =
        receivableJsonFileRepository.findByTransmissionId(transmissionId);
    String errorMessage =
        objectMapper.writeValueAsString(
            Error.builder()
                .errorCode(Constants.ERROR_CODE_RMCS_010)
                .errorMessage("file corrupted")
                .timeStamp(
                    ZonedDateTime.now(ZoneId.of(Constants.TIME_ZONE_AMERICA_NEW_YORK))
                        .format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT)))
                .transmissionId(transmissionId)
                .build());
    if (receivableJsonEntity.isPresent()) {
      ReceivableJsonFileEntity entry = receivableJsonEntity.get();
      entry.setStatus(Constants.STATUS_FATAL);
      entry.setMessage(errorMessage);
      receivableJsonFileRepository.save(entry);
    } else {
      ReceivableJsonFileEntity newEntry = new ReceivableJsonFileEntity();
      newEntry.setId(UUID.randomUUID());
      newEntry.setS3FileUrl(StringUtils.isNotBlank(s3Url) ? s3Url : Constants.UNKNOWN_VALUE);
      newEntry.setVersion(0);
      newEntry.setRevenueStream(
          StringUtils.isNotBlank(revenueStream) ? revenueStream : Constants.UNKNOWN_VALUE);
      newEntry.setTransmissionId(transmissionId);
      newEntry.setDmDataStatusChangeEvent(
          businessObjectDataStatusChangeEvent != null
              ? objectMapper.writeValueAsString(businessObjectDataStatusChangeEvent)
              : Constants.UNKNOWN_VALUE);
      newEntry.setStatus(Constants.STATUS_FATAL);
      newEntry.setCreatedBy(Constants.LAMBDA_NAME);
      newEntry.setCreatedDate(
          ZonedDateTime.now(ZoneId.of(Constants.TIME_ZONE_AMERICA_NEW_YORK)).toLocalDateTime());
      receivableJsonFileRepository.save(newEntry);
    }
  }
}
