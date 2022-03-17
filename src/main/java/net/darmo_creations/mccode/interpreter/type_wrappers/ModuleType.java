package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.Program;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.Variable;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.stream.Collectors;

@Type(name = ModuleType.NAME,
    generateCastOperator = false,
    doc = "Modules are programs that have been imported through the 'import' statement.")
public class ModuleType extends TypeBase<Program> {
  public static final String NAME = "module";

  public static final String MODULE_KEY = "Module";

  @Override
  public Class<Program> getWrappedType() {
    return Program.class;
  }

  @Override
  protected List<String> getAdditionalPropertiesNames(final Program self) {
    return self.getScope().getVariables().values().stream()
        .filter(Variable::isPubliclyVisible)
        .map(Variable::getName)
        .collect(Collectors.toList());
  }

  @Override
  protected Object __get_property__(final Scope scope, final Program self, final String propertyName) {
    if (self.getScope().isVariableDefined(propertyName)) {
      return self.getScope().getVariable(propertyName, true);
    } else {
      return super.__get_property__(scope, self, propertyName);
    }
  }

  @Override
  protected void __set_property__(final Scope scope, Program self, final String propertyName, final Object value) {
    if (propertyName.equals("__name__")) {
      throw new EvaluationException(scope, "mccode.interpreter.error.cannot_set_module_name");
    }
    if (self.getScope().isVariableDefined(propertyName)) {
      self.getScope().setVariable(propertyName, ProgramManager.getTypeForValue(value).copy(scope, value), true);
    } else {
      super.__set_property__(scope, self, propertyName, value);
    }
  }

  @Override
  protected Object __add__(final Scope scope, final Program self, final Object o, final boolean inPlace) {
    if (o instanceof String s) {
      return this.__str__(self) + s;
    }
    return super.__add__(scope, self, o, inPlace);
  }

  @Override
  protected CompoundTag _writeToNBT(final Program self) {
    CompoundTag tag = super._writeToNBT(self);
    tag.put(MODULE_KEY, self.writeToNBT());
    return tag;
  }

  @Override
  public Program readFromNBT(final Scope scope, final CompoundTag tag) {
    return new Program(tag.getCompound(MODULE_KEY), scope.getProgram().getProgramManager());
  }
}
