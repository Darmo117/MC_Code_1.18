package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#atan(double)} function.
 */
@Function(parametersDoc = {"A value to get the arc tangent of."},
    returnDoc = "The angle in radians between -π/2 and π/2.",
    doc = "Returns the arc tangent of the given value.")
public class AtanFunction extends BuiltinFunction {
  /**
   * Create a function that returns the arc tangent of its parameter.
   */
  public AtanFunction() {
    super("atan", ProgramManager.getTypeInstance(FloatType.class), false,
        new Parameter("x", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return Math.atan(this.getParameterValue(scope, 0));
  }
}
