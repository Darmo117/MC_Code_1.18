package net.darmo_creations.mccode.interpreter;

import net.darmo_creations.mccode.interpreter.annotations.Property;
import net.darmo_creations.mccode.interpreter.annotations.PropertySetter;
import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.darmo_creations.mccode.interpreter.exceptions.MCCodeException;
import net.darmo_creations.mccode.interpreter.exceptions.TypeException;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * This class represents a property of a builtin type.
 * It wraps one or two Java {@link Method} objects annoted by the
 * {@link Property} or {@link PropertySetter} annotations inside
 * a class extending {@link TypeBase}.
 */
public class ObjectProperty {
  private final TypeBase<?> hostType;
  private final String name;
  private final TypeBase<?> type;
  private final Method getter;
  private final Method setter;
  private final String doc;

  /**
   * Create a type property.
   *
   * @param hostType Type of property’s host.
   * @param name     Property’s name.
   * @param type     Property’s type.
   * @param getter   Java method to get this property’s value.
   * @param setter   Java method to set this property’s value.
   * @param doc      Property’s documentation. May be null.
   */
  public ObjectProperty(TypeBase<?> hostType, final String name, TypeBase<?> type, final Method getter, final Method setter, final String doc) {
    this.hostType = Objects.requireNonNull(hostType);
    this.name = Objects.requireNonNull(name);
    this.type = Objects.requireNonNull(type);
    this.getter = Objects.requireNonNull(getter);
    this.setter = setter;
    this.doc = doc;
  }

  /**
   * Return this property’s name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Return this property’s documentation.
   */
  public Optional<String> getDoc() {
    return Optional.ofNullable(this.doc);
  }

  /**
   * Return the type of this property.
   */
  public TypeBase<?> getType() {
    return this.type;
  }

  /**
   * Return the value of this property for the given instance.
   *
   * @param self The instance to get the value from.
   * @return The property’s value.
   * @throws TypeException If the MCCode type of the instance differs from this property’s type.
   */
  public Object get(final Object self) {
    if (this.hostType != ProgramManager.getTypeForValue(self)) {
      throw new TypeException(String.format("property %s expected instance of type %s, got %s",
          this.getName(), this.hostType.getWrappedType(), self != null ? self.getClass() : null));
    }
    try {
      return this.getter.invoke(this.hostType, self);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new MCCodeException(e);
    }
  }

  /**
   * Set the value of this property for the given instance.
   *
   * @param self  The instance to set value on.
   * @param value The new value.
   * @throws TypeException       If the MCCode type of the instance differs from this property’s type.
   * @throws EvaluationException If this property cannot be set.
   */
  public void set(final Scope scope, Object self, final Object value) throws EvaluationException {
    if (this.setter != null) {
      if (this.hostType != ProgramManager.getTypeForValue(self)) {
        throw new TypeException(String.format("property %s expected instance of type %s, got %s",
            this.getName(), this.hostType.getWrappedType(), self != null ? self.getClass() : null));
      }
      try {
        this.setter.invoke(this.hostType, self, this.type.copy(scope, this.type.implicitCast(scope, value)));
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new MCCodeException(e);
      }
    } else {
      throw new EvaluationException(scope, "mccode.interpreter.error.set_property",
          ProgramManager.getTypeForValue(self), this.name);
    }
  }
}
