package net.darmo_creations.mccode.interpreter.nodes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.*;
import java.util.function.Function;

/**
 * Utility class for deserializing {@link Node}s from NBT tags.
 */
public final class NodeNBTHelper {
  private static final Map<Integer, Function<CompoundTag, Node>> NODE_PROVIDERS = new HashMap<>();

  static {
    NODE_PROVIDERS.put(NullLiteralNode.ID, NullLiteralNode::new);
    NODE_PROVIDERS.put(BooleanLiteralNode.ID, BooleanLiteralNode::new);
    NODE_PROVIDERS.put(IntLiteralNode.ID, IntLiteralNode::new);
    NODE_PROVIDERS.put(FloatLiteralNode.ID, FloatLiteralNode::new);
    NODE_PROVIDERS.put(StringLiteralNode.ID, StringLiteralNode::new);
    NODE_PROVIDERS.put(ListLiteralNode.ID, ListLiteralNode::new);
    NODE_PROVIDERS.put(MapLiteralNode.ID, MapLiteralNode::new);
    NODE_PROVIDERS.put(SetLiteralNode.ID, SetLiteralNode::new);

    NODE_PROVIDERS.put(VariableNode.ID, VariableNode::new);
    NODE_PROVIDERS.put(PropertyCallNode.ID, PropertyCallNode::new);
    NODE_PROVIDERS.put(MethodCallNode.ID, MethodCallNode::new);
    NODE_PROVIDERS.put(FunctionCallNode.ID, FunctionCallNode::new);

    NODE_PROVIDERS.put(UnaryOperatorNode.ID, UnaryOperatorNode::new);
    NODE_PROVIDERS.put(BinaryOperatorNode.ID, BinaryOperatorNode::new);
  }

  /**
   * Return the node corresponding to the given tag.
   *
   * @param tag The tag to deserialize.
   * @return The node.
   * @throws IllegalArgumentException If no {@link Node} correspond to the {@link Node#ID_KEY} property.
   */
  public static Node getNodeForTag(final CompoundTag tag) {
    int tagID = tag.getInt(Node.ID_KEY);
    if (!NODE_PROVIDERS.containsKey(tagID)) {
      throw new IllegalArgumentException("Undefined node ID: " + tagID);
    }
    return NODE_PROVIDERS.get(tagID).apply(tag);
  }

  /**
   * Deserialize a list of nodes.
   *
   * @param tag The tag to extract nodes from.
   * @param key The key where the list is located.
   * @return The nodes list.
   */
  public static List<Node> deserializeNodesList(final CompoundTag tag, final String key) {
    List<Node> nodes = new ArrayList<>();
    for (Tag t : tag.getList(key, Tag.TAG_COMPOUND)) {
      nodes.add(getNodeForTag((CompoundTag) t));
    }
    return nodes;
  }

  /**
   * Serialize a list of nodes.
   *
   * @param nodes The nodes to serialize.
   * @return The tag list.
   */
  public static ListTag serializeNodesList(final Collection<Node> nodes) {
    ListTag nodesList = new ListTag();
    nodes.forEach(s -> nodesList.add(s.writeToNBT()));
    return nodesList;
  }

  private NodeNBTHelper() {
  }
}
