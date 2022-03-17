package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#sqrt(double)} function.
 */
@Function(parametersDoc = {"A number."},
    returnDoc = "The square root of the number.",
    doc = "Returns the square root of the given value.")
public class SqrtFunction extends BuiltinFunction {
  /**
   * Create a function that returns the square root of its parameter.
   */
  public SqrtFunction() {
    super("sqrt", ProgramManager.getTypeInstance(FloatType.class), false,
        new Parameter("x", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return Math.sqrt(this.getParameterValue(scope, 0));
  }
}
