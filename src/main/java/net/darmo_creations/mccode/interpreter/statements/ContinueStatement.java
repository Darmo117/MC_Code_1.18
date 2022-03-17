package net.darmo_creations.mccode.interpreter.statements;

import net.darmo_creations.mccode.interpreter.Scope;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/**
 * Statement that is used to skip to the next iteration of a loop.
 */
public class ContinueStatement extends Statement {
  public static final int ID = 61;

  /**
   * Create a continue statement.
   *
   * @param line   The line this statement starts on.
   * @param column The column in the line this statement starts at.
   */
  public ContinueStatement(final int line, final int column) {
    super(line, column);
  }

  /**
   * Create a continue statement from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public ContinueStatement(final CompoundTag tag) {
    super(tag);
  }

  @Override
  protected StatementAction executeWrapped(final Scope scope) {
    return StatementAction.CONTINUE_LOOP;
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public String toString() {
    return "continue;";
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof ContinueStatement;
  }

  @Override
  public int hashCode() {
    return Objects.hash();
  }
}
