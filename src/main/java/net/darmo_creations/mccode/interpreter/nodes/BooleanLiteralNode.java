package net.darmo_creations.mccode.interpreter.nodes;

import net.minecraft.nbt.CompoundTag;

import java.util.function.BiConsumer;

/**
 * A node that represents a boolean literal.
 */
public class BooleanLiteralNode extends LiteralNode<Boolean> {
  public static final int ID = 1;

  /**
   * Create a boolean literal node.
   *
   * @param value  Boolean value.
   * @param line   The line this node starts on.
   * @param column The column in the line this node starts at.
   */
  public BooleanLiteralNode(final boolean value, final int line, final int column) {
    super(value, line, column);
  }

  /**
   * Create a boolean literal node from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public BooleanLiteralNode(final CompoundTag tag) {
    super(tag, tag::getBoolean);
  }

  @Override
  protected BiConsumer<String, Boolean> getValueSerializer(final CompoundTag tag) {
    return tag::putBoolean;
  }

  @Override
  public int getID() {
    return ID;
  }
}
