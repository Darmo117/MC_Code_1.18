package net.darmo_creations.mccode.interpreter.nodes;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/**
 * A node that represents a call to an object’s property.
 */
public class PropertyCallNode extends Node {
  public static final int ID = 101;

  public static final String INSTANCE_KEY = "Instance";
  public static final String PROPERTY_NAME_KEY = "PropertyName";

  private final Node object;
  private final String propertyName;

  /**
   * Create a node that represents a call to an object’s property.
   *
   * @param object       Expression that evaluates to the object to get the property of.
   * @param propertyName Name of the property.
   * @param line         The line this node starts on.
   * @param column       The column in the line this node starts at.
   */
  public PropertyCallNode(final Node object, final String propertyName, final int line, final int column) {
    super(line, column);
    this.object = Objects.requireNonNull(object);
    this.propertyName = Objects.requireNonNull(propertyName);
  }

  /**
   * Create a node that represents a call to an object’s property from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public PropertyCallNode(final CompoundTag tag) {
    super(tag);
    this.object = NodeNBTHelper.getNodeForTag(tag.getCompound(INSTANCE_KEY));
    this.propertyName = tag.getString(PROPERTY_NAME_KEY);
  }

  @Override
  protected Object evaluateWrapped(final Scope scope) {
    Object obj = this.object.evaluate(scope);
    TypeBase<?> objectType = ProgramManager.getTypeForValue(obj);
    return objectType.getPropertyValue(scope, obj, this.propertyName);
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = super.writeToNBT();
    tag.put(INSTANCE_KEY, this.object.writeToNBT());
    tag.putString(PROPERTY_NAME_KEY, this.propertyName);
    return tag;
  }

  @Override
  public String toString() {
    return String.format("%s.%s", this.object, this.propertyName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    PropertyCallNode that = (PropertyCallNode) o;
    return this.object.equals(that.object) && this.propertyName.equals(that.propertyName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.object, this.propertyName);
  }
}
