package org.finra.rmcs.service;

import java.util.Map;
import org.finra.rmcs.constants.HerdEntity;
import org.finra.rmcs.entity.ReceivableJsonFileEntity;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;

public interface ProcessResponseService {

  Map<String, String> createResponseFile(
      String type,
      String transmissionId,
      String jsonLineCount,
      ReceivableJsonFileEntity receivableJsonFileEntity,
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent);

  void registerResponseFileToDM(
      String fileName,
      String file,
      HerdEntity herdEntity,
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent);

  ReceivableJsonFileEntity getReceivableJsonEntityByTransmissionId(String transmissionId);

  void updateReceivableJsonEntityStatus(
      ReceivableJsonFileEntity receivableJsonFileEntity,
      Map<String, String> responsePayload,
      String status);

  void sendErrorNotificationEmail(Exception e, String s3Url, String snsMessageId, String transmissionId);

}
