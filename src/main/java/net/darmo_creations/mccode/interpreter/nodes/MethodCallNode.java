package net.darmo_creations.mccode.interpreter.nodes;

import net.darmo_creations.mccode.interpreter.*;
import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.darmo_creations.mccode.interpreter.type_wrappers.ModuleType;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;
import net.darmo_creations.mccode.interpreter.types.Function;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A node that represents the call to an object’s method.
 */
public class MethodCallNode extends OperationNode {
  public static final int ID = 102;

  public static final String INSTANCE_KEY = "Instance";
  public static final String METHOD_NAME_KEY = "MethodName";

  protected final Node instance;
  private final String methodName;

  /**
   * Create a method call node.
   *
   * @param instance  Expression evaluating to an object the method will be applied on.
   * @param arguments Method’s arguments.
   * @param line      The line this node starts on.
   * @param column    The column in the line this node starts at.
   */
  public MethodCallNode(final Node instance, final String methodName, final List<Node> arguments, final int line, final int column) {
    super(arguments, line, column);
    this.instance = Objects.requireNonNull(instance);
    this.methodName = Objects.requireNonNull(methodName);
  }

  /**
   * Create a method call node from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public MethodCallNode(final CompoundTag tag) {
    super(tag);
    this.instance = NodeNBTHelper.getNodeForTag(tag.getCompound(INSTANCE_KEY));
    this.methodName = tag.getString(METHOD_NAME_KEY);
  }

  @Override
  protected Object evaluateWrapped(final Scope scope) {
    Object self = this.instance.evaluate(scope);
    TypeBase<?> selfType = ProgramManager.getTypeForValue(self);

    if (selfType.getClass() == ModuleType.class) {
      Object property = selfType.getPropertyValue(scope, self, this.methodName);

      Function function;
      try {
        function = (Function) property;
      } catch (ClassCastException e) {
        throw new EvaluationException(scope, "mccode.interpreter.error.calling_non_callable", selfType);
      }

      int callStackSize = scope.getProgram().getScope().getCallStackSize();
      scope.getProgram().getScope().setCallStackSize(callStackSize + 1);
      // Use global scope as user functions can only be defined in global scope
      // and it should not matter for builtin function.
      Scope functionScope = new Scope(function.getName(), scope.getProgram().getScope());

      if (this.arguments.size() != function.getParameters().size()) {
        throw new EvaluationException(scope, "mccode.interpreter.error.invalid_function_arguments_number",
            function.getName(), function.getParameters().size(), this.arguments.size());
      }

      for (int i = 0; i < this.arguments.size(); i++) {
        Parameter parameter = function.getParameter(i);
        functionScope.declareVariable(new Variable(parameter.getName(), false, false, false, true, this.arguments.get(i).evaluate(scope)));
      }

      Object result = function.apply(functionScope);
      scope.getProgram().getScope().setCallStackSize(callStackSize);

      return result;

    } else {
      MemberFunction method = selfType.getMethod(this.methodName);
      if (method == null) {
        throw new EvaluationException(scope, "mccode.interpreter.error.no_method_for_type", selfType.getName(), this.methodName);
      }
      Scope functionScope = new Scope(method.getName(), scope);

      if (this.arguments.size() != method.getParameters().size()) {
        throw new EvaluationException(scope, "mccode.interpreter.error.invalid_method_arguments_number",
            method.getHostType(), method.getName(), method.getParameters().size(), this.arguments.size());
      }

      functionScope.declareVariable(new Variable(MemberFunction.SELF_PARAM_NAME, false, false, true, false, self));
      for (int i = 0; i < this.arguments.size(); i++) {
        Parameter parameter = method.getParameter(i);
        functionScope.declareVariable(new Variable(parameter.getName(), false, false, false, true, this.arguments.get(i).evaluate(scope)));
      }

      return method.apply(functionScope);
    }
  }

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = super.writeToNBT();
    tag.put(INSTANCE_KEY, this.instance.writeToNBT());
    tag.putString(METHOD_NAME_KEY, this.methodName);
    return tag;
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public String toString() {
    String parameters = this.arguments.stream().map(Node::toString).collect(Collectors.joining(", "));
    return String.format("%s.%s(%s)", this.instance, this.methodName, parameters);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    MethodCallNode that = (MethodCallNode) o;
    return this.instance.equals(that.instance) && this.methodName.equals(that.methodName) && this.arguments.equals(that.arguments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.instance, this.methodName, this.arguments);
  }
}
