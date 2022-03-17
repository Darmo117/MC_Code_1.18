package net.darmo_creations.mccode.interpreter.exceptions;

/**
 * Exception thrown when loading a program but no script file was found.
 */
public class ProgramFileNotFoundException extends ProgramStatusException {
  /**
   * Create an exception.
   *
   * @param programName Program’s name.
   */
  public ProgramFileNotFoundException(final String programName) {
    super("mccode.interpreter.error.program_file_not_found", programName);
  }
}
