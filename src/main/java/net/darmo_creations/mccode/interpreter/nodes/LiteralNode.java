package net.darmo_creations.mccode.interpreter.nodes;

import net.darmo_creations.mccode.interpreter.Scope;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Base node that represents a literal value (boolean, int, float, string, null, etc.)
 *
 * @param <T> Type of the literal’s value.
 */
public abstract class LiteralNode<T> extends Node {
  public static final String VALUE_KEY = "Value";

  protected final T value;

  /**
   * Create a literal node.
   *
   * @param value  Literal’s value.
   * @param line   The line this node starts on.
   * @param column The column in the line this node starts at.
   */
  public LiteralNode(final T value, final int line, final int column) {
    super(line, column);
    this.value = value;
  }

  /**
   * Create a literal node from deserializing function.
   *
   * @param tag          The tag to deserialize.
   * @param deserializer The type-specific function to deserialize the tag.
   */
  public LiteralNode(final CompoundTag tag, final Function<String, T> deserializer) {
    super(tag);
    this.value = deserializer.apply(VALUE_KEY);
  }

  @Override
  protected Object evaluateWrapped(final Scope scope) {
    return this.value;
  }

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = super.writeToNBT();
    BiConsumer<String, T> serializer = this.getValueSerializer(tag);
    if (serializer != null) {
      serializer.accept(VALUE_KEY, this.value);
    }
    return tag;
  }

  /**
   * Return a method for the given NBT tag that accepts a string key and this literal’s value.
   *
   * @param tag The tag to serialize the value into.
   * @return The serializing function.
   */
  protected abstract BiConsumer<String, T> getValueSerializer(final CompoundTag tag);

  @Override
  public String toString() {
    return String.valueOf(this.value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    LiteralNode<?> that = (LiteralNode<?>) o;
    return Objects.equals(this.value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.value);
  }
}
