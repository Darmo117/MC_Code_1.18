package net.darmo_creations.mccode.interpreter.exceptions;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.type_wrappers.ListType;
import net.darmo_creations.mccode.interpreter.types.MCMap;

import java.util.Collections;

/**
 * Exception thrown by __get_item__ method of {@link ListType}.
 */
public class IndexOutOfBoundsException extends MCCodeRuntimeException {
  /**
   * Create an exception.
   *
   * @param scope The scope this exception was thrown from.
   * @param index Index that raised the error.
   */
  public IndexOutOfBoundsException(final Scope scope, final int index) {
    super(scope, new MCMap(Collections.singletonMap("index", index)),
        "mccode.interpreter.error.index_out_of_bounds", index);
  }

  @Override
  public String getName() {
    return "index_out_of_bounds_error";
  }
}
