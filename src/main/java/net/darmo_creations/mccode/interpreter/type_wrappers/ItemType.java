package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Property;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.exceptions.MCCodeRuntimeException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Wrapper type for {@link Item} class.
 * <p>
 * Implements __eq__ operator. Can explicitly cast {@link String}s and {@link Block}s.
 */
@Type(name = ItemType.NAME, doc = "Type that represents an item.")
public class ItemType extends TypeBase<Item> {
  public static final String NAME = "item";

  public static final String ID_KEY = "ID";

  @Override
  public Class<Item> getWrappedType() {
    return Item.class;
  }

  @Property(name = "id", doc = "The ID of an item.")
  public String getID(final Item self) {
    //noinspection ConstantConditions
    return self.getRegistryName().toString();
  }

  @Property(name = "max_stack_size", doc = "The max stack size of an item.")
  public Long getMaxStackSize(final Item self) {
    //noinspection deprecation
    return (long) self.getMaxStackSize();
  }

  @Override
  protected Object __add__(final Scope scope, final Item self, final Object o, final boolean inPlace) {
    if (o instanceof String s) {
      return this.__str__(self) + s;
    }
    return super.__add__(scope, self, o, inPlace);
  }

  @Override
  protected Object __eq__(final Scope scope, final Item self, final Object o) {
    if (o instanceof Item i) {
      return this.getID(self).equals(this.getID(i));
    }
    return false;
  }

  @Override
  protected String __str__(final Item self) {
    //noinspection ConstantConditions
    return self.getRegistryName().toString();
  }

  @Override
  public Item explicitCast(final Scope scope, final Object o) throws MCCodeRuntimeException {
    if (o instanceof String s) {
      return ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
    } else if (o instanceof Block b) {
      Item itemBlock = Item.BY_BLOCK.get(b);
      if (itemBlock == Items.AIR) {
        return null;
      }
      return itemBlock;
    }
    return super.explicitCast(scope, o);
  }

  @Override
  public CompoundTag _writeToNBT(final Item self) {
    CompoundTag tag = super._writeToNBT(self);
    tag.putString(ID_KEY, this.getID(self));
    return tag;
  }

  @Override
  public Item readFromNBT(final Scope scope, final CompoundTag tag) {
    return ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag.getString(ID_KEY)));
  }
}
