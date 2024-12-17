package org.finra.rmcs.exception;

import static org.springframework.test.util.AssertionErrors.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
public class InvoiceWdExceptionTest {

  private DummyService dummyService = new DummyService();

  @Test
  public void invoiceWdExceptionTest() {
    RuntimeException origRuntimeEx = new RuntimeException();
    try {
      dummyService.methodThrowingInvoiceWdException("testing", origRuntimeEx);
    } catch (Throwable th) {
      assertTrue(
          "Exception should be of type ConnectDataException", th instanceof InvoiceWdException);
    }

    try {
      dummyService.methodThrowingInvoiceWdException("testing", null);
    } catch (Throwable th) {
      assertTrue(
          "Exception should be of type ConnectDataException", th instanceof InvoiceWdException);
    }
  }

  public class DummyService {

    public void methodThrowingInvoiceWdException(String message, Throwable cause)
        throws InvoiceWdException {
      if (cause != null) {
        throw new InvoiceWdException(message);
      } else {
        throw new InvoiceWdException(message, cause);
      }
    }
  }
}
