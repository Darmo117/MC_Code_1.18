package net.darmo_creations.mccode.interpreter.nodes;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.Utils;
import net.darmo_creations.mccode.interpreter.types.MCMap;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A node that represents a map literal.
 */
public class MapLiteralNode extends Node {
  public static final int ID = 6;

  public static final String VALUES_KEY = "Values";

  private final Map<String, Node> values;

  /**
   * Create a map literal node.
   *
   * @param values Map’s entries.
   * @param line   The line this node starts on.
   * @param column The column in the line this node starts at.
   */
  public MapLiteralNode(final Map<String, Node> values, final int line, final int column) {
    super(line, column);
    this.values = new HashMap<>(values);
  }

  /**
   * Create a map literal node from an NBT tag.
   *
   * @param tag The tag to deserialize.
   */
  public MapLiteralNode(final CompoundTag tag) {
    super(tag);
    CompoundTag map = tag.getCompound(VALUES_KEY);
    this.values = new HashMap<>();
    for (String k : map.getAllKeys()) {
      this.values.put(k, NodeNBTHelper.getNodeForTag(map.getCompound(k)));
    }
  }

  @Override
  protected Object evaluateWrapped(Scope scope) {
    return new MCMap(this.values.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
      Object v = e.getValue().evaluate(scope);
      return ProgramManager.getTypeForValue(v).copy(scope, v);
    })));
  }

  @Override
  public CompoundTag writeToNBT() {
    CompoundTag tag = super.writeToNBT();
    CompoundTag map = new CompoundTag();
    this.values.forEach((k, v) -> map.put(k, v.writeToNBT()));
    tag.put(VALUES_KEY, map);
    return tag;
  }

  @Override
  public int getID() {
    return ID;
  }

  @Override
  public String toString() {
    return this.values.entrySet().stream()
        .map(e -> String.format("%s: %s", Utils.escapeString(e.getKey()), e.getValue().toString()))
        .collect(Collectors.joining(", ", "{", "}"));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    MapLiteralNode that = (MapLiteralNode) o;
    return this.values.equals(that.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.values);
  }
}
