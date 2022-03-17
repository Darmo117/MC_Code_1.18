package net.darmo_creations.mccode.interpreter.exceptions;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.types.MCMap;

import java.util.Collections;

/**
 * Exception for math-related errors.
 */
public class MathException extends MCCodeRuntimeException {
  /**
   * Create a math exception.
   *
   * @param scope   The scope this exception was thrown from.
   * @param message Error message.
   */
  public MathException(final Scope scope, final int line, final int column, final String message) {
    super(scope, new MCMap(Collections.singletonMap("message", message)), line, column,
        "mccode.interpreter.error.math_error", message);
  }

  @Override
  public String getName() {
    return "math_error";
  }
}
