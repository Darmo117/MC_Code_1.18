package net.darmo_creations.mccode.interpreter.exceptions;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;
import net.darmo_creations.mccode.interpreter.types.MCMap;

/**
 * Exception thrown when an attempt was made to cast an object to an incompatible type.
 */
public class CastException extends MCCodeRuntimeException {
  /**
   * Create a cast exception.
   *
   * @param scope      The scope this exception was thrown from.
   * @param targetType Cast target type.
   * @param sourceType Type of cast object.
   */
  public CastException(final Scope scope, final TypeBase<?> targetType, final TypeBase<?> sourceType) {
    super(scope, buildErrorObject(targetType, sourceType),
        "mccode.interpreter.error.invalid_cast", sourceType, targetType);
  }

  @Override
  public String getName() {
    return "cast_error";
  }

  private static MCMap buildErrorObject(final TypeBase<?> target, final TypeBase<?> source) {
    MCMap map = new MCMap();
    map.put("target_type", target.getName());
    map.put("source_type", source.getName());
    return map;
  }
}
