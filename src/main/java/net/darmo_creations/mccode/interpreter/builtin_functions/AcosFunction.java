package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#acos(double)} function.
 */
@Function(parametersDoc = {"A value to get the arc cosine of."},
    returnDoc = "The angle in radians between 0 and π.",
    doc = "Returns the arc cosine of the given value.")
public class AcosFunction extends BuiltinFunction {
  /**
   * Create a function that returns the arc cosine of its parameter.
   */
  public AcosFunction() {
    super("acos", ProgramManager.getTypeInstance(FloatType.class), false,
        new Parameter("x", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return Math.acos(this.getParameterValue(scope, 0));
  }
}
