package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.AnyType;
import net.darmo_creations.mccode.interpreter.type_wrappers.IntType;
import net.darmo_creations.mccode.interpreter.type_wrappers.UnaryOperator;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;

/**
 * A function that returns the length of the given object.
 * The argument object’s wrapper type must implement the __len__ method or else an exception will be thrown.
 */
@Function(parametersDoc = {"An iterable value (`map, `list, `set, `string, etc.)."},
    returnDoc = "The number of elements contained in the argument.",
    doc = "Returns the number of elements of the given collection. " +
        "Will raise an error if the argument is not iterable.")
public class LenFunction extends BuiltinFunction {
  /**
   * Create a function that returns the length of a collection.
   */
  public LenFunction() {
    super("len", ProgramManager.getTypeInstance(IntType.class), false,
        new Parameter("o", ProgramManager.getTypeInstance(AnyType.class)));
  }

  @Override
  public Object apply(Scope scope) {
    Object parameter = this.getParameterValue(scope, 0);
    return ProgramManager.getTypeForValue(parameter).applyOperator(scope, UnaryOperator.LENGTH, parameter, null, null, false);
  }
}
