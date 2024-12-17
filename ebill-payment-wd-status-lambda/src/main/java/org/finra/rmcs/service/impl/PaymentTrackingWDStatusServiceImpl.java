package org.finra.rmcs.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finra.rmcs.constants.WorkdayStatusEnum;
import org.finra.rmcs.entity.PaymentTrackingEntity;
import org.finra.rmcs.model.NotificationMessage;
import org.finra.rmcs.repo.PaymentTrackingRepo;
import org.finra.rmcs.service.PaymentTrackingWDStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentTrackingWDStatusServiceImpl implements PaymentTrackingWDStatusService {

  private final PaymentTrackingRepo paymentTrackingRepo;

  @Override
  public void updatePaymentTrackingWDStatus(String messageId,
      NotificationMessage notificationMessage) {

    log.info("PaymentTrackingWDStatusServiceImpl: updatePaymentTrackingWDStatus");
    if (notificationMessage != null && notificationMessage.getPayment() != null
        && notificationMessage.getPayment().getEbillId() != null) {
      Optional<PaymentTrackingEntity> entity = paymentTrackingRepo.findById(
          UUID.fromString(notificationMessage.getPayment().getEbillId()));
      if (entity.isPresent()) {
        PaymentTrackingEntity paymentTrackingEntity = entity.get();
        paymentTrackingEntity.setWdStatus(WorkdayStatusEnum.INITIATED.getStatus());
        paymentTrackingEntity.setSentToWorkdayTimestamp(LocalDate.now(ZoneId.of("America/New_York")));
        paymentTrackingRepo.save(paymentTrackingEntity);
      }
      log.info("Updated successfully for messageId {} with payment id: {}",
          messageId, notificationMessage.getPayment().getId());
    }
  }

}
