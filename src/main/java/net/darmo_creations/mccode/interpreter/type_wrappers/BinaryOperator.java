package net.darmo_creations.mccode.interpreter.type_wrappers;

/**
 * Enumerates all available binary operators.
 */
public enum BinaryOperator implements Operator {
  // Math
  PLUS("+"),
  SUB("-"),
  MUL("*"),
  DIV("/"),
  INT_DIV("//"),
  MOD("%"),
  POW("^"),

  // Comparison
  EQUAL("=="),
  NOT_EQUAL("!="),
  GT(">"),
  GE(">="),
  LT("<"),
  LE("<="),

  // LE
  AND("and"),
  OR("or"),

  // Collections
  IN("in", true),
  NOT_IN("not in", true),
  GET_ITEM("get_item"),
  DEL_ITEM("del_item"),
  ;

  private final String symbol;
  private final boolean flipped;

  BinaryOperator(final String symbol) {
    this.symbol = symbol;
    this.flipped = false;
  }

  BinaryOperator(final String symbol, final boolean flipped) {
    this.symbol = symbol;
    this.flipped = flipped;
  }

  /**
   * Return the symbol of this operator.
   */
  @Override
  public String getSymbol() {
    return this.symbol;
  }

  /**
   * Return the number of operands this operator accepts.
   */
  @Override
  public int getArity() {
    return 2;
  }

  /**
   * Return whether the instance and second argument of this operator are flipped.
   * <p>
   * Specifically concerns the {@link #IN} and {@link #NOT_IN} operators.
   */
  public boolean isFlipped() {
    return this.flipped;
  }

  /**
   * Return the operator with the given symbol.
   *
   * @param symbol The symbol.
   * @return The operator or null if none matched.
   */
  public static BinaryOperator fromString(final String symbol) {
    for (BinaryOperator operator : values()) {
      if (operator.getSymbol().equals(symbol)) {
        return operator;
      }
    }
    return null;
  }
}
