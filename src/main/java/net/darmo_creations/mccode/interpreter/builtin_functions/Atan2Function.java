package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * Wrapper function for Java’s {@link Math#atan2(double, double)} function.
 */
@Function(parametersDoc = {"The ordinate coordinate.", "The abscissa coordinate."},
    returnDoc = "The $θ component of the point ($r, $θ) in polar coordinates " +
        "that corresponds to the point ($x, $y) in Cartesian coordinates.",
    doc = "Returns the angle $θ from the conversion of rectangular coordinates ($x, $y)" +
        " to polar coordinates ($r, $θ). This method computes the phase $θ by computing an arc tangent" +
        " of $y/$x in the range of -π to π.")
public class Atan2Function extends BuiltinFunction {
  /**
   * Create a function that returns the angle θ from the conversion of rectangular coordinates (x, y)
   * to polar coordinates (r, θ). This method computes the phase θ by computing an arc tangent
   * of y/x in the range of -π to π.
   */
  public Atan2Function() {
    super("atan2", ProgramManager.getTypeInstance(FloatType.class), false,
        new Parameter("y", ProgramManager.getTypeInstance(FloatType.class)),
        new Parameter("x", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    return Math.atan2(this.getParameterValue(scope, 0), this.getParameterValue(scope, 1));
  }
}
