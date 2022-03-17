package net.darmo_creations.mccode.interpreter.type_wrappers;

/**
 * Common interface for enumerations that define operators.
 */
public interface Operator {
  /**
   * Return the symbol of this operator.
   */
  String getSymbol();

  /**
   * Return the number of operands this operator accepts.
   */
  int getArity();
}
