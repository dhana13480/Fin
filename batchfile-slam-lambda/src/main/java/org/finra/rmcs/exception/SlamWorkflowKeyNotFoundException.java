package org.finra.rmcs.exception;

public class SlamWorkflowKeyNotFoundException extends RuntimeException {
  public SlamWorkflowKeyNotFoundException(String message) {
    super(message);
  }

  public SlamWorkflowKeyNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
