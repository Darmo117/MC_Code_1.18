package net.darmo_creations.mccode.interpreter.types;

import java.util.HashMap;
import java.util.Map;

/**
 * A map that associates string keys to any object.
 */
public class MCMap extends HashMap<String, Object> {
  /**
   * Create an empty map.
   */
  public MCMap() {
  }

  /**
   * Create a new map from the another.
   *
   * @param map The map to copy items from.
   */
  public MCMap(Map<? extends String, ?> map) {
    super(map);
  }
}
