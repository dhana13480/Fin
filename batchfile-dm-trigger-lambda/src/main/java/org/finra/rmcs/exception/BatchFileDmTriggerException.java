package org.finra.rmcs.exception;

public class BatchFileDmTriggerException extends RuntimeException {

  public BatchFileDmTriggerException(String message) {
    super(message);
  }

  public BatchFileDmTriggerException(String message, Throwable cause) {
    super(message, cause);
  }
}
