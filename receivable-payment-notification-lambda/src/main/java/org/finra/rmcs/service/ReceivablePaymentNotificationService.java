package org.finra.rmcs.service;

import org.finra.rmcs.model.PaymentNoticeChangeEvent;

public interface ReceivablePaymentNotificationService {

  void updateReceivablePaymentStatus(
      String correlationId, PaymentNoticeChangeEvent paymentNoticeChangeEvent);
}
