package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#sin(double)} function.
 */
@Function(parametersDoc = {"An angle in radians."},
    returnDoc = "The sine of the given angle.",
    doc = "Returns the sine of the given angle.")
public class SinFunction extends BuiltinFunction {
  /**
   * Create a function that returns the sine of its parameter.
   */
  public SinFunction() {
    super("sin", ProgramManager.getTypeInstance(FloatType.class), false,
        new Parameter("a", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return Math.sin(this.getParameterValue(scope, 0));
  }
}
