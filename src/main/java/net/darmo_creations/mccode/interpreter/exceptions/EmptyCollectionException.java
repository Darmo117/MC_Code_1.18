package net.darmo_creations.mccode.interpreter.exceptions;

import net.darmo_creations.mccode.interpreter.Scope;

/**
 * Exception thrown by max and min functions.
 */
public class EmptyCollectionException extends MCCodeRuntimeException {
  /**
   * Create an exception.
   *
   * @param scope The scope this exception was thrown from.
   */
  public EmptyCollectionException(final Scope scope) {
    super(scope, null, "mccode.interpreter.error.empty_collection");
  }

  @Override
  public String getName() {
    return "empty_collection_error";
  }
}
