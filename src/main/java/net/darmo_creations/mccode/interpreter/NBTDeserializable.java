package net.darmo_creations.mccode.interpreter;

import net.minecraft.nbt.CompoundTag;

/**
 * Objects implementing this interface indicate that
 * they can be serialized and deserialized to and from an NBT tag.
 */
public interface NBTDeserializable extends NBTSerializable {
  /**
   * Restore the state of this object from the given tag.
   *
   * @param tag The tag to deserialize.
   */
  void readFromNBT(final CompoundTag tag);
}
