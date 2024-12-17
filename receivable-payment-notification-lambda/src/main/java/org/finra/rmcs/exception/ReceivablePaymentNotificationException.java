package org.finra.rmcs.exception;

public class ReceivablePaymentNotificationException extends RuntimeException {

  public ReceivablePaymentNotificationException(String message) {
    super(message);
  }

  public ReceivablePaymentNotificationException(String message, Throwable cause) {
    super(message, cause);
  }
}
