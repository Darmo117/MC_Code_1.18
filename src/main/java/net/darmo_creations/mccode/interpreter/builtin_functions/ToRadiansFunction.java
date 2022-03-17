package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#toRadians(double)} function.
 */
@Function(parametersDoc = {"An angle in degrees."},
    returnDoc = "The angle in radians.",
    doc = "Converts the given angle from degrees to radians. " +
        "The conversion from degrees to radians is generally inexact.")
public class ToRadiansFunction extends BuiltinFunction {
  /**
   * Create a function that converts the given angle in degrees to radians.
   */
  public ToRadiansFunction() {
    super("to_radians", ProgramManager.getTypeInstance(FloatType.class), false,
        new Parameter("degrees", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return Math.toRadians(this.getParameterValue(scope, 0));
  }
}
