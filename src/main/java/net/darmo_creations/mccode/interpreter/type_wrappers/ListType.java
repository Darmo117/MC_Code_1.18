package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Method;
import net.darmo_creations.mccode.interpreter.annotations.ParameterMeta;
import net.darmo_creations.mccode.interpreter.annotations.ReturnMeta;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.exceptions.IndexOutOfBoundsException;
import net.darmo_creations.mccode.interpreter.types.MCList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Wrapper type for {@link MCList} class.
 * <p>
 * Lists are iterable and support the __get_item__, __set_item__ and __del_item__ operators.
 */
@Type(name = ListType.NAME, doc = "Lists are data structures that store values sequentially.")
public class ListType extends TypeBase<MCList> {
  public static final String NAME = "list";

  public static final String VALUES_KEY = "Values";

  /**
   * Return a comparator to sort instances of this type.
   *
   * @param scope    A scope object to query types from.
   * @param reversed Whether to reverse sort the target list.
   * @return The comparator.
   */
  public static Comparator<Object> comparator(final Scope scope, final boolean reversed) {
    return (e1, e2) -> {
      TypeBase<?> e1Type = ProgramManager.getTypeForValue(e1);
      Object gt = e1Type.applyOperator(scope, BinaryOperator.GT, e1, e2, null, false);
      if (ProgramManager.getTypeForValue(gt).toBoolean(gt)) {
        return reversed ? -1 : 1;
      }
      Object equal = e1Type.applyOperator(scope, BinaryOperator.EQUAL, e1, e2, null, false);
      return ProgramManager.getTypeForValue(equal).toBoolean(equal) ? 0 : (reversed ? 1 : -1);
    };
  }

  @Override
  public Class<MCList> getWrappedType() {
    return MCList.class;
  }

  @Method(name = "clear", doc = "Removes all values from a `list. Modifies the `list.")
  public Void clear(final Scope scope, final MCList self) {
    self.clear();
    return null;
  }

  @Method(name = "add",
      parametersMetadata = {
          @ParameterMeta(name = "value", mayBeNull = true, doc = "The value to add to the `list.")
      },
      doc = "Adds a value at the end of a `list. Modifies the `list.")
  public Void add(final Scope scope, final MCList self, final Object value) {
    self.add(ProgramManager.getTypeForValue(value).copy(scope, value));
    return null;
  }

  @Method(name = "insert",
      parametersMetadata = {
          @ParameterMeta(name = "index", doc = "Index at which to insert the value."),
          @ParameterMeta(name = "value", mayBeNull = true, doc = "The value to insert into the `list.")
      },
      doc = "Adds a value at the specified index of a `list. Modifies the `list.")
  public Void insert(final Scope scope, final MCList self, final Long index, final Object value) {
    if (index < 0 || index > self.size()) {
      throw new IndexOutOfBoundsException(scope, index.intValue());
    }
    self.add(index.intValue(), ProgramManager.getTypeForValue(value).copy(scope, value));
    return null;
  }

  @Method(name = "count",
      parametersMetadata = {
          @ParameterMeta(name = "value", mayBeNull = true, doc = "The value to count the occurences of.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of occurences of the value."),
      doc = "Counts the number of times the given value occurs in a `list.")
  public Long count(final Scope scope, final MCList self, final Object value) {
    return self.stream().filter(e -> e.equals(value)).count();
  }

  @Method(name = "index",
      parametersMetadata = {
          @ParameterMeta(name = "value", mayBeNull = true, doc = "The value to get the index of.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The index of the first occurence or -1 if the value was not found."),
      doc = "Returns the index of the first occurence of the given value in a `list." +
          " Returns -1 if the value is not present in the `list.")
  public Long indexOf(final Scope scope, final MCList self, final Object value) {
    return (long) self.indexOf(value);
  }

  @Method(name = "sort",
      parametersMetadata = {
          @ParameterMeta(name = "reversed", doc = "If #true the `list will be sorted in reverse order.")
      },
      doc = "Sorts a `list using natural ordering of its elements. Modifies the `list.")
  public Void sort(final Scope scope, final MCList self, final boolean reversed) {
    self.sort(comparator(scope, reversed));
    return null;
  }

  @Override
  protected Object __get_item__(final Scope scope, final MCList self, final Object key) {
    if (key instanceof Long || key instanceof Boolean) {
      Long index = ProgramManager.getTypeInstance(IntType.class).implicitCast(scope, key);
      if (index < 0 || index >= self.size()) {
        throw new IndexOutOfBoundsException(scope, index.intValue());
      }
      return self.get(index.intValue());
    }
    return super.__get_item__(scope, self, key);
  }

  @Override
  protected void __set_item__(final Scope scope, MCList self, final Object key, final Object value) {
    if (key instanceof Long || key instanceof Boolean) {
      Long index = ProgramManager.getTypeInstance(IntType.class).implicitCast(scope, key);
      if (index < 0 || index >= self.size()) {
        throw new IndexOutOfBoundsException(scope, index.intValue());
      }
      // Deep copy value
      self.set(index.intValue(), ProgramManager.getTypeForValue(value).copy(scope, value));
    } else {
      super.__set_item__(scope, self, key, value);
    }
  }

  @Override
  protected void __del_item__(final Scope scope, MCList self, final Object key) {
    if (key instanceof Long || key instanceof Boolean) {
      Long index = ProgramManager.getTypeInstance(IntType.class).implicitCast(scope, key);
      if (index < 0 || index >= self.size()) {
        throw new IndexOutOfBoundsException(scope, index.intValue());
      }
      self.remove(index.intValue());
    } else {
      super.__del_item__(scope, self, key);
    }
  }

  /**
   * Add all values of o to self.
   */
  @Override
  protected Object __add__(final Scope scope, MCList self, final Object o, final boolean inPlace) {
    if (o instanceof MCList l) {
      if (inPlace) {
        return this.add(scope, self, l, true);
      }
      return this.add(scope, new MCList(self), l, false);
    } else if (o instanceof String s) {
      return this.__str__(self) + s;
    }
    return super.__add__(scope, self, o, inPlace);
  }

  private MCList add(final Scope scope, MCList list1, final MCList list2, final boolean inPlace) {
    // Deep copy all elements to add
    if (!inPlace) {
      MCList temp = this.__copy__(scope, list1);
      list1.clear();
      list1.addAll(temp);
    }
    list1.addAll(this.__copy__(scope, list2));
    return list1;
  }

  /**
   * Remove all values of o from self.
   */
  @Override
  protected Object __sub__(final Scope scope, MCList self, final Object o, final boolean inPlace) {
    if (o instanceof MCList l) {
      if (inPlace) {
        return this.sub(self, l);
      }
      return this.sub(new MCList(self), l);
    }
    return super.__sub__(scope, self, o, inPlace);
  }

  private MCList sub(MCList list1, final MCList list2) {
    list1.removeAll(list2);
    return list1;
  }

  /**
   * Duplicates this list o times.
   */
  @Override
  protected Object __mul__(final Scope scope, MCList self, final Object o, final boolean inPlace) {
    if (o instanceof Long || o instanceof Boolean) {
      long nb = ProgramManager.getTypeInstance(IntType.class).implicitCast(scope, o);
      if (inPlace) {
        return this.mul(scope, self, nb);
      }
      return this.mul(scope, new MCList(self), nb);
    }
    return super.__mul__(scope, self, o, inPlace);
  }

  private MCList mul(final Scope scope, MCList list, final long nb) {
    if (nb <= 0) {
      list.clear();
      return list;
    }
    MCList temp = this.__copy__(scope, list);
    list.clear();
    for (int i = 0; i < nb; i++) {
      // Deep copy all elements to add
      list.addAll(this.__copy__(scope, temp));
    }
    return list;
  }

  @Override
  protected Object __eq__(final Scope scope, final MCList self, final Object o) {
    if (o instanceof MCList l) {
      return self.equals(l);
    }
    return false;
  }

  @Override
  protected Object __gt__(final Scope scope, final MCList self, final Object o) {
    if (o instanceof MCList other) {
      if (self.size() > other.size()) {
        return true;
      }
      if (self.size() < other.size()) {
        return false;
      }
      for (int i = 0; i < self.size(); i++) {
        Object selfItem = self.get(i);
        Object equal = ProgramManager.getTypeForValue(selfItem).applyOperator(scope, BinaryOperator.EQUAL, selfItem, other.get(i), null, false);
        if (!ProgramManager.getTypeForValue(equal).toBoolean(equal)) {
          Object greater = ProgramManager.getTypeForValue(selfItem).applyOperator(scope, BinaryOperator.GT, selfItem, other.get(i), null, false);
          return ProgramManager.getTypeForValue(greater).toBoolean(greater);
        }
      }
      return false;
    }
    return super.__gt__(scope, self, o);
  }

  @Override
  protected Object __in__(final Scope scope, final MCList self, final Object o) {
    return self.contains(o);
  }

  @Override
  protected boolean __bool__(final MCList self) {
    return !self.isEmpty();
  }

  @Override
  protected Iterator<?> __iter__(final Scope scope, final MCList self) {
    return self.iterator();
  }

  @Override
  protected MCList __copy__(final Scope scope, final MCList self) {
    return new MCList(self.stream().map(e -> ProgramManager.getTypeForValue(e).copy(scope, e)).collect(Collectors.toList()));
  }

  @Override
  protected long __len__(final Scope scope, final MCList self) {
    return self.size();
  }

  @Override
  public MCList explicitCast(final Scope scope, final Object o) {
    if (o instanceof Collection<?> c) {
      return this.__copy__(scope, new MCList(c));
    } else if (o instanceof Iterable<?> i) {
      MCList list = new MCList();
      for (Object e : i) {
        list.add(ProgramManager.getTypeForValue(e).copy(scope, e));
      }
      return list;
    } else if (o instanceof String s) {
      MCList list = new MCList();
      for (int i = 0; i < s.length(); i++) {
        list.add(String.valueOf(s.charAt(i)));
      }
      return list;
    }
    return super.explicitCast(scope, o);
  }

  @Override
  protected CompoundTag _writeToNBT(final MCList self) {
    CompoundTag tag = super._writeToNBT(self);
    ListTag list = new ListTag();
    self.forEach(v -> list.add(ProgramManager.getTypeForValue(v).writeToNBT(v)));
    tag.put(VALUES_KEY, list);
    return tag;
  }

  @Override
  public MCList readFromNBT(final Scope scope, final CompoundTag tag) {
    MCList list = new MCList();
    ListTag tagsList = tag.getList(VALUES_KEY, Tag.TAG_COMPOUND);
    tagsList.forEach(t -> list.add(ProgramManager.getTypeForName(((CompoundTag) t).getString(TypeBase.NAME_KEY)).readFromNBT(scope, (CompoundTag) t)));
    return list;
  }
}
