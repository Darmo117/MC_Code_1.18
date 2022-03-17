package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.exceptions.MCCodeException;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;
import net.darmo_creations.mccode.interpreter.types.Function;
import net.darmo_creations.mccode.interpreter.types.UserFunction;
import net.minecraft.nbt.CompoundTag;

/**
 * Wrapper for {@link Function} class.
 * <p>
 * It does not have a cast operator.
 */
@Type(name = FunctionType.NAME,
    generateCastOperator = false,
    doc = "Functions are objects that can be called and return a value based on the values of its parameters.")
public class FunctionType extends TypeBase<Function> {
  public static final String NAME = "function";

  public static final String FUNCTION_TYPE_KEY = "Type";
  public static final String FUNCTION_KEY = "Function";

  private static final String FUNCTION_TYPE_BUILTIN = "builtin";
  private static final String FUNCTION_TYPE_USER = "user";

  @Override
  public Class<Function> getWrappedType() {
    return Function.class;
  }

  @Override
  protected Object __add__(final Scope scope, final Function self, final Object o, final boolean inPlace) {
    if (o instanceof String s) {
      return this.__str__(self) + s;
    }
    return super.__add__(scope, self, o, inPlace);
  }

  @Override
  protected CompoundTag _writeToNBT(final Function self) {
    CompoundTag tag = super._writeToNBT(self);
    if (self instanceof BuiltinFunction) {
      tag.putString(FUNCTION_TYPE_KEY, FUNCTION_TYPE_BUILTIN);
      tag.putString(FUNCTION_KEY, self.getName());
    } else if (self instanceof UserFunction) {
      tag.putString(FUNCTION_TYPE_KEY, FUNCTION_TYPE_USER);
      CompoundTag functionTag = ((UserFunction) self).writeToNBT();
      tag.put(FUNCTION_KEY, functionTag);
    }
    return tag;
  }

  @Override
  public Function readFromNBT(final Scope scope, final CompoundTag tag) {
    String functionType = tag.getString(FUNCTION_TYPE_KEY);
    return switch (functionType) {
      // Type-safe as builtin functions cannot be deleted nor overridden
      case FUNCTION_TYPE_BUILTIN -> (Function) scope.getVariable(tag.getString(FUNCTION_KEY), false);
      case FUNCTION_TYPE_USER -> new UserFunction(tag.getCompound(FUNCTION_KEY));
      default -> throw new MCCodeException("invalid function type " + functionType);
    };
  }
}
