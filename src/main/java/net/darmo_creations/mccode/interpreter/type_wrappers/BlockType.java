package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Property;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.exceptions.MCCodeRuntimeException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Wrapper type for {@link Block} class.
 * <p>
 * New instances can be created by casting {@link String}s.
 */
@Type(name = BlockType.NAME, doc = "Type that represents a block.")
public class BlockType extends TypeBase<Block> {
  public static final String NAME = "block";

  private static final String ID_KEY = "ID";

  @Override
  public Class<Block> getWrappedType() {
    return Block.class;
  }

  @Property(name = "id", doc = "The ID of a `block.")
  public String getID(final Block self) {
    //noinspection ConstantConditions
    return self.getRegistryName().toString();
  }

  @Override
  protected Object __add__(final Scope scope, final Block self, final Object o, boolean inPlace) {
    if (o instanceof String s) {
      return this.__str__(self) + s;
    }
    return super.__add__(scope, self, o, inPlace);
  }

  @Override
  protected Object __eq__(final Scope scope, final Block self, final Object o) {
    if (o instanceof Block b) {
      return this.getID(self).equals(this.getID(b));
    }
    return false;
  }

  @Override
  protected String __str__(final Block self) {
    //noinspection ConstantConditions
    return self.getRegistryName().toString();
  }

  @Override
  public Block explicitCast(final Scope scope, final Object o) throws MCCodeRuntimeException {
    if (o instanceof String s) {
      return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
    }
    return super.explicitCast(scope, o);
  }

  @Override
  public CompoundTag _writeToNBT(final Block self) {
    CompoundTag tag = super._writeToNBT(self);
    tag.putString(ID_KEY, this.getID(self));
    return tag;
  }

  @Override
  public Block readFromNBT(final Scope scope, final CompoundTag tag) {
    return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString(ID_KEY)));
  }
}
