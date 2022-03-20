package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.exceptions.CastException;
import net.darmo_creations.mccode.interpreter.exceptions.EmptyCollectionException;
import net.darmo_creations.mccode.interpreter.type_wrappers.AnyType;
import net.darmo_creations.mccode.interpreter.type_wrappers.ListType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;
import net.darmo_creations.mccode.interpreter.types.MCList;
import net.darmo_creations.mccode.interpreter.types.MCSet;
import net.darmo_creations.mccode.interpreter.types.Range;

import java.util.Spliterators;
import java.util.stream.StreamSupport;

/**
 * A function that returns the maximum value of an iterable object.
 */
@Function(parametersDoc = {"An iterable object (`map, `list, `set, `range, `string, etc.)."},
    returnDoc = "The object that has the highest value according to its natural ordering operator.",
    doc = "Returns the maximum value of an iterable object. Will throw an error if the object is empty.")
public class MaxFunction extends BuiltinFunction {
  /**
   * Create a function that returns the maximum value of an iterable object.
   */
  public MaxFunction() {
    super("max", ProgramManager.getTypeInstance(AnyType.class), false,
        new Parameter("o", ProgramManager.getTypeInstance(AnyType.class)));
  }

  @Override
  public Object apply(Scope scope) {
    Object p = this.getParameterValue(scope, 0);
    if (p instanceof MCList || p instanceof MCSet || p instanceof String) {
      MCList list = new MCList(ProgramManager.getTypeInstance(ListType.class).explicitCast(scope, p));
      if (list.isEmpty()) {
        throw new EmptyCollectionException(scope);
      }
      return list.stream().max(ListType.comparator(scope, false)).get();
    } else if (p instanceof Range r) {
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(r.iterator(), 0), false)
          .max(Long::compareTo).orElseThrow(() -> new EmptyCollectionException(scope));
    }
    throw new CastException(scope, ProgramManager.getTypeInstance(ListType.class), ProgramManager.getTypeForValue(p));
  }
}
