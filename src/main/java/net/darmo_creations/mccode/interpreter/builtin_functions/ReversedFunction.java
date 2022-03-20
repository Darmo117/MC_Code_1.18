package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.exceptions.CastException;
import net.darmo_creations.mccode.interpreter.type_wrappers.AnyType;
import net.darmo_creations.mccode.interpreter.type_wrappers.ListType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;
import net.darmo_creations.mccode.interpreter.types.MCList;

import java.util.List;

/**
 * A function that reverses the given iterable object.
 */
@Function(parametersDoc = {"An ordered iterable object (`list or `string)."},
    returnDoc = "A new `list or `string containing all elements of the arguments in reverse order.",
    doc = "Reverses the order of the given ordered iterable object. Returns a new object.")
public class ReversedFunction extends BuiltinFunction {
  /**
   * Create a function that reverses the given iterable object.
   */
  public ReversedFunction() {
    super("reversed", ProgramManager.getTypeInstance(AnyType.class), false,
        new Parameter("o", ProgramManager.getTypeInstance(AnyType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    Object p = this.getParameterValue(scope, 0);
    if (p instanceof List<?> list) {
      MCList res = new MCList();
      list.forEach(v -> res.add(0, v));
      return res;
    } else if (p instanceof String s) {
      StringBuilder sb = new StringBuilder();
      s.codePoints().forEach(cp -> sb.insert(0, (char) cp));
      return sb.toString();
    }
    throw new CastException(scope, ProgramManager.getTypeInstance(ListType.class), ProgramManager.getTypeForValue(p));
  }
}
