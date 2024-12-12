package org.finra.rmcs.exception;

public class UnRetryableException extends RuntimeException {

  public UnRetryableException(String message) {
    super(message);
  }
}
