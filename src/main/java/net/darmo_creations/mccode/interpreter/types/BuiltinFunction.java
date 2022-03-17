package net.darmo_creations.mccode.interpreter.types;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Base class for builtin functions.
 */
public abstract class BuiltinFunction extends Function {
  // Set by ProgramManager.processFunctionsAnnotations() method
  @SuppressWarnings("unused")
  private String doc;

  /**
   * Create a builtin function.
   *
   * @param name          Function’s name.
   * @param returnType    Function’s return type.
   * @param mayReturnNull Whether this function may return a null value.
   * @param parameters    Function’s parameter types. Parameter names are generated:
   *                      {@code _x&lt;i>_} where {@code i} is the parameter’s index.
   */
  public BuiltinFunction(final String name, final TypeBase<?> returnType, boolean mayReturnNull, final Parameter... parameters) {
    super(name, Arrays.asList(parameters), returnType, mayReturnNull);
  }

  /**
   * Return the value of the given parameter.
   *
   * @param scope Scope the function is called from.
   * @param index Parameter’s index.
   * @param <T>   Parameter’s wrapped type.
   * @return Parameter’s value.
   */
  protected <T> T getParameterValue(final Scope scope, final int index) {
    Parameter parameter = this.parameters.get(index);
    //noinspection unchecked
    return (T) parameter.getType().implicitCast(scope, scope.getVariable(parameter.getName(), false));
  }

  @Override
  public String toString() {
    String params = this.parameters.stream()
        .map(p -> p.getType().getName() + " " + p.getName())
        .collect(Collectors.joining(", "));
    return String.format("builtin function %s(%s) -> %s", this.getName(), params, this.getReturnType());
  }

  /**
   * Return the documentation string for this function.
   */
  public Optional<String> getDoc() {
    return Optional.ofNullable(this.doc);
  }
}
