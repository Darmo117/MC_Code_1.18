package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.Utils;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.darmo_creations.mccode.interpreter.exceptions.MCCodeRuntimeException;
import net.darmo_creations.mccode.interpreter.types.Position;
import net.minecraft.nbt.CompoundTag;

/**
 * Wrapper type for {@link Double}.
 */
@Type(name = FloatType.NAME, doc = "Type representing real numbers.")
public class FloatType extends TypeBase<Double> {
  public static final String NAME = "float";

  public static final String VALUE_KEY = "Value";

  @Override
  public Class<Double> getWrappedType() {
    return Double.class;
  }

  @Override
  protected Object __minus__(final Scope scope, final Double self) {
    return self == 0 ? 0.0 : -self; // Avoid -0.0
  }

  @Override
  protected Object __add__(final Scope scope, final Double self, final Object o, boolean inPlace) {
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
  protected Object __sub__(final Scope scope, final Double self, final Object o, boolean inPlace) {
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
  protected Object __mul__(final Scope scope, final Double self, final Object o, boolean inPlace) {
    if (o instanceof Long l) {
      return self * l;
    } else if (o instanceof Double d) {
      return self * d;
    } else if (o instanceof Boolean b) {
      return self * (b ? 1 : 0);
    } else if (o instanceof Position p) {
      return ProgramManager.getTypeInstance(PosType.class).__mul__(scope, p, self, inPlace);
    }
    return super.__mul__(scope, self, o, inPlace);
  }

  @Override
  protected Object __div__(final Scope scope, final Double self, final Object o, boolean inPlace) {
    if (o instanceof Long l) {
      if (l == 0) {
        throw new ArithmeticException("/ by 0");
      }
      return self / l;
    } else if (o instanceof Double d) {
      if (d == 0) {
        throw new ArithmeticException("/ by 0");
      }
      return self / d;
    } else if (o instanceof Boolean b) {
      if (!b) {
        throw new ArithmeticException("/ by 0");
      }
      return self;
    }
    return super.__div__(scope, self, o, inPlace);
  }

  @Override
  protected Object __mod__(final Scope scope, final Double self, final Object o, boolean inPlace) {
    if (o instanceof Long l) {
      return Utils.trueModulo(self, l);
    } else if (o instanceof Double d) {
      return Utils.trueModulo(self, d);
    } else if (o instanceof Boolean b) {
      if (b) {
        return 0.0;
      }
      throw new ArithmeticException("/ by 0");
    }
    return super.__mod__(scope, self, o, inPlace);
  }

  @Override
  protected Object __pow__(final Scope scope, final Double self, final Object o, boolean inPlace) {
    if (o instanceof Long l) {
      return Math.pow(self, l);
    } else if (o instanceof Double d) {
      return Math.pow(self, d);
    } else if (o instanceof Boolean b) {
      return !b ? 1.0 : self;
    }
    return super.__pow__(scope, self, o, inPlace);
  }

  @Override
  protected Object __eq__(final Scope scope, final Double self, final Object o) {
    if (o instanceof Number n) {
      return self == n.doubleValue();
    } else if (o instanceof Boolean b) {
      return self == (b ? 1 : 0);
    }
    return super.__eq__(scope, self, o);
  }

  @Override
  protected Object __gt__(final Scope scope, final Double self, final Object o) {
    if (o instanceof Number || o instanceof Boolean) {
      return self > this.implicitCast(scope, o);
    }
    return super.__gt__(scope, self, o);
  }

  @Override
  protected boolean __bool__(final Double self) {
    return self != 0.0;
  }

  @Override
  public Double implicitCast(final Scope scope, final Object o) {
    if (o instanceof Number n) {
      return n.doubleValue();
    } else if (o instanceof Boolean b) {
      return b ? 1.0 : 0.0;
    }
    return super.implicitCast(scope, o);
  }

  @Override
  public Double explicitCast(Scope scope, Object o) throws MCCodeRuntimeException {
    if (o instanceof String s) {
      try {
        return Double.parseDouble(s);
      } catch (NumberFormatException e) {
        throw new EvaluationException(scope, "mccode.interpreter.error.float_format", Utils.escapeString(s));
      }
    }
    return super.explicitCast(scope, o);
  }

  @Override
  protected CompoundTag _writeToNBT(final Double self) {
    CompoundTag tag = super._writeToNBT(self);
    tag.putDouble(VALUE_KEY, self);
    return tag;
  }

  @Override
  public Double readFromNBT(final Scope scope, final CompoundTag tag) {
    return tag.getDouble(VALUE_KEY);
  }
}
