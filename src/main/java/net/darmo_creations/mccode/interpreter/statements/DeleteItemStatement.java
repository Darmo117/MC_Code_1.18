package net.darmo_creations.mccode.interpreter.statements;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.nodes.Node;
import net.darmo_creations.mccode.interpreter.nodes.NodeNBTHelper;
import net.darmo_creations.mccode.interpreter.type_wrappers.BinaryOperator;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/**
 * Statement that applies the "delete item" operator.
 */
public class DeleteItemStatement extends Statement {
  public static final int ID = 21;

  public static final String TARGET_KEY = "Target";
  public static final String KEY_KEY = "Key";

  private final Node target;
  private final Node key;

  /**
   * Create a statement that applies the "delete item" operator.
   *
   * @param target Expression that returns the object to apply the operator on.
   * @param key    Expression that returns the key to delete.
   * @param line   The line this statement starts on.
   * @param column The column in the line this statement starts at.
   */
  public DeleteItemStatement(final Node target, final Node key, final int line, final int column) {
    super(line, column);
    this.target = Objects.requireNonNull(target);
    this.key = Objects.requireNonNull(key);
  }

  /**
   * Create a statement that applies the "delete item" operator from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public DeleteItemStatement(final CompoundTag tag) {
    super(tag);
    this.target = NodeNBTHelper.getNodeForTag(tag.getCompound(TARGET_KEY));
    this.key = NodeNBTHelper.getNodeForTag(tag.getCompound(KEY_KEY));
  }

  @Override
  protected StatementAction executeWrapped(final Scope scope) {
    Object targetValue = this.target.evaluate(scope);
    TypeBase<?> targetType = ProgramManager.getTypeForValue(targetValue);
    targetType.applyOperator(scope, BinaryOperator.DEL_ITEM, targetValue, this.key.evaluate(scope), null, true);
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
    return tag;
  }

  @Override
  public String toString() {
    return String.format("del %s[%s];", this.target, this.key);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    DeleteItemStatement that = (DeleteItemStatement) o;
    return this.target.equals(that.target) && this.key.equals(that.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.target, this.key);
  }
}
