package net.darmo_creations.mccode.interpreter.exceptions;

/**
 * Exception thrown when attempting to run an already running program.
 */
public class ProgramAlreadyRunningException extends ProgramStatusException {
  /**
   * Create an exception.
   *
   * @param programName Program’s name.
   */
  public ProgramAlreadyRunningException(final String programName) {
    super("mccode.interpreter.error.program_already_running", programName);
  }
}
