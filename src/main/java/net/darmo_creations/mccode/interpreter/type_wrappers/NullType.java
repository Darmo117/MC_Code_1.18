package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.exceptions.CastException;
import net.minecraft.nbt.CompoundTag;

/**
 * Wrapper type for {@link Void} class (null value).
 * <p>
 * It does not have a cast operator.
 */
@Type(name = NullType.NAME,
    generateCastOperator = false,
    doc = "Placeholder type for the null value.")
public class NullType extends TypeBase<Void> {
  public static final String NAME = "null";

  @Override
  public Class<Void> getWrappedType() {
    return Void.class;
  }

  @Override
  protected Object __add__(Scope scope, Void self, Object o, boolean inPlace) {
    if (o instanceof String s) {
      return this.__str__(self) + s;
    }
    return super.__add__(scope, self, o, inPlace);
  }

  @Override
  protected boolean __bool__(final Void self) {
    return false;
  }

  @Override
  protected String __str__(final Void self) {
    return "null";
  }

  @Override
  public Void implicitCast(final Scope scope, final Object o) throws CastException {
    if (o == null) {
      return null;
    }
    return super.implicitCast(scope, o);
  }

  @Override
  public Void readFromNBT(final Scope scope, final CompoundTag tag) {
    return null;
  }
}
