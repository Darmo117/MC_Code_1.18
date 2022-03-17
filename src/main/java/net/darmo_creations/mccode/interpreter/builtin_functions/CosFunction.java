package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#cos(double)} function.
 */
@Function(parametersDoc = {"An angle in radians."},
    returnDoc = "The cosine of the given angle.",
    doc = "Returns the cosine of the given angle.")
public class CosFunction extends BuiltinFunction {
  /**
   * Create a function that returns the cosine of its parameter.
   */
  public CosFunction() {
    super("cos", ProgramManager.getTypeInstance(FloatType.class), false,
        new Parameter("a", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return Math.cos(this.getParameterValue(scope, 0));
  }
}
