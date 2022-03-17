package net.darmo_creations.mccode.interpreter.statements;

import net.darmo_creations.mccode.interpreter.type_wrappers.BinaryOperator;

import java.util.Objects;
import java.util.Optional;

/**
 * Enumerates all available assignment operators.
 */
public enum AssigmentOperator {
  ASSIGN(":=", null),
  PLUS("+=", BinaryOperator.PLUS),
  SUB("-=", BinaryOperator.SUB),
  MUL("*=", BinaryOperator.MUL),
  DIV("/=", BinaryOperator.DIV),
  INT_DIV("//=", BinaryOperator.INT_DIV),
  MOD("%=", BinaryOperator.MOD),
  POW("^=", BinaryOperator.POW),
  ;

  private final String symbol;
  private final BinaryOperator baseOperator;

  AssigmentOperator(final String symbol, final BinaryOperator baseOperator) {
    this.symbol = Objects.requireNonNull(symbol);
    this.baseOperator = baseOperator;
  }

  /**
   * Return the symbol of this operator.
   */
  public String getSymbol() {
    return this.symbol;
  }

  /**
   * Return the {@link BinaryOperator} to apply before assigning the value.
   */
  public Optional<BinaryOperator> getBaseOperator() {
    return Optional.ofNullable(this.baseOperator);
  }

  /**
   * Return the operator with the given symbol.
   *
   * @param symbol The symbol.
   * @return The operator or null if none matched.
   */
  public static AssigmentOperator fromString(final String symbol) {
    for (AssigmentOperator operator : values()) {
      if (operator.getSymbol().equals(symbol)) {
        return operator;
      }
    }
    return null;
  }
}
