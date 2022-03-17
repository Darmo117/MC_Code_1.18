package net.darmo_creations.mccode.interpreter.exceptions;

import net.darmo_creations.mccode.interpreter.Scope;

/**
 * Base class for runtime exceptions of MCCode.
 */
public class MCCodeRuntimeException extends MCCodeException {
  private final Scope scope;
  private final Object data;
  private final Object[] args;
  private int line = -1;
  private int column = -1;

  /**
   * Create a runtime exception.
   *
   * @param scope          The scope this exception was thrown from.
   * @param data           Optional data accessible to try-catch statement.
   * @param translationKey Unlocalized string of the error message.
   * @param args           Values to use to format the error message.
   */
  public MCCodeRuntimeException(final Scope scope, final Object data, final String translationKey, final Object... args) {
    super(translationKey);
    this.scope = scope;
    this.data = data;
    this.args = args;
  }

  /**
   * Create a runtime exception.
   *
   * @param scope          The scope this exception was thrown from.
   * @param data           Optional data accessible to try-catch statement.
   * @param translationKey Unlocalized string of the error message.
   * @param args           Values to use to format the error message.
   * @param line           Line where this error occured on.
   * @param column         Column of the line this error occured on.
   */
  public MCCodeRuntimeException(final Scope scope, final Object data, final int line, final int column,
                                final String translationKey, final Object... args) {
    super(translationKey);
    this.scope = scope;
    this.data = data;
    this.args = args;
    this.line = line;
    this.column = column;
  }

  public String getName() {
    return "error";
  }

  public Object getData() {
    return this.data;
  }

  /**
   * Return the line where this error occured on.
   */
  public int getLine() {
    return this.line;
  }

  /**
   * Set the line this error occured on.
   *
   * @param line Line number.
   */
  public void setLine(int line) {
    this.line = line;
  }

  /**
   * Return the column where this error occured on.
   */
  public int getColumn() {
    return this.column;
  }

  /**
   * Set the column this error occured on.
   *
   * @param column Column number.
   */
  public void setColumn(int column) {
    this.column = column;
  }

  /**
   * Return the scope of the this exception was thrown from.
   */
  public Scope getScope() {
    return this.scope;
  }

  /**
   * Return the unlocalized string of the error message.
   */
  public String getTranslationKey() {
    return this.getMessage();
  }

  /**
   * Return the values to use to format the error message.
   */
  public Object[] getArgs() {
    return this.args;
  }
}
