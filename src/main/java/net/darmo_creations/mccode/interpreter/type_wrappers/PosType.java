package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.*;
import net.darmo_creations.mccode.interpreter.exceptions.CastException;
import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.darmo_creations.mccode.interpreter.types.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Rotation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

/**
 * Wrapper type for {@link Position} class.
 * <p>
 * New instances can be created by casting {@link List}s or {@link Map}s.
 */
@Type(name = PosType.NAME, doc = "Type representing a block position.")
public class PosType extends TypeBase<Position> {
  public static final String NAME = "pos";

  public static final String X_KEY = "X";
  public static final String Y_KEY = "Y";
  public static final String Z_KEY = "Z";
  public static final String X_REL_KEY = "XRelative";
  public static final String Y_REL_KEY = "YRelative";
  public static final String Z_REL_KEY = "ZRelative";

  @Override
  public Class<Position> getWrappedType() {
    return Position.class;
  }

  @Property(name = "x", doc = "The x component of a position.")
  public Number getX(final Position self) {
    double x = self.getX();
    if (x == Math.floor(x)) { // Cannot use ?: as it will convert long to double
      return (long) x;
    }
    return x;
  }

  @Property(name = "y", doc = "The y component of a position.")
  public Number getY(final Position self) {
    double y = self.getY();
    if (y == Math.floor(y)) { // Cannot use ?: as it will convert long to double
      return (long) y;
    }
    return y;
  }

  @Property(name = "z", doc = "The z component of a position.")
  public Number getZ(final Position self) {
    double z = self.getZ();
    if (z == Math.floor(z)) { // Cannot use ?: as it will convert long to double
      return (long) z;
    }
    return z;
  }

  @Property(name = "x_relative", doc = "Indicates whether the x component is relative (tilde or caret notation).")
  public Boolean isXRelative(final Position self) {
    return self.isXRelative();
  }

  @Property(name = "y_relative", doc = "Indicates whether the y component is relative (tilde or caret notation).")
  public Boolean isYRelative(final Position self) {
    return self.isYRelative();
  }

  @Property(name = "z_relative", doc = "Indicates whether the z component is relative (tilde or caret notation).")
  public Boolean isZRelative(final Position self) {
    return self.isZRelative();
  }

  @Method(name = "up",
      parametersMetadata = {
          @ParameterMeta(name = "offset", doc = "The number of blocks up.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The resulting position."),
      doc = "Returns a position that is a certain amount of blocks above.")
  public Position up(final Scope scope, final Position self, final Long offset) {
    return self.up(offset.intValue());
  }

  @Method(name = "down",
      parametersMetadata = {
          @ParameterMeta(name = "offset", doc = "The number of blocks down.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The resulting position."),
      doc = "Returns a position that is a certain amount of blocks below.")
  public Position down(final Scope scope, final Position self, final Long offset) {
    return self.down(offset.intValue());
  }

  @Method(name = "north",
      parametersMetadata = {
          @ParameterMeta(name = "offset", doc = "The number of blocks north.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The resulting position."),
      doc = "Returns a position that is a certain amount of blocks to the north.")
  public Position north(final Scope scope, final Position self, final Long offset) {
    return self.north(offset.intValue());
  }

  @Method(name = "south",
      parametersMetadata = {
          @ParameterMeta(name = "offset", doc = "The number of blocks south.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The resulting position."),
      doc = "Returns a position that is a certain amount of blocks to the south.")
  public Position south(final Scope scope, final Position self, final Long offset) {
    return self.south(offset.intValue());
  }

  @Method(name = "west",
      parametersMetadata = {
          @ParameterMeta(name = "offset", doc = "The number of blocks west.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The resulting position."),
      doc = "Returns a position that is a certain amount of blocks to the west.")
  public Position west(final Scope scope, final Position self, final Long offset) {
    return self.west(offset.intValue());
  }

  @Method(name = "east",
      parametersMetadata = {
          @ParameterMeta(name = "offset", doc = "The number of blocks east.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The resulting position."),
      doc = "Returns a position that is a certain amount of blocks to the east.")
  public Position east(final Scope scope, final Position self, final Long offset) {
    return self.east(offset.intValue());
  }

  @Method(name = "rotate",
      parametersMetadata = {
          @ParameterMeta(name = "quadrant", doc = "0 for 0°, 1 for 90°, 2 for 180°, 3 for 270°.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The resulting position."),
      doc = "Rotates a position by 0°, 90°, 180° or 270°.")
  public Position rotate(final Scope scope, final Position self, final Long quadrant) {
    return self.rotate(Rotation.values()[(int) (quadrant % 4)]);
  }

  @Method(name = "distance",
      parametersMetadata = {
          @ParameterMeta(name = "pos", doc = "The position to get the distance to.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The euclidian distance between the two positions."),
      doc = "Returns the distance between two positions.")
  public Double getDistance(final Scope scope, final Position self, final Position other) {
    return self.getDistance(other);
  }

  @Method(name = "distance_sq",
      parametersMetadata = {
          @ParameterMeta(name = "pos", doc = "The position to get the distance to.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The squared euclidian distance between the two positions."),
      doc = "Returns the squared distance between two positions.")
  public Double getDistanceSq(final Scope scope, final Position self, final Position other) {
    return self.getDistanceSq(other);
  }

  @Override
  protected Object __minus__(final Scope scope, final Position self) {
    return new Position(-self.getX(), -self.getY(), -self.getZ(),
        self.getXRelativity(), self.getYRelativity(), self.getZRelativity());
  }

  @Override
  protected Object __add__(final Scope scope, final Position self, final Object o, final boolean inPlace) {
    if (o instanceof Position p) {
      return self.add(p);
    } else if (o instanceof String s) {
      return this.__str__(self) + s;
    }
    return super.__add__(scope, self, o, inPlace);
  }

  @Override
  protected Object __sub__(final Scope scope, final Position self, final Object o, final boolean inPlace) {
    if (o instanceof Position p) {
      return self.subtract(p);
    }
    return super.__sub__(scope, self, o, inPlace);
  }

  @Override
  protected Object __mul__(final Scope scope, final Position self, final Object o, final boolean inPlace) {
    if (o instanceof Number || o instanceof Boolean) {
      return self.multiply(ProgramManager.getTypeInstance(FloatType.class).implicitCast(scope, o));
    }
    return super.__mul__(scope, self, o, inPlace);
  }

  @Override
  protected Object __div__(final Scope scope, final Position self, final Object o, final boolean inPlace) {
    if (o instanceof Number || o instanceof Boolean) {
      double n = ProgramManager.getTypeInstance(FloatType.class).implicitCast(scope, o);
      if (n == 0) {
        throw new ArithmeticException("/ by 0");
      }
      return self.divide(n);
    }
    return super.__div__(scope, self, o, inPlace);
  }

  @Override
  protected Object __intdiv__(final Scope scope, final Position self, final Object o, final boolean inPlace) {
    if (o instanceof Number || o instanceof Boolean) {
      double n = ProgramManager.getTypeInstance(FloatType.class).implicitCast(scope, o);
      if (n == 0) {
        throw new ArithmeticException("/ by 0");
      }
      return self.intDivide(n);
    }
    return super.__intdiv__(scope, self, o, inPlace);
  }

  @Override
  protected Object __mod__(final Scope scope, final Position self, final Object o, final boolean inPlace) {
    if (o instanceof Number || o instanceof Boolean) {
      double n = ProgramManager.getTypeInstance(FloatType.class).implicitCast(scope, o);
      if (n == 0) {
        throw new ArithmeticException("/ by 0");
      }
      return self.modulo(n);
    }
    return super.__mod__(scope, self, o, inPlace);
  }

  @Override
  protected Object __pow__(final Scope scope, final Position self, final Object o, final boolean inPlace) {
    if (o instanceof Number || o instanceof Boolean) {
      return self.pow(ProgramManager.getTypeInstance(FloatType.class).implicitCast(scope, o));
    }
    return super.__pow__(scope, self, o, inPlace);
  }

  @Override
  protected Object __eq__(final Scope scope, final Position self, final Object o) {
    if (o instanceof Position p) {
      return self.equals(p);
    }
    return false;
  }

  @Override
  protected Object __gt__(final Scope scope, final Position self, final Object o) {
    if (o instanceof Position p) {
      return self.compareTo(p) > 0;
    }
    return super.__gt__(scope, self, o);
  }

  @Override
  protected String __str__(final Position self) {
    return self.toString();
  }

  @Override
  public Position explicitCast(final Scope scope, final Object o) {
    if (o instanceof List<?> list) {
      if (list.size() != 3) {
        throw new EvaluationException(scope, "mccode.interpreter.error.pos_list_format", list);
      }
      Pair<Double, Position.Relativity> resX = this.extractComponent(scope, list.get(0));
      Pair<Double, Position.Relativity> resY = this.extractComponent(scope, list.get(1));
      Pair<Double, Position.Relativity> resZ = this.extractComponent(scope, list.get(2));
      return new Position(resX.getLeft(), resY.getLeft(), resZ.getLeft(),
          resX.getRight(), resY.getRight(), resZ.getRight());

    } else if (o instanceof Map<?, ?> map) {
      if (map.size() != 3 || !map.containsKey("x") || !map.containsKey("y") || !map.containsKey("z")) {
        throw new EvaluationException(scope, "mccode.interpreter.error.pos_map_format", map);
      }
      Pair<Double, Position.Relativity> resX = this.extractComponent(scope, map.get("x"));
      Pair<Double, Position.Relativity> resY = this.extractComponent(scope, map.get("y"));
      Pair<Double, Position.Relativity> resZ = this.extractComponent(scope, map.get("z"));
      return new Position(resX.getLeft(), resY.getLeft(), resZ.getLeft(),
          resX.getRight(), resY.getRight(), resZ.getRight());
    }

    return super.explicitCast(scope, o);
  }

  private Pair<Double, Position.Relativity> extractComponent(final Scope scope, final Object o) {
    if (o instanceof Number n) {
      return new ImmutablePair<>(n.doubleValue(), Position.Relativity.ABSOLUTE);
    } else if (o instanceof String s) {
      Position.Relativity relativity = Position.Relativity.fromString(s.charAt(0) + "");
      if (relativity.isRelative()) {
        if (s.length() == 1) {
          s = "0";
        } else {
          s = s.substring(1);
        }
      }
      return new ImmutablePair<>(Double.parseDouble(s), relativity);
    } else {
      throw new CastException(scope, ProgramManager.getTypeInstance(FloatType.class), ProgramManager.getTypeForValue(o));
    }
  }

  @Override
  protected CompoundTag _writeToNBT(final Position self) {
    CompoundTag tag = super._writeToNBT(self);
    tag.putDouble(X_KEY, self.getX());
    tag.putDouble(Y_KEY, self.getY());
    tag.putDouble(Z_KEY, self.getZ());
    tag.putString(X_REL_KEY, self.getXRelativity().getSymbol());
    tag.putString(Y_REL_KEY, self.getYRelativity().getSymbol());
    tag.putString(Z_REL_KEY, self.getZRelativity().getSymbol());
    return tag;
  }

  @Override
  public Position readFromNBT(final Scope scope, final CompoundTag tag) {
    return new Position(tag.getDouble(X_KEY), tag.getDouble(Y_KEY), tag.getDouble(Z_KEY),
        Position.Relativity.fromString(tag.getString(X_REL_KEY)),
        Position.Relativity.fromString(tag.getString(Y_REL_KEY)),
        Position.Relativity.fromString(tag.getString(Z_REL_KEY)));
  }
}
