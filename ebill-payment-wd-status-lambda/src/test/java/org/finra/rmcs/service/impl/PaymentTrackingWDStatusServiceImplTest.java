package org.finra.rmcs.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.UUID;
import org.finra.rmcs.dto.Payment;
import org.finra.rmcs.model.NotificationMessage;
import org.finra.rmcs.repo.PaymentTrackingRepo;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
class PaymentTrackingWDStatusServiceImplTest {

  @InjectMocks
  PaymentTrackingWDStatusServiceImpl paymentTrackingWDStatusServiceImpl;

  @Mock
  PaymentTrackingRepo paymentTrackingRepo;

  @Test
  public void test() {
    Payment payment = new Payment();
    payment.setEbillId(UUID.randomUUID().toString());
    payment.setUpdatedDate(LocalDateTime.now());
    NotificationMessage message = NotificationMessage.builder()
        .status("completed").payment(payment).build();
    paymentTrackingWDStatusServiceImpl.updatePaymentTrackingWDStatus("test", message);
    verify(paymentTrackingRepo, times(1)).findById(any());
  }

}