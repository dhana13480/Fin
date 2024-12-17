package org.finra.rmcs.service;

import org.finra.rmcs.dto.BusinessObjectData;
import org.finra.rmcs.dto.DmNotification;

public interface DmFileService {

  public BusinessObjectData getDmFileInformation(String correlationId, DmNotification notificationMessage);

}
