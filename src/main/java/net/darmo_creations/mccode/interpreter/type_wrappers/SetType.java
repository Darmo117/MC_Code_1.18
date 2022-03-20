package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Method;
import net.darmo_creations.mccode.interpreter.annotations.ParameterMeta;
import net.darmo_creations.mccode.interpreter.annotations.ReturnMeta;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.exceptions.UnsupportedOperatorException;
import net.darmo_creations.mccode.interpreter.types.MCSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wrapper type for {@link MCSet} class.
 * <p>
 * Sets are iterable.
 */
@Type(name = SetType.NAME,
    doc = "Sets are data structures that store unique values. Values in a set are not ordered.")
public class SetType extends TypeBase<MCSet> {
  public static final String NAME = "set";

  public static final String VALUES_KEY = "Values";

  @Override
  public Class<MCSet> getWrappedType() {
    return MCSet.class;
  }

  @Method(name = "clear", doc = "Removes all values from a `set. Modifies the `set.")
  public Void clear(final Scope scope, final MCSet self) {
    self.clear();
    return null;
  }

  @Method(name = "add",
      parametersMetadata = {
          @ParameterMeta(name = "value", mayBeNull = true, doc = "The value to add to the `set.")
      },
      doc = "Adds a value to a `set. Modifies the `set.")
  public Void add(final Scope scope, final MCSet self, final Object value) {
    self.add(ProgramManager.getTypeForValue(value).copy(scope, value));
    return null;
  }

  @Method(name = "union",
      parametersMetadata = {
          @ParameterMeta(name = "s", doc = "The `set perform the union with.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "A new `set containing the values of both `set objects."),
      doc = "Returns the union of values of two `set objects. Alias of '+' operator. " +
          "Does not modify the `set objects it is applied to.")
  public MCSet union(final Scope scope, final MCSet self, final MCSet other) {
    return (MCSet) this.__add__(scope, self, other, false);
  }

  @Method(name = "intersection",
      parametersMetadata = {
          @ParameterMeta(name = "s", doc = "The `set to perform the intersection with.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "A new `set containing only the values present in both `set objects."),
      doc = "Returns the intersection of values of two `set objects. Does not modify the `set objects it is applied to.")
  public MCSet intersection(final Scope scope, final MCSet self, final MCSet other) {
    MCSet set = this.__copy__(scope, self);
    set.retainAll(other);
    return set;
  }

  /**
   * Add all elements of o in self.
   */
  @Override
  protected Object __add__(final Scope scope, MCSet self, final Object o, final boolean inPlace) {
    if (o instanceof MCSet s) {
      if (inPlace) {
        return this.add(scope, self, s, true);
      }
      return this.add(scope, new MCSet(self), s, false);
    } else if (o instanceof String s) {
      return this.__str__(self) + s;
    }
    return super.__add__(scope, self, o, inPlace);
  }

  private MCSet add(final Scope scope, MCSet set1, final MCSet set2, final boolean inPlace) {
    // Deep copy all elements to add
    if (!inPlace) {
      MCSet temp = this.__copy__(scope, set1);
      set1.clear();
      set1.addAll(temp);
    }
    set1.addAll(this.__copy__(scope, set2));
    return set1;
  }

  /**
   * Remove all elements of o from self.
   */
  @Override
  protected Object __sub__(final Scope scope, MCSet self, final Object o, final boolean inPlace) {
    if (o instanceof MCSet s) {
      if (inPlace) {
        return this.sub(self, s);
      }
      return this.sub(new MCSet(self), s);
    }
    return super.__sub__(scope, self, o, inPlace);
  }

  private MCSet sub(MCSet set1, final MCSet set2) {
    set1.removeAll(set2);
    return set1;
  }

  @Override
  protected Object __eq__(final Scope scope, final MCSet self, final Object o) {
    if (o instanceof MCSet s) {
      return self.equals(s);
    }
    return false;
  }

  /**
   * Test whether self is a proper superset of o, that is, self >= o and self != o.
   *
   * @return True if self is a proper superset of o, false otherwise.
   */
  @Override
  protected Object __gt__(final Scope scope, final MCSet self, final Object o) {
    return (Boolean) this.__ge__(scope, self, o) && !(Boolean) this.__eq__(scope, self, o);
  }

  /**
   * Test whether every element in o is in self.
   *
   * @return True if all elements in o are in self, false otherwise.
   */
  @Override
  protected Object __ge__(final Scope scope, final MCSet self, final Object o) {
    if (o instanceof MCSet s) {
      return self.containsAll(s);
    }
    throw new UnsupportedOperatorException(scope, BinaryOperator.GE, this, ProgramManager.getTypeForValue(o));
  }

  /**
   * Test whether self is a proper subset of o, that is, self <= o and self != o.
   *
   * @return True if self is a proper subset of o, false otherwise.
   */
  @Override
  protected Object __lt__(final Scope scope, final MCSet self, final Object o) {
    return (Boolean) this.__le__(scope, self, o) && !(Boolean) this.__eq__(scope, self, o);
  }

  /**
   * Test whether every element in self is in o.
   *
   * @return True if all elements in self are in o, false otherwise.
   */
  @Override
  protected Object __le__(final Scope scope, final MCSet self, final Object o) {
    if (o instanceof MCSet s) {
      return s.containsAll(self);
    }
    throw new UnsupportedOperatorException(scope, BinaryOperator.LE, this, ProgramManager.getTypeForValue(o));
  }

  @Override
  protected Object __in__(final Scope scope, final MCSet self, final Object o) {
    return self.contains(o);
  }

  @Override
  protected boolean __bool__(final MCSet self) {
    return !self.isEmpty();
  }

  @Override
  protected Iterator<?> __iter__(final Scope scope, final MCSet self) {
    return self.iterator();
  }

  @Override
  protected MCSet __copy__(final Scope scope, final MCSet self) {
    return new MCSet(self.stream().map(e -> ProgramManager.getTypeForValue(e).copy(scope, e)).collect(Collectors.toSet()));
  }

  @Override
  protected long __len__(final Scope scope, final MCSet self) {
    return self.size();
  }

  @Override
  public MCSet explicitCast(final Scope scope, final Object o) {
    if (o instanceof Collection<?> c) {
      return this.__copy__(scope, new MCSet(c));
    } else if (o instanceof Iterable<?> i) {
      MCSet list = new MCSet();
      for (Object e : i) {
        list.add(ProgramManager.getTypeForValue(e).copy(scope, e));
      }
      return list;
    } else if (o instanceof Map<?, ?> m) {
      return this.__copy__(scope, new MCSet(m.keySet()));
    } else if (o instanceof String s) {
      MCSet set = new MCSet();
      for (int i = 0; i < s.length(); i++) {
        set.add(String.valueOf(s.charAt(i)));
      }
      return set;
    }
    return super.explicitCast(scope, o);
  }

  @Override
  protected CompoundTag _writeToNBT(final MCSet self) {
    CompoundTag tag = super._writeToNBT(self);
    ListTag list = new ListTag();
    self.forEach(v -> list.add(ProgramManager.getTypeForValue(v).writeToNBT(v)));
    tag.put(VALUES_KEY, list);
    return tag;
  }

  @Override
  public MCSet readFromNBT(final Scope scope, final CompoundTag tag) {
    MCSet set = new MCSet();
    ListTag tagsList = tag.getList(VALUES_KEY, Tag.TAG_COMPOUND);
    tagsList.forEach(t -> set.add(ProgramManager.getTypeForName(((CompoundTag) t).getString(TypeBase.NAME_KEY)).readFromNBT(scope, (CompoundTag) t)));
    return set;
  }
}
