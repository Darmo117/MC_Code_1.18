package net.darmo_creations.mccode.interpreter.statements;

import net.darmo_creations.mccode.interpreter.Program;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.Variable;
import net.darmo_creations.mccode.interpreter.exceptions.MCCodeRuntimeException;
import net.darmo_creations.mccode.interpreter.exceptions.SyntaxErrorException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Statement that imports another module.
 */
public class ImportStatement extends Statement {
  public static final int ID = 0;

  public static final String NAME_KEY = "ModuleName";
  public static final String ALIAS_KEY = "Alias";

  private final List<String> moduleNamePath;
  private final String alias;

  /**
   * Create a statement that imports another module.
   *
   * @param moduleNamePath Module’s name.
   * @param alias          Alias to use instead of the full name. May be null.
   * @param line           The line this statement starts on.
   * @param column         The column in the line this statement starts at.
   */
  public ImportStatement(final List<String> moduleNamePath, final String alias, final int line, final int column) {
    super(line, column);
    this.moduleNamePath = new ArrayList<>(moduleNamePath);
    this.alias = alias;
  }

  /**
   * Create a statement that imports another module from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public ImportStatement(final CompoundTag tag) {
    super(tag);
    ListTag list = tag.getList(NAME_KEY, Tag.TAG_STRING);
    this.moduleNamePath = new ArrayList<>();
    for (Tag t : list) {
      this.moduleNamePath.add(t.getAsString());
    }
    this.alias = tag.contains(ALIAS_KEY) ? tag.getString(ALIAS_KEY) : null;
  }

  @Override
  protected StatementAction executeWrapped(Scope scope) {
    String name = this.getModulePath();
    Program module;
    try {
      module = scope.getProgram().getProgramManager().loadProgram(name, null, true);
      module.execute();
    } catch (SyntaxErrorException e) {
      Object[] a = new Object[e.getArgs().length + 3];
      a[0] = name;
      a[1] = e.getLine();
      a[2] = e.getColumn();
      System.arraycopy(e.getArgs(), 0, a, 3, e.getArgs().length);
      throw new MCCodeRuntimeException(scope, null, e.getTranslationKey(), a);
    }
    scope.declareVariable(new Variable(this.alias != null ? this.alias : name.replace('.', '_'),
        false, false, false, true, module));
    return StatementAction.PROCEED;
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = super.writeToNBT();
    ListTag list = new ListTag();
    this.moduleNamePath.forEach(name -> list.add(StringTag.valueOf(name)));
    tag.put(NAME_KEY, list);
    if (this.alias != null) {
      tag.putString(ALIAS_KEY, this.alias);
    }
    return tag;
  }

  @Override
  public String toString() {
    return String.format("import %s%s;",
        String.join(".", this.moduleNamePath), this.alias != null ? (" as " + this.alias) : "");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    ImportStatement that = (ImportStatement) o;
    return this.moduleNamePath.equals(that.moduleNamePath) && Objects.equals(this.alias, that.alias);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.moduleNamePath, this.alias);
  }

  /**
   * Return the formatted path of the imported module.
   */
  public String getModulePath() {
    return String.join(".", this.moduleNamePath);
  }
}
