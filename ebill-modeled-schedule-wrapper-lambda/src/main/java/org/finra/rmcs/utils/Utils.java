package org.finra.rmcs.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.dto.NotificationMessage;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class Utils {

  public NotificationMessage constructSNSMessage(
      String correlationId, String module, String status) {

    NotificationMessage notificationMessage = new NotificationMessage();
      notificationMessage.setModule(module);
      notificationMessage.setStatus(status);
      notificationMessage.setTransmissionId(
        correlationId != null ? correlationId : StringUtils.EMPTY);
    return notificationMessage;
   }
}

