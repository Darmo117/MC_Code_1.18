package net.darmo_creations.mccode.interpreter.nodes;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;
import net.darmo_creations.mccode.interpreter.type_wrappers.UnaryOperator;
import net.minecraft.nbt.CompoundTag;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A node that represents an operator with a single operand.
 */
public class UnaryOperatorNode extends OperatorNode {
  public static final int ID = 200;

  private final UnaryOperator operator;

  /**
   * Create a unary operator node.
   *
   * @param operator Operator’s symbol.
   * @param operand  Operator’s operand.
   * @param line     The line this node starts on.
   * @param column   The column in the line this node starts at.
   */
  public UnaryOperatorNode(final UnaryOperator operator, final Node operand, final int line, final int column) {
    super(operator.getSymbol(), 1, Collections.singletonList(Objects.requireNonNull(operand)), line, column);
    this.operator = operator;
  }

  /**
   * Create a unary operator node from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public UnaryOperatorNode(final CompoundTag tag) {
    super(tag);
    this.operator = UnaryOperator.fromString(this.getSymbol());
  }

  @Override
  protected final Object evaluateImpl(Scope scope, final List<Object> values) {
    Object arg1 = values.get(0);
    TypeBase<?> arg1Type = ProgramManager.getTypeForValue(arg1);
    return arg1Type.applyOperator(scope, this.operator, arg1, null, null, false);
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public String toString() {
    Node operand = this.arguments.get(0);
    String symbol = this.getSymbol() + (this.operator == UnaryOperator.NOT ? " " : "");
    if (operand instanceof OperatorNode) {
      return String.format("%s(%s)", symbol, operand);
    } else {
      return symbol + this.arguments.get(0);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    UnaryOperatorNode that = (UnaryOperatorNode) o;
    return this.operator == that.operator && this.getSymbol().equals(that.getSymbol()) && this.arguments.equals(that.arguments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.operator, this.getSymbol(), this.arguments);
  }
}
