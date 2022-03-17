package net.darmo_creations.mccode.interpreter;

/**
 * A class that reports an error that occured during execution of a program.
 */
public class ProgramErrorReport {
  private final Scope scope;
  private final int line;
  private final int column;
  private final String translationKey;
  private final Object[] args;

  /**
   * Create an error report.
   *
   * @param scope          Program that throwed the error.
   * @param translationKey Error’s unlocalized translation key.
   * @param args           Report’s arguments to be used for translation of the error message.
   * @param line           Line where the error occured on.
   * @param column         Column of the line the error occured on.
   */
  public ProgramErrorReport(final Scope scope, final int line, final int column, final String translationKey, final Object... args) {
    this.scope = scope;
    this.line = line;
    this.column = column;
    this.translationKey = translationKey;
    this.args = args;
  }

  /**
   * Return scope of the program that throwed the error.
   */
  public Scope getScope() {
    return this.scope;
  }

  /**
   * Error’s unlocalized translation key.
   */
  public String getTranslationKey() {
    return this.translationKey;
  }

  /**
   * Report’s arguments to be used for translation of the error message.
   */
  public Object[] getArgs() {
    return this.args;
  }

  /**
   * Return the line where the error occured on.
   */
  public int getLine() {
    return this.line;
  }

  /**
   * Return the column where the error occured on.
   */
  public int getColumn() {
    return this.column;
  }
}
