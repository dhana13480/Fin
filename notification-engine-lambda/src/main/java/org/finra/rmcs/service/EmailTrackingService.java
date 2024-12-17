package org.finra.rmcs.service;

import org.finra.rmcs.dto.EmailResponse;
import org.finra.rmcs.dto.GatewayEmailNotificationRequest;
import org.finra.rmcs.model.NotificationMessage;

public interface EmailTrackingService {
  void saveEmailTrackingDetails(
      NotificationMessage notificationMessage, GatewayEmailNotificationRequest request, EmailResponse response );
}
