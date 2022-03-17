package net.darmo_creations.mccode.interpreter.nodes;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.exceptions.SyntaxErrorException;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A node that represents an operator.
 */
public abstract class OperatorNode extends OperationNode {
  public static final String SYMBOL_KEY = "Symbol";

  private final String symbol;

  /**
   * Create an operator node.
   *
   * @param symbol   Operator’s symbol.
   * @param arity    Operator’s arity, i.e. its number of operands.
   * @param operands Operator’s operands.
   * @param line     The line this node starts on.
   * @param column   The column in the line this node starts at.
   * @throws SyntaxErrorException If the number of operands does not match the arity.
   */
  public OperatorNode(final String symbol, final int arity, final List<Node> operands, final int line, final int column)
      throws SyntaxErrorException {
    super(operands, line, column);
    this.symbol = Objects.requireNonNull(symbol);
    if (this.arguments.size() != arity) {
      throw new SyntaxErrorException(line, column,
          "mccode.interpreter.error.invalid_operator_operands_number", symbol, arity, operands.size());
    }
  }

  /**
   * Create an operator node from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public OperatorNode(final CompoundTag tag) {
    super(tag);
    this.symbol = tag.getString(SYMBOL_KEY);
  }

  /**
   * Return this operator’s symbol.
   */
  public String getSymbol() {
    return this.symbol;
  }

  @Override
  protected Object evaluateWrapped(Scope scope) {
    return this.evaluateImpl(scope, this.arguments.stream()
        .map(node -> node.evaluate(scope))
        .collect(Collectors.toList()));
  }

  /**
   * Delegate method that returns the result of the operator.
   *
   * @param scope  Scope this operator is called from.
   * @param values Values of the operands.
   * @return Operator’s result.
   */
  protected abstract Object evaluateImpl(Scope scope, final List<Object> values);

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = super.writeToNBT();
    tag.putString(SYMBOL_KEY, this.symbol);
    return tag;
  }
}
