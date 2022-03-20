package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.type_wrappers.IntType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#ceil(double)} function.
 */
@Function(parametersDoc = {"A number."},
    doc = "Returns the smallest `int that is greater than or equal to the argument " +
        "and is equal to a mathematical integer.")
public class CeilFunction extends BuiltinFunction {
  /**
   * Create a function that returns the smallest float value
   * that is greater than or equal to the argument and is equal to a mathematical integer.
   */
  public CeilFunction() {
    super("ceil", ProgramManager.getTypeInstance(IntType.class), false,
        new Parameter("x", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return (long) Math.ceil(this.getParameterValue(scope, 0));
  }
}
