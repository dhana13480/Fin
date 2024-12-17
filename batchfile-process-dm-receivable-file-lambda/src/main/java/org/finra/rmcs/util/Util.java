package org.finra.rmcs.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.fidelius.FideliusClient;
import org.finra.herd.sdk.model.Attribute;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.exception.ProcessDmReceivableFileException;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;

@Slf4j
public class Util {

  private Util() {}

  @SneakyThrows
  public static String getPassword(String passwordKey, String component) {
    FideliusClient fideliusClient = new FideliusClient();
    String credPassword =
        fideliusClient.getCredential(
            passwordKey,
            Constants.AGS,
            System.getenv(Constants.SPRING_PROFILES_ACTIVE),
            component,
            null);
    if (StringUtils.isBlank(credPassword)) {
      log.info("Failed to retrieve password of {} from Fidelius", passwordKey);
    } else {
      log.info("Successfully retrieved password of {} from Fidelius", passwordKey);
    }
    return credPassword;
  }

  public static String generateS3Location(BusinessObjectData bizObjData) {
    String filePath = bizObjData.getStorageUnits().get(0).getStorageFiles().get(0).getFilePath();
    Attribute bucketName =
        bizObjData.getStorageUnits().get(0).getStorage().getAttributes().stream()
            .filter(attribute -> attribute.getName().equals(Constants.ATTR_BUCKET_NAME))
            .findAny()
            .orElse(null);

    if (StringUtils.isBlank(filePath) || bucketName == null) {
      throw new ProcessDmReceivableFileException("Invalid filePath or BucketName");
    }

    return String.format(
        "%s%s/%s",
        Constants.DATA_RETRIEVAL_PROTOCOL,
        bucketName.getValue(),
        filePath);
  }

  public static String generateTransmissionId(BusinessObjectData bizObjData) {
    return String.format(
        "%s/%s/%s",
        bizObjData.getPartitionValue(),
        bizObjData.getSubPartitionValues().get(0),
        StringUtils.substringAfterLast(
            bizObjData.getStorageUnits().get(0).getStorageFiles().get(0).getFilePath(), "/"));
  }

  public static String getRevenueStreamFromNameSpace(
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent) {
    return StringUtils.substringAfterLast(
        businessObjectDataStatusChangeEvent.getBusinessObjectDataKey().getNamespace(), "-");
  }

  public static String getMetaDataByKey(BusinessObjectData businessObjectData, String key) {
    return businessObjectData.getAttributes().stream()
        .filter(attribute -> attribute.getName().equalsIgnoreCase(key))
        .findFirst()
        .map(Attribute::getValue)
        .orElse("");
  }
}
