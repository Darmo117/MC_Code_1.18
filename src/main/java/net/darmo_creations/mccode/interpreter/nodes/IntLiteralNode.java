package net.darmo_creations.mccode.interpreter.nodes;

import net.minecraft.nbt.CompoundTag;

import java.util.function.BiConsumer;

/**
 * A node that represents an integer literal.
 */
public class IntLiteralNode extends LiteralNode<Long> {
  public static final int ID = 2;

  /**
   * Create an integer literal node.
   *
   * @param value  Integer value.
   * @param line   The line this node starts on.
   * @param column The column in the line this node starts at.
   */
  public IntLiteralNode(final long value, final int line, final int column) {
    super(value, line, column);
  }

  /**
   * Create an int literal node from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public IntLiteralNode(final CompoundTag tag) {
    super(tag, tag::getLong);
  }

  @Override
  protected BiConsumer<String, Long> getValueSerializer(final CompoundTag tag) {
    return tag::putLong;
  }

  @Override
  public int getID() {
    return ID;
  }
}
