package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.darmo_creations.mccode.interpreter.type_wrappers.FloatType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * A function that returns a random float in a range.
 */
@Function(parametersDoc = {"The lowest possible value (included).", "The highest possible value (excluded)."},
    returnDoc = "A random `float value in [$a, $b[.",
    doc = "Returns a random `float in the given range. RNG seed may be set by %set_random_seed function.")
public class RandomFloatFunction extends BuiltinFunction {
  /**
   * Create a function that returns a random float in a range.
   */
  public RandomFloatFunction() {
    super("random_float", ProgramManager.getTypeInstance(FloatType.class), false,
        new Parameter("a", ProgramManager.getTypeInstance(FloatType.class)),
        new Parameter("b", ProgramManager.getTypeInstance(FloatType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    double a = this.getParameterValue(scope, 0);
    double b = this.getParameterValue(scope, 1);
    if (a > b) {
      throw new EvaluationException(scope, "mccode.interpreter.error.random_invalid_bounds", a, b);
    }
    return scope.getProgram().getRNG().nextDouble() * (b - a) + a;
  }
}
