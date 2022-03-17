package net.darmo_creations.mccode.interpreter.statements;

/**
 * Enumerates all possible actions statements can apply on the program’s flow.
 */
public enum StatementAction {
  PROCEED,
  EXIT_FUNCTION,
  EXIT_LOOP,
  CONTINUE_LOOP,
  WAIT
}
