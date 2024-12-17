package org.finra.rmcs.exception;

public class EwsTokenException extends Exception {
  private static final long serialVersionUID = 7105625716321276013L;

  public EwsTokenException() {
    super();
  }

  public EwsTokenException(String message) {
    super(message);
  }

  public EwsTokenException(String message, Throwable cause) {
    super(message, cause);
  }
}
