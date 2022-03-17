package net.darmo_creations.mccode.interpreter.nodes;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.Variable;
import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.darmo_creations.mccode.interpreter.types.Function;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A node that represents the call to a function.
 */
public class FunctionCallNode extends OperationNode {
  public static final int ID = 103;

  public static final String FUNCTION_OBJ_KEY = "FunctionObject";

  protected final Node functionObject;

  /**
   * Create a function call node.
   *
   * @param functionObject Expression that evaluates to a {@link Function} object.
   * @param arguments      Function’s arguments.
   * @param line           The line this node starts on.
   * @param column         The column in the line this node starts at.
   */
  public FunctionCallNode(final Node functionObject, final List<Node> arguments, final int line, final int column) {
    super(arguments, line, column);
    this.functionObject = Objects.requireNonNull(functionObject);
  }

  /**
   * Create a function call node from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public FunctionCallNode(final CompoundTag tag) {
    super(tag);
    this.functionObject = NodeNBTHelper.getNodeForTag(tag.getCompound(FUNCTION_OBJ_KEY));
  }

  @Override
  protected Object evaluateWrapped(final Scope scope) {
    Object o = this.functionObject.evaluate(scope);

    Function function;
    try {
      function = (Function) o;
    } catch (ClassCastException e) {
      throw new EvaluationException(scope, "mccode.interpreter.error.calling_non_callable",
          ProgramManager.getTypeForValue(o));
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
  }

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = super.writeToNBT();
    tag.put(FUNCTION_OBJ_KEY, this.functionObject.writeToNBT());
    return tag;
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public String toString() {
    return String.format("%s(%s)", this.functionObject, this.arguments.stream().map(Node::toString).collect(Collectors.joining(", ")));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    FunctionCallNode that = (FunctionCallNode) o;
    return this.functionObject.equals(that.functionObject) && this.arguments.equals(that.arguments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.functionObject, this.arguments);
  }
}
