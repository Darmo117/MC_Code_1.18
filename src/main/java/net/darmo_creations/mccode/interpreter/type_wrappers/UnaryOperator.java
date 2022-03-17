package net.darmo_creations.mccode.interpreter.type_wrappers;

/**
 * Enumerates all available unary operators.
 */
public enum UnaryOperator implements Operator {
  // Math
  MINUS("-"),

  // Logic
  NOT("not"),

  // Collections
  ITERATE("iter"),
  LENGTH("len"),
  ;

  private final String symbol;

  UnaryOperator(final String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String getSymbol() {
    return this.symbol;
  }

  @Override
  public int getArity() {
    return 1;
  }

  /**
   * Return the operator with the given symbol.
   *
   * @param symbol The symbol.
   * @return The operator or null if none matched.
   */
  public static UnaryOperator fromString(final String symbol) {
    for (UnaryOperator operator : values()) {
      if (operator.getSymbol().equals(symbol)) {
        return operator;
      }
    }
    return null;
  }
}
