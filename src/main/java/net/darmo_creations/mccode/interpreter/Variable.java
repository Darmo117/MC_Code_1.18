package net.darmo_creations.mccode.interpreter;

import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.darmo_creations.mccode.interpreter.exceptions.MCCodeException;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/**
 * Variables can hold values of any type. They may be visible and editable from in-game commands.
 * A constant variable cannot have its value changed. If the deletable flag is set to false,
 * any attempt to delete through {@link Scope#declareVariable(Variable)} will throw an error.
 */
public class Variable implements NBTSerializable {
  public static final String NAME_KEY = "Name";
  public static final String PUBLIC_KEY = "Public";
  public static final String EDITABLE_KEY = "Editable";
  public static final String CONSTANT_KEY = "Constant";
  public static final String VALUE_KEY = "Value";
  public static final String TYPE_KEY = "Type";
  public static final String DELETABLE_KEY = "Deletable";

  private final String name;
  private final boolean publiclyVisible;
  private final boolean editableFromOutside;
  private final boolean constant;
  private final boolean deletable;
  private Object value;

  /**
   * Create a new variable.
   *
   * @param name                Variable’s name.
   * @param publiclyVisible     Whether this variable’s value should be accessible from outside the program.
   * @param editableFromOutside Whether this variable’s value should be editable from outside the program.
   * @param constant            Whether this variable’s value is constant or not.
   * @param deletable           Whether deletion of this variable should be prevented.
   * @param value               Variable’s value.
   */
  public Variable(final String name, boolean publiclyVisible, final boolean editableFromOutside, final boolean constant, final boolean deletable, Object value) {
    if (constant && editableFromOutside) {
      throw new MCCodeException("constant cannot be editable from outside the program");
    }
    if (!publiclyVisible && editableFromOutside) {
      throw new MCCodeException("private variable cannot be editable from outside the program");
    }
    this.name = name;
    this.value = value;
    this.constant = constant;
    this.publiclyVisible = publiclyVisible;
    this.editableFromOutside = editableFromOutside;
    this.deletable = deletable;
  }

  /**
   * Create a variable from an NBT tag.
   *
   * @param tag   The tag to deserialize.
   * @param scope The scope this variable is deserialized in.
   */
  public Variable(final CompoundTag tag, final Scope scope) {
    this.name = tag.getString(NAME_KEY);
    this.publiclyVisible = tag.getBoolean(PUBLIC_KEY);
    this.editableFromOutside = tag.getBoolean(EDITABLE_KEY);
    this.constant = tag.getBoolean(CONSTANT_KEY);
    this.deletable = tag.getBoolean(DELETABLE_KEY);
    this.value = ProgramManager.getTypeForName(tag.getString(TYPE_KEY)).readFromNBT(scope, tag.getCompound(VALUE_KEY));
  }

  /**
   * Return this variable’s name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Return this variable’s value.
   *
   * @param scope       The scope this variable is declared in.
   * @param fromOutside Whether this operation is performed from outside the program.
   * @throws EvaluationException If the variable is queried from outside the program but is not publicly visible.
   */
  public Object getValue(final Scope scope, final boolean fromOutside) {
    if (!this.publiclyVisible && fromOutside) {
      throw new EvaluationException(scope, "mccode.interpreter.error.getting_from_outside", this.name);
    }
    return this.value;
  }

  /**
   * Set this variable’s value.
   *
   * @param scope       The scope this variable is declared in.
   * @param value       New variable value.
   * @param fromOutside Whether this operation is performed from outside the program.
   * @throws EvaluationException If the variable is constant or is edited from outside the program
   *                             but editable flag is false.
   */
  public void setValue(final Scope scope, final Object value, final boolean fromOutside) {
    if (this.isConstant()) {
      throw new EvaluationException(scope, "mccode.interpreter.error.setting_constant_variable", this.name);
    }
    if (!this.isEditableFromOutside() && fromOutside) {
      throw new EvaluationException(scope, "mccode.interpreter.error.setting_from_outside", this.name);
    }
    this.value = value;
  }

  /**
   * Return whether this variable is constant.
   */
  public boolean isConstant() {
    return this.constant;
  }

  /**
   * Return whether this variable should be visible from outside the program.
   */
  public boolean isPubliclyVisible() {
    return this.publiclyVisible;
  }

  /**
   * Return whether this variable should be editable from outside the program.
   */
  public boolean isEditableFromOutside() {
    return this.editableFromOutside;
  }

  /**
   * Return whether this variable can be deleted.
   */
  public boolean isDeletable() {
    return this.deletable;
  }

  /**
   * Serialize this variable to an NBT tag.
   *
   * @return The tag.
   */
  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putString(NAME_KEY, this.name);
    tag.putBoolean(PUBLIC_KEY, this.publiclyVisible);
    tag.putBoolean(EDITABLE_KEY, this.editableFromOutside);
    tag.putBoolean(CONSTANT_KEY, this.constant);
    tag.putBoolean(DELETABLE_KEY, this.deletable);
    TypeBase<?> type = ProgramManager.getTypeForValue(this.value);
    tag.putString(TYPE_KEY, type.getName());
    tag.put(VALUE_KEY, type.writeToNBT(this.value));
    return tag;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    Variable variable = (Variable) o;
    return this.publiclyVisible == variable.publiclyVisible
        && this.editableFromOutside == variable.editableFromOutside
        && this.constant == variable.constant
        && this.deletable == variable.deletable
        && this.name.equals(variable.name)
        && Objects.equals(this.value, variable.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.publiclyVisible, this.editableFromOutside, this.constant, this.deletable, this.value);
  }

  @Override
  public String toString() {
    return String.format("Variable{name=%s,publiclyVisible=%b,editableFromOutside=%b,constant=%b,deletable=%b,value=%s}",
        this.name,
        this.publiclyVisible,
        this.editableFromOutside,
        this.constant,
        this.deletable,
        this.value);
  }
}
