package net.darmo_creations.mccode.interpreter.nodes;

import net.minecraft.nbt.CompoundTag;

import java.util.function.BiConsumer;

/**
 * A node that represents a float literal.
 */
public class FloatLiteralNode extends LiteralNode<Double> {
  public static final int ID = 3;

  /**
   * Create a float literal node.
   *
   * @param value  Float value.
   * @param line   The line this node starts on.
   * @param column The column in the line this node starts at.
   */
  public FloatLiteralNode(final double value, final int line, final int column) {
    super(value, line, column);
  }

  /**
   * Create a float literal node from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public FloatLiteralNode(final CompoundTag tag) {
    super(tag, tag::getDouble);
  }

  @Override
  protected BiConsumer<String, Double> getValueSerializer(final CompoundTag tag) {
    return tag::putDouble;
  }

  @Override
  public int getID() {
    return ID;
  }
}
