package net.darmo_creations.mccode.interpreter.exceptions;

/**
 * Base class for interpreter’s exceptions.
 */
public class MCCodeException extends RuntimeException {
  public MCCodeException(final String s) {
    super(s);
  }

  public MCCodeException(final Throwable throwable) {
    super(throwable);
  }

  public MCCodeException(final String s, final Throwable throwable) {
    super(s, throwable);
  }
}
