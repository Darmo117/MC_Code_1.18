package net.darmo_creations.mccode.interpreter.exceptions;

/**
 * Base exception for program status related errors.
 */
public class ProgramStatusException extends MCCodeException {
  private final String programName;

  /**
   * Create an exception.
   *
   * @param translationKey The unlocalized key.
   * @param programName    Program’s name.
   */
  public ProgramStatusException(final String translationKey, final String programName) {
    super(translationKey);
    this.programName = programName;
  }

  /**
   * Return the unlocalized string of the error message.
   */
  public String getTranslationKey() {
    return this.getMessage();
  }

  /**
   * Return the name of the program.
   */
  public String getProgramName() {
    return this.programName;
  }
}
