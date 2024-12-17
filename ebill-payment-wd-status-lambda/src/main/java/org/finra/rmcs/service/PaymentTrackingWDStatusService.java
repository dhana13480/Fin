package org.finra.rmcs.service;

import org.finra.rmcs.model.NotificationMessage;

public interface PaymentTrackingWDStatusService {

  void updatePaymentTrackingWDStatus(
      String messageId, NotificationMessage notificationMessage);
}
