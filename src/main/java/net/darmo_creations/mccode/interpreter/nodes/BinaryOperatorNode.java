package net.darmo_creations.mccode.interpreter.nodes;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.type_wrappers.BinaryOperator;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;
import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A node that represents an operator with two operands.
 */
public class BinaryOperatorNode extends OperatorNode {
  public static final int ID = 201;

  private final BinaryOperator operator;

  /**
   * Create a binary operator node with two operands.
   *
   * @param operator Operator to apply.
   * @param left     Left operand.
   * @param right    Right operand.
   * @param line     The line this node starts on.
   * @param column   The column in the line this node starts at.
   */
  public BinaryOperatorNode(final BinaryOperator operator, final Node left, final Node right, final int line, final int column) {
    super(operator.getSymbol(), 2, Arrays.asList(Objects.requireNonNull(left), Objects.requireNonNull(right)), line, column);
    this.operator = operator;
  }

  /**
   * Create a binary operator node from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public BinaryOperatorNode(final CompoundTag tag) {
    super(tag);
    this.operator = BinaryOperator.fromString(this.getSymbol());
  }

  @Override
  protected final Object evaluateImpl(Scope scope, final List<Object> values) {
    boolean flipped = this.operator.isFlipped();
    Object arg1 = values.get(0);
    Object arg2 = values.get(1);
    TypeBase<?> argType = ProgramManager.getTypeForValue(flipped ? arg2 : arg1);
    return argType.applyOperator(scope, this.operator, flipped ? arg2 : arg1, flipped ? arg1 : arg2, null, false);
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public String toString() {
    return String.format("%s %s %s", this.arguments.get(0), this.getSymbol(), this.arguments.get(1));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    BinaryOperatorNode that = (BinaryOperatorNode) o;
    return this.operator == that.operator && this.getSymbol().equals(that.getSymbol()) && this.arguments.equals(that.arguments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.operator, this.getSymbol(), this.arguments);
  }
}
