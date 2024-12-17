package org.finra.rmcs.service;

import java.util.Map;
import org.finra.herd.sdk.model.BusinessObjectData;
import org.finra.rmcs.model.BusinessObjectDataStatusChangeEvent;

public interface ProcessDmReceivableFileService {

  void upsertEntry(
      String transmissionId,
      String s3Url,
      String revenueStream,
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent,
      Map<String, Object> returnMap);

  void validateReceivableFile(
      String transmissionId,
      String s3Url,
      String revenueSteam,
      BusinessObjectData businessObjectData,
      Map<String, Object> returnMap);

  void handleUnRetryableException(
      String transmissionId,
      String s3Url,
      String revenueStream,
      BusinessObjectDataStatusChangeEvent businessObjectDataStatusChangeEvent,
      Map<String, Object> returnMap);
}
