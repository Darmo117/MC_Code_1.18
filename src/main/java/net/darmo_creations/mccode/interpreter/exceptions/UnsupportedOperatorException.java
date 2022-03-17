package net.darmo_creations.mccode.interpreter.exceptions;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.type_wrappers.*;
import net.darmo_creations.mccode.interpreter.types.MCMap;

/**
 * Exception thrown when an operator was called on an incompatible object.
 */
public class UnsupportedOperatorException extends MCCodeRuntimeException {
  /**
   * Create an exception for a unary operator.
   *
   * @param scope    The scope this exception was thrown from.
   * @param operator The operator that raised the error.
   * @param type     Type of the object the operator was applied to.
   */
  public UnsupportedOperatorException(final Scope scope, final UnaryOperator operator,
                                      final TypeBase<?> type) {
    super(scope, buildErrorObject(operator, type),
        "mccode.interpreter.error.unsupported_operator_unary",
        type, operator.getSymbol());
  }

  /**
   * Create an exception a binary operator.
   *
   * @param scope    The scope this exception was thrown from.
   * @param operator The operator that raised the error.
   * @param type1    Type of the operator’s left operand.
   * @param type2    Type of the operator’s right operand.
   */
  public UnsupportedOperatorException(final Scope scope, final BinaryOperator operator,
                                      final TypeBase<?> type1, final TypeBase<?> type2) {
    super(scope, buildErrorObject(operator, type1, type2),
        "mccode.interpreter.error.unsupported_binary_operator",
        type1, operator.getSymbol(), type2);
  }

  /**
   * Create an exception a ternary operator.
   *
   * @param scope    The scope this exception was thrown from.
   * @param operator The operator that raised the error.
   * @param type1    Type of the first operand.
   * @param type2    Type of the second operand.
   * @param type3    Type of the third operand.
   */
  public UnsupportedOperatorException(final Scope scope, final TernaryOperator operator,
                                      final TypeBase<?> type1, final TypeBase<?> type2, final TypeBase<?> type3) {
    super(scope, buildErrorObject(operator, type1, type2, type3),
        "mccode.interpreter.error.unsupported_ternary_operator",
        type1, operator.getSymbol(), type2, type3);
  }

  @Override
  public String getName() {
    return "unsupported_operator_error";
  }

  private static MCMap buildErrorObject(final Operator operator, final TypeBase<?>... types) {
    MCMap map = new MCMap();
    map.put("operator", operator.getSymbol());
    for (int i = 0; i < types.length; i++) {
      map.put("type_" + (i + 1), types[i].getName());
    }
    return map;
  }
}
