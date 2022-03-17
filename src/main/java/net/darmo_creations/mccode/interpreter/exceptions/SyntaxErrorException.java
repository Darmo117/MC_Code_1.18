package net.darmo_creations.mccode.interpreter.exceptions;

/**
 * Error thrown when a program features a syntax error.
 */
public class SyntaxErrorException extends MCCodeException {
  private final int line;
  private final int column;
  private final Object[] args;

  /**
   * Create syntax error exception.
   *
   * @param line           Line where this error occured on.
   * @param column         Column of the line this error occured on.
   * @param translationKey Unlocalized string of the error message.
   * @param args           Values to use to format the error message.
   */
  public SyntaxErrorException(final int line, final int column, final String translationKey, final Object... args) {
    super(translationKey);
    this.line = line;
    this.column = column;
    this.args = args;
  }

  /**
   * Return the line where this error occured on.
   */
  public int getLine() {
    return this.line;
  }

  /**
   * Return the column where this error occured on.
   */
  public int getColumn() {
    return this.column;
  }

  /**
   * Return the unlocalized string of the error message.
   */
  public String getTranslationKey() {
    return super.getMessage();
  }

  /**
   * Return the values to use to format the error message.
   */
  public Object[] getArgs() {
    return this.args;
  }
}
