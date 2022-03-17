package net.darmo_creations.mccode.interpreter.statements;

import net.darmo_creations.mccode.interpreter.Scope;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/**
 * Statement that is used to exit from a loop.
 */
public class BreakStatement extends Statement {
  public static final int ID = 60;

  /**
   * Create a break statement.
   *
   * @param line   The line this statement starts on.
   * @param column The column in the line this statement starts at.
   */
  public BreakStatement(final int line, final int column) {
    super(line, column);
  }

  /**
   * Create a break statement from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public BreakStatement(final CompoundTag tag) {
    super(tag);
  }

  @Override
  protected StatementAction executeWrapped(final Scope scope) {
    return StatementAction.EXIT_LOOP;
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public String toString() {
    return "break;";
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof BreakStatement;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
