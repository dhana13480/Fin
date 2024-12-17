package org.finra.rmcs.service.impl;

import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.dto.Receivable;
import org.finra.rmcs.dto.ReceivableItem;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.entity.ReceivableItemEntity;
import org.finra.rmcs.entity.RevenueStreamEntity;
import org.finra.rmcs.exception.InvoiceWdException;
import org.finra.rmcs.repo.ReceivableRepo;
import org.finra.rmcs.repo.ReceivableRevenueStreamRepo;
import org.finra.rmcs.service.InvoiceWdService;
import org.finra.rmcs.utils.ReceivableUtil;
import org.finra.rmcs.utils.S3Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

@Slf4j
@Service
public class InvoiceWdServiceImpl implements InvoiceWdService {

  private boolean writeToExternalBucket = false;
  private final String assumeRole;
  private final String externalS3Bucket;
  private final String internalS3Bucket;
  private final String internalKMSKey;
  private final String externalS3KeyPrefix;
  private final String internalS3KeyPrefix;
  private final ReceivableRepo receivableRepo;
  private final ReceivableRevenueStreamRepo revenueStreamRepo;
  private final ReceivableUtil receivableUtil;

  private final String maxInvoiceCount;

  private final String maxLineCount;

  public InvoiceWdServiceImpl(
      @Value("${s3.external.assumeRoleArn}") String assumeRole,
      @Value("${s3.external.bucket}") String externalS3Bucket,
      @Value("${s3.internal.bucket}") String internalS3Bucket,
      @Value("${s3.internal.kms}") String internalKMSKey,
      @Value("${s3.external.keyPrefix}") String externalS3KeyPrefix,
      @Value("${s3.internal.keyPrefix}") String internalS3KeyPrefix,
      ReceivableRepo receivableRepo,
      ReceivableRevenueStreamRepo revenueStreamRepo,
      ReceivableUtil receivableUtil,
      @Value("${json.invoice.count}") String maxInvoiceCount,
      @Value("${json.line.count}") String maxLineCount) {
    this.assumeRole = assumeRole;
    this.externalS3Bucket = externalS3Bucket;
    this.internalS3Bucket = internalS3Bucket;
    this.internalKMSKey = internalKMSKey;
    this.externalS3KeyPrefix = externalS3KeyPrefix;
    this.internalS3KeyPrefix = internalS3KeyPrefix;
    this.receivableRepo = receivableRepo;
    this.revenueStreamRepo = revenueStreamRepo;
    this.receivableUtil = receivableUtil;
    this.maxInvoiceCount = maxInvoiceCount;
    this.maxLineCount = maxLineCount;
  }

  @Override
  @SneakyThrows
  public void sentReceivableInvoiceToWd(String correlationId, String revenueStream) {
    String methodName =
        new StringBuilder()
            .append(Constants.CLASS)
            .append(this.getClass().getSimpleName())
            .append(Constants.SPACE)
            .append(Constants.METHOD)
            .append(Thread.currentThread().getStackTrace()[1].getMethodName())
            .append(Constants.SPACE)
            .append(Constants.CORRELATION_ID)
            .append(correlationId)
            .toString();

    log.info("{} message: method execution started", methodName);

    List<RevenueStreamEntity> revenueStreamEntities = revenueStreamRepo.findAll();

    Map<String, Boolean> sendToWDMap =
        revenueStreamEntities.stream()
            .collect(
                Collectors.toMap(
                    RevenueStreamEntity::getRevenueStreamName, RevenueStreamEntity::isSendToWD));
    Map<String, Boolean> parallelRunMap =
        revenueStreamEntities.stream()
            .collect(
                Collectors.toMap(
                    RevenueStreamEntity::getRevenueStreamName, RevenueStreamEntity::isParallelRun));
      writeToExternalBucket = !parallelRunMap.get(revenueStream);

    if (Boolean.FALSE.equals(sendToWDMap.get(revenueStream))) {
      log.info("{} message: send_to_wd is not enabled for {}", methodName, revenueStream);
      return;
    }

    List<ReceivableEntity> receivableEntityList =
        receivableRepo.getInvoicedReceivable(revenueStream);

    Map<String, Boolean> paymentReceivedMap =
        revenueStreamEntities.stream()
            .collect(
                Collectors.toMap(
                    RevenueStreamEntity::getRevenueStreamName,
                    RevenueStreamEntity::isPaymentValidation));

    if (CollectionUtils.isEmpty(receivableEntityList)) {
      log.info("{} message: No receivables are present which are in invoice state", methodName);
    } else {
      log.info(
          "{} message: The total number of invoice records are {} ",
          methodName,
          receivableEntityList.size());
      List<Receivable> receivableList =
          generateReceivableList(
              receivableEntityList.stream()
                  .filter(item -> validatePaymentField(item, paymentReceivedMap))
                  .collect(Collectors.toList()));

      if (CollectionUtils.isEmpty(receivableList)) {
        log.info("{} message: No receivables are present which are in valid status", methodName);
        return;
      }

      List<Receivable> finalReceivableList = calcReceivableList(receivableList, methodName);

      List<List<Receivable>> receivableSubSets =
          Lists.partition(
              finalReceivableList,
              (int)
                  Math.ceil(
                      finalReceivableList.size()
                          / Math.ceil(
                          finalReceivableList.size() / (Double.parseDouble(maxInvoiceCount)))));
      for (List<Receivable> receivableSubSet : receivableSubSets) {
        List<String> jsonLines = receivableUtil.getJsonLines(receivableSubSet);
        if (jsonLines.size() < 2) {
          log.info("{} message:jsonLines are empty with No data {}", methodName, jsonLines);
          throw new InvoiceWdException(Constants.ERROR_OCCURRED_CONVERTING_RECEIVABLE_DATA);
        }
        String collectedLines = jsonLines.parallelStream().collect(Collectors.joining("\n"));
        try {
          // Write file to S3 bucket
          this.writeFilesToS3Bucket(correlationId, collectedLines, revenueStream);

          Set<UUID> receivableIds =
              receivableSubSet.parallelStream().map(Receivable::getId).collect(Collectors.toSet());
          int recordUpdated = receivableRepo.updateReceivablesFromInvoicedToSendToWD(receivableIds);
          for (List<Receivable> receivables : receivableSubSets) {
            for (Receivable receivable : receivables) {
              log.info(
                  Constants.REGULAR_EVENT_LOG_FORMAT,
                  Constants.LAMBDA_NAME,
                  methodName,
                  Constants.EVENT_SENT_TO_WD_RECEIVABLE,
                  correlationId,
                  receivable.getInvoiceId(),
                  receivable.getRevenueStream());
            }
          }
          log.info("{} message:Total Records Updated {}", methodName, recordUpdated);
        } catch (Exception e) {
          for (List<Receivable> receivables : receivableSubSets) {
            for (Receivable receivable : receivables) {
              log.info(
                  Constants.REGULAR_EVENT_LOG_FORMAT,
                  Constants.LAMBDA_NAME,
                  methodName,
                  Constants.EVENT_ERROR_SENT_TO_WD_RECEIVABLE,
                  correlationId,
                  receivable.getInvoiceId(),
                  receivable.getRevenueStream());
            }
          }
          throw new RuntimeException(e);
        }
      }
    }
  }

  private List<Receivable> generateReceivableList(List<ReceivableEntity> receivableEntities) {
    return receivableEntities.parallelStream()
        .map(
            receivableEntity -> {
              Receivable receivable = receivableUtil.convertReceivableEntityToDto(receivableEntity);
              List<ReceivableItemEntity> receivableItemEntities =
                  receivableEntity.getReceivableItems();
              List<ReceivableItem> receivableItemList =
                  receivableItemEntities.parallelStream()
                      .map(
                          receivableItemEntity -> {
                            ReceivableItem receivableItem =
                                receivableUtil.convertReceivableItemEntityToDto(
                                    receivableItemEntity, receivableEntity.getInvoiceId());
                            receivableItem.setSourceId(receivableEntity.getInvoiceId());
                            return receivableItem;
                          })
                      .collect(Collectors.toList());
              receivable.setLines(receivableItemList);
              return receivable;
            })
        .collect(Collectors.toList());
  }

  public boolean validatePaymentField(
      ReceivableEntity receivable, Map<String, Boolean> revenueStreamMap) {
    String rs =
        StringUtils.isNotBlank(receivable.getProcessingRevenueStream())
            ? receivable.getProcessingRevenueStream()
            : receivable.getRevenueStream();

    if (revenueStreamMap.get(rs)) {
      return Constants.PAYMENT_RECEIVED_FLAG.equalsIgnoreCase(receivable.getPaymentReceived());
    }
    return true;
  }

  public List<Receivable> calcReceivableList(List<Receivable> receivableList, String methodName) {
    return receivableList.parallelStream()
        .map(
            receivable -> {
              List<Receivable> intermediateReceivableList = new ArrayList<>();
              if (receivable.getLines().size() > Integer.parseInt(maxLineCount)) {
                List<List<ReceivableItem>> receivableItemSubsets =
                    Lists.partition(
                        receivable.getLines(),
                        (int)
                            Math.ceil(
                                receivable.getLines().size()
                                    / Math.ceil(
                                    receivable.getLines().size()
                                        / (Double.parseDouble(maxLineCount)))));
                for (List<ReceivableItem> receivableItemSubset : receivableItemSubsets) {
                  Receivable receivableTemp;
                  try {
                    receivableTemp = (Receivable) receivable.clone();
                  } catch (CloneNotSupportedException exception) {
                    log.error(
                        "{} message:Error while cloning receivable {} for receivable id {} ",
                        methodName,
                        exception.getStackTrace(),
                        receivable.getId());
                    throw new InvoiceWdException(
                        receivable.getId() + Constants.SPACE + exception.getMessage());
                  }
                  receivableTemp.setLines(receivableItemSubset);
                  receivableTemp.setTotalLine(receivableItemSubset.size());
                  intermediateReceivableList.add(receivableTemp);
                }
              } else {
                intermediateReceivableList.add(receivable);
              }
              return intermediateReceivableList;
            })
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  /// CLOVER:OFF
  @Override
  public String testOperationsForExternalBucket(Map<String, Object> input) {
    String response = null;
    final String APIBI_REVENUE_STREAM = "APIBI";

    String methodName =
        new StringBuilder()
            .append(Constants.CLASS)
            .append(this.getClass().getSimpleName())
            .append(Constants.SPACE)
            .append(Constants.METHOD)
            .append(Thread.currentThread().getStackTrace()[1].getMethodName())
            .append(Constants.SPACE)
            .append(Constants.CORRELATION_ID)
            .append(Constants.TESTING_1)
            .toString();

    log.info("{} message: method entry", methodName);

    String revenueStream =
        input.getOrDefault(Constants.REVENUE_STREAM, APIBI_REVENUE_STREAM).toString();
    List<RevenueStreamEntity> revenueStreamEntities = revenueStreamRepo.findAll();

    Map<String, Boolean> parallelRunMap =
        revenueStreamEntities.stream()
            .collect(
                Collectors.toMap(
                    RevenueStreamEntity::getRevenueStreamName, RevenueStreamEntity::isParallelRun));
    writeToExternalBucket = !parallelRunMap.get(revenueStream);

    log.info("revenue stream: {} and writeToExternalBucket: {}", revenueStream, writeToExternalBucket);

    Entry<String, Long> latestEntry = null;
    String env = System.getenv(Constants.SPRING_PROFILES_ACTIVE);
    String prefix =
        writeToExternalBucket
            ? input.getOrDefault(Constants.PREFIX, externalS3KeyPrefix).toString()
            : input.getOrDefault(Constants.PREFIX, Constants.RMCS_AUTOMATION_TO_WORKDAY).toString();
    String bucket = writeToExternalBucket ? externalS3Bucket : internalS3Bucket;

    try {

      String operation = input.getOrDefault(Constants.OPERATION, Constants.LIST).toString();
      String[] inputFileNameKeys =
          input
              .getOrDefault(Constants.FILE_NAMES, Constants.BLANK_STRING)
              .toString()
              .split(Constants.COMMA);

      S3Client externalS3Client = S3Util.getExternalS3Client(assumeRole);
      S3Client localClient = S3Util.getInternalS3Client();

      ListObjectsV2Response objects = null;

      switch (operation) {
        case Constants.LIST: {
          List<String> objectKeys = new ArrayList<>();
          log.info("TESTING LIST FILES IN EXTERNAL BUCKET");
          Long millis = (Long) input.getOrDefault(Constants.DATE, 1674318359000L);
          Date inputDate = new Date(millis);
          ListObjectsV2Request req =
              ListObjectsV2Request.builder().bucket(bucket).prefix(prefix).build();

          ListObjectsV2Iterable listObjectsV2Responses =
              externalS3Client.listObjectsV2Paginator(req);

          response = addObjectKeysOperationList(listObjectsV2Responses, objectKeys, inputDate);

          break;
        }

        case Constants.LATEST: {
          log.info("TESTING LATEST FILE NAME IN EXTERNAL BUCKET");
          ListObjectsV2Request req =
              ListObjectsV2Request.builder().bucket(bucket).prefix(prefix).build();

          ListObjectsV2Iterable listObjectsV2Responses =
              externalS3Client.listObjectsV2Paginator(req);

          response = addObjectKeysOperationLatest(listObjectsV2Responses, latestEntry);
          break;
        }

        case Constants.CLEANUP: {
          log.info("TESTING CLEAN OBJECTS IN EXTERNAL BUCKET");
          log.info("bucket name: " + bucket);
          log.info("prefix: " + prefix);
          ListObjectsV2Request req =
              ListObjectsV2Request.builder().bucket(bucket).prefix(prefix).build();

          objects = externalS3Client.listObjectsV2(req);

          if (!env.equalsIgnoreCase(Constants.PROD)) {
            objects.contents().parallelStream()
                .forEach(
                    s3Object -> {
                      log.info("deleting s3 object: " + s3Object.key());
                      externalS3Client.deleteObject(
                          DeleteObjectRequest.builder()
                              .bucket(bucket)
                              .key(s3Object.key())
                              .build());
                    });
          }

          objects = externalS3Client.listObjectsV2(req);
          response =
              String.valueOf(
                  String.format(Constants.OBJECT_SIZE_AFTER_DELETE, objects.contents().size()));

          break;
        }
        case Constants.COPY: {
          log.info("TESTING COPY FILES FROM EXTERNAL BUCKET TO INTERNAL BUCKET");
          String bucketToPaste = null;
          if (!env.equalsIgnoreCase(Constants.PROD)) {
            bucketToPaste = internalS3Bucket;
          }

          String localBucket =
              input.getOrDefault(Constants.LOCAL_BUCKET, bucketToPaste).toString();
          String localPrefix =
              input.getOrDefault(Constants.LOCAL_PREFIX, internalS3KeyPrefix).toString();
          Arrays.stream(inputFileNameKeys)
              .forEach(
                  fileName -> {
                    GetObjectRequest getObjectRequest =
                        GetObjectRequest.builder().key(fileName).bucket(bucket).build();
                    ResponseBytes<GetObjectResponse> s3Object =
                        externalS3Client.getObjectAsBytes(getObjectRequest);

                    String[] keyParts = fileName.split(Constants.SLASH);
                    String toKey = localPrefix + keyParts[keyParts.length - 1];

                    PutObjectRequest putObjectRequest =
                        PutObjectRequest.builder()
                            .bucket(localBucket)
                            .serverSideEncryption(ServerSideEncryption.AWS_KMS)
                            .ssekmsKeyId(internalKMSKey)
                            .key(toKey)
                            .build();
                    localClient.putObject(
                        putObjectRequest, RequestBody.fromBytes(s3Object.asByteArray()));
                  });

          response = Constants.SUCCESS;
          break;
        }

        default: {
          throw new IllegalStateException(Constants.UNEXPECTED_VALUE + operation);
        }
      }
    } catch (Exception e) {
      throw new InvoiceWdException(Constants.TESTING_1 + Constants.SPACE + e.getMessage());
    }

    return response;
  }

  public String addObjectKeysOperationList(
      ListObjectsV2Iterable listObjectsV2Responses, List<String> objectKeys, Date inputDate) {
    for (ListObjectsV2Response page : listObjectsV2Responses) {
      log.info("s3 objects size: " + page.contents().size());
      for (S3Object object : page.contents()) {
        log.info("s3 object: " + object.key());
        if (object.lastModified().isAfter(inputDate.toInstant())) {
          log.info(object.key() + "\t" + object.lastModified());
          objectKeys.add(object.key());
        }
      }
    }

    return String.join(",", objectKeys);
  }

  public String addObjectKeysOperationLatest(
      ListObjectsV2Iterable listObjectsV2Responses, Entry<String, Long> latestEntry) {
    Map<String, Long> map = new HashMap<>();
    String res = null;
    Date lastUpdatedTime = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L);
    for (ListObjectsV2Response page : listObjectsV2Responses) {
      log.info("s3 objects size: " + page.contents().size());
      log.info("last updated time: " + lastUpdatedTime.toInstant().toString());
      for (S3Object object : page.contents()) {
        log.info("s3 object: " + object.key());
        if (object.lastModified().isAfter(lastUpdatedTime.toInstant())) {
          log.info(object.key() + "\t" + object.lastModified());
          map.put(object.key(), object.lastModified().toEpochMilli());
        }
      }
    }

    if (map.size() != 0) {
      latestEntry =
          map.entrySet().stream()
              .max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1)
              .orElse(null);
    }

    if (latestEntry != null) {
      res = String.valueOf(latestEntry.getValue());
    } else {
      res = String.valueOf(0);
    }
    return res;
  }
  /// CLOVER:ON

  private void writeFilesToS3Bucket(String correlationId, String collectedLines,
      String revenueStream) {
    String methodName =
        new StringBuilder()
            .append(Constants.CLASS)
            .append(this.getClass().getSimpleName())
            .append(Constants.SPACE)
            .append(Constants.METHOD)
            .append(Thread.currentThread().getStackTrace()[1].getMethodName())
            .append(Constants.SPACE)
            .append(Constants.CORRELATION_ID)
            .append(correlationId)
            .toString();

    String s3FileName =
        String.format(
            Constants.S3_FILENAME_FORMAT,
            String.format(Constants.S3_FILENAME_PREFIX, revenueStream),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT)),
            Constants.S3_FILENAME_EXTENSION);

    if (writeToExternalBucket) {
      String key = String.format(Constants.S3_KEY_FORMAT, externalS3KeyPrefix, s3FileName);
      log.info(
          "{} : Writing file {} to external bucket {} with key {}",
          methodName,
          s3FileName,
          externalS3Bucket,
          key);
      S3Client externalS3Client = S3Util.getExternalS3Client(assumeRole);
      PutObjectRequest putObjectRequest =
          PutObjectRequest.builder().bucket(externalS3Bucket).key(key).build();
      externalS3Client.putObject(putObjectRequest, RequestBody.fromString(collectedLines));
    } else {
      String key = String.format(Constants.S3_KEY_FORMAT, internalS3KeyPrefix, s3FileName);
      log.info(
          "{} : Writing file {} to internal bucket {} with key {}",
          methodName,
          s3FileName,
          internalS3Bucket,
          key);
      S3Client internalS3Client = S3Util.getInternalS3Client();
      PutObjectRequest putObjectRequest =
          PutObjectRequest.builder()
              .serverSideEncryption(ServerSideEncryption.AWS_KMS)
              .ssekmsKeyId(internalKMSKey)
              .bucket(internalS3Bucket)
              .key(key)
              .build();
      internalS3Client.putObject(putObjectRequest, RequestBody.fromString(collectedLines));
    }
  }
}
