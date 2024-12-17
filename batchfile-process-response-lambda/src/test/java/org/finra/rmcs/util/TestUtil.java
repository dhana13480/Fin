package org.finra.rmcs.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.herd.sdk.model.Attribute;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.herd.sdk.model.Storage;
import org.finra.herd.sdk.model.StorageDirectory;
import org.finra.herd.sdk.model.StorageFile;
import org.finra.herd.sdk.model.StorageUnit;
import org.finra.rmcs.constants.Constants;
import org.slf4j.LoggerFactory;

@Slf4j
public class TestUtil {

  public static String getResourceFileContents(String fileName) throws Exception {
    try {
      InputStream is = TestUtil.class.getResourceAsStream(fileName);
      InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
      BufferedReader reader = new BufferedReader(streamReader);
      StringBuilder buffer = new StringBuilder();
      for (String line; (line = reader.readLine()) != null; ) {
        buffer.append(line).append("\n");
      }
      return buffer.toString();
    } catch (Exception e) {
      log.error(String.format(" ### Error Reading Test File: %s ###", fileName), e);
      throw e;
    }
  }

  public static BusinessObjectData getBusinessObjectData(boolean isValid) {
    List<StorageUnit> storageUnits = new ArrayList<>();
    List<StorageFile> storageFiles = new ArrayList<>();
    StorageFile storageFile = new StorageFile();
    storageFile.setFilePath(
        "rmcs-apibi/dapi/billing/jsonl/rmcs-apibi-receivables-in/schm-v0/data-v0/upload-dt%3D2023-03-15/uuid%3D920b633d-3f43-479c-842a-631ff319a65f/apibi_2023-03-15T11-50-00-246Z.jsonl");
    storageFiles.add(storageFile);
    StorageUnit storageUnit = new StorageUnit();
    storageUnit.setStorageFiles(storageFiles);
    StorageDirectory storageDirectory = new StorageDirectory();
    storageDirectory.setDirectoryPath(isValid ? "testPath" : StringUtils.EMPTY);
    storageUnit.setStorageDirectory(storageDirectory);
    storageUnits.add(storageUnit);
    Storage storage = new Storage();
    List<Attribute> storageAttributes = new ArrayList<>();
    Attribute bucketName = new Attribute();
    bucketName.setName(isValid ? Constants.ATTR_BUCKET_NAME : StringUtils.EMPTY);
    bucketName.setValue("4652-5751-2377-datamgt-rmcs-kms");
    Attribute kms = new Attribute();
    kms.setName(isValid ? Constants.ATTR_KMS_KEY_ID : StringUtils.EMPTY);
    kms.setValue("kmsKey");
    storageAttributes.add(bucketName);
    storageAttributes.add(kms);
    storage.setAttributes(storageAttributes);
    storageUnit.setStorage(storage);
    BusinessObjectData businessObjectData = new BusinessObjectData();
    businessObjectData.setStorageUnits(storageUnits);
    businessObjectData.setBusinessObjectDefinitionName("RMCS-APIBI-RECEIVABLES-IN");
    businessObjectData.setPartitionValue("2023-03-15");
    businessObjectData.setSubPartitionValues(
        Arrays.asList("D920b633d-3f43-479c-842a-631ff319a65f"));
    businessObjectData.setStorageUnits(storageUnits);
    return businessObjectData;
  }

  public static Appender setUpLogMonitorForClass(Class<?> clazz) {
    Appender appender = new Appender();
    Logger logger = (Logger) LoggerFactory.getLogger(clazz);
    logger.addAppender(appender);
    appender.start();
    return appender;
  }

  @Getter
  public static class Appender extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> events = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
      events.add(iLoggingEvent);
    }
  }
}
