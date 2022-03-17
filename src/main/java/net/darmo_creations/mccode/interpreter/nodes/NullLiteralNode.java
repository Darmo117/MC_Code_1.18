package net.darmo_creations.mccode.interpreter.nodes;

import net.minecraft.nbt.CompoundTag;

import java.util.function.BiConsumer;

/**
 * A node that represents a null value.
 */
public class NullLiteralNode extends LiteralNode<Void> {
  public static final int ID = 0;

  /**
   * Create the null literal node.
   *
   * @param line   The line this node starts on.
   * @param column The column in the line this node starts at.
   */
  public NullLiteralNode(final int line, final int column) {
    super(null, line, column);
  }

  /**
   * Create the null literal node from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public NullLiteralNode(final CompoundTag tag) {
    super(tag, key -> null);
  }

  @Override
  protected BiConsumer<String, Void> getValueSerializer(final CompoundTag tag) {
    return null; // No value to serialize
  }

  @Override
  public int getID() {
    return ID;
  }
}
