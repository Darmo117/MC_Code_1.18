package net.darmo_creations.mccode.interpreter.statements;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.nodes.Node;
import net.darmo_creations.mccode.interpreter.nodes.NodeNBTHelper;
import net.darmo_creations.mccode.interpreter.type_wrappers.IntType;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/**
 * Statement that pauses the execution of a program.
 */
public class WaitStatement extends Statement {
  public static final int ID = 50;

  public static final String TICKS_KEY = "Ticks";

  private final Node value;

  /**
   * Create a "wait" statement.
   *
   * @param value  Amount of ticks to pause the program. Expression that evaluates to a positive integer.
   * @param line   The line this statement starts on.
   * @param column The column in the line this statement starts at.
   */
  public WaitStatement(final Node value, final int line, final int column) {
    super(line, column);
    this.value = Objects.requireNonNull(value);
  }

  /**
   * Create a "wait" statement from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public WaitStatement(final CompoundTag tag) {
    super(tag);
    this.value = NodeNBTHelper.getNodeForTag(tag.getCompound(TICKS_KEY));
  }

  @Override
  protected StatementAction executeWrapped(Scope scope) {
    long ticks = ProgramManager.getTypeInstance(IntType.class).implicitCast(scope, this.value.evaluate(scope));
    scope.getProgram().wait(scope, ticks);
    return StatementAction.WAIT;
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = super.writeToNBT();
    tag.put(TICKS_KEY, this.value.writeToNBT());
    return tag;
  }

  @Override
  public String toString() {
    return String.format("wait %s;", this.value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    WaitStatement that = (WaitStatement) o;
    return this.value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.value);
  }
}
