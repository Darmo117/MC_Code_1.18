package net.darmo_creations.mccode.interpreter.statements;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.nodes.Node;
import net.darmo_creations.mccode.interpreter.nodes.NodeNBTHelper;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/**
 * Statement that assigns a value to a variable.
 */
public class AssignVariableStatement extends Statement {
  public static final int ID = 12;

  public static final String VAR_NAME_KEY = "VariableName";
  public static final String OPERATOR_KEY = "Operator";
  public static final String VALUE_KEY = "Value";

  private final String variableName;
  private final AssigmentOperator operator;
  private final Node value;

  /**
   * Create a variable assignment statement.
   *
   * @param variableName Name of the variable to assign the value to.
   * @param operator     Operation to perform before assigning the value.
   * @param value        The value to assign.
   * @param line         The line this statement starts on.
   * @param column       The column in the line this statement starts at.
   */
  public AssignVariableStatement(final String variableName, final AssigmentOperator operator, final Node value, final int line, final int column) {
    super(line, column);
    this.variableName = Objects.requireNonNull(variableName);
    this.operator = Objects.requireNonNull(operator);
    this.value = Objects.requireNonNull(value);
  }

  /**
   * Create a variable assignment statement from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public AssignVariableStatement(final CompoundTag tag) {
    super(tag);
    this.variableName = tag.getString(VAR_NAME_KEY);
    this.operator = AssigmentOperator.fromString(tag.getString(OPERATOR_KEY));
    this.value = NodeNBTHelper.getNodeForTag(tag.getCompound(VALUE_KEY));
  }

  @Override
  protected StatementAction executeWrapped(Scope scope) {
    Object targetObject = scope.getVariable(this.variableName, false);
    TypeBase<?> targetType = ProgramManager.getTypeForValue(targetObject);
    Object valueObject = this.value.evaluate(scope);
    Object result = this.operator.getBaseOperator()
        .map(op -> targetType.applyOperator(scope, op, targetObject, valueObject, null, true))
        .orElse(ProgramManager.getTypeForValue(valueObject).copy(scope, valueObject));
    scope.setVariable(this.variableName, result, false);

    return StatementAction.PROCEED;
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = super.writeToNBT();
    tag.putString(VAR_NAME_KEY, this.variableName);
    tag.putString(OPERATOR_KEY, this.operator.getSymbol());
    tag.put(VALUE_KEY, this.value.writeToNBT());
    return tag;
  }

  @Override
  public String toString() {
    return String.format("%s %s %s;", this.variableName, this.operator.getSymbol(), this.value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    AssignVariableStatement that = (AssignVariableStatement) o;
    return this.variableName.equals(that.variableName) && this.operator == that.operator && this.value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.variableName, this.operator, this.value);
  }
}
