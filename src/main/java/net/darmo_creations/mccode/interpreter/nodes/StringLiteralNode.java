package net.darmo_creations.mccode.interpreter.nodes;

import net.darmo_creations.mccode.interpreter.Utils;
import net.darmo_creations.mccode.interpreter.exceptions.MCCodeException;
import net.minecraft.nbt.CompoundTag;

import java.util.function.BiConsumer;

/**
 * A node that represents a string literal.
 */
public class StringLiteralNode extends LiteralNode<String> {
  public static final int ID = 4;

  /**
   * Create a string literal node.
   *
   * @param value  String value.
   * @param line   The line this node starts on.
   * @param column The column in the line this node starts at.
   */
  public StringLiteralNode(final String value, final int line, final int column) {
    super(value, line, column);
    if (value.matches("[\t\b\r\f]")) {
      throw new MCCodeException("illegal character(s) in string literal");
    }
  }

  /**
   * Create a string literal node from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public StringLiteralNode(final CompoundTag tag) {
    super(tag, tag::getString);
  }

  @Override
  protected BiConsumer<String, String> getValueSerializer(final CompoundTag tag) {
    return tag::putString;
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public String toString() {
    return Utils.escapeString(this.value);
  }
}
