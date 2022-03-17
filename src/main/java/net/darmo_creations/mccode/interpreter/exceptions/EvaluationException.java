package net.darmo_creations.mccode.interpreter.exceptions;

import net.darmo_creations.mccode.interpreter.Scope;

/**
 * Generic exception thrown when evaluating an expression.
 */
public class EvaluationException extends MCCodeRuntimeException {
  /**
   * Create an evaluation exception.
   *
   * @param scope          The scope this exception was thrown from.
   * @param translationKey Unlocalized string of the error message.
   * @param args           Values to used to format the error message.
   */
  public EvaluationException(final Scope scope, final String translationKey, final Object... args) {
    super(scope, null, translationKey, args);
  }

  @Override
  public String getName() {
    return "evaluation_error";
  }
}
