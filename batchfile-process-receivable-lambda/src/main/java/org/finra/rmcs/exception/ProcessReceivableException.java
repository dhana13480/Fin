package org.finra.rmcs.exception;

public class ProcessReceivableException extends RuntimeException {

  public ProcessReceivableException(String message) {
    super(message);
  }

  public ProcessReceivableException(String message, Throwable cause) {
    super(message, cause);
  }
}
