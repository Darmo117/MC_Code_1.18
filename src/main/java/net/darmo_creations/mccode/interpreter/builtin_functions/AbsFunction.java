package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#abs(double)} function.
 */
@Function(parametersDoc = {"A number."},
    doc = "Returns the absolute value of the argument.")
public class AbsFunction extends BuiltinFunction {
  /**
   * Create a function that returns the absolute value of its parameter.
   */
  public AbsFunction() {
    super("abs", ProgramManager.getTypeInstance(FloatType.class), false,
        new Parameter("x", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return Math.abs(this.<Double>getParameterValue(scope, 0));
  }
}
