package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.MemberFunction;
import net.darmo_creations.mccode.interpreter.ObjectProperty;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Method;
import net.darmo_creations.mccode.interpreter.annotations.Property;
import net.darmo_creations.mccode.interpreter.annotations.PropertySetter;
import net.darmo_creations.mccode.interpreter.exceptions.*;
import net.minecraft.nbt.CompoundTag;

import java.util.*;

/**
 * A type is a class that wraps a data type to make it usable from programs.
 * It may possess properties and methods annoted with {@link Property}, {@link PropertySetter} or {@link Method}.
 * <p>
 * Once declared in an {@link ProgramManager} instance it becomes available to programs of the manager.
 *
 * @param <T> Type of data wrapped by this class.
 */
public abstract class TypeBase<T> {
  public static final String NAME_KEY = "Name";

  // Set by ProgramManager.declareType() method
  @SuppressWarnings("unused")
  private String name;
  // Set by ProgramManager.declareType() method
  @SuppressWarnings("unused")
  private boolean generateCastOperator;
  // Set by ProgramManager.declareType() method
  @SuppressWarnings("unused")
  private String doc;
  // Set by ProgramManager.processTypeAnnotations() method
  @SuppressWarnings("unused")
  private Map<String, ObjectProperty> properties;
  // Set by ProgramManager.processTypeAnnotations() method
  @SuppressWarnings("unused")
  private Map<String, MemberFunction> methods;

  /**
   * Return the MCCode name of this type.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Return the class of the wrapped type.
   */
  public abstract Class<T> getWrappedType();

  /**
   * Return whether to generate an explicit cast operator.
   */
  public boolean generateCastOperator() {
    return this.generateCastOperator;
  }

  /**
   * Return the documentation string for this type.
   */
  public Optional<String> getDoc() {
    return Optional.ofNullable(this.doc);
  }

  /**
   * Return the name of all properties.
   *
   * @param self Instance object to get the properties of.
   * @throws TypeException If the MCCode type of the instance differs from this type.
   */
  public List<String> getPropertiesNames(final Object self) {
    if (this != ProgramManager.getTypeForValue(self)) {
      throw new TypeException(String.format("property %s expected instance of type %s, got %s",
          this.getName(), this.getWrappedType(), self != null ? self.getClass() : null));
    }
    ArrayList<String> list = new ArrayList<>(this.properties.keySet());
    //noinspection unchecked
    list.addAll(this.getAdditionalPropertiesNames((T) self));
    list.sort(Comparator.comparing(String::toLowerCase));
    return list;
  }

  /**
   * Return a list of additional property names that may be accessible through
   * the {@link #__get_property__(Scope, T, String)} method.
   */
  protected List<String> getAdditionalPropertiesNames(final T self) {
    return Collections.emptyList();
  }

  public TypeBase<?> getPropertyType(final Scope scope, final Object self, final String propertyName) {
    if (this.properties.containsKey(propertyName)) {
      return this.properties.get(propertyName).getType();
    } else {
      if (this != ProgramManager.getTypeForValue(self)) {
        throw new TypeException(String.format("property %s expected instance of type %s, got %s",
            this.getName(), this.getWrappedType(), self != null ? self.getClass() : null));
      }
      //noinspection unchecked
      return ProgramManager.getTypeForValue(this.__get_property__(scope, (T) self, propertyName));
    }
  }

  /**
   * Return the property object with the given name.
   *
   * @param name Property’s name.
   * @return The property object.
   */
  public ObjectProperty getProperty(final String name) {
    return this.properties.get(name);
  }

  /**
   * Return all property objects of this type.
   * Does not include dynamic properties accessible through {@link #__get_property__(Scope, Object, String)}.
   */
  public Map<String, ObjectProperty> getProperties() {
    return this.properties;
  }

  /**
   * Return the value of a property for the given instance object.
   *
   * @param scope        The scope this property is queried from.
   * @param self         Instance object to query. Must match this type’s wrapped type.
   * @param propertyName Name of the property.
   * @return Value of the property.
   * @throws TypeException       If the MCCode type of the instance differs from this type.
   * @throws EvaluationException If the instance object does not have a property with the given name.
   */
  public Object getPropertyValue(final Scope scope, final Object self, final String propertyName) {
    if (this.properties.containsKey(propertyName)) {
      return this.properties.get(propertyName).get(self);
    } else {
      if (this != ProgramManager.getTypeForValue(self)) {
        throw new TypeException(String.format("property %s expected instance of type %s, got %s",
            this.getName(), this.getWrappedType(), self != null ? self.getClass() : null));
      }
      //noinspection unchecked
      return this.__get_property__(scope, (T) self, propertyName);
    }
  }

  /**
   * Set the value of a property for the given instance object.
   *
   * @param scope        The scope this property is set from.
   * @param self         Instance object to use. Must match this type’s wrapped type.
   * @param propertyName Name of the property.
   * @param value        Value to assign to the property.
   * @throws TypeException       If the MCCode type of the instance differs from this type.
   * @throws EvaluationException If the instance object does not have a property with the given name.
   */
  public void setPropertyValue(final Scope scope, final Object self, final String propertyName, final Object value) {
    if (this.properties.containsKey(propertyName)) {
      this.properties.get(propertyName).set(scope, self, value);
    } else {
      if (this != ProgramManager.getTypeForValue(self)) {
        throw new TypeException(String.format("property %s expected instance of type %s, got %s",
            this.getName(), this.getWrappedType(), self != null ? self.getClass() : null));
      }
      //noinspection unchecked
      this.__set_property__(scope, (T) self, propertyName, value);
    }
  }

  /**
   * Return the method with the given name.
   *
   * @param name Method’s name.
   * @return The method.
   */
  public MemberFunction getMethod(final String name) {
    return this.methods.get(name);
  }

  /**
   * Return all methods of this type.
   */
  public Map<String, MemberFunction> getMethods() {
    return this.methods;
  }

  /**
   * Cast the given object into an object of this type’s wrapped type.
   * <p>
   * Called when evaluating operators or functions.
   *
   * @param scope The scope this operation is performed from.
   * @param o     The object to cast.
   * @return The cast object.
   * @throws CastException If the object cannot be cast into this type’s wrapped type.
   */
  public T implicitCast(final Scope scope, final Object o) throws CastException {
    if (o == null) {
      return null;
    }
    if (this.getWrappedType().isAssignableFrom(o.getClass())) {
      //noinspection unchecked
      return (T) o;
    }
    throw new CastException(scope, this, ProgramManager.getTypeForValue(o));
  }

  /**
   * Cast the given object into an object of this type’s wrapped type.
   * Default behavior is the same as implicit cast.
   * <p>
   * Called when this type’s cast operator is called.
   *
   * @param scope The scope this operation is performed from.
   * @param o     The object to cast.
   * @return The new instance.
   * @throws MCCodeRuntimeException If this type does not have a cast operator,
   *                                i.e. method {@link #generateCastOperator()} returns false.
   */
  public T explicitCast(final Scope scope, final Object o) throws MCCodeRuntimeException {
    if (this.generateCastOperator()) {
      return this.implicitCast(scope, o);
    }
    throw new EvaluationException(scope, "mccode.interpreter.error.no_cast_operator_for_type", this);
  }

  /**
   * Convert the given object to a boolean value.
   *
   * @param self An instance of this type to convert.
   * @return The boolean value for the object.
   */
  public final boolean toBoolean(final Object self) {
    //noinspection unchecked
    return this.__bool__((T) self);
  }

  /**
   * Returns the string representation of an object of this type.
   *
   * @param self An instance of this type to convert.
   * @return The string representation of the object.
   */
  public final String toString(final Object self) {
    //noinspection unchecked
    return this.__str__((T) self);
  }

  /**
   * Return a deep copy of the given object.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @return A deep copy of the argument.
   */
  public T copy(final Scope scope, final Object self) {
    this.ensureType(self, String.format("attempt to clone object of type \"%s\" from type \"%s\"",
        ProgramManager.getTypeForValue(self), this));
    //noinspection unchecked
    return this.__copy__(scope, (T) self);
  }

  /**
   * Applie an operator on the given values.
   *
   * @param scope    Scope the operator is applied from.
   * @param operator Operator to apply.
   * @param self     The object to apply the operator on.
   * @param o1       An optional second object to apply the operator on.
   *                 Ignored if the passed operator supports only one operand.
   * @param o2       An optional third object to apply the operator on.
   *                 Ignored if the passed operator supports only one or two operands.
   * @param inPlace  Whether this operator should modify the object instead of creating a new instance.
   *                 May be ignored by some operators.
   * @return The result of the operation; null if the operator does not return anything.
   * @throws MCCodeException If the given operator is invalid.
   */
  public Object applyOperator(final Scope scope, final Operator operator, Object self, Object o1, Object o2, final boolean inPlace) {
    if (ProgramManager.getTypeForValue(self) != this) {
      throw new MCCodeException(String.format("operator %s expected instance object of type %s, got %s",
          operator.getSymbol(), this.getWrappedType(), self.getClass()));
    }
    //noinspection unchecked
    T $this = (T) self;

    if (operator instanceof UnaryOperator) {
      return switch ((UnaryOperator) operator) {
        case MINUS -> this.__minus__(scope, $this);
        case NOT -> this.__not__(scope, $this);
        case ITERATE -> this.__iter__(scope, $this);
        case LENGTH -> this.__len__(scope, $this);
      };
    } else if (operator instanceof BinaryOperator) {
      switch ((BinaryOperator) operator) {
        case PLUS:
          return this.__add__(scope, $this, o1, inPlace);
        case SUB:
          return this.__sub__(scope, $this, o1, inPlace);
        case MUL:
          return this.__mul__(scope, $this, o1, inPlace);
        case DIV:
          return this.__div__(scope, $this, o1, inPlace);
        case INT_DIV:
          return this.__intdiv__(scope, $this, o1, inPlace);
        case MOD:
          return this.__mod__(scope, $this, o1, inPlace);
        case POW:
          return this.__pow__(scope, $this, o1, inPlace);
        case EQUAL:
          return this.__eq__(scope, $this, o1);
        case NOT_EQUAL:
          return this.__neq__(scope, $this, o1);
        case GT:
          return this.__gt__(scope, $this, o1);
        case GE:
          return this.__ge__(scope, $this, o1);
        case LT:
          return this.__lt__(scope, $this, o1);
        case LE:
          return this.__le__(scope, $this, o1);
        case IN:
          return this.__in__(scope, $this, o1);
        case NOT_IN:
          Object res = this.__in__(scope, $this, o1);
          return !ProgramManager.getTypeForValue(res).toBoolean(res);
        case AND:
          return this.__and__(scope, $this, o1);
        case OR:
          return this.__or__(scope, $this, o1);
        case GET_ITEM:
          return this.__get_item__(scope, $this, o1);
        case DEL_ITEM:
          this.__del_item__(scope, $this, o1);
          return null;
      }
    } else if (operator instanceof TernaryOperator) {
      //noinspection SwitchStatementWithTooFewBranches
      switch ((TernaryOperator) operator) {
        case SET_ITEM -> {
          this.__set_item__(scope, $this, o1, o2);
          return null;
        }
      }
    }

    throw new MCCodeException("invalid operator " + operator);
  }

  /**
   * Method that performs the GET_ITEM operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @param key   Key to get the value of.
   * @return The value for the key.
   */
  protected Object __get_item__(Scope scope, T self, Object key) {
    throw new UnsupportedOperatorException(scope, BinaryOperator.GET_ITEM, this, ProgramManager.getTypeForValue(key));
  }

  /**
   * Method that performs the SET_ITEM operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @param key   Key to set the value of.
   * @param value The value to set.
   */
  protected void __set_item__(Scope scope, T self, Object key, Object value) {
    throw new UnsupportedOperatorException(scope, TernaryOperator.SET_ITEM, this, ProgramManager.getTypeForValue(key), ProgramManager.getTypeForValue(value));
  }

  /**
   * Method that performs the DELETE_ITEM operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @param key   Key to delete.
   */
  protected void __del_item__(Scope scope, T self, Object key) {
    throw new UnsupportedOperatorException(scope, BinaryOperator.DEL_ITEM, this, ProgramManager.getTypeForValue(key));
  }

  /**
   * Method that returns the value of the given property.
   * <p>
   * Called when no property defined through the {@link Property} annotation was found
   * by {@link #getPropertyValue(Scope, Object, String)}.
   *
   * @param scope        Scope the operation is performed from.
   * @param self         Instance of this type to apply the operator to.
   * @param propertyName Name of the property.
   * @throws EvaluationException If the instance object does not have a property with the given name.
   */
  protected Object __get_property__(Scope scope, T self, final String propertyName) {
    throw new EvaluationException(scope, "mccode.interpreter.error.no_property_for_type", this, propertyName);
  }

  /**
   * Method that returns the value of the given property.
   * <p>
   * Called when no property defined through the {@link PropertySetter} annotation was found
   * by {@link #setPropertyValue(Scope, Object, String, Object)}.
   *
   * @param scope        Scope the operation is performed from.
   * @param self         Instance of this type to apply the operator to.
   * @param propertyName Name of the property.
   * @param value        Property’s new value.
   * @throws EvaluationException If the instance object does not have a property with the given name.
   */
  protected void __set_property__(Scope scope, T self, final String propertyName, Object value) {
    throw new EvaluationException(scope, "mccode.interpreter.error.no_property_for_type", this, propertyName);
  }

  /**
   * Method that performs the unary "minus" operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @return The result of the operator.
   */
  protected Object __minus__(Scope scope, T self) {
    throw new UnsupportedOperatorException(scope, UnaryOperator.MINUS, this);
  }

  /**
   * Method that performs the "not" operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @return The result of the operator.
   */
  @SuppressWarnings("unused")
  protected Object __not__(Scope scope, T self) {
    return !this.toBoolean(self);
  }

  /**
   * Method that performs the "addition" operation.
   *
   * @param scope   Scope the operation is performed from.
   * @param self    Instance of this type to apply the operator to.
   * @param o       The second object.
   * @param inPlace Whether this operation should modify the instance instead of creating a new one.
   * @return The result of the operator.
   */
  protected Object __add__(Scope scope, T self, Object o, final boolean inPlace) {
    throw new UnsupportedOperatorException(scope, BinaryOperator.PLUS, this, ProgramManager.getTypeForValue(o));
  }

  /**
   * Method that performs the "subtraction" operation.
   *
   * @param scope   Scope the operation is performed from.
   * @param self    Instance of this type to apply the operator to.
   * @param o       The second object.
   * @param inPlace Whether this operation should modify the instance instead of creating a new one.
   * @return The result of the operator.
   */
  protected Object __sub__(Scope scope, T self, Object o, final boolean inPlace) {
    throw new UnsupportedOperatorException(scope, BinaryOperator.SUB, this, ProgramManager.getTypeForValue(o));
  }

  /**
   * Method that performs the "multiplication" operation.
   *
   * @param scope   Scope the operation is performed from.
   * @param self    Instance of this type to apply the operator to.
   * @param o       The second object.
   * @param inPlace Whether this operation should modify the instance instead of creating a new one.
   * @return The result of the operator.
   */
  protected Object __mul__(Scope scope, T self, Object o, final boolean inPlace) {
    throw new UnsupportedOperatorException(scope, BinaryOperator.MUL, this, ProgramManager.getTypeForValue(o));
  }

  /**
   * Method that performs the "division" operation.
   *
   * @param scope   Scope the operation is performed from.
   * @param self    Instance of this type to apply the operator to.
   * @param o       The second object.
   * @param inPlace Whether this operation should modify the instance instead of creating a new one.
   * @return The result of the operator.
   */
  protected Object __div__(Scope scope, T self, Object o, final boolean inPlace) {
    throw new UnsupportedOperatorException(scope, BinaryOperator.DIV, this, ProgramManager.getTypeForValue(o));
  }

  /**
   * Method that performs the "integer division" operation.
   *
   * @param scope   Scope the operation is performed from.
   * @param self    Instance of this type to apply the operator to.
   * @param o       The second object.
   * @param inPlace Whether this operation should modify the instance instead of creating a new one.
   * @return The result of the operator.
   */
  protected Object __intdiv__(Scope scope, T self, Object o, final boolean inPlace) {
    return (long) Math.floor(((Number) this.__div__(scope, self, o, inPlace)).doubleValue());
  }

  /**
   * Method that performs the "modulus" operation.
   *
   * @param scope   Scope the operation is performed from.
   * @param self    Instance of this type to apply the operator to.
   * @param o       The second object.
   * @param inPlace Whether this operation should modify the instance instead of creating a new one.
   * @return The result of the operator.
   */
  protected Object __mod__(Scope scope, T self, Object o, final boolean inPlace) {
    throw new UnsupportedOperatorException(scope, BinaryOperator.MOD, this, ProgramManager.getTypeForValue(o));
  }

  /**
   * Method that performs the "power/exponent" operation.
   *
   * @param scope   Scope the operation is performed from.
   * @param self    Instance of this type to apply the operator to.
   * @param o       The second object.
   * @param inPlace Whether this operation should modify the instance instead of creating a new one.
   * @return The result of the operator.
   */
  protected Object __pow__(Scope scope, T self, Object o, final boolean inPlace) {
    throw new UnsupportedOperatorException(scope, BinaryOperator.POW, this, ProgramManager.getTypeForValue(o));
  }

  /**
   * Method that performs the "equality" operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @param o     The second object.
   * @return The result of the operator.
   */
  protected Object __eq__(Scope scope, T self, Object o) {
    return self == o;
  }

  /**
   * Method that performs the "inequality" operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @param o     The second object.
   * @return The result of the operator.
   */
  protected Object __neq__(Scope scope, T self, Object o) {
    return !(Boolean) this.__eq__(scope, self, o);
  }

  /**
   * Method that performs the "greater than" operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @param o     The second object.
   * @return The result of the operator.
   */
  protected Object __gt__(Scope scope, T self, Object o) {
    throw new UnsupportedOperatorException(scope, BinaryOperator.GT, this, ProgramManager.getTypeForValue(o));
  }

  /**
   * Method that performs the "greater than or equal to" operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @param o     The second object.
   * @return The result of the operator.
   */
  protected Object __ge__(Scope scope, T self, Object o) {
    return ((Boolean) this.__gt__(scope, self, o)) || ((Boolean) this.__eq__(scope, self, o));
  }

  /**
   * Method that performs the "less than" operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @param o     The second object.
   * @return The result of the operator.
   */
  protected Object __lt__(Scope scope, T self, Object o) {
    return !(Boolean) this.__ge__(scope, self, o);
  }

  /**
   * Method that performs the "less than or equal to" operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @param o     The second object.
   * @return The result of the operator.
   */
  protected Object __le__(Scope scope, T self, Object o) {
    return !(Boolean) this.__gt__(scope, self, o);
  }

  /**
   * Method that performs the "in" operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @param o     The second object.
   * @return The result of the operator.
   */
  protected Object __in__(Scope scope, T self, Object o) {
    throw new UnsupportedOperatorException(scope, BinaryOperator.IN, this, ProgramManager.getTypeForValue(o));
  }

  /**
   * Method that performs the "len" operation.
   * It should return the length of the given object.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @return The length of the object.
   */
  protected long __len__(Scope scope, T self) {
    throw new UnsupportedOperatorException(scope, UnaryOperator.LENGTH, this);
  }

  /**
   * Method that performs the "logical and" operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @param o     The second object.
   * @return The result of the operator.
   */
  @SuppressWarnings("unused")
  protected Object __and__(Scope scope, T self, Object o) {
    if (!this.toBoolean(self)) {
      return self;
    } else {
      return o;
    }
  }

  /**
   * Method that performs the "logical or" operation.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @param o     The second object.
   * @return The result of the operator.
   */
  @SuppressWarnings("unused")
  protected Object __or__(Scope scope, T self, Object o) {
    if (this.toBoolean(self)) {
      return self;
    } else {
      return o;
    }
  }

  /**
   * Convert the given instance object to a boolean value.
   *
   * @param self An instance of this type to convert.
   * @return The boolean value for the object.
   */
  protected boolean __bool__(final T self) {
    return true;
  }

  /**
   * Returns the string representation of an object of this type.
   *
   * @param self An instance of this type to convert.
   * @return The string representation of the object.
   */
  protected String __str__(final T self) {
    return self.toString();
  }

  /**
   * Method that performs the "iterator" operation.
   * It should return an iterator over the values of the given object.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @return An iterator over the values of the object..
   */
  protected Iterator<?> __iter__(Scope scope, T self) {
    throw new UnsupportedOperatorException(scope, UnaryOperator.ITERATE, this);
  }

  /**
   * Method that performs the "deep copy" operation.
   * It should return a deep copy of the given object.
   *
   * @param scope Scope the operation is performed from.
   * @param self  Instance of this type to apply the operator to.
   * @return A deep copy of the argument.
   */
  protected T __copy__(final Scope scope, final T self) {
    return self;
  }

  /**
   * Serialize an instance of this type to an NBT tag.
   *
   * @param self Instance of this type to apply the serialize.
   * @return A tag.
   */
  public CompoundTag writeToNBT(final Object self) {
    this.ensureType(self, String.format("attempt to serialize object of type \"%s\" from type \"%s\"",
        ProgramManager.getTypeForValue(self), this));
    //noinspection unchecked
    return this._writeToNBT((T) self);
  }

  /**
   * Serialize an instance of this type to an NBT tag.
   *
   * @param self Instance of this type to apply the serialize.
   * @return A tag.
   */
  protected CompoundTag _writeToNBT(final T self) {
    CompoundTag tag = new CompoundTag();
    tag.putString(NAME_KEY, this.getName());
    return tag;
  }

  /**
   * Create a new instance of this type by deserializing the given NBT tag.
   *
   * @param scope Scope the operation is performed from.
   * @param tag   Tag to deserialize.
   * @return A new instance of this type.
   */
  public abstract T readFromNBT(final Scope scope, final CompoundTag tag);

  @Override
  public String toString() {
    return this.getName();
  }

  /**
   * Raise an exception if the type of the given object does not match this type.
   */
  private void ensureType(final Object o, final String errorMessage) {
    if (ProgramManager.getTypeForValue(o) != this) {
      throw new TypeException(errorMessage);
    }
  }
}
