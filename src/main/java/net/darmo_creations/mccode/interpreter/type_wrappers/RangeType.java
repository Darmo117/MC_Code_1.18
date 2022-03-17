package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.types.Range;
import net.minecraft.nbt.CompoundTag;

import java.util.Iterator;

/**
 * Wrapper type for {@link Range} class.
 * <p>
 * Ranges are iterable.
 */
@Type(name = RangeType.NAME,
    generateCastOperator = false,
    doc = "Ranges are objects that generate integers within between two values with a given step.")
public class RangeType extends TypeBase<Range> {
  public static final String NAME = "range";

  public static final String START_KEY = "Start";
  public static final String END_KEY = "End";
  public static final String STEP_KEY = "Step";

  @Override
  public Class<Range> getWrappedType() {
    return Range.class;
  }

  @Override
  protected Object __add__(final Scope scope, final Range self, final Object o, final boolean inPlace) {
    if (o instanceof String s) {
      return this.__str__(self) + s;
    }
    return super.__add__(scope, self, o, inPlace);
  }

  @Override
  protected Object __eq__(final Scope scope, final Range self, final Object o) {
    if (o instanceof Range r) {
      return self.equals(r);
    }
    return super.__eq__(scope, self, o);
  }

  @Override
  protected Iterator<?> __iter__(final Scope scope, final Range self) {
    return self.iterator();
  }

  @Override
  public Range __copy__(final Scope scope, final Range self) {
    return self.clone();
  }

  @Override
  protected CompoundTag _writeToNBT(final Range self) {
    CompoundTag tag = super._writeToNBT(self);
    tag.putLong(START_KEY, self.getStart());
    tag.putLong(END_KEY, self.getEnd());
    tag.putLong(STEP_KEY, self.getStep());
    return tag;
  }

  @Override
  public Range readFromNBT(final Scope scope, final CompoundTag tag) {
    return new Range(tag.getLong(START_KEY), tag.getLong(END_KEY), tag.getLong(STEP_KEY));
  }
}
