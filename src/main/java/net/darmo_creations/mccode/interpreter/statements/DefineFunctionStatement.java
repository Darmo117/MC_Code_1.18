package net.darmo_creations.mccode.interpreter.statements;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.Utils;
import net.darmo_creations.mccode.interpreter.Variable;
import net.darmo_creations.mccode.interpreter.types.UserFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Statement that defines a function.
 */
public class DefineFunctionStatement extends Statement {
  public static final int ID = 11;

  public static final String NAME_KEY = "Name";
  public static final String PARAMS_LIST_KEY = "Parameters";
  public static final String STATEMENTS_LIST_KEY = "Statements";
  public static final String PUBLIC_KEY = "Public";

  private final String name;
  private final List<String> parametersNames;
  private final List<Statement> statements;
  private final boolean publiclyVisible;

  /**
   * Create a statement that defines a function.
   *
   * @param name            Function’s name.
   * @param parametersNames Function’s parameter names.
   * @param statements      Function’s statements.
   * @param publiclyVisible Whether the function should be visible from outside the program.
   * @param line            The line this statement starts on.
   * @param column          The column in the line this statement starts at.
   */
  public DefineFunctionStatement(final String name, final List<String> parametersNames, final List<Statement> statements,
                                 final boolean publiclyVisible, final int line, final int column) {
    super(line, column);
    this.name = Objects.requireNonNull(name);
    this.parametersNames = parametersNames;
    this.statements = statements;
    this.publiclyVisible = publiclyVisible;
  }

  /**
   * Create a statement that defines a function from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public DefineFunctionStatement(final CompoundTag tag) {
    super(tag);
    this.name = tag.getString(NAME_KEY);
    this.publiclyVisible = tag.getBoolean(PUBLIC_KEY);
    ListTag paramsTag = tag.getList(PARAMS_LIST_KEY, Tag.TAG_STRING);
    this.parametersNames = new ArrayList<>();
    for (Tag t : paramsTag) {
      this.parametersNames.add(t.getAsString());
    }
    ListTag statementsTag = tag.getList(STATEMENTS_LIST_KEY, Tag.TAG_COMPOUND);
    this.statements = new ArrayList<>();
    for (Tag t : statementsTag) {
      this.statements.add(StatementNBTHelper.getStatementForTag((CompoundTag) t));
    }
  }

  @Override
  protected StatementAction executeWrapped(Scope scope) {
    UserFunction function = new UserFunction(this.name, this.parametersNames, this.statements);
    scope.declareVariable(new Variable(this.name, this.publiclyVisible, false, true, true, function));
    return StatementAction.PROCEED;
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = super.writeToNBT();
    tag.putString(NAME_KEY, this.name);
    tag.putBoolean(PUBLIC_KEY, this.publiclyVisible);
    ListTag paramsList = new ListTag();
    this.parametersNames.forEach(s -> paramsList.add(StringTag.valueOf(s)));
    tag.put(PARAMS_LIST_KEY, paramsList);
    ListTag statementsList = new ListTag();
    this.statements.forEach(s -> statementsList.add(s.writeToNBT()));
    tag.put(STATEMENTS_LIST_KEY, statementsList);
    return tag;
  }

  @Override
  public String toString() {
    String params = String.join(", ", this.parametersNames);
    String s = " ";
    if (!this.statements.isEmpty()) {
      s = Utils.indentStatements(this.statements);
    }
    return String.format("%sfunction %s(%s)%send", this.publiclyVisible ? "public " : "", this.name, params, s);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    DefineFunctionStatement that = (DefineFunctionStatement) o;
    return this.name.equals(that.name) && this.parametersNames.equals(that.parametersNames) && this.statements.equals(that.statements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.parametersNames, this.statements);
  }
}
