package net.darmo_creations.mccode.interpreter.statements;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.Utils;
import net.darmo_creations.mccode.interpreter.nodes.Node;
import net.darmo_creations.mccode.interpreter.nodes.NodeNBTHelper;
import net.darmo_creations.mccode.interpreter.type_wrappers.BooleanType;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Objects;

/**
 * Statement that represents a while-loop.
 */
public class WhileLoopStatement extends Statement {
  public static final int ID = 41;

  public static final String CONDITION_KEY = "Condition";
  public static final String STATEMENTS_KEY = "Statements";
  public static final String IP_KEY = "IP";
  public static final String PAUSED_KEY = "Paused";

  private final Node condition;
  private final List<Statement> statements;
  /**
   * Instruction pointer.
   */
  private int ip;
  /**
   * Whether the loop encountered a "wait" statement.
   */
  private boolean paused;

  /**
   * Create a statement that represents a while-loop.
   *
   * @param condition  Expression that evaluates to a boolean.
   * @param statements Statements of the loop.
   * @param line       The line this statement starts on.
   * @param column     The column in the line this statement starts at.
   */
  public WhileLoopStatement(final Node condition, final List<Statement> statements, final int line, final int column) {
    super(line, column);
    this.condition = condition;
    this.statements = statements;
    this.ip = 0;
    this.paused = false;
  }

  /**
   * Create a statement that represents a while-loop.
   *
   * @param tag The tag to deserialize.
   */
  public WhileLoopStatement(final CompoundTag tag) {
    super(tag);
    this.condition = NodeNBTHelper.getNodeForTag(tag.getCompound(CONDITION_KEY));
    this.statements = StatementNBTHelper.deserializeStatementsList(tag, STATEMENTS_KEY);
    this.ip = tag.getInt(IP_KEY);
    this.paused = tag.getBoolean(PAUSED_KEY);
  }

  @Override
  protected StatementAction executeWrapped(Scope scope) {
    BooleanType booleanType = ProgramManager.getTypeInstance(BooleanType.class);
    exit:
    // Do not re-evaluate condition if loop was paused by "wait" a statement
    while (this.paused || booleanType.implicitCast(scope, this.condition.evaluate(scope))) {
      if (this.paused) {
        this.paused = false;
      }
      while (this.ip < this.statements.size()) {
        Statement statement = this.statements.get(this.ip);
        StatementAction action = statement.execute(scope);
        if (action == StatementAction.EXIT_LOOP) {
          this.ip = 0;
          break exit;
        } else if (action == StatementAction.CONTINUE_LOOP) {
          break;
        } else if (action == StatementAction.EXIT_FUNCTION || action == StatementAction.WAIT) {
          if (action == StatementAction.WAIT) {
            this.paused = true;
            if (statement instanceof WaitStatement) {
              this.ip++;
            }
          } else {
            this.ip = 0;
          }
          return action;
        } else {
          this.ip++;
        }
      }
      this.ip = 0;
    }

    return StatementAction.PROCEED;
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = super.writeToNBT();
    tag.put(CONDITION_KEY, this.condition.writeToNBT());
    tag.put(STATEMENTS_KEY, StatementNBTHelper.serializeStatementsList(this.statements));
    tag.putInt(IP_KEY, this.ip);
    tag.putBoolean(PAUSED_KEY, this.paused);
    return tag;
  }

  @Override
  public String toString() {
    String s = " ";
    if (!this.statements.isEmpty()) {
      s = Utils.indentStatements(this.statements);
    }
    return String.format("while %s do%send", this.condition, s);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    WhileLoopStatement that = (WhileLoopStatement) o;
    return this.ip == that.ip && this.paused == that.paused && this.condition.equals(that.condition) && this.statements.equals(that.statements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.condition, this.statements, this.ip, this.paused);
  }
}
