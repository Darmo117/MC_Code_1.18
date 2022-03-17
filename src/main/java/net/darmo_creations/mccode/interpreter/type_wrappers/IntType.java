package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.Utils;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.darmo_creations.mccode.interpreter.exceptions.MCCodeRuntimeException;
import net.darmo_creations.mccode.interpreter.types.MCList;
import net.darmo_creations.mccode.interpreter.types.Position;
import net.minecraft.nbt.CompoundTag;

/**
 * Wrapper type for {@link Integer}.
 */
@Type(name = IntType.NAME, doc = "Type representing integers.")
public class IntType extends TypeBase<Long> {
  public static final String NAME = "int";

  public static final String VALUE_KEY = "Value";

  @Override
  public Class<Long> getWrappedType() {
    return Long.class;
  }

  @Override
  protected Object __minus__(final Scope scope, final Long self) {
    return -self;
  }

  @Override
  protected Object __add__(final Scope scope, final Long self, final Object o, boolean inPlace) {
    if (o instanceof Long l) {
      return self + l;
    } else if (o instanceof Double d) {
      return self + d;
    } else if (o instanceof Boolean b) {
      return self + (b ? 1 : 0);
    } else if (o instanceof String s) {
      return this.__str__(self) + s;
    }
    return super.__add__(scope, self, o, inPlace);
  }

  @Override
  protected Object __sub__(final Scope scope, final Long self, final Object o, boolean inPlace) {
    if (o instanceof Long l) {
      return self - l;
    } else if (o instanceof Double d) {
      return self - d;
    } else if (o instanceof Boolean b) {
      return self - (b ? 1 : 0);
    }
    return super.__sub__(scope, self, o, inPlace);
  }

  @Override
  protected Object __mul__(final Scope scope, final Long self, final Object o, boolean inPlace) {
    if (o instanceof Long l) {
      return self * l;
    } else if (o instanceof Double d) {
      return self * d;
    } else if (o instanceof Boolean b) {
      return self * (b ? 1 : 0);
    } else if (o instanceof Position p) {
      return ProgramManager.getTypeInstance(PosType.class).__mul__(scope, p, self, inPlace);
    } else if (o instanceof String s) {
      // Return a new string instance everytime
      return ProgramManager.getTypeInstance(StringType.class).__mul__(scope, s, self, false);
    } else if (o instanceof MCList l) {
      // Return a new list instance everytime
      return ProgramManager.getTypeInstance(ListType.class).__mul__(scope, l, self, false);
    }
    return super.__mul__(scope, self, o, inPlace);
  }

  @Override
  protected Object __div__(final Scope scope, final Long self, final Object o, boolean inPlace) {
    if (o instanceof Long l) {
      if (l == 0) {
        throw new ArithmeticException("/ by 0");
      }
      return self.doubleValue() / l;
    } else if (o instanceof Double d) {
      if (d == 0) {
        throw new ArithmeticException("/ by 0");
      }
      return self / d;
    } else if (o instanceof Boolean b) {
      if (!b) {
        throw new ArithmeticException("/ by 0");
      }
      return (double) self;
    }
    return super.__div__(scope, self, o, inPlace);
  }

  @Override
  protected Object __mod__(final Scope scope, final Long self, final Object o, boolean inPlace) {
    if (o instanceof Long l) {
      if (l == 0) {
        throw new ArithmeticException("/ by 0");
      }
      return (long) Utils.trueModulo(self, l);
    } else if (o instanceof Double d) {
      if (d == 0) {
        throw new ArithmeticException("/ by 0");
      }
      return Utils.trueModulo(self, d);
    } else if (o instanceof Boolean b) {
      if (b) {
        return 0L;
      }
      throw new ArithmeticException("/ by 0");
    }
    return super.__mod__(scope, self, o, inPlace);
  }

  @Override
  protected Object __pow__(final Scope scope, final Long self, final Object o, boolean inPlace) {
    if (o instanceof Long l) {
      return (long) Math.pow(self, l);
    } else if (o instanceof Double d) {
      return Math.pow(self, d);
    } else if (o instanceof Boolean b) {
      return !b ? 1 : self;
    }
    return super.__pow__(scope, self, o, inPlace);
  }

  @Override
  protected Object __eq__(final Scope scope, final Long self, final Object o) {
    FloatType floatType = ProgramManager.getTypeInstance(FloatType.class);
    double d = floatType.implicitCast(scope, self);
    return floatType.__eq__(scope, d, o);
  }

  @Override
  protected Object __gt__(final Scope scope, final Long self, final Object o) {
    FloatType floatType = ProgramManager.getTypeInstance(FloatType.class);
    double d = floatType.implicitCast(scope, self);
    return floatType.__gt__(scope, d, o);
  }

  @Override
  protected boolean __bool__(final Long self) {
    return self != 0;
  }

  @Override
  public Long implicitCast(final Scope scope, final Object o) {
    if (o instanceof Boolean b) {
      return b ? 1L : 0;
    }
    return super.implicitCast(scope, o);
  }

  @Override
  public Long explicitCast(final Scope scope, final Object o) throws MCCodeRuntimeException {
    if (o instanceof Number n) {
      return n.longValue();
    } else if (o instanceof String s) {
      try {
        return Long.parseLong(s);
      } catch (NumberFormatException e) {
        throw new EvaluationException(scope, "mccode.interpreter.error.int_format", Utils.escapeString(s));
      }
    }
    return super.explicitCast(scope, o);
  }

  @Override
  protected CompoundTag _writeToNBT(final Long self) {
    CompoundTag tag = super._writeToNBT(self);
    tag.putLong(VALUE_KEY, self);
    return tag;
  }

  @Override
  public Long readFromNBT(final Scope scope, final CompoundTag tag) {
    return tag.getLong(VALUE_KEY);
  }
}
