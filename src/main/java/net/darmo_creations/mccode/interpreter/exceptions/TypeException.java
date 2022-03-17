package net.darmo_creations.mccode.interpreter.exceptions;

/**
 * Exception thrown when a type error occurs while processing type annotations
 * or an object is passed to the wrong type.
 */
public class TypeException extends MCCodeException {
  public TypeException(final String s) {
    super(s);
  }
}
