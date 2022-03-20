package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.exceptions.CastException;
import net.darmo_creations.mccode.interpreter.type_wrappers.AnyType;
import net.darmo_creations.mccode.interpreter.type_wrappers.BooleanType;
import net.darmo_creations.mccode.interpreter.type_wrappers.ListType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;
import net.darmo_creations.mccode.interpreter.types.MCList;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * A function that sorts the given iterable object.
 */
@Function(parametersDoc = {
    "An iterable object (`list, `set or `string) to be sorted.",
    "If #true values will be sorted in reverse order, otherwise natural order will be used."},
    returnDoc = "A new `list or `string containing all elements of the arguments sorted in natural or reverse order.",
    doc = "Sorts the given iterable object. Returns a new object.")
public class SortedFunction extends BuiltinFunction {
  /**
   * Create a function that sorts the given iterable object.
   */
  public SortedFunction() {
    super("sorted", ProgramManager.getTypeInstance(AnyType.class), false,
        new Parameter("o", ProgramManager.getTypeInstance(AnyType.class)),
        new Parameter("reversed", ProgramManager.getTypeInstance(BooleanType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    Object p = this.getParameterValue(scope, 0);
    boolean reversed = this.getParameterValue(scope, 1);
    if (p instanceof List<?> l) {
      MCList list = new MCList(l);
      list.sort(ListType.comparator(scope, reversed));
      return list;
    } else if (p instanceof Set<?> s) {
      MCList list = new MCList(s);
      list.sort(ListType.comparator(scope, reversed));
      return list;
    } else if (p instanceof String s) {
      return s.codePoints()
          .mapToObj(cp -> (char) cp)
          .sorted(Comparator.comparing(cp -> reversed ? -cp : cp))
          .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
          .toString();
    }
    throw new CastException(scope, ProgramManager.getTypeInstance(ListType.class), ProgramManager.getTypeForValue(p));
  }
}
