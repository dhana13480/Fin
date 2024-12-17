package org.finra.rmcs.exception;

public class RetryableException extends RuntimeException {

  public RetryableException(String message) {
    super(message);
  }
}
