package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.type_wrappers.IntType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#round(double)} function.
 */
@Function(parametersDoc = {"The number to be rounded."},
    returnDoc = "The closest `int to the argument.",
    doc = "Returns the `int closest to the given value.")
public class RoundFunction extends BuiltinFunction {
  /**
   * Create a function that returns the integer closest to its parameter.
   */
  public RoundFunction() {
    super("round", ProgramManager.getTypeInstance(IntType.class), false,
        new Parameter("x", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return Math.round(this.<Double>getParameterValue(scope, 0));
  }
}
