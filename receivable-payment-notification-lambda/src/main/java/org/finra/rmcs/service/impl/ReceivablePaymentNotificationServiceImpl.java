package org.finra.rmcs.service.impl;

import com.amazonaws.util.CollectionUtils;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.model.PaymentNoticeChangeEvent;
import org.finra.rmcs.repo.ReceivableRepo;
import org.finra.rmcs.service.ReceivablePaymentNotificationService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ReceivablePaymentNotificationServiceImpl
    implements ReceivablePaymentNotificationService {

  private ReceivableRepo receivableRepo;

  @Override
  public void updateReceivablePaymentStatus(
      String correlationId, PaymentNoticeChangeEvent paymentNoticeChangeEvent) {
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
            .append(Constants.SPACE)
            .append(Constants.TRANSMISSION_ID)
            .append(paymentNoticeChangeEvent.getTransmissionId())
            .toString();
    log.info("{} message : Entering method", methodName);
    List<String> validInvoiceIdList =
        paymentNoticeChangeEvent.getInvoiceId().stream().filter(StringUtils::isNotBlank).toList();
    if (!validInvoiceIdList.isEmpty()) {
      List<ReceivableEntity> receivables = receivableRepo.getReceivable(validInvoiceIdList);
      log.info(
          "{} message : Processing invoice_id {}, payment_id {}, processing_revenue_stream {}",
          methodName,
          validInvoiceIdList,
          paymentNoticeChangeEvent.getPaymentId(),
          paymentNoticeChangeEvent.getProcessingRevenueStream());
      List<UUID> ids =
          receivables.stream().map(receivable -> receivable.getId()).collect(Collectors.toList());
      if (!CollectionUtils.isNullOrEmpty(ids)) {
        receivableRepo.updateReceivablesFromInvoicedToSendToWD(Constants.PAYMENT_RECEIVED_Y, ids);
      }
    } else {
      log.info(
          "{} message : Skip processing as invoice_ids is empty, payment_id {}, processing_revenue_stream {}",
          methodName,
          paymentNoticeChangeEvent.getPaymentId(),
          paymentNoticeChangeEvent.getProcessingRevenueStream());
    }
    log.info("{} message : Updated receivable", methodName);
  }
}
