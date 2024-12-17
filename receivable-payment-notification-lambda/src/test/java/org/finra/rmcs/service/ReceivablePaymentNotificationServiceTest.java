package org.finra.rmcs.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.finra.rmcs.entity.ReceivableEntity;
import org.finra.rmcs.model.PaymentNoticeChangeEvent;
import org.finra.rmcs.repo.ReceivableRepo;
import org.finra.rmcs.service.impl.ReceivablePaymentNotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class ReceivablePaymentNotificationServiceTest {

  @Mock
  private ReceivableRepo receivableRepo;

  @InjectMocks
  private ReceivablePaymentNotificationServiceImpl receivablePaymentNotificationService;

  @Test
  public void testUpdateReceivablePaymentStatusY() {
    List<ReceivableEntity> receivables = new ArrayList<>();
    ReceivableEntity receivable = new ReceivableEntity();
    UUID id = UUID.randomUUID();
    receivable.setId(id);
    receivables.add(receivable);
    when(receivableRepo.getReceivable(any())).thenReturn(receivables);
    PaymentNoticeChangeEvent paymentNoticeChangeEvent = new PaymentNoticeChangeEvent();
    paymentNoticeChangeEvent.setEventName("PAID");
    paymentNoticeChangeEvent.setInvoiceId(Arrays.asList("1"));
    receivablePaymentNotificationService.updateReceivablePaymentStatus(
        "", paymentNoticeChangeEvent);
    List<UUID> ids = new ArrayList<>();
    ids.add(id);
    verify(receivableRepo, times(1)).updateReceivablesFromInvoicedToSendToWD("Y", ids);
  }
}
