package net.darmo_creations.mccode.interpreter.exceptions;

/**
 * Exception thrown when attempting to pause an already paused program.
 */
public class ProgramAlreadyPausedException extends ProgramStatusException {
  /**
   * Create an exception.
   *
   * @param programName Program’s name.
   */
  public ProgramAlreadyPausedException(final String programName) {
    super("mccode.interpreter.error.program_already_paused", programName);
  }
}
