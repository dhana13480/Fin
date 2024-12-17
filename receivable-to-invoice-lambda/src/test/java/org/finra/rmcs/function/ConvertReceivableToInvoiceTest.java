package org.finra.rmcs.function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import org.finra.rmcs.service.impl.InvoiceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class ConvertReceivableToInvoiceTest {

  @Mock
  private InvoiceServiceImpl invoiceService;

  @InjectMocks
  private ConvertReceivableToInvoice convertReceivableToInvoice;

  @Test
  public void getTest() {
    String correlationId = convertReceivableToInvoice.apply(Collections.emptyMap());
    verify(invoiceService).convertReceivableToInvoice(any());
    Assertions.assertFalse(correlationId.isEmpty());
  }
}
