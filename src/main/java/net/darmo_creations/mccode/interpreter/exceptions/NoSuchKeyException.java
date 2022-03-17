package net.darmo_creations.mccode.interpreter.exceptions;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.type_wrappers.MapType;
import net.darmo_creations.mccode.interpreter.types.MCMap;

import java.util.Collections;

/**
 * Exception thrown by __del_item__ method of {@link MapType}.
 */
public class NoSuchKeyException extends MCCodeRuntimeException {
  /**
   * Create an exception.
   *
   * @param scope The scope this exception was thrown from.
   * @param key   Key that raised the error.
   */
  public NoSuchKeyException(final Scope scope, final String key) {
    super(scope, new MCMap(Collections.singletonMap("key", key)),
        "mccode.interpreter.error.no_such_key", key);
  }

  @Override
  public String getName() {
    return "no_such_key_error";
  }
}
