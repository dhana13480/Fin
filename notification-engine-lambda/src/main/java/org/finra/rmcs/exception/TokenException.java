package org.finra.rmcs.exception;

public class TokenException extends Exception {
  private static final long serialVersionUID = 1105625716321276013L;

  public TokenException() {
    super();
  }

  public TokenException(String message) {
    super(message);
  }

  public TokenException(String message, Throwable cause) {
    super(message, cause);
  }
}
