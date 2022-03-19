package net.darmo_creations.mccode.interpreter;

import net.darmo_creations.mccode.interpreter.exceptions.MCCodeException;
import net.darmo_creations.mccode.interpreter.parser.ProgramParser;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;

import java.util.Objects;

/**
 * A class that represents a function parameter.
 */
public class Parameter {
  private final String name;
  private final TypeBase<?> type;
  private final boolean nullable;

  /**
   * Create a function parameter.
   *
   * @param name Parameter’s name.
   * @param type Parameter’s type.
   */
  public Parameter(final String name, final TypeBase<?> type) {
    this(name, type, false);
  }

  /**
   * Create a function parameter.
   *
   * @param name     Parameter’s name.
   * @param type     Parameter’s type.
   * @param nullable Whether this parameter’s value may be null.
   */
  public Parameter(final String name, final TypeBase<?> type, final boolean nullable) {
    if (!ProgramParser.IDENTIFIER_PATTERN.asPredicate().test(name)) {
      throw new MCCodeException("invalid parameter name \"%s\"".formatted(name));
    }
    this.name = name;
    this.type = Objects.requireNonNull(type);
    this.nullable = nullable;
  }

  /**
   * Return parameter’s name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Return parameter’s type.
   */
  public TypeBase<?> getType() {
    return this.type;
  }

  /**
   * Return whether this parameter’s value may be null.
   */
  public boolean isNullable() {
    return this.nullable;
  }

  @Override
  public String toString() {
    return String.format("Parameter{name=%s,type=%s,nullable=%b}", this.name, this.type, this.nullable);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    Parameter parameter = (Parameter) o;
    return this.name.equals(parameter.name) && this.type.equals(parameter.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.type);
  }
}
