package net.darmo_creations.mccode.interpreter;

import net.minecraft.nbt.CompoundTag;

/**
 * Objects implementing this interface indicate that they can be serialized to an NBT tag.
 */
public interface NBTSerializable {
  /**
   * Serialize the state of this object to an NBT tag.
   *
   * @return The tag.
   */
  CompoundTag writeToNBT();
}
