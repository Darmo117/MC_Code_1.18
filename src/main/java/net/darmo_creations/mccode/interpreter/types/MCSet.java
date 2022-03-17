package net.darmo_creations.mccode.interpreter.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A set of objects.
 */
public class MCSet extends HashSet<Object> {
  /**
   * Create an empty set.
   */
  public MCSet() {
  }

  /**
   * Create a set from the given collection.
   *
   * @param collection The collection to copy values from.
   */
  public MCSet(Collection<?> collection) {
    super(collection);
  }

  @Override
  public String toString() {
    Iterator<Object> it = this.iterator();
    if (!it.hasNext()) {
      return "{}";
    }

    StringBuilder sb = new StringBuilder();
    sb.append('{');
    while (true) {
      Object e = it.next();
      sb.append(e == this ? "(this Set)" : e);
      if (!it.hasNext()) {
        return sb.append('}').toString();
      }
      sb.append(',').append(' ');
    }
  }
}
