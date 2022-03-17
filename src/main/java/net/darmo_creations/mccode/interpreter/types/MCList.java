package net.darmo_creations.mccode.interpreter.types;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A list of objects.
 */
public class MCList extends ArrayList<Object> {
  /**
   * Create an empty list.
   */
  public MCList() {
  }

  /**
   * Create a list from the given collection.
   *
   * @param collection The collection to copy values from.
   */
  public MCList(Collection<?> collection) {
    super(collection);
  }
}
