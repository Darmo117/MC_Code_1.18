package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#asin(double)} function.
 */
@Function(parametersDoc = {"A value to get the arc sine of."},
    returnDoc = "The angle in radians between -π/2 and π/2.",
    doc = "Returns the arc sine of the given value.")
public class AsinFunction extends BuiltinFunction {
  /**
   * Create a function that returns the arc sine of its parameter.
   */
  public AsinFunction() {
    super("asin", ProgramManager.getTypeInstance(FloatType.class), false,
        new Parameter("x", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return Math.asin(this.getParameterValue(scope, 0));
  }
}
