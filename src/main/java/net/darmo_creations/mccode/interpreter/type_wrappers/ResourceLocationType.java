package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Property;
import net.darmo_creations.mccode.interpreter.annotations.Type;
import net.darmo_creations.mccode.interpreter.exceptions.CastException;
import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Wrapper type for {@link ResourceLocation} class.
 * <p>
 * New instances can be created by casting {@link String}s or {@link Map}s.
 */
@Type(name = ResourceLocationType.NAME,
    doc = "Resource locations are objects that point to a resource in the game (block, item, etc.).")
public class ResourceLocationType extends TypeBase<ResourceLocation> {
  public static final String NAME = "resource_location";

  public static final String VALUE_KEY = "Value";

  @Override
  public Class<ResourceLocation> getWrappedType() {
    return ResourceLocation.class;
  }

  @Property(name = "namespace", doc = "The namespace of a resource.")
  public String getNamespace(final ResourceLocation self) {
    return self.getNamespace();
  }

  @Property(name = "path", doc = "The path of a resource.")
  public String getPath(final ResourceLocation self) {
    return self.getPath();
  }

  @Override
  protected Object __add__(final Scope scope, final ResourceLocation self, final Object o, final boolean inPlace) {
    if (o instanceof String s) {
      return this.__str__(self) + s;
    }
    return super.__add__(scope, self, o, inPlace);
  }

  @Override
  protected Object __eq__(final Scope scope, final ResourceLocation self, final Object o) {
    if (o instanceof ResourceLocation r) {
      return self.equals(r);
    } else if (o instanceof String s) {
      return self.equals(this.explicitCast(scope, s));
    }
    return false;
  }

  @Override
  protected Object __gt__(final Scope scope, final ResourceLocation self, final Object o) {
    if (o instanceof ResourceLocation r) {
      return self.compareTo(r) > 0;
    }
    return super.__gt__(scope, self, o);
  }

  @Override
  public ResourceLocation explicitCast(Scope scope, final Object o) {
    if (o instanceof String s) {
      return new ResourceLocation(s);
    } else if (o instanceof Map<?, ?> m) {
      StringType stringType = ProgramManager.getTypeInstance(StringType.class);
      if (m.size() != 2 || !m.containsKey("namespace") || !m.containsKey("path")) {
        throw new EvaluationException(scope, "mccode.interpreter.error.resource_location_map_format", m);
      }
      Object namespace = m.get("namespace");
      Object path = m.get("path");
      if (!(path instanceof String)) {
        throw new CastException(scope, stringType, ProgramManager.getTypeForValue(path));
      } else if (namespace == null) {
        return new ResourceLocation((String) path);
      } else if (!(namespace instanceof String)) {
        throw new CastException(scope, stringType, ProgramManager.getTypeForValue(namespace));
      }
      return new ResourceLocation((String) namespace, (String) path);
    }
    return super.explicitCast(scope, o);
  }

  @Override
  protected CompoundTag _writeToNBT(final ResourceLocation self) {
    CompoundTag tag = super._writeToNBT(self);
    tag.putString(VALUE_KEY, self.toString());
    return tag;
  }

  @Override
  public ResourceLocation readFromNBT(final Scope scope, final CompoundTag tag) {
    return new ResourceLocation(tag.getString(VALUE_KEY));
  }
}
