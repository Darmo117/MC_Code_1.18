package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#log(double)} function.
 */
@Function(parametersDoc = {"A number."},
    doc = "Returns the natural logarithm (base $e) of a value.")
public class LogFunction extends BuiltinFunction {
  /**
   * Create a function that returns the natural logarithm (base e) of its parameter.
   */
  public LogFunction() {
    super("log", ProgramManager.getTypeInstance(FloatType.class), false,
        new Parameter("x", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return Math.log(this.getParameterValue(scope, 0));
  }
}
