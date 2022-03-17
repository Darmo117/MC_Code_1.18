package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.exceptions.MCCodeException;
import net.minecraft.nbt.CompoundTag;

/**
 * Wrapper type for {@link Object} class.
 * <p>
 * It does not have a cast operator.
 */
@Type(name = AnyType.NAME,
    generateCastOperator = false,
    doc = "Base type. Placeholder for functions parameters.")
public class AnyType extends TypeBase<Object> {
  public static final String NAME = "any";

  @Override
  public Class<Object> getWrappedType() {
    return Object.class;
  }

  @Override
  public Object implicitCast(final Scope scope, final Object o) {
    return o;
  }

  @Override
  public CompoundTag _writeToNBT(final Object self) {
    throw new MCCodeException("cannot serialize objects of type any");
  }

  @Override
  public Object readFromNBT(final Scope scope, final CompoundTag tag) {
    throw new MCCodeException("cannot deserialize objects of type any");
  }
}
