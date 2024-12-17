package org.finra.rmcs.exception;

public class InvoiceWdException extends RuntimeException {

  public InvoiceWdException(String message) {
    super(message);
  }

  public InvoiceWdException(String message, Throwable cause) {
    super(message, cause);
  }
}
