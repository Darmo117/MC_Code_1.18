package net.darmo_creations.mccode.interpreter.exceptions;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.builtin_functions.ErrorFunction;

/**
 * Exception thrown by the {@link ErrorFunction} class.
 */
public class UserException extends MCCodeRuntimeException {
  /**
   * Create a user exception.
   *
   * @param scope   The scope this exception was thrown from.
   * @param message The error message.
   * @param data    Optional data accessible to try-catch statement.
   */
  public UserException(final Scope scope, final String message, final Object data) {
    super(scope, data, "mccode.interpreter.error.user_exception", message);
  }

  @Override
  public String getName() {
    return "user_error";
  }
}
