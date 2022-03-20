package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#toDegrees(double)} function.
 */
@Function(parametersDoc = {"An angle in radians."},
    returnDoc = "The angle in degrees.",
    doc = "Converts the given angle from radians to degrees. " +
        "The conversion from radians to degrees is generally inexact; " +
        "users should not expect %cos(%to_radians(90.0)) to exactly equal 0.0.")
public class ToDegreesFunction extends BuiltinFunction {
  /**
   * Create a function that converts the given angle in radians to degrees.
   */
  public ToDegreesFunction() {
    super("to_degrees", ProgramManager.getTypeInstance(FloatType.class), false,
        new Parameter("radians", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return Math.toDegrees(this.getParameterValue(scope, 0));
  }
}
