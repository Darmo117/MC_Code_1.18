package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.IntType;
import net.darmo_creations.mccode.interpreter.type_wrappers.NullType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * A function that sets the random number generator’s seed for the program it is called from.
 */
@Function(parametersDoc = {"The new seed of the random number generator."},
    doc = "Sets the seed of the random number generator of this program/module. " +
        "@Important: Seed will be lost if the level is unloaded while the program is running " +
        "and may thus cause unexpected behavior. As such, §oit is strongly discouraged to rely on " +
        "this function outside of debugging purposes§r.")
public class SetRandomSeedFunction extends BuiltinFunction {
  /**
   * Create a function that sets the random number generator’s seed for the program it is called from.
   */
  public SetRandomSeedFunction() {
    super("set_random_seed", ProgramManager.getTypeInstance(NullType.class), false,
        new Parameter("seed", ProgramManager.getTypeInstance(IntType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    scope.getProgram().setRNGSeed(this.getParameterValue(scope, 0));
    return null;
  }
}
