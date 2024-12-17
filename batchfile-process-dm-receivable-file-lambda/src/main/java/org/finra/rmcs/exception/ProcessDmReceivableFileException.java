package org.finra.rmcs.exception;

public class ProcessDmReceivableFileException extends RuntimeException {
  public ProcessDmReceivableFileException(String message) {
    super(message);
  }

  public ProcessDmReceivableFileException(String message, Throwable cause) {
    super(message, cause);
  }
}
