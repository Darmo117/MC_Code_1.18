package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.darmo_creations.mccode.interpreter.type_wrappers.IntType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * A function that returns a random integer in a range.
 */
@Function(parametersDoc = {"The lowest possible value (included).", "The highest possible value (excluded)."},
    returnDoc = "A random int value in [$a, $b[.",
    doc = "Returns a random int in the given range. Bounds may not be outside of range [-2147483647, 2147483647]. " +
        "RNG seed may be set by %set_random_seed function.")
public class RandomIntFunction extends BuiltinFunction {
  /**
   * Create a function that returns a random integer in a range.
   */
  public RandomIntFunction() {
    super("random_int", ProgramManager.getTypeInstance(IntType.class), false,
        new Parameter("a", ProgramManager.getTypeInstance(IntType.class)),
        new Parameter("b", ProgramManager.getTypeInstance(IntType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    int a = this.<Long>getParameterValue(scope, 0).intValue();
    int b = this.<Long>getParameterValue(scope, 1).intValue();
    if (a > b) {
      throw new EvaluationException(scope, "mccode.interpreter.error.random_invalid_bounds", a, b);
    }
    return scope.getProgram().getRNG().nextInt(b - a) + a;
  }
}
