package org.finra.rmcs.exception;

public class ProcessResponseException extends RuntimeException {

  public ProcessResponseException(String message) {
    super(message);
  }

  public ProcessResponseException(String message, Throwable cause) {
    super(message, cause);
  }
}
