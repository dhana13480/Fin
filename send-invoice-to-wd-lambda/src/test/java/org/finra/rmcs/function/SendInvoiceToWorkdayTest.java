package org.finra.rmcs.function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import org.finra.rmcs.constants.Constants;
import org.finra.rmcs.exception.InvoiceWdException;
import org.finra.rmcs.service.InvoiceWdService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class SendInvoiceToWorkdayTest {

  @Mock
  InvoiceWdService invoiceWdService;

  @InjectMocks
  SendInvoiceToWorkday sendInvoiceToWorkday;

  @Test
  public void applySuccessTest() {
    Map<String, Object> input = new HashMap<>();
    input.put(Constants.REVENUE_STREAM, "APIBI");
    String correlationId = sendInvoiceToWorkday.apply(input);
    verify(invoiceWdService).sentReceivableInvoiceToWd(any(), any());
  }

  @Test
  public void applySuccessTestForRevenueStreamNull() {
    Map<String, Object> input = new HashMap<>();
    input.put(Constants.REVENUE_STREAM, null);
    String correlationId = sendInvoiceToWorkday.apply(input);
    verify(invoiceWdService).sentReceivableInvoiceToWd(any(), any());
  }

  @Test
  public void applyTestingTest() {
    Map<String, Object> input = new HashMap<>();
    input.put(Constants.TESTING, true);
    String correlationId = sendInvoiceToWorkday.apply(input);
    verify(invoiceWdService, never()).sentReceivableInvoiceToWd(any(), any());
  }

  @Test
  public void applyExceptionTest() {
    doThrow(new RuntimeException()).when(invoiceWdService).sentReceivableInvoiceToWd(any(), any());
    Map<String, Object> input = new HashMap<>();
    input.put(Constants.REVENUE_STREAM, "APIBI");
    Assertions.assertThrows(InvoiceWdException.class, () -> {
      sendInvoiceToWorkday.apply(input);
    });

  }
}
