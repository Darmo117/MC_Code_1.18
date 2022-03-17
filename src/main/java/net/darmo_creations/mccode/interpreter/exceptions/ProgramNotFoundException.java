package net.darmo_creations.mccode.interpreter.exceptions;

/**
 * Exception thrown when attempting to delete a program that is not loaded.
 */
public class ProgramNotFoundException extends ProgramStatusException {
  /**
   * Create an exception.
   *
   * @param programName Program’s name.
   */
  public ProgramNotFoundException(final String programName) {
    super("mccode.interpreter.error.program_not_found", programName);
  }
}
