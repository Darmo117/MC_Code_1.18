package net.darmo_creations.mccode.interpreter.statements;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.nodes.Node;
import net.darmo_creations.mccode.interpreter.nodes.NodeNBTHelper;
import net.darmo_creations.mccode.interpreter.type_wrappers.BinaryOperator;
import net.darmo_creations.mccode.interpreter.type_wrappers.TernaryOperator;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/**
 * Statement that sets the value of an item on an object.
 */
public class SetItemStatement extends Statement {
  public static final int ID = 13;

  public static final String TARGET_KEY = "Target";
  public static final String KEY_KEY = "Key";
  public static final String OPERATOR_KEY = "Operator";
  public static final String VALUE_KEY = "Value";

  private final Node target;
  private final Node key;
  private final AssigmentOperator operator;
  private final Node value;

  /**
   * Create an item assigment statement.
   *
   * @param target   Object to get the key from.
   * @param key      The key of the item.
   * @param operator The assignment operator to apply.
   * @param value    The value to assign.
   * @param line     The line this statement starts on.
   * @param column   The column in the line this statement starts at.
   */
  public SetItemStatement(final Node target, final Node key, final AssigmentOperator operator, final Node value,
                          final int line, final int column) {
    super(line, column);
    this.target = Objects.requireNonNull(target);
    this.key = Objects.requireNonNull(key);
    this.operator = Objects.requireNonNull(operator);
    this.value = Objects.requireNonNull(value);
  }

  /**
   * Create an item assigment statement.
   *
   * @param tag The tag to deserialize.
   */
  public SetItemStatement(final CompoundTag tag) {
    super(tag);
    this.target = NodeNBTHelper.getNodeForTag(tag.getCompound(TARGET_KEY));
    this.key = NodeNBTHelper.getNodeForTag(tag.getCompound(KEY_KEY));
    this.operator = AssigmentOperator.fromString(tag.getString(OPERATOR_KEY));
    this.value = NodeNBTHelper.getNodeForTag(tag.getCompound(VALUE_KEY));
  }

  @Override
  protected StatementAction executeWrapped(Scope scope) {
    Object targetObject = this.target.evaluate(scope);
    TypeBase<?> targetObjectType = ProgramManager.getTypeForValue(targetObject);
    Object keyValue = this.key.evaluate(scope);
    Object newValue = this.value.evaluate(scope);
    Object oldValue = targetObjectType.applyOperator(scope, BinaryOperator.GET_ITEM, targetObject, keyValue, null, false);
    TypeBase<?> oldValueType = ProgramManager.getTypeForValue(oldValue);
    Object resultValue = this.operator.getBaseOperator()
        .map(op -> oldValueType.applyOperator(scope, op, oldValue, newValue, null, false))
        .orElse(newValue);
    targetObjectType.applyOperator(scope, TernaryOperator.SET_ITEM, targetObject, keyValue, resultValue, true);

    return StatementAction.PROCEED;
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = super.writeToNBT();
    tag.put(TARGET_KEY, this.target.writeToNBT());
    tag.put(KEY_KEY, this.key.writeToNBT());
    tag.putString(OPERATOR_KEY, this.operator.getSymbol());
    tag.put(VALUE_KEY, this.value.writeToNBT());
    return tag;
  }

  @Override
  public String toString() {
    return String.format("%s[%s] %s %s;", this.target, this.key, this.operator.getSymbol(), this.value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    SetItemStatement that = (SetItemStatement) o;
    return this.target.equals(that.target) && this.key.equals(that.key) && this.operator == that.operator && this.value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.target, this.key, this.operator, this.value);
  }
}
