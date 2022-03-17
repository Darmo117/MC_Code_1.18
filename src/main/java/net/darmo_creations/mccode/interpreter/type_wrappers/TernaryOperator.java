package net.darmo_creations.mccode.interpreter.type_wrappers;

/**
 * Enumerates all available ternary operators.
 */
public enum TernaryOperator implements Operator {
  SET_ITEM("set_item"),
  ;

  private final String symbol;

  TernaryOperator(final String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String getSymbol() {
    return this.symbol;
  }

  @Override
  public int getArity() {
    return 3;
  }

  /**
   * Return the operator with the given symbol.
   *
   * @param symbol The symbol.
   * @return The operator or null if none matched.
   */
  public static TernaryOperator fromString(final String symbol) {
    for (TernaryOperator operator : values()) {
      if (operator.getSymbol().equals(symbol)) {
        return operator;
      }
    }
    return null;
  }
}
