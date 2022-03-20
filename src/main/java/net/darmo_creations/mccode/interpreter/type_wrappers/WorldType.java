package net.darmo_creations.mccode.interpreter.type_wrappers;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.darmo_creations.mccode.interpreter.Program;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.Utils;
import net.darmo_creations.mccode.interpreter.annotations.*;
import net.darmo_creations.mccode.interpreter.types.*;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Wrapper type for {@link WorldProxy} class.
 * <p>
 * It does not have a cast operator.
 */
@Type(name = WorldType.NAME,
    generateCastOperator = false,
    doc = "This type represents a specific dimension (overworld, nether, end, etc.).")
public class WorldType extends TypeBase<WorldProxy> {
  public static final String NAME = "world";

  @Override
  public Class<WorldProxy> getWrappedType() {
    return WorldProxy.class;
  }

  @Override
  protected List<String> getAdditionalPropertiesNames(final WorldProxy self) {
    List<String> gamerules = new ArrayList<>();
    GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
      @Override
      public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
        gamerules.add("gr_" + key.getId());
      }
    });
    return gamerules;
  }

  @Override
  protected Object __get_property__(final Scope scope, final WorldProxy self, final String propertyName) {
    // Hack to get the value out of the anonymous class
    List<Object> container = new ArrayList<>();
    GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
      @Override
      public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
        if (("gr_" + key.getId()).equals(propertyName)) {
          T value = self.getWorld().getServer().getGameRules().getRule(key);
          if (value instanceof GameRules.BooleanValue b) {
            container.add(b.get());
          } else if (value instanceof GameRules.IntegerValue i) {
            container.add(i.get());
          } else {
            container.add(value.serialize());
          }
        }
      }
    });
    if (!container.isEmpty()) {
      return container.get(0);
    }

    return super.__get_property__(scope, self, propertyName);
  }

  @Override
  protected void __set_property__(final Scope scope, WorldProxy self, final String propertyName, final Object value) {
    MinecraftServer server = self.getWorld().getServer();
    // Hack to get the value out of the anonymous class
    List<Boolean> container = new ArrayList<>();

    GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
      @Override
      public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
        if (("gr_" + key.getId()).equals(propertyName)) {
          GameRules.Value<T> v = server.getGameRules().getRule(key);
          if (v instanceof GameRules.BooleanValue b) {
            b.set(ProgramManager.getTypeInstance(BooleanType.class).implicitCast(scope, value), server);
          } else if (v instanceof GameRules.IntegerValue i) {
            i.set(ProgramManager.getTypeInstance(IntType.class).implicitCast(scope, value).intValue(), server);
          } else {
            //noinspection unchecked
            v.setFrom((T) value, server);
          }
          container.add(true);
        }
      }
    });

    if (!container.isEmpty()) {
      super.__set_property__(scope, self, propertyName, value);
    }
  }

  @Property(name = "seed", doc = "The seed of the world.")
  public Long getSeed(final WorldProxy self) {
    return self.getWorld().getSeed();
  }

  @Property(name = "day", doc = "The current day of the world.")
  public Long getWorldDay(final WorldProxy self) {
    return self.getWorld().getDayTime() / 24000 % 0x7fffffff;
  }

  @Property(name = "day_time", doc = "The current time of day of the world.")
  public Long getWorldDayTime(final WorldProxy self) {
    return self.getWorld().getDayTime() % 24000;
  }

  @Property(name = "game_time", doc = "The current game time of the world.")
  public Long getWorldTick(final WorldProxy self) {
    return self.getWorld().getGameTime() % 0x7fffffff;
  }

  /*
   * /advancement command
   */

  @Method(name = "grant_all_advancements",
      parametersMetadata = {@ParameterMeta(name = "targets", doc = "An entity selector that targets players.")},
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of advancements granted to all targetted players or #null if the action failed."),
      doc = "Grants all advancements to the selected players.")
  public Long grantAllAdvancements(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "advancement",
        "grant", targetSelector, "everything"
    ).orElse(null);
  }

  @Method(name = "grant_advancement",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The advancement to grant."),
          @ParameterMeta(name = "criterion", mayBeNull = true, doc = "An optional criterion for the advancement.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of advancements granted to all targetted players or #null if the action failed."),
      doc = "Grants the given advancement to the selected players.")
  public Long grantAdvancement(final Scope scope, WorldProxy self, final String targetSelector, final String advancement, final String criterion) {
    List<String> args = new ArrayList<>(Arrays.asList("grant", targetSelector, "only", advancement));
    if (criterion != null) {
      args.add(criterion);
    }
    return executeCommand(
        self,
        "advancement",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  @Method(name = "grant_advancements_from",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The root advancement.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of advancements granted to all targetted players or #null if the action failed."),
      doc = "Grants all advancements down from the specified one to the selected players.")
  public Long grantAdvancementsFrom(final Scope scope, WorldProxy self, final String targetSelector, final String advancement) {
    return executeCommand(
        self,
        "advancement",
        "grant", targetSelector, "from", advancement
    ).orElse(null);
  }

  @Method(name = "grant_advancements_through",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The root advancement.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of advancements granted to all targetted players or #null if the action failed."),
      doc = "Grants all advancements whose tree contains the specified one to the selected players.")
  public Long grantAdvancementsThrough(final Scope scope, WorldProxy self, final String targetSelector, final String advancement) {
    return executeCommand(
        self,
        "advancement",
        "grant", targetSelector, "through", advancement
    ).orElse(null);
  }

  @Method(name = "grant_advancements_until",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The root advancement.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of advancements granted to all targetted players or #null if the action failed."),
      doc = "Grants all advancements from the root to the specified to the selected players.")
  public Long grantAdvancementsUntil(final Scope scope, WorldProxy self, final String targetSelector, final String advancement) {
    return executeCommand(
        self,
        "advancement",
        "grant", targetSelector, "until", advancement
    ).orElse(null);
  }

  @Method(name = "revoke_all_advancements",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of advancements revoked from all targetted players or #null if the action failed."),
      doc = "Revokes all advancements from the selected players.")
  public Long revokeAllAdvancements(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "advancement",
        "revoke", targetSelector, "everything"
    ).orElse(null);
  }

  @Method(name = "revoke_advancement",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The advancement to revoke."),
          @ParameterMeta(name = "criterion", mayBeNull = true, doc = "An optional criterion for the advancement.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of advancements revoked from all targetted players or #null if the action failed."),
      doc = "Revokes the given advancement to the selected players.")
  public Long revokeAdvancements(final Scope scope, WorldProxy self, final String targetSelector, final String advancement, final String criterion) {
    List<String> args = new ArrayList<>(Arrays.asList("revoke", targetSelector, "only", advancement));
    if (criterion != null) {
      args.add(criterion);
    }
    return executeCommand(
        self,
        "advancement",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  @Method(name = "revoke_advancements_from",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The root advancement.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of advancements revoked from all targetted players or #null if the action failed."),
      doc = "Revokes all advancements down from the specified one from the selected players.")
  public Long revokeAdvancementsFrom(final Scope scope, WorldProxy self, final String targetSelector, final String advancement) {
    return executeCommand(
        self,
        "advancement",
        "revoke", targetSelector, "from", advancement
    ).orElse(null);
  }

  @Method(name = "revoke_advancements_through",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The root advancement.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of advancements revoked from all targetted players or #null if the action failed."),
      doc = "Revokes all advancements whose tree contains the specified one from the selected players.")
  public Long revokeAdvancementsThrough(final Scope scope, WorldProxy self, final String targetSelector, final String advancement) {
    return executeCommand(
        self,
        "advancement",
        "revoke", targetSelector, "through", advancement
    ).orElse(null);
  }

  @Method(name = "revoke_advancements_until",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The root advancement.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of advancements revoked from all targetted players or #null if the action failed."),
      doc = "Revokes all advancements from the root to the specified from the selected players.")
  public Long revokeAdvancementsUntil(final Scope scope, WorldProxy self, final String targetSelector, final String advancement) {
    return executeCommand(
        self,
        "advancement",
        "revoke", targetSelector, "until", advancement
    ).orElse(null);
  }

  // FIXME test option has been removed
  @Method(name = "has_advancement",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The advancement to check."),
          @ParameterMeta(name = "criterion", mayBeNull = true, doc = "An optional criterion for the advancement.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "#True if all the targetted players have the specified advancement, #false otherwise, #null if an error occured."),
      doc = "Returns whether the selected players have the given advancement.")
  public Boolean hasAdvancement(final Scope scope, WorldProxy self, final String targetSelector, final String advancement, final String criterion) {
    List<String> args = new ArrayList<>(Arrays.asList("test", targetSelector, advancement));
    if (criterion != null) {
      args.add(criterion);
    }
    return executeCommand(
        self,
        "advancement",
        args.toArray(String[]::new)
    ).map(i -> i > 0).orElse(null);
  }

  /*
   * /attribute command
   */

  // TEST
  @Method(name = "get_attribute_value",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets a single player, mob or armor stand."),
          @ParameterMeta(name = "attribute", doc = "The attribute’s name."),
          @ParameterMeta(name = "scale", doc = "A value to multiply the attribute’s value by."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The attribute’s value for the entity, scaled by the given value then cast to an `int, #null if the action failed."),
      doc = "Returns the total value of the given attribute of a single player, armor stand or mob.")
  public Long getAttributeValue(final Scope scope, WorldProxy self, final String targetSelector, final String attributeName, final Double scale) {
    return executeCommand(
        self,
        "attribute",
        targetSelector, attributeName, "get", scale.toString()
    ).orElse(null);
  }

  // TEST
  @Method(name = "get_attribute_base_value",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets a single player, mob or armor stand."),
          @ParameterMeta(name = "attribute", doc = "The attribute’s name."),
          @ParameterMeta(name = "scale", doc = "A value to multiply the attribute’s base value by."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The attribute’s base value for the entity, scaled by the given value then cast to an `int, #null if the action failed."),
      doc = "Returns the base value of the given attribute of a single player, armor stand or mob.")
  public Long getAttributeBaseValue(final Scope scope, WorldProxy self, final String targetSelector, final String attributeName, final Double scale) {
    return executeCommand(
        self,
        "attribute",
        targetSelector, attributeName, "base", "get", scale.toString()
    ).orElse(null);
  }

  // TEST
  @Method(name = "set_attribute_base_value",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets a single player, mob or armor stand."),
          @ParameterMeta(name = "attribute", doc = "The attribute’s name."),
          @ParameterMeta(name = "value", doc = "The new base value."),
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the base value of the given attribute of a single player, armor stand or mob.")
  public Boolean setAttributeBaseValue(final Scope scope, WorldProxy self, final String targetSelector,
                                       final String attributeName, final Double value) {
    return executeCommand(
        self,
        "attribute",
        targetSelector, attributeName, "base", "set", value.toString()
    ).orElse(0L) != 0;
  }

  // TEST
  @Method(name = "add_attribute_modifier",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets a single player, mob or armor stand."),
          @ParameterMeta(name = "attribute", doc = "The attribute’s name."),
          @ParameterMeta(name = "modifier_uuid", doc = "The modifier’s UUID."),
          @ParameterMeta(name = "modifier_name", doc = "The modifier’s name."),
          @ParameterMeta(name = "modifier_value", doc = "The modifier’s value."),
          @ParameterMeta(name = "operation", doc = "The modifier’s operation. One of \"add\", \"multiply\" or \"multiply_base\"."),
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Adds a modifier to the given attribute of a single player, armor stand or mob.")
  public Boolean addAttributeModifier(final Scope scope, WorldProxy self, final String targetSelector, final String attributeName,
                                      final String modifierUUID, final String modifierName, final Double value, final String modifierOperation) {
    return executeCommand(
        self,
        "attribute",
        targetSelector, attributeName, "modifier", "add", modifierUUID, modifierName, value.toString(), modifierOperation
    ).orElse(0L) != 0;
  }

  // TEST
  @Method(name = "remove_attribute_modifier",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets a single player, mob or armor stand."),
          @ParameterMeta(name = "attribute", doc = "The attribute’s name."),
          @ParameterMeta(name = "modifier_uuid", doc = "The modifier’s UUID.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Removes a modifier from the given attribute of a single player, armor stand or mob.")
  public Boolean removeAttributeModifier(final Scope scope, WorldProxy self, final String targetSelector, final String attributeName,
                                         final String modifierUUID) {
    return executeCommand(
        self,
        "attribute",
        targetSelector, attributeName, "modifier", "remove", modifierUUID
    ).orElse(0L) != 0;
  }

  // TEST
  @Method(name = "get_attribute_modifier_value",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets a single player, mob or armor stand."),
          @ParameterMeta(name = "attribute", doc = "The attribute’s name."),
          @ParameterMeta(name = "modifier_uuid", doc = "The modifier’s UUID."),
          @ParameterMeta(name = "scale", doc = "A value to multiply the modifier’s value by."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The modifier’s value for the entity, scaled by the given value then cast to an `int, or #null if the action failed."),
      doc = "Returns the value of a modifier of the given attribute of a single player, armor stand or mob.")
  public Long getAttributeModifierValue(final Scope scope, WorldProxy self, final String targetSelector, final String attributeName,
                                        final String modifierUUID, final Double scale) {
    return executeCommand(
        self,
        "attribute",
        targetSelector, attributeName, "modifier", "get", modifierUUID, scale.toString()
    ).orElse(null);
  }

  /*
   * /bossbar command
   */

  // TEST
  @Method(name = "add_boss_bar",
      parametersMetadata = {
          @ParameterMeta(name = "id", doc = "ID of the bar."),
          @ParameterMeta(name = "name", doc = "Display name of the boss bar")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of custom bossbars that exist after the command is executed or #null if the action failed."),
      doc = "Adds a new boss bar.")
  public Long addBossBar(final Scope scope, WorldProxy self, final String id, final String name) {
    return executeCommand(
        self,
        "bossbar",
        "add", id, name
    ).orElse(null);
  }

  // TEST
  @Method(name = "get_boss_bar_max_value",
      parametersMetadata = {
          @ParameterMeta(name = "id", doc = "ID of the bar.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The boss bar’s maximum value or #null if the action failed."),
      doc = "Returns the maximum value of a boss bar.")
  public Long getBossBarMaxValue(final Scope scope, WorldProxy self, final String id) {
    return executeCommand(
        self,
        "bossbar",
        "get", id, "max"
    ).orElse(null);
  }

  // TEST
  @Method(name = "set_boss_bar_max_value",
      parametersMetadata = {
          @ParameterMeta(name = "id", doc = "ID of the bar."),
          @ParameterMeta(name = "value", doc = "The new max value."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The boss bar’s new maximum value or #null if the action failed."),
      doc = "Sets the maximum value of a boss bar.")
  public Long setBossBarMaxValue(final Scope scope, WorldProxy self, final String id, final Long value) {
    return executeCommand(
        self,
        "bossbar",
        "set", id, "max", value.toString()
    ).orElse(null);
  }

  // TEST
  @Method(name = "get_boss_bar_value",
      parametersMetadata = {
          @ParameterMeta(name = "id", doc = "ID of the bar.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The boss bar’s value or #null if the action failed."),
      doc = "Returns the value of a boss bar.")
  public Long getBossBarValue(final Scope scope, WorldProxy self, final String id) {
    return executeCommand(
        self,
        "bossbar",
        "get", id, "value"
    ).orElse(null);
  }

  // TEST
  @Method(name = "set_boss_bar_value",
      parametersMetadata = {
          @ParameterMeta(name = "id", doc = "ID of the bar."),
          @ParameterMeta(name = "value", doc = "The new value."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The boss bar’s new value or #null if the action failed."),
      doc = "Sets the value of a boss bar.")
  public Long setBossBarValue(final Scope scope, WorldProxy self, final String id, final Long value) {
    return executeCommand(
        self,
        "bossbar",
        "set", id, "value", value.toString()
    ).orElse(null);
  }

  // TEST
  @Method(name = "is_boss_bar_visible",
      parametersMetadata = {
          @ParameterMeta(name = "id", doc = "ID of the bar.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "#True if the boss bar is visible, #false otherwise, #null if an error occured."),
      doc = "Returns whether a boss bar is visible.")
  public Boolean isBossBarVisible(final Scope scope, WorldProxy self, final String id) {
    return executeCommand(
        self,
        "bossbar",
        "get", id, "visible"
    ).map(i -> i > 0).orElse(null);
  }

  // TEST
  @Method(name = "set_boss_bar_visible",
      parametersMetadata = {
          @ParameterMeta(name = "id", doc = "ID of the bar."),
          @ParameterMeta(name = "visible", doc = "Whether the bar should be visible."),
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if action succeeded, #false otherwise."),
      doc = "Sets the visibility of a boss bar.")
  public Boolean setBossBarVisible(final Scope scope, WorldProxy self, final String id, final Boolean visible) {
    return executeCommand(
        self,
        "bossbar",
        "set", id, "visible", visible.toString()
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "get_boss_bar_players_number",
      parametersMetadata = {
          @ParameterMeta(name = "id", doc = "ID of the bar.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of players to whom the bar is visible or #null if the action failed."),
      doc = "Returns the number of players to whom a bar is visible.")
  public Long getBossBarPlayersNumber(final Scope scope, WorldProxy self, final String id) {
    return executeCommand(
        self,
        "bossbar",
        "get", id, "players"
    ).orElse(null);
  }

  // TEST
  @Method(name = "set_boss_bar_players",
      parametersMetadata = {
          @ParameterMeta(name = "id", doc = "ID of the bar."),
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players for whom the bar should be visible."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of players to whom the bar is visible or #null if the action failed."),
      doc = "Sets the set of players to whom a bar is visible.")
  public Long setBossBarPlayers(final Scope scope, WorldProxy self, final String id, final String targetSelector) {
    return executeCommand(
        self,
        "bossbar",
        "set", id, "players", targetSelector
    ).orElse(null);
  }

  // TEST
  @Method(name = "set_boss_bar_name",
      parametersMetadata = {
          @ParameterMeta(name = "id", doc = "ID of the bar."),
          @ParameterMeta(name = "name", doc = "The new name."),
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if action succeeded, #false otherwise."),
      doc = "Sets the display name of a boss bar.")
  public Boolean setBossBarName(final Scope scope, WorldProxy self, final String id, final String name) {
    return executeCommand(
        self,
        "bossbar",
        "set", id, "name", name
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "set_boss_bar_style",
      parametersMetadata = {
          @ParameterMeta(name = "id", doc = "ID of the bar."),
          @ParameterMeta(name = "style", doc = "The new style. One of \"notched_6\", \"notched_10\", \"notched_12\", \"notched_20\" or \"progress\"."),
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if action succeeded, #false otherwise."),
      doc = "Sets the display name of a boss bar.")
  public Boolean setBossBarStyle(final Scope scope, WorldProxy self, final String id, final String style) {
    return executeCommand(
        self,
        "bossbar",
        "set", id, "style", style
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "set_boss_bar_color",
      parametersMetadata = {
          @ParameterMeta(name = "id", doc = "ID of the bar."),
          @ParameterMeta(name = "color", doc = "The new color. One of \"blue\", \"green\", \"pink\", \"purple\", \"red\", \"white\" or \"yellow\"."),
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if action succeeded, #false otherwise."),
      doc = "Sets the color of a boss bar.")
  public Boolean setBossBarColor(final Scope scope, WorldProxy self, final String id, final String color) {
    return executeCommand(
        self,
        "bossbar",
        "set", id, "color", color
    ).orElse(-1L) > 0;
  }

  /*
   * /clear command
   */

  @Method(name = "clear_inventory",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of removed items or #null if the action failed."),
      doc = "Removes all items from the inventory of the selected players.")
  public Long clearInventory(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "clear",
        targetSelector
    ).orElse(null);
  }

  @Method(name = "clear_items",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "item", doc = "The item to delete."),
          @ParameterMeta(name = "data_tags", mayBeNull = true, doc = "Optional data tags for the item."),
          @ParameterMeta(name = "max_count", doc = "The maximum number of items to delete.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise"),
      doc = "Removes matching items from the inventory of selected players.")
  public Boolean clearItems(final Scope scope, WorldProxy self, final String targetSelector,
                            final String item, final MCMap dataTags, final Long maxCount) {
    List<String> args = new ArrayList<>(Arrays.asList(targetSelector, item + mapToDataTag(dataTags)));
    if (maxCount != null) {
      args.add(maxCount.toString());
    }
    return executeCommand(
        self,
        "clear",
        args.toArray(String[]::new)
    ).orElse(-1L) > 0;
  }

  @Method(name = "get_items_count",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "item", doc = "ID of the item to query the number of occurences."),
          @ParameterMeta(name = "data_tags", mayBeNull = true, doc = "Optional data tags for the item.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of matching items or #null if the operation failed."),
      doc = "Returns the number of items matching the given one that are present in the given players’ inventory.")
  public Long getItemsCount(final Scope scope, WorldProxy self, final String targetSelector,
                            final String item, final MCMap dataTags) {
    return executeCommand(
        self,
        "clear",
        targetSelector, item + mapToDataTag(dataTags), "0"
    ).orElse(null);
  }

  /*
   * /clone command
   */

  @Method(name = "clone",
      parametersMetadata = {
          @ParameterMeta(name = "pos1", doc = "First position."),
          @ParameterMeta(name = "pos2", doc = "Second position."),
          @ParameterMeta(name = "dest", doc = "Destination position. Corresponds to the smallest position of the cloned region."),
          @ParameterMeta(name = "mask_mode", doc = "Either \"replace\" to copy all blocks or \"masked\" to copy only non-air blocks."),
          @ParameterMeta(name = "clone_mode", doc = "Either \"force\" to force clone even if regions overlap, " +
              "\"move\" to move blocks in region instead of simply cloning them or " +
              "\"normal\" to not force in case of overlap nor move blocks.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of affected blocks or #null if the action failed."),
      doc = "Clones blocks from one region to another.")
  public Long clone(final Scope scope, WorldProxy self, final Position pos1, final Position pos2, final Position destination,
                    final String maskMode, final String cloneMode) {
    BlockPos p1 = pos1.toBlockPos();
    BlockPos p2 = pos2.toBlockPos();
    BlockPos dest = destination.toBlockPos();
    return executeCommand(
        self,
        "clone",
        "" + p1.getX(), "" + p1.getY(), "" + p1.getZ(),
        "" + p2.getX(), "" + p2.getY(), "" + p2.getZ(),
        "" + dest.getX(), "" + dest.getY(), "" + dest.getZ(),
        maskMode, cloneMode
    ).orElse(null);
  }

  @Method(name = "clone_filtered",
      parametersMetadata = {
          @ParameterMeta(name = "pos1", doc = "First position."),
          @ParameterMeta(name = "pos2", doc = "Second position."),
          @ParameterMeta(name = "dest", doc = "Destination position. Corresponds to the smallest position of the cloned region."),
          @ParameterMeta(name = "block", doc = "ID of the blocks to clone."),
          @ParameterMeta(name = "block_state", mayBeNull = true, doc = "Optional block state of the blocks to clone."),
          @ParameterMeta(name = "data_tags", mayBeNull = true, doc = "Optional data tags of the blocks to clone."),
          @ParameterMeta(name = "clone_mode", doc = "Either \"force\" to force clone even if regions overlap, " +
              "\"move\" to move blocks in region instead of simply cloning them or " +
              "\"normal\" to not force in case of overlap nor move blocks.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of affected blocks or #null if the action failed."),
      doc = "Clones from one region to another only blocks that match the given predicate.")
  public Long clone(final Scope scope, WorldProxy self, final Position pos1, final Position pos2, final Position destination,
                    final String block, final MCMap blockState, final MCMap dataTags, final String cloneMode) {
    BlockPos p1 = pos1.toBlockPos();
    BlockPos p2 = pos2.toBlockPos();
    BlockPos dest = destination.toBlockPos();
    String blockPredicate = block;
    if (blockState != null) {
      blockPredicate += mapToBlockState(blockState);
    }
    if (dataTags != null) {
      blockPredicate += mapToDataTag(dataTags);
    }
    return executeCommand(
        self,
        "clone",
        "" + p1.getX(), "" + p1.getY(), "" + p1.getZ(),
        "" + p2.getX(), "" + p2.getY(), "" + p2.getZ(),
        "" + dest.getX(), "" + dest.getY(), "" + dest.getZ(),
        "filtered", blockPredicate, cloneMode
    ).orElse(null);
  }

  /*
   * /data command
   */

  // TEST
  @Method(name = "get_data",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to get data from. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to get data from."),
          @ParameterMeta(name = "target_nbt_path", mayBeNull = true, doc = "The path to the data to query. If #null, all data is returned.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The queried data or #null if an error occured."),
      doc = "Returns NBT data from the specified target.")
  public Object getData(final Scope scope, WorldProxy self, final String targetType, final Object target, final String targetNBTPath) {
    // TODO
    return null;
  }

  // TEST
  @Method(name = "append_data_from",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to modify. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to change the data of."),
          @ParameterMeta(name = "target_nbt_path", doc = "The NBT path where to append the data."),
          @ParameterMeta(name = "source_type", doc = "Type of the source to get data from. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "source", doc = "Depending on the value of the previous argument, the block position, " +
              "entity selector or storage resource location to get data from."),
          @ParameterMeta(name = "source_nbt_path", mayBeNull = true, doc = "The NBT path where to get the data from. May be #null.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of lists or arrays to which new elements are added or #null if an error occured."),
      doc = "Appends data from the given source to the end of the given target’s list.")
  public Long appendDataFrom(final Scope scope, WorldProxy self,
                             final String targetType, final Object target, final String targetNBTPath,
                             final String sourceType, final Object source, final String sourceNBTPath) {
    List<String> args = new ArrayList<>(List.of("modify"));
    appendDataTarget(scope, targetType, target, args);
    args.add(targetNBTPath);
    args.add("append");
    args.add("from");
    appendDataTarget(scope, sourceType, source, args);
    if (sourceNBTPath != null) {
      args.add(sourceNBTPath);
    }
    return executeCommand(
        self,
        "data",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "append_data",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to modify. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to change the data of."),
          @ParameterMeta(name = "target_nbt_path", doc = "The NBT path where to append the data."),
          @ParameterMeta(name = "nbt", doc = "A `string representing an NBT tag.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of lists or arrays to which new elements are added or #null if an error occured."),
      doc = "Appends data to the end of the given target’s list.")
  public Long appendData(final Scope scope, WorldProxy self,
                         final String targetType, final Object target, final String targetNBTPath,
                         final String nbt) {
    List<String> args = new ArrayList<>(List.of("modify"));
    appendDataTarget(scope, targetType, target, args);
    args.add(targetNBTPath);
    args.add("append");
    args.add("value");
    args.add(nbt);
    return executeCommand(
        self,
        "data",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "prepend_data_from",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to modify. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to change the data of."),
          @ParameterMeta(name = "target_nbt_path", doc = "The NBT path where to prepend the data."),
          @ParameterMeta(name = "source_type", doc = "Type of the source to get data from. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "source", doc = "Depending on the value of the previous argument, the block position, " +
              "entity selector or storage resource location to get data from."),
          @ParameterMeta(name = "source_nbt_path", mayBeNull = true, doc = "The NBT path where to get the data from. May be #null.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of lists or arrays to which new elements are added or #null if an error occured."),
      doc = "Prepends data from the given source to the start of the given target’s list.")
  public Long prependDataFrom(final Scope scope, WorldProxy self,
                              final String targetType, final Object target, final String targetNBTPath,
                              final String sourceType, final Object source, final String sourceNBTPath) {
    List<String> args = new ArrayList<>(List.of("modify"));
    appendDataTarget(scope, targetType, target, args);
    args.add(targetNBTPath);
    args.add("prepend");
    args.add("from");
    appendDataTarget(scope, sourceType, source, args);
    if (sourceNBTPath != null) {
      args.add(sourceNBTPath);
    }
    return executeCommand(
        self,
        "data",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "prepend_data",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to modify. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to change the data of."),
          @ParameterMeta(name = "target_nbt_path", doc = "The NBT path where to prepend the data."),
          @ParameterMeta(name = "nbt", doc = "A `string representing an NBT tag.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of lists or arrays to which new elements are added or #null if an error occured."),
      doc = "Prepends data to the start of the given target’s list.")
  public Long prependData(final Scope scope, WorldProxy self,
                          final String targetType, final Object target, final String targetNBTPath,
                          final String nbt) {
    List<String> args = new ArrayList<>(List.of("modify"));
    appendDataTarget(scope, targetType, target, args);
    args.add(targetNBTPath);
    args.add("prepend");
    args.add("value");
    args.add(nbt);
    return executeCommand(
        self,
        "data",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "insert_data_from",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to modify. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to change the data of."),
          @ParameterMeta(name = "target_nbt_path", doc = "The NBT path where to insert the data."),
          @ParameterMeta(name = "index", doc = "The index at which data should be inserted."),
          @ParameterMeta(name = "source_type", doc = "Type of the source to get data from. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "source", doc = "Depending on the value of the previous argument, the block position, " +
              "entity selector or storage resource location to get data from."),
          @ParameterMeta(name = "source_nbt_path", mayBeNull = true, doc = "The NBT path where to get the data from. May be #null.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of lists or arrays to which new elements are added or #null if an error occured."),
      doc = "Inserts data from the given source to the specified index in the given target’s list.")
  public Long insertDataFrom(final Scope scope, WorldProxy self,
                             final String targetType, final Object target, final String targetNBTPath,
                             final Long index,
                             final String sourceType, final Object source, final String sourceNBTPath) {
    List<String> args = new ArrayList<>(List.of("modify"));
    appendDataTarget(scope, targetType, target, args);
    args.add(targetNBTPath);
    args.add("insert");
    args.add(index.toString());
    args.add("from");
    appendDataTarget(scope, sourceType, source, args);
    if (sourceNBTPath != null) {
      args.add(sourceNBTPath);
    }
    return executeCommand(
        self,
        "data",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "insert_data",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to modify. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to change the data of."),
          @ParameterMeta(name = "target_nbt_path", doc = "The NBT path where to insert the data."),
          @ParameterMeta(name = "index", doc = "The index at which data should be inserted."),
          @ParameterMeta(name = "nbt", doc = "A `string representing an NBT tag.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of lists or arrays to which new elements are added or #null if an error occured."),
      doc = "Prepends data to the specified index in the given target’s list.")
  public Long insertData(final Scope scope, WorldProxy self,
                         final String targetType, final Object target, final String targetNBTPath,
                         final Long index, final String nbt) {
    List<String> args = new ArrayList<>(List.of("modify"));
    appendDataTarget(scope, targetType, target, args);
    args.add(targetNBTPath);
    args.add("insert");
    args.add(index.toString());
    args.add("value");
    args.add(nbt);
    return executeCommand(
        self,
        "data",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "merge_data_from",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to modify. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to change the data of."),
          @ParameterMeta(name = "target_nbt_path", doc = "The NBT path where to merge the data."),
          @ParameterMeta(name = "source_type", doc = "Type of the source to get data from. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "source", doc = "Depending on the value of the previous argument, the block position, " +
              "entity selector or storage resource location to get data from."),
          @ParameterMeta(name = "source_nbt_path", mayBeNull = true, doc = "The NBT path where to get the data from. May be #null.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of target tags that were successfully modified or #null if an error occured."),
      doc = "Merges data from the given source into the given target’s object.")
  public Long mergeDataFrom(final Scope scope, WorldProxy self,
                            final String targetType, final Object target, final String targetNBTPath,
                            final String sourceType, final Object source, final String sourceNBTPath) {
    List<String> args = new ArrayList<>(List.of("modify"));
    appendDataTarget(scope, targetType, target, args);
    args.add(targetNBTPath);
    args.add("merge");
    args.add("from");
    appendDataTarget(scope, sourceType, source, args);
    if (sourceNBTPath != null) {
      args.add(sourceNBTPath);
    }
    return executeCommand(
        self,
        "data",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "merge_data",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to modify. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to change the data of."),
          @ParameterMeta(name = "target_nbt_path", doc = "The NBT path where to merge the data."),
          @ParameterMeta(name = "nbt", doc = "A `string representing an NBT tag.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of target tags that were successfully modified or #null if an error occured."),
      doc = "Merges data into the given target’s object.")
  public Long mergeData(final Scope scope, WorldProxy self,
                        final String targetType, final Object target, final String targetNBTPath,
                        final String nbt) {
    List<String> args = new ArrayList<>(List.of("modify"));
    appendDataTarget(scope, targetType, target, args);
    args.add(targetNBTPath);
    args.add("merge");
    args.add("value");
    args.add(nbt);
    return executeCommand(
        self,
        "data",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "set_data_from",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to modify. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to change the data of."),
          @ParameterMeta(name = "target_nbt_path", doc = "The NBT path where to set the data."),
          @ParameterMeta(name = "source_type", doc = "Type of the source to get data from. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "source", doc = "Depending on the value of the previous argument, the block position, " +
              "entity selector or storage resource location to get data from."),
          @ParameterMeta(name = "source_nbt_path", mayBeNull = true, doc = "The NBT path where to get the data from. May be #null.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of target tags that were successfully modified or #null if an error occured."),
      doc = "Sets the tag specified by $target_nbt_path to the source data.")
  public Long setDataFrom(final Scope scope, WorldProxy self,
                          final String targetType, final Object target, final String targetNBTPath,
                          final String sourceType, final Object source, final String sourceNBTPath) {
    List<String> args = new ArrayList<>(List.of("modify"));
    appendDataTarget(scope, targetType, target, args);
    args.add(targetNBTPath);
    args.add("set");
    args.add("from");
    appendDataTarget(scope, sourceType, source, args);
    if (sourceNBTPath != null) {
      args.add(sourceNBTPath);
    }
    return executeCommand(
        self,
        "data",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "set_data",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to modify. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to change the data of."),
          @ParameterMeta(name = "target_nbt_path", doc = "The NBT path where to set the data."),
          @ParameterMeta(name = "nbt", doc = "A `string representing an NBT tag.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of target tags that were successfully modified or #null if an error occured."),
      doc = "Sets the tag specified by $target_nbt_path to the specified value.")
  public Long setData(final Scope scope, WorldProxy self,
                      final String targetType, final Object target, final String targetNBTPath,
                      final String nbt) {
    List<String> args = new ArrayList<>(List.of("modify"));
    appendDataTarget(scope, targetType, target, args);
    args.add(targetNBTPath);
    args.add("set");
    args.add("value");
    args.add(nbt);
    return executeCommand(
        self,
        "data",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "merge_nbt_data",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to modify. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to change the data of."),
          @ParameterMeta(name = "nbt", doc = "A `string representing an NBT compound tag.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Merges the given data with the specified target’s data.")
  public Boolean mergeNBTData(final Scope scope, WorldProxy self,
                              final String targetType, final Object target, final String nbt) {
    List<String> args = new ArrayList<>(List.of("merge"));
    appendDataTarget(scope, targetType, target, args);
    args.add(nbt);
    return executeCommand(
        self,
        "data",
        args.toArray(String[]::new)
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "remove_data",
      parametersMetadata = {
          @ParameterMeta(name = "target_type", doc = "Type of the target to modify. One of \"block\", \"entity\" or \"storage\"."),
          @ParameterMeta(name = "target", doc = "Depending on the value of the first argument, the block position, " +
              "entity selector or storage resource location to change the data of."),
          @ParameterMeta(name = "target_nbt_path", doc = "The path to the data to be removed.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Removes the data specified by $target_nbt_path from the specified target.")
  public Boolean removeData(final Scope scope, WorldProxy self,
                            final String targetType, final Object target, final String targetNBTPath) {
    List<String> args = new ArrayList<>(List.of("remove"));
    appendDataTarget(scope, targetType, target, args);
    args.add(targetNBTPath);
    return executeCommand(
        self,
        "data",
        args.toArray(String[]::new)
    ).orElse(-1L) > 0;
  }

  private static void appendDataTarget(final Scope scope, final String type, final Object source, List<String> args) {
    args.add(type);
    if ("block".equals(type)) {
      BlockPos p = ProgramManager.getTypeInstance(PosType.class).implicitCast(scope, source).toBlockPos();
      args.add("" + p.getX());
      args.add("" + p.getY());
      args.add("" + p.getZ());
    } else {
      args.add(source.toString());
    }
  }

  /*
   * /datapack command
   */

  // TEST
  @Method(name = "disable_datapack",
      parametersMetadata = {
          @ParameterMeta(name = "name", doc = "Name of the datapack to disable.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the datapack was disabled, #false otherwise."),
      doc = "Disables a datapack.")
  public Boolean disableDatapack(final Scope scope, WorldProxy self, final String name) {
    return executeCommand(
        self,
        "datapack",
        "disable", name
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "enable_datapack",
      parametersMetadata = {
          @ParameterMeta(name = "name", doc = "Name of the datapack to enable.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the datapack was enabled, #false otherwise."),
      doc = "Enables a datapack.")
  public Boolean enableDatapack(final Scope scope, WorldProxy self, final String name) {
    return executeCommand(
        self,
        "datapack",
        "enable", name
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "enable_datapack_first",
      parametersMetadata = {
          @ParameterMeta(name = "name", doc = "Name of the datapack to enable.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the datapack was enabled, #false otherwise."),
      doc = "Enables a datapack before all others.")
  public Boolean enableDatapackFirst(final Scope scope, WorldProxy self, final String name) {
    return executeCommand(
        self,
        "datapack",
        "enable", name, "first"
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "enable_datapack_last",
      parametersMetadata = {
          @ParameterMeta(name = "name", doc = "Name of the datapack to enable.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the datapack was enabled, #false otherwise."),
      doc = "Enables a datapack after all others.")
  public Boolean enableDatapackLast(final Scope scope, WorldProxy self, final String name) {
    return executeCommand(
        self,
        "datapack",
        "enable", name, "last"
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "enable_datapack_before",
      parametersMetadata = {
          @ParameterMeta(name = "name", doc = "Name of the datapack to enable."),
          @ParameterMeta(name = "other", doc = "Name of the datapack the first one should be loaded before.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the datapack was enabled, #false otherwise."),
      doc = "Enables a datapack before another one.")
  public Boolean enableDatapackBefore(final Scope scope, WorldProxy self, final String name, final String other) {
    return executeCommand(
        self,
        "datapack",
        "enable", name, "before", other
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "enable_datapack_after",
      parametersMetadata = {
          @ParameterMeta(name = "name", doc = "Name of the datapack to enable."),
          @ParameterMeta(name = "other", doc = "Name of the datapack the first one should be loaded after.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the datapack was enabled, #false otherwise."),
      doc = "Enables a datapack before after another one.")
  public Boolean enableDatapackAfter(final Scope scope, WorldProxy self, final String name, final String other) {
    return executeCommand(
        self,
        "datapack",
        "enable", name, "after", other
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "list_all_datapacks",
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The `list of names of all available and/or enabled datapacks, #null if an error occured."),
      doc = "Returns the `list of names of all available and/or enabled datapacks.")
  public MCList listAllDatapacks(final Scope scope, WorldProxy self) {
    MCList list = listAvailablePacks(scope);
    list.addAll(listEnabledPacks(scope));
    return list;
  }

  // TEST
  @Method(name = "list_available_datapacks",
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The `list of names of all available datapacks, #null if an error occured."),
      doc = "Returns the `list of names of all available datapacks.")
  public MCList listAvailableDatapacks(final Scope scope, WorldProxy self) {
    return listAvailablePacks(scope);
  }

  // TEST
  @Method(name = "list_enabled_datapacks",
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The `list of names of all enabled datapacks, #null if an error occured."),
      doc = "Returns the `list of names of all available datapacks.")
  public MCList listEnabledDatapacks(final Scope scope, WorldProxy self) {
    return listEnabledPacks(scope);
  }

  private static MCList listAvailablePacks(final Scope scope) {
    PackRepository packrepository = scope.getProgram().getProgramManager().getWorld().getServer().getPackRepository();
    packrepository.reload();
    Collection<String> selectedPacks = packrepository.getSelectedPacks().stream().map(Pack::getId).toList();
    Collection<String> availablePacks = packrepository.getAvailablePacks().stream().map(Pack::getId).toList();
    return new MCList(availablePacks.stream().filter(pack -> !selectedPacks.contains(pack)).collect(Collectors.toList()));
  }

  private static MCList listEnabledPacks(final Scope scope) {
    PackRepository packrepository = scope.getProgram().getProgramManager().getWorld().getServer().getPackRepository();
    packrepository.reload();
    return new MCList(packrepository.getSelectedPacks().stream().map(Pack::getId).toList());
  }

  /*
   * /defaultgamemode command
   */

  @Method(name = "set_default_gamemode",
      parametersMetadata = {
          @ParameterMeta(name = "gamemode",
              doc = "The new default gamemode. One of \"survival\", \"creative\", \"adventure\" or \"spectator\".")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action was successful, #false otherwise."),
      doc = "Sets the default game mode for new players.")
  public Boolean setDefaultGameMode(final Scope scope, WorldProxy self, final String gamemode) {
    return executeCommand(
        self,
        "defaultgamemode",
        gamemode
    ).orElse(-1L) > 0;
  }

  /*
   * /difficulty command
   */

  // TEST
  @Method(name = "get_difficulty",
      returnTypeMetadata = @ReturnMeta(mayBeNull = true, doc = "The current difficulty."),
      doc = "Returns the current difficulty.")
  public String getDifficulty(final Scope scope, WorldProxy self) {
    return executeCommand(
        self,
        "difficulty"
    ).map(i -> switch ((int) (i % 4)) {
      case 1 -> "easy";
      case 2 -> "normal";
      case 3 -> "hard";
      default -> "peaceful";
    }).orElse(null);
  }

  @Method(name = "set_difficulty",
      parametersMetadata = {
          @ParameterMeta(name = "difficulty",
              doc = "The new difficulty. One of \"peaceful\", \"easy\", \"normal\" or \"difficulty\".")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action was successful, #false otherwise."),
      doc = "Sets the difficulty level.")
  public Boolean setDifficulty(final Scope scope, WorldProxy self, final String difficulty) {
    return executeCommand(
        self,
        "difficulty",
        difficulty
    ).orElse(-1L) > 0;
  }

  /*
   * /effect command
   */

  @Method(name = "clear_all_effects",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of affected entities or #null if the action failed."),
      doc = "Removes all effects from the selected entities.")
  public Long clearAllEffects(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "effect",
        targetSelector, "clear"
    ).orElse(null);
  }

  @Method(name = "clear_effect",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector."),
          @ParameterMeta(name = "effect", doc = "The effect to clear.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of affected entities or #null if the action failed."),
      doc = "Removes a single effect from the selected entities.")
  public Long clearAllEffects(final Scope scope, WorldProxy self, final String targetSelector, final String effect) {
    return executeCommand(
        self,
        "effect",
        targetSelector, "clear", effect
    ).orElse(null);
  }

  @Method(name = "give_effect",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "The entities to target."),
          @ParameterMeta(name = "effect", doc = "The effect to apply."),
          @ParameterMeta(name = "seconds", doc = "The number of seconds the effect should last."),
          @ParameterMeta(name = "amplifier", doc = "The number of additional levels to add to the effect"),
          @ParameterMeta(name = "hide_particles", doc = "Whether to hide particles.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of affected entities or #null if the action failed."),
      doc = "Gives an effect to the selected entities.")
  public Long giveEffect(final Scope scope, WorldProxy self, final String targetSelector, final String effect,
                         final Long seconds, final Long amplifier, final Boolean hideParticles) {
    return executeCommand(
        self,
        "effect",
        targetSelector, effect, seconds.toString(), amplifier.toString(), hideParticles.toString()
    ).orElse(null);
  }

  /*
   * /enchant command
   */

  @Method(name = "enchant_selected_item",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector."),
          @ParameterMeta(name = "enchantment", doc = "Name of the enchantment to apply to the players’ selected item."),
          @ParameterMeta(name = "level", doc = "Enchantment’s level.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of players whose items are successfully enchanted or #null if the action failed."),
      doc = "Enchants the active item of all selected entities with the given enchantment.")
  public Long enchantSelectedItem(final Scope scope, WorldProxy self, final String targetSelector,
                                  final String enchantment, final Long level) {
    return executeCommand(
        self,
        "enchant",
        targetSelector, enchantment, level.toString()
    ).orElse(null);
  }

  /*
   * /experience /xp commands
   */

  @Method(name = "get_xp",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players to get XP of."),
          @ParameterMeta(name = "levels", doc = "If #true returned amount is XP levels, otherwise XP points.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of experience points or levels the players have or #null if the action failed."),
      doc = "Returns the number of experience points or levels the players have.")
  public Long getXP(final Scope scope, WorldProxy self, final String targetSelector, final Boolean levels) {
    return executeCommand(
        self,
        "xp",
        targetSelector, levels ? "levels" : "points"
    ).orElse(null);
  }

  @Method(name = "set_xp",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players to set XP of."),
          @ParameterMeta(name = "amount", doc = "The amount of XP points or levels to set."),
          @ParameterMeta(name = "levels", doc = "If #true amount is interpreted as XP levels, otherwise as XP points.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targeted players or #null if the action failed."),
      doc = "Sets the XP points or levels of the selected players.")
  public Long setXP(final Scope scope, WorldProxy self, final String targetSelector, final Long amount, final Boolean levels) {
    return executeCommand(
        self,
        "xp",
        targetSelector, amount.toString(), levels ? "levels" : "points"
    ).orElse(null);
  }

  @Method(name = "give_xp",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players to give XP to."),
          @ParameterMeta(name = "amount", doc = "The amount of XP points or levels to give to the selected players. May be negative."),
          @ParameterMeta(name = "levels", doc = "If #true amount is interpreted as XP levels, otherwise as XP points.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targeted players or #null if the action failed."),
      doc = "Gives the indicated amount of XP points or levels to the selected players.")
  public Long giveXP(final Scope scope, WorldProxy self, final String targetSelector, final Long amount, final Boolean levels) {
    return executeCommand(
        self,
        "xp",
        targetSelector, amount.toString(), levels ? "levels" : "points"
    ).orElse(null);
  }

  /*
   * /fill command
   */

  @Method(name = "fill",
      parametersMetadata = {
          @ParameterMeta(name = "pos1", doc = "First position."),
          @ParameterMeta(name = "pos2", doc = "Second position."),
          @ParameterMeta(name = "block", doc = "ID of block to fill region with."),
          @ParameterMeta(name = "block_state", mayBeNull = true, doc = "State of the block."),
          @ParameterMeta(name = "data_tags", mayBeNull = true, doc = "Data tags for the block."),
          @ParameterMeta(name = "mode", doc = "Fill mode. One of \"destroy\", \"hollow\", \"keep\", \"outline\" or \"replace\".")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of affected blocks or #null if the action failed."),
      doc = "Fills the region between the given positions in this world with the specified block.")
  public Long fill(final Scope scope, WorldProxy self, final Position pos1, final Position pos2,
                   final String block, final MCMap blockState, final MCMap dataTags, final String mode) {
    BlockPos p1 = pos1.toBlockPos();
    BlockPos p2 = pos2.toBlockPos();
    String blockPredicate = block;
    if (blockState != null) {
      blockPredicate += mapToBlockState(blockState);
    }
    if (dataTags != null) {
      blockPredicate += mapToDataTag(dataTags);
    }
    List<String> args = new ArrayList<>(Arrays.asList(
        "" + p1.getX(), "" + p1.getY(), "" + p1.getZ(),
        "" + p2.getX(), "" + p2.getY(), "" + p2.getZ(),
        blockPredicate, mode
    ));
    if (!"replace".equals(mode)) {
      args.add(mapToJSON(dataTags));
    }
    return executeCommand(
        self,
        "fill",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  @Method(name = "fill_replace",
      parametersMetadata = {
          @ParameterMeta(name = "pos1", doc = "First position."),
          @ParameterMeta(name = "pos2", doc = "Second position."),
          @ParameterMeta(name = "block", doc = "ID of block to fill region with."),
          @ParameterMeta(name = "block_state", mayBeNull = true, doc = "State of the block."),
          @ParameterMeta(name = "data_tags", mayBeNull = true, doc = "Data tags for the block."),
          @ParameterMeta(name = "block2", doc = "ID of blocks to replace."),
          @ParameterMeta(name = "block_state2", mayBeNull = true, doc = "State of the blocks to replace."),
          @ParameterMeta(name = "data_tags2", mayBeNull = true, doc = "Data tags for the blocks to replace.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of affected blocks or #null if the action failed."),
      doc = "Fills the region between the given positions in this world with the specified block in replace mode.")
  public Long fill(final Scope scope, WorldProxy self, final Position pos1, final Position pos2,
                   final String block, final MCMap blockState, final MCMap dataTags,
                   final String blockToReplace, final MCMap blockStateToReplace, final MCMap dataTagsToReplace) {
    BlockPos p1 = pos1.toBlockPos();
    BlockPos p2 = pos2.toBlockPos();
    String blockPredicate = block;
    if (blockState != null) {
      blockPredicate += mapToBlockState(blockState);
    }
    if (dataTags != null) {
      blockPredicate += mapToDataTag(dataTags);
    }
    String blockPredicate2 = blockToReplace;
    if (blockStateToReplace != null) {
      blockPredicate2 += mapToBlockState(blockStateToReplace);
    }
    if (dataTagsToReplace != null) {
      blockPredicate2 += mapToDataTag(dataTagsToReplace);
    }
    return executeCommand(
        self,
        "fill",
        "" + p1.getX(), "" + p1.getY(), "" + p1.getZ(),
        "" + p2.getX(), "" + p2.getY(), "" + p2.getZ(),
        blockPredicate, "replace", blockPredicate2
    ).orElse(null);
  }

  /*
   * /forceload command
   */

  // TEST
  @Method(name = "force_load_chunks",
      parametersMetadata = {
          @ParameterMeta(name = "from", doc = "Starting column position."),
          @ParameterMeta(name = "to", mayBeNull = true, doc = "Ending column position.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of forceloaded chunks that were newly added, #null if an error occured."),
      doc = "Forces the chunks at the <from> position (through to <to> if set) to be loaded constantly. " +
          "There is no guarantee that affected chunks will be load in the same tick as this method is executed.")
  public Long forceLoadChunks(final Scope scope, WorldProxy self, final Position from, final Position to) {
    BlockPos p1 = from.toBlockPos();
    List<String> args = new ArrayList<>(Arrays.asList("add", "" + p1.getX(), "" + p1.getZ()));
    if (to != null) {
      BlockPos p2 = to.toBlockPos();
      args.add("" + p2.getX());
      args.add("" + p2.getZ());
    }
    return executeCommand(
        self,
        "forceload",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "unforce_load_chunks",
      parametersMetadata = {
          @ParameterMeta(name = "from", doc = "Starting column position."),
          @ParameterMeta(name = "to", mayBeNull = true, doc = "Ending column position.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of forceloaded chunks that were successfully removed, #null if an error occured."),
      doc = "Unforces the chunks at the <from> position (through to <to> if set) to be loaded constantly.")
  public Long unforceLoadChunks(final Scope scope, WorldProxy self, final Position from, final Position to) {
    BlockPos p1 = from.toBlockPos();
    List<String> args = new ArrayList<>(Arrays.asList("remove", "" + p1.getX(), "" + p1.getZ()));
    if (to != null) {
      BlockPos p2 = to.toBlockPos();
      args.add("" + p2.getX());
      args.add("" + p2.getZ());
    }
    return executeCommand(
        self,
        "forceload",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "unforce_load_all_chunks",
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Unforces all chunks to be loaded constantly.")
  public Boolean unforceLoadAllChunks(final Scope scope, WorldProxy self) {
    return executeCommand(
        self,
        "forceload",
        "remove", "all"
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "get_force_loaded_chunks",
      returnTypeMetadata = @ReturnMeta(doc = "The `list of force loaded chunks’ positions."),
      doc = "Queries all force loaded chunks.")
  public MCList getForceLoadedChunks(final Scope scope, WorldProxy self) {
    ServerLevel serverlevel = scope.getProgram().getProgramManager().getWorld().getLevel();
    LongSet longset = serverlevel.getForcedChunks();
    return new MCList(longset.stream().sorted().map(l -> {
      ChunkPos p = new ChunkPos(l);
      return new Position(p.x, 0, p.z);
    }).collect(Collectors.toList()));
  }

  // TEST
  @Method(name = "is_chunk_force_loaded",
      parametersMetadata = {
          @ParameterMeta(name = "pos", doc = "Position of the chunk to test.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the chunk is force loaded, #false otherwise."),
      doc = "Checks whether the chunk at the given position is force loaded.")
  public Boolean isChunkForceLoaded(final Scope scope, WorldProxy self, final Position position) {
    BlockPos p = position.toBlockPos();
    ChunkPos chunkpos = new ChunkPos(SectionPos.blockToSectionCoord(p.getX()), SectionPos.blockToSectionCoord(p.getZ()));
    ServerLevel serverlevel = scope.getProgram().getProgramManager().getWorld().getLevel();
    return serverlevel.getForcedChunks().contains(chunkpos.toLong());
  }

  /*
   * /function command
   */

  @Method(name = "run_mcfunction",
      parametersMetadata = {
          @ParameterMeta(name = "function_id", doc = "Function’s ID or function tag.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of executed commands plus the number of embedded functions, #null if an error occured."),
      doc = "Runs the given mcfunction.")
  public Long runMCFunction(final Scope scope, WorldProxy self, final String functionID) {
    return executeCommand(
        self,
        "function",
        functionID
    ).orElse(null);
  }

  /*
   * /gamemode command
   */

  @Method(name = "set_gamemode",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players to change gamemode of."),
          @ParameterMeta(name = "gamemode", doc = "The new gamemode.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "Returns the number of affected players or #null if the action failed."),
      doc = "Sets the game mode of the selected players.")
  public Long setGameMode(final Scope scope, WorldProxy self, final String targetSelector, final String gameMode) {
    return executeCommand(
        self,
        "gamemode",
        gameMode, targetSelector
    ).orElse(null);
  }

  /*
   * /give command
   */

  @Method(name = "give_items",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players to give items to."),
          @ParameterMeta(name = "item", doc = "ID of the item to give."),
          @ParameterMeta(name = "data_tags", mayBeNull = true, doc = "Optional data tags for the item to give.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "Returns the number of affected players or #null if the action failed."),
      doc = "Gives items to the selected players.")
  public Long giveItems(final Scope scope, WorldProxy self, final String targetSelector, final String item, final MCMap dataTags) {
    return executeCommand(
        self,
        "give",
        targetSelector, item + (dataTags != null ? mapToDataTag(dataTags) : "")
    ).orElse(null);
  }

  /*
   * Get block info
   */

  @Method(name = "get_block",
      parametersMetadata = {
          @ParameterMeta(name = "pos", doc = "Position of the block.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The `block at the given position."),
      doc = "Returns the block at the given position.")
  public Block getBlock(final Scope scope, final WorldProxy self, final Position position) {
    return self.getWorld().getBlockState(position.toBlockPos()).getBlock();
  }

  @Method(name = "get_block_state",
      parametersMetadata = {
          @ParameterMeta(name = "pos", doc = "Position of the block.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "A `map containing all properties of the block."),
      doc = "Returns the block state at the given position in this world.")
  public MCMap getBlockState(final Scope scope, final WorldProxy self, final Position position) {
    BlockState blockState = self.getWorld().getBlockState(position.toBlockPos());
    return new MCMap(blockState.getProperties().stream().collect(Collectors.toMap(
        net.minecraft.world.level.block.state.properties.Property::getName,
        p -> {
          Comparable<?> value = blockState.getValue(p);
          if (value instanceof Enum || value instanceof Character) {
            return value.toString();
          } else if (value instanceof Byte || value instanceof Short || value instanceof Integer) {
            return ((Number) value).longValue();
          } else if (value instanceof Float f) {
            return f.doubleValue();
          }
          return value;
        }
    )));
  }

  // TODO get_block_data_tags$

  @Method(name = "is_block_loaded",
      parametersMetadata = {
          @ParameterMeta(name = "pos", doc = "Position of the block to check.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the block is loaded, #false otherwise."),
      doc = "Returns whether the block at the given position is currently loaded.")
  public Boolean isBlockLoaded(final Scope scope, final WorldProxy self, final Position position) {
    return self.getWorld().isLoaded(position.toBlockPos());
  }

  /*
   * /item command
   */

  // TEST
  @Method(name = "modify_item_for_block",
      parametersMetadata = {
          @ParameterMeta(name = "pos", doc = "Position of the block to modify."),
          @ParameterMeta(name = "slot", doc = "Slot ID of the block’s inventory."),
          @ParameterMeta(name = "modifier_id", doc = "ID of the modifier to apply to the item.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Modifies an item in a block’s inventory.")
  public Boolean modifyItemForBlock(final Scope scope, WorldProxy self, final Position position,
                                    final String slotID, final String modifierID) {
    BlockPos p = position.toBlockPos();
    return executeCommand(
        self,
        "item",
        "modify", "block",
        "" + p.getX(), "" + p.getY(), "" + p.getZ(),
        slotID, modifierID
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "modify_item_for_entities",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector."),
          @ParameterMeta(name = "slot", doc = "Slot ID of the block’s inventory."),
          @ParameterMeta(name = "modifier_id", doc = "ID of the modifier to apply to the item.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of affected entities, #null if an error occured."),
      doc = "Modifies an item in inventories of selected entities.")
  public Long modifyItemForEntities(final Scope scope, WorldProxy self, final String targetSelector,
                                    final String slotID, final String modifierID) {
    return executeCommand(
        self,
        "item",
        "modify", "entity",
        targetSelector, slotID, modifierID
    ).orElse(null);
  }

  // TEST
  @Method(name = "replace_item_for_block",
      parametersMetadata = {
          @ParameterMeta(name = "pos", doc = "Position of the block to modify."),
          @ParameterMeta(name = "slot", doc = "Slot ID of the block’s inventory."),
          @ParameterMeta(name = "item", doc = "ID of the item to be placed in the slot."),
          @ParameterMeta(name = "data_tags", mayBeNull = true, doc = "Optional data tags for the item to be placed."),
          @ParameterMeta(name = "count", doc = "Size of the item stack.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Replaces an item in a block’s inventory.")
  public Boolean replaceItemForBlock(final Scope scope, WorldProxy self, final Position position,
                                     final String slotID, final String item, final MCMap dataTags, final Long count) {
    BlockPos p = position.toBlockPos();
    return executeCommand(
        self,
        "item",
        "replace", "block",
        "" + p.getX(), "" + p.getY(), "" + p.getZ(),
        slotID,
        "with",
        item + (dataTags != null ? mapToDataTag(dataTags) : ""), count.toString()
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "replace_item_for_entities",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector."),
          @ParameterMeta(name = "slot", doc = "Slot ID of the block’s inventory."),
          @ParameterMeta(name = "item", doc = "ID of the item to be placed in the slot."),
          @ParameterMeta(name = "data_tags", mayBeNull = true, doc = "Optional data tags for the item to be placed."),
          @ParameterMeta(name = "count", doc = "Size of the item stack.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of affected entities, #null if an error occured."),
      doc = "Replaces an item in inventories of selected entities.")
  public Long replaceItemForEntities(final Scope scope, WorldProxy self, final String targetSelector,
                                     final String slotID, final String item, final MCMap dataTags, final Long count) {
    return executeCommand(
        self,
        "item",
        "replace", "entity",
        targetSelector, slotID,
        "with",
        item + (dataTags != null ? mapToDataTag(dataTags) : ""), count.toString()
    ).orElse(null);
  }

  // TEST
  @Method(name = "copy_item_from_block_to_block",
      parametersMetadata = {
          @ParameterMeta(name = "source_pos", doc = "Position of the source block."),
          @ParameterMeta(name = "source_slot", doc = "ID of the source slot."),
          @ParameterMeta(name = "dest_pos", doc = "Position of the target block."),
          @ParameterMeta(name = "dest_slot", doc = "ID of the target slot."),
          @ParameterMeta(name = "modifier_id", mayBeNull = true, doc = "Optional modifier to apply to the copied item."),
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Copies an item from a block’s inventory into another’s.")
  public Boolean copyItemFromBlockToBlock(final Scope scope, WorldProxy self, final Position sourcePos, final String sourceSlotID,
                                          final Position targetPos, final String targetSlotID, final String modifierID) {
    BlockPos p1 = targetPos.toBlockPos();
    BlockPos p2 = sourcePos.toBlockPos();
    List<String> args = new ArrayList<>(Arrays.asList(
        "replace", "block",
        "" + p1.getX(), "" + p1.getY(), "" + p1.getZ(), targetSlotID,
        "from", "block",
        "" + p2.getX(), "" + p2.getY(), "" + p2.getZ(), sourceSlotID
    ));
    if (modifierID != null) {
      args.add(modifierID);
    }
    return executeCommand(
        self,
        "item",
        args.toArray(String[]::new)
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "copy_item_from_entities_to_block",
      parametersMetadata = {
          @ParameterMeta(name = "source", doc = "Entity selector for source entities."),
          @ParameterMeta(name = "source_slot", doc = "ID of the source slot."),
          @ParameterMeta(name = "dest_pos", doc = "Position of the target block."),
          @ParameterMeta(name = "dest_slot", doc = "ID of the target slot."),
          @ParameterMeta(name = "modifier_id", mayBeNull = true, doc = "Optional modifier to apply to the copied item."),
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Copies an item the inventories of selected entities into a block’s inventory.")
  public Boolean copyItemFromEntitiesToBlock(final Scope scope, WorldProxy self, final String sourceSelector, final String sourceSlotID,
                                             final Position targetPos, final String targetSlotID, final String modifierID) {
    BlockPos p = targetPos.toBlockPos();
    List<String> args = new ArrayList<>(Arrays.asList(
        "replace", "block",
        "" + p.getX(), "" + p.getY(), "" + p.getZ(), targetSlotID,
        "from", "entity",
        sourceSelector, sourceSlotID
    ));
    if (modifierID != null) {
      args.add(modifierID);
    }
    return executeCommand(
        self,
        "item",
        args.toArray(String[]::new)
    ).orElse(-1L) > 0;
  }

  // TEST
  @Method(name = "copy_item_from_block_to_entities",
      parametersMetadata = {
          @ParameterMeta(name = "source_pos", doc = "Position of the source block."),
          @ParameterMeta(name = "source_slot", doc = "ID of the source slot."),
          @ParameterMeta(name = "dest", doc = "Entity selector for destination entities."),
          @ParameterMeta(name = "dest_slot", doc = "ID of the target slot."),
          @ParameterMeta(name = "modifier_id", mayBeNull = true, doc = "Optional modifier to apply to the copied item."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of entities whose items were successfully replaced or #null if an error occured."),
      doc = "Copies an item from a block’s inventory into the inventories of selected entities.")
  public Long copyItemFromBlockToEntities(final Scope scope, WorldProxy self, final Position sourcePos, final String sourceSlotID,
                                          final String targetSelector, final String targetSlotID, final String modifierID) {
    BlockPos p = sourcePos.toBlockPos();
    List<String> args = new ArrayList<>(Arrays.asList(
        "replace", "entity",
        targetSelector, targetSlotID,
        "from", "block",
        "" + p.getX(), "" + p.getY(), "" + p.getZ(), sourceSlotID
    ));
    if (modifierID != null) {
      args.add(modifierID);
    }
    return executeCommand(
        self,
        "item",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  // TEST
  @Method(name = "copy_item_from_entities_to_entities",
      parametersMetadata = {
          @ParameterMeta(name = "source", doc = "Entity selector for source entities."),
          @ParameterMeta(name = "source_slot", doc = "ID of the source slot."),
          @ParameterMeta(name = "dest", doc = "Entity selector for destination entities."),
          @ParameterMeta(name = "dest_slot", doc = "ID of the target slot."),
          @ParameterMeta(name = "modifier_id", mayBeNull = true, doc = "Optional modifier to apply to the copied item."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of entities whose items were successfully replaced or #null if an error occured."),
      doc = "Copies an item from inventories of selected entities into other’s.")
  public Long copyItemFromEntitiesToEntities(final Scope scope, WorldProxy self, final String sourceSelector, final String sourceSlotID,
                                             final String targetSelector, final String targetSlotID, final String modifierID) {
    List<String> args = new ArrayList<>(Arrays.asList(
        "replace", "entity",
        targetSelector, targetSlotID,
        "from", "entity",
        sourceSelector, sourceSlotID
    ));
    if (modifierID != null) {
      args.add(modifierID);
    }
    return executeCommand(
        self,
        "item",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  /*
   * /kill command
   */

  @Method(name = "kill_entities",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets entities to kill.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of affected entities or #null if an error occured."),
      doc = "Kills all selected entities.")
  public Long killEntities(final Scope scope, final WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "kill",
        targetSelector
    ).orElse(null);
  }

  /*
   * /list command
   */

  @Method(name = "list_players",
      returnTypeMetadata = @ReturnMeta(doc = "A `list containing data for all connected players."),
      doc = "Fetches profile data for all connected players.")
  public MCMap listPlayers(final Scope scope, final WorldProxy self) {
    return new MCMap(self.getWorld().getServer().getPlayerList().getPlayers()
        .stream()
        .collect(Collectors.toMap(
            p -> p.getGameProfile().getId().toString(),
            p -> {
              MCMap properties = new MCMap();
              GameProfile gameProfile = p.getGameProfile();
              properties.put("name", gameProfile.getName());
              for (Map.Entry<String, com.mojang.authlib.properties.Property> entry : gameProfile.getProperties().entries()) {
                properties.put(entry.getKey(), entry.getValue().getValue());
              }
              return properties;
            }
        )));
  }

  /*
   * /locate command
   */

  @Method(name = "locate_structure",
      parametersMetadata = {
          @ParameterMeta(name = "structure_id", doc = "ID of the structure to find."),
          @ParameterMeta(name = "around", doc = "Position to look around of."),
          @ParameterMeta(name = "radius", doc = "Search radius around the position."),
          @ParameterMeta(name = "include_unexplored", doc = "Whether to include unexplored structures in the search.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The position of the nearest structure of desired type or #null if an error occured."),
      doc = "Returns the coordinates of the closest structure around the given point.")
  public Position locateStructure(final Scope scope, final WorldProxy self, final String structureID,
                                  final Position around, final Long radius, final Boolean findUnexplored) {
    StructureFeature<?> structureFeature = ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation(structureID));
    if (structureFeature == null) {
      return null;
    }
    BlockPos pos = self.getWorld().findNearestMapFeature(structureFeature, around.toBlockPos(), radius.intValue(), findUnexplored);
    if (pos == null) {
      return null;
    }
    return new Position(pos);
  }

  /*
   * /locatebiome command
   */

  // TEST
  @Method(name = "locate_biome",
      parametersMetadata = {
          @ParameterMeta(name = "biome_id", doc = "ID of the biome to find."),
          @ParameterMeta(name = "around", doc = "Position to look around of."),
          @ParameterMeta(name = "radius", doc = "Search radius around the position.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "A position in the nearest biome of desired type or #null if an error occured."),
      doc = "Returns the coordinates of the closest structure around the given point.")
  public Position locateBiome(final Scope scope, final WorldProxy self, final String biomeID,
                              final Position around, final Long radius) {
    Biome biome = self.getWorld().getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY)
        .getOptional(new ResourceLocation(biomeID)).orElse(null);
    if (biome == null) {
      return null;
    }
    BlockPos pos = self.getWorld().findNearestBiome(biome, around.toBlockPos(), radius.intValue(), 8); // TODO understand what the last arg means
    if (pos == null) {
      return null;
    }
    return new Position(pos);
  }

  /*
   * /loot command
   */

  // TODO /loot command

  /*
   * /msg /tell /w /tellraw commands
   */

  @Method(name = "send_message",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets entities to send the message to."),
          @ParameterMeta(name = "message", doc = "The message. May be either a `string or a `map representing a valid JSON object.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targetted players or #null if an error occured."),
      doc = "Sends a private message to the selected players. " +
          "The message can be either a `string or a `map representing a JSON object.")
  public Long sendMessage(final Scope scope, WorldProxy self, final String targetSelector, final Object message) {
    String command;
    String msg;
    if (message instanceof String s) {
      command = "msg";
      msg = s;
    } else {
      command = "tellraw";
      msg = mapToJSON(ProgramManager.getTypeInstance(MapType.class).implicitCast(scope, message));
    }
    return executeCommand(
        self,
        command,
        targetSelector, msg
    ).orElse(null);
  }

  /*
   * /particle command
   */

  @Method(name = "spawn_particles",
      parametersMetadata = {
          @ParameterMeta(name = "name", doc = "Name of the particle."),
          @ParameterMeta(name = "pos", doc = "Position where to spawn the particle."),
          @ParameterMeta(name = "delta_x", doc = "Length along the x axis of the box within which particles will spawn."),
          @ParameterMeta(name = "delta_y", doc = "Length along the y axis of the box within which particles will spawn."),
          @ParameterMeta(name = "delta_z", doc = "Length along the z axis of the box within which particles will spawn."),
          @ParameterMeta(name = "speed", doc = "Speed of the particles. May be ignored by some particles."),
          @ParameterMeta(name = "count", doc = """
              Number of particles to spawn each tick.
              §lException 1§r: When $name is "entity_effect" or "ambient_entity_effect", a $count of 0 causes the\
               $delta and $speed parameters to act like RGBE values instead, ranging from 0.0 to 1.0. The three\
               $delta parameters give red, green, and blue components for the color; the $speed provides an\
               exponent that makes the colors brighter or dimmer, with 128 being the default.
              §lException 2§r: When $name is "note", a $count of 0 causes the value of $delta_x to specify the\
               note’s color, ranging from 0.0 to 1.0. The values of 0.0 and 1.0 produce green particles, and the\
               values in between produce colors according to the note blocks table.
              §lException 3§r: For all other particles, when $count is set to 0, the $delta parameters instead act\
               as motion values for the particle, with $speed acting as a multiplier. Particles that don’t have\
               any motion to begin with are not affected by this (e.g.: "barrier").
              """),
          @ParameterMeta(name = "force", doc = "If #true the particles will be visible from up to 512 blocks away, " +
              "otherwise they will only be visible from up to 32 blocks away."),
          @ParameterMeta(name = "viewers", doc = "An entity selector that targerts all players that should see the particles."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of players who can see the particles or #null if an error occured."),
      doc = "Spawns particles at the given position.")
  public Long spawnParticles(final Scope scope, WorldProxy self, final String name, final Position position,
                             final Double deltaX, final Double deltaY, final Double deltaZ,
                             final Double speed, final Long count,
                             final Boolean force, final String targetSelector) {
    return executeCommand(
        self,
        "particle",
        name, "" + position.getX(), "" + position.getY(), "" + position.getZ(),
        deltaX.toString(), deltaY.toString(), deltaZ.toString(),
        speed.toString(), count.toString(),
        force ? "force" : "normal", targetSelector
    ).orElse(null);
  }

  /*
   * /placefeature command
   */

  // TEST
  @Method(name = "place_feature",
      parametersMetadata = {
          @ParameterMeta(name = "feature_id", doc = "ID of the feature to place."),
          @ParameterMeta(name = "pos", doc = "The position where to place the feature at.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Places a feature at the specified position.")
  public Boolean placeFeature(final Scope scope, WorldProxy self, final String featureID, final Position position) {
    BlockPos p = position.toBlockPos();
    return executeCommand(
        self,
        "placefeature",
        featureID, "" + p.getX(), "" + p.getY(), "" + p.getZ()
    ).orElse(-1L) > 0;
  }

  /*
   * /playsound command
   */

  @Method(name = "play_sound",
      parametersMetadata = {
          @ParameterMeta(name = "sound_id", doc = "ID of the sound to play."),
          @ParameterMeta(name = "category", doc = "The channel in which the sound should play. " +
              "One of \"master\", \"music\", \"record\", \"weather\", \"block\", \"hostile\", \"neutral\", \"player\", \"ambient\", or \"voice\""),
          @ParameterMeta(name = "targets", doc = "An entity selector that targets the players that should hear the sound."),
          @ParameterMeta(name = "pos", doc = "The position at which to play the sound.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of players who can hear the sound or #null if an error occured."),
      doc = "Plays the specified sound.")
  public Long playSound(final Scope scope, WorldProxy self, final String sound, final String category,
                        final String targetSelector, final Position position) {
    List<String> args = new ArrayList<>(Arrays.asList(
        sound, category, targetSelector,
        "" + position.getX(), "" + position.getY(), "" + position.getZ()
    ));
    return executeCommand(
        self,
        "playsound",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  @Method(name = "play_sound_with_volume",
      parametersMetadata = {
          @ParameterMeta(name = "sound_id", doc = "ID of the sound to play."),
          @ParameterMeta(name = "category", doc = "The channel in which the sound should play. " +
              "One of \"master\", \"music\", \"record\", \"weather\", \"block\", \"hostile\", \"neutral\", \"player\", \"ambient\", or \"voice\""),
          @ParameterMeta(name = "targets", doc = "An entity selector that targets the players that should hear the sound."),
          @ParameterMeta(name = "pos", doc = "The position at which to play the sound."),
          @ParameterMeta(name = "volume", doc = "Sound’s volume."),
          @ParameterMeta(name = "pitch", mayBeNull = true, doc = "Sound’s pitch. If #null, the last argument will be ignored."),
          @ParameterMeta(name = "min_volume", mayBeNull = true, doc = "Minimum volume. May be #null."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of players who can hear the sound or #null if an error occured."),
      doc = "Plays the specified sound.")
  public Long playSoundWithVolume(final Scope scope, WorldProxy self, final String sound, final String category,
                                  final String targetSelector, final Position position,
                                  final Double volume, final Double pitch, final Double minVolume) {
    List<String> args = new ArrayList<>(Arrays.asList(
        sound, category, targetSelector,
        "" + position.getX(), "" + position.getY(), "" + position.getZ()
    ));
    args.add(volume.toString());
    if (pitch != null) {
      args.add(pitch.toString());
      if (minVolume != null) {
        args.add(minVolume.toString());
      }
    }
    return executeCommand(
        self,
        "playsound",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  /*
   * /recipe command
   */

  @Method(name = "unlock_all_recipes",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The total number of recipes given to each player or #null if an error occured."),
      doc = "Unlocks all recipes for the selected players.")
  public Long unlockAllRecipes(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "recipe",
        "give", targetSelector, "*"
    ).orElse(null);
  }

  @Method(name = "unlock_recipe",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "recipe_id", doc = "ID of the recipe to unlock.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The total number of recipes given to each player or #null if an error occured."),
      doc = "Unlocks the given recipe for the selected players.")
  public Long unlockRecipe(final Scope scope, WorldProxy self, final String targetSelector, final String recipe) {
    return executeCommand(
        self,
        "recipe",
        "give", targetSelector, recipe
    ).orElse(-1L);
  }

  @Method(name = "lock_all_recipes",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The total number of recipes taken from each player or #null if an error occured."),
      doc = "Locks all recipes for the selected players.")
  public Long lockAllRecipes(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "recipe",
        "take", targetSelector, "*"
    ).orElse(null);
  }

  @Method(name = "lock_recipe",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "recipe_id", doc = "ID of the recipe to lock.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The total number of recipes taken from each player or #null if an error occured."),
      doc = "Locks the given recipe for the selected players.")
  public Long lockRecipe(final Scope scope, WorldProxy self, final String targetSelector, final String recipe) {
    return executeCommand(
        self,
        "recipe",
        "take", targetSelector, recipe
    ).orElse(null);
  }

  /*
   * /reload command
   */

  @Method(name = "reload_datapacks", doc = "Reloads all datapacks.")
  public Void reloadDataPacks(final Scope scope, WorldProxy self) {
    executeCommand(self, "reload");
    return null;
  }

  /*
   * /schedule command
   */

  // TEST
  @Method(name = "reschedule_function",
      parametersMetadata = {
          @ParameterMeta(name = "function_id", doc = "ID or tag of the function to reschedule."),
          @ParameterMeta(name = "time", doc = "The in-game time delay before the function is executed."),
          @ParameterMeta(name = "time_unit", doc = "The time unit. One of \"d\" for days, \"s\" for seconds or \"t\" for ticks.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The game tick the function is to execute at modulo 2,147,483,647 or #null if an error occured."),
      doc = "Reschedules a function. Replaces any previous schedule for the same function.")
  public Long rescheduleFunction(final Scope scope, WorldProxy self, final String functionID, final Long time, final String unit) {
    return executeCommand(
        self,
        "schedule",
        "function", functionID, time.toString() + unit, "replace"
    ).orElse(null);
  }

  // TEST
  @Method(name = "schedule_function",
      parametersMetadata = {
          @ParameterMeta(name = "function_id", doc = "ID or tag of the function to schedule."),
          @ParameterMeta(name = "time", doc = "The in-game time delay before the function is executed."),
          @ParameterMeta(name = "time_unit", doc = "The time unit. One of \"d\" for days, \"s\" for seconds or \"t\" for ticks.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The game tick the function is to execute at modulo 2,147,483,647 or #null if an error occured."),
      doc = "Creates a new schedule for a function.")
  public Long scheduleFunction(final Scope scope, WorldProxy self, final String functionID, final Long time, final String unit) {
    return executeCommand(
        self,
        "schedule",
        "function", functionID, time.toString() + unit, "append"
    ).orElse(null);
  }

  // TEST
  @Method(name = "unschedule_function",
      parametersMetadata = {
          @ParameterMeta(name = "function_id", doc = "ID or tag of the function to unschedule.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of schedules that were cleared or #null if an error occured."),
      doc = "Removes all schedules for a function.")
  public Long unscheduleFunction(final Scope scope, WorldProxy self, final String functionID) {
    return executeCommand(
        self,
        "schedule",
        "clear", functionID
    ).orElse(null);
  }

  /*
   * /scoreboard command
   */

  // /scoreboard objectives

  @Method(name = "sb_get_objectives",
      returnTypeMetadata = @ReturnMeta(doc = "A `list of `map objects that each contain data of a single objective."),
      doc = "Returns the list of defined scoreboard objectives.")
  public MCList getScoreboardObjectives(final Scope scope, WorldProxy self) {
    return new MCList(self.getWorld().getScoreboard().getObjectives().stream().map(obj -> {
      MCMap map = new MCMap();
      map.put("name", obj.getName());
      map.put("display_name", obj.getDisplayName());
      map.put("render_type", obj.getRenderType().getId());
      map.put("criteria", obj.getCriteria().getName());
      map.put("read_only", obj.getCriteria().isReadOnly());
      return map;
    }).collect(Collectors.toList()));
  }

  @Method(name = "sb_create_objective",
      parametersMetadata = {
          @ParameterMeta(name = "name", doc = "Name of the new objective."),
          @ParameterMeta(name = "criteria", doc = "The criterion for the new objective."),
          @ParameterMeta(name = "display_name", mayBeNull = true, doc = "A `map representing a JSON object containing data to display as the name.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of objectives in scoreboard system after execution or #null if an error occured."),
      doc = "Adds an objective to the scoreboard.")
  public Long createScoreboardObjective(final Scope scope, WorldProxy self, final String name, final String criteria, final MCMap displayName) {
    List<String> args = new ArrayList<>(Arrays.asList(
        "objectives", "add", name, criteria
    ));
    if (displayName != null) {
      args.add(mapToJSON(displayName));
    }
    return executeCommand(
        self,
        "scoreboard",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  @Method(name = "sb_delete_objective",
      parametersMetadata = {
          @ParameterMeta(name = "name", doc = "Name of the objective.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of objectives in scoreboard system after execution or #null if an error occured."),
      doc = "Removes an objective from the scoreboard.")
  public Long deleteScoreboardObjective(final Scope scope, WorldProxy self, final String name) {
    return executeCommand(
        self,
        "scoreboard",
        "objectives", "remove", name
    ).orElse(null);
  }

  @Method(name = "sb_set_objective_display_slot",
      parametersMetadata = {
          @ParameterMeta(name = "slot", doc = "Slot to display the objective in."),
          @ParameterMeta(name = "name", doc = "Name of the objective.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the display slot of a scoreboard objective.")
  public Boolean setScoreboardObjectiveDisplaySlot(final Scope scope, WorldProxy self, final String slot, final String name) {
    return executeCommand(
        self,
        "scoreboard",
        "objectives", "setdisplay", slot, name
    ).orElse(-1L) > 0;
  }

  @Method(name = "sb_clear_display_slot",
      parametersMetadata = {
          @ParameterMeta(name = "slot", doc = "Slot to clear.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Clears a display slot of the scoreboard.")
  public Boolean clearScoreboardDisplaySlot(final Scope scope, WorldProxy self, final String slot) {
    return executeCommand(
        self,
        "scoreboard",
        "objectives", "setdisplay", slot
    ).orElse(-1L) > 0;
  }

  @Method(name = "sb_set_objective_display_name",
      parametersMetadata = {
          @ParameterMeta(name = "objective", doc = "Name of the objective."),
          @ParameterMeta(name = "display_name", mayBeNull = true, doc = "A `map representing a JSON object containing data to display as the name.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the display name of a scoreboard objective.")
  public Boolean setScoreboardObjectiveDisplayName(final Scope scope, WorldProxy self, final String objective, final String displayName) {
    return executeCommand(
        self,
        "scoreboard",
        "objectives", "modify", objective, "displayname", displayName
    ).orElse(-1L) > 0;
  }

  @Method(name = "sb_set_objective_render_type",
      parametersMetadata = {
          @ParameterMeta(name = "objective", doc = "Name of the objective."),
          @ParameterMeta(name = "render_type", doc = "Either \"hearts\" or \"integer\".")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the render type of the score of a scoreboard objective.")
  public Boolean setScoreboardObjectiveRenderType(final Scope scope, WorldProxy self, final String objective, final String renderType) {
    return executeCommand(
        self,
        "scoreboard",
        "objectives", "modify", objective, "rendertype", renderType
    ).orElse(-1L) > 0;
  }

  // /scoreboard players

  @Method(name = "sb_get_tracked_players",
      returnTypeMetadata = @ReturnMeta(doc = "A sorted `list of the names of all players tracked by the scoreboard."),
      doc = "Returns the names of all players tracked by the scoreboard.")
  public MCList getPlayersInScoreboard(final Scope scope, WorldProxy self) {
    MCList list = new MCList(self.getWorld().getScoreboard().getObjectiveNames());
    list.sort(null);
    return list;
  }

  @Method(name = "sb_get_player_scores",
      parametersMetadata = {
          @ParameterMeta(name = "player_name", doc = "The name of the player.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "A `map containing the scores of each objective."),
      doc = "Returns the scoreboard scores for the given player.")
  public MCMap getPlayerScores(final Scope scope, WorldProxy self, final String name) {
    return new MCMap(self.getWorld().getScoreboard().getPlayerScores(name).entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey().getName(), e -> e.getValue().getScore())));
  }

  @Method(name = "sb_set_players_score",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players, or \"*\" for all players tracked by the scoreboard."),
          @ParameterMeta(name = "objective", doc = "Name of the objective to update."),
          @ParameterMeta(name = "score", doc = "The new score value.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targets multiplied by the specified score or #null if an error occured."),
      doc = "Sets the score of an objective of the selected entities.")
  public Long setPlayerScore(final Scope scope, WorldProxy self, final String targetSelector,
                             final String objective, final Long score) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "set", targetSelector, objective, score.toString()
    ).orElse(null);
  }

  @Method(name = "sb_update_player_score",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector targetting players."),
          @ParameterMeta(name = "objective", doc = "Name of the objective to update."),
          @ParameterMeta(name = "value", doc = "Value to add to the current score. May be negative.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The sum of the objective’s score value of each target after execution or #null if an error occured."),
      doc = "Updates the score of an objective of the selected players.")
  public Long updatePlayerScore(final Scope scope, WorldProxy self, final String targetSelector,
                                final String objective, Long amount) {
    String action;
    if (amount < 0) {
      action = "remove";
      amount = -amount;
    } else {
      action = "add";
    }
    return executeCommand(
        self,
        "scoreboard",
        "players", action, targetSelector, objective, amount.toString()
    ).orElse(null);
  }

  @Method(name = "sb_reset_player_score",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector targetting players"),
          @ParameterMeta(name = "objective", doc = "Name of the objective to reset.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targetted players or #null if an error occured."),
      doc = "Resets the score of a single objective of the selected players.")
  public Long resetPlayerScore(final Scope scope, WorldProxy self, final String targetSelector, final String objective) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "reset", targetSelector, objective
    ).orElse(null);
  }

  @Method(name = "sb_reset_player_scores",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector targetting players")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targetted players or #null if an error occured."),
      doc = "Resets all scores of the selected players.")
  public Long resetPlayerScores(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "reset", targetSelector
    ).orElse(null);
  }

  @Method(name = "sb_enable_trigger_for_players",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "objective", doc = "Name of the objective.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of the targets whose scores are newly enabled or #null if an error occured."),
      doc = "Enables the targets to use the /trigger command on the specified objective.")
  public Boolean enableTriggerForPlayers(final Scope scope, WorldProxy self, final String targetSelector, final String objective) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "enable", targetSelector, objective
    ).orElse(-1L) > 0;
  }

  // FIXME test option has been removed
  @Method(name = "sb_is_player_score_within_range",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "objective", doc = "Name of the objective to check."),
          @ParameterMeta(name = "min", doc = "Lower bound of the range."),
          @ParameterMeta(name = "max", doc = "Upper bound of the range.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "#True if the score of all targetted players is within the range, #false otherwise, #null if an error occured."),
      doc = "Checks whether the score of the selected players is within the given range.")
  public Boolean isPlayerScoreWithinRange(final Scope scope, WorldProxy self, final String targetSelector,
                                          final String objective, final Long min, final Long max) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "test", targetSelector, objective, min.toString(), max.toString()
    ).orElse(-1L) > 0;
  }

  @Method(name = "sb_apply_score_operation",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players whose score should be updated."),
          @ParameterMeta(name = "target_objective", doc = "The objective to update."),
          @ParameterMeta(name = "operation", doc = "The operatior to apply."),
          @ParameterMeta(name = "sources", doc = "An entity selector that targets players whose score is to be used to perform the operation."),
          @ParameterMeta(name = "source_objective", doc = "The objective to get the scores from."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The sum of the objective’s score value of each target after the operation on it or #null if an error occured."),
      doc = "Applies an arithmetic operation altering the targets’ scores in the target objective, " +
          "using sources’ scores in the source objective as input.")
  public Long applyScoreOperation(final Scope scope, WorldProxy self,
                                  final String targetSelector, final String targetObjective,
                                  final String operation,
                                  final String sourceSelector, final String sourceObjective) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "operation", targetSelector, targetObjective, operation, sourceSelector, sourceObjective
    ).orElse(null);
  }

  /*
   * /setblock command
   */

  @Method(name = "set_block",
      parametersMetadata = {
          @ParameterMeta(name = "pos", doc = "Block’s position."),
          @ParameterMeta(name = "block", doc = "ID of the block to place."),
          @ParameterMeta(name = "block_state", mayBeNull = true, doc = "Optional block state."),
          @ParameterMeta(name = "data_tags", mayBeNull = true, doc = "Optional data tags."),
          @ParameterMeta(name = "mode", doc = "Placing mode. One of \"destroy\", \"keep\" or \"replace\".")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the block was placed, #false otherwise."),
      doc = "Sets the block at the given position. ")
  public Boolean setBlock(final Scope scope, WorldProxy self, final Position position,
                          final String block, final MCMap blockState, final MCMap dataTags, final String mode) {
    BlockPos p = position.toBlockPos();
    String blockPredicate = block;
    if (blockState != null) {
      blockPredicate += mapToBlockState(blockState);
    }
    if (dataTags != null) {
      blockPredicate += mapToDataTag(dataTags);
    }
    return executeCommand(
        self,
        "setblock",
        "" + p.getX(), "" + p.getY(), "" + p.getZ(), blockPredicate, mode
    ).orElse(-1L) > 0;
  }

  /*
   * /setworldspawn command
   */

  @Method(name = "set_world_spawn",
      parametersMetadata = {
          @ParameterMeta(name = "pos", doc = "New world spawn position."),
          @ParameterMeta(name = "yaw", mayBeNull = true, doc = "Optional yaw angle that players will spawn with.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the world’s spawn.")
  public Boolean setWorldSpawn(final Scope scope, WorldProxy self, final Position position, final Double yawAngle) {
    List<String> args = new ArrayList<>(Arrays.asList("" + position.getX(), "" + position.getY(), "" + position.getZ()));
    if (yawAngle != null) {
      args.add(yawAngle.toString());
    }
    return executeCommand(
        self,
        "setworldspawn",
        args.toArray(String[]::new)
    ).orElse(-1L) > 0;
  }

  /*
   * /spawnpoint command
   */

  @Method(name = "set_player_spawn",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "pos", doc = "The new spawn position for each targetted player."),
          @ParameterMeta(name = "yaw", mayBeNull = true, doc = "Optional yaw angle that players will spawn with.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of players whose spawn point was changed or #null if an error occured."),
      doc = "Sets the spawn point of the selected players.")
  public Long setPlayerSpawn(final Scope scope, WorldProxy self, final String targetSelector, final Position position, final Double yawAngle) {
    List<String> args = new ArrayList<>(Arrays.asList(targetSelector, "" + position.getX(), "" + position.getY(), "" + position.getZ()));
    if (yawAngle != null) {
      args.add(yawAngle.toString());
    }
    return executeCommand(
        self,
        "spawnpoint",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  /*
   * /spreadplayers command
   */

  @Method(name = "spread_players",
      parametersMetadata = {
          @ParameterMeta(name = "center_x", doc = "X coordinate of the spread region’s center."),
          @ParameterMeta(name = "center_z", doc = "Z coordinate of the spread region’s center."),
          @ParameterMeta(name = "spread_distance", doc = "The minimum distance between targets."),
          @ParameterMeta(name = "max_range", doc = "The maximum distance on each horizontal axis from the center of the area to spread targets."),
          @ParameterMeta(name = "max_height", doc = "The maximum height for resulting positions. Must be at least 1."),
          @ParameterMeta(name = "respect_teams", doc = "Whether to keep teams together."),
          @ParameterMeta(name = "targets", doc = "An entity selector that targets the entities to spread."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of teams that the targets are in if $respect_teams is #true, " +
              "otherwise the number of targeted entities, #null if an error occured."),
      doc = "Teleports players to a random location in the given area.")
  public Long spreadPlayers(final Scope scope, WorldProxy self, final Double centerX, final Double centerZ,
                            final Double spreadDistance, final Double maxRange, final Double maxHeight,
                            final Boolean respectTeams, final String targetSelector) {
    return executeCommand(
        self,
        "spreadplayers",
        centerX.toString(), centerZ.toString(), spreadDistance.toString(), maxRange.toString(),
        "under", maxHeight.toString(), respectTeams.toString(), targetSelector
    ).orElse(null);
  }

  /*
   * /stopsound command
   */

  @Method(name = "stop_sounds",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "category", mayBeNull = true, doc = "The sound category to stop the sound in. " +
              "One of \"master\", \"music\", \"record\", \"weather\", \"block\", \"hostile\", \"neutral\", \"player\", " +
              "\"ambient\", \"voice\" or \"*\". If #null, the next argument is ignored."),
          @ParameterMeta(name = "sound_id", mayBeNull = true, doc = "The sound to stop. May be #null.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targetted players or #null if an error occured."),
      doc = "Stops the selected sounds for specified players.")
  public Long stopSounds(final Scope scope, WorldProxy self, final String targetSelector, final String category, final String soundID) {
    List<String> args = new ArrayList<>();
    args.add(targetSelector);
    if (category != null) {
      args.add(category);
      if (soundID != null) {
        args.add(soundID);
      }
    }
    return executeCommand(
        self,
        "spreadplayers",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  /*
   * /summon command
   */

  @Method(name = "summon",
      parametersMetadata = {
          @ParameterMeta(name = "entity_type", doc = "Type of the entity to summon."),
          @ParameterMeta(name = "pos", doc = "The position where the entity should be summoned."),
          @ParameterMeta(name = "nbt", mayBeNull = true, doc = "Option `map that contains NBT data to apply to the entity.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the entity was summoned, #false otherwise."),
      doc = "Summons an entity.")
  public Boolean summonEntity(final Scope scope, WorldProxy self, final String entityType,
                              final Position position, final MCMap nbtData) {
    return executeCommand(
        self,
        "summon",
        entityType, "" + position.getX(), "" + position.getY(), "" + position.getZ(), mapToJSON(nbtData)
    ).orElse(-1L) > 0;
  }

  /*
   * /tag command
   */

  @Method(name = "sb_get_entities_tags",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "A `map that contains the tags of each targetted entities or #null if an error occurs. " +
              "Keys correspond to entities IDs."),
      doc = "Returns the tags of selected entities.")
  public MCMap getEntitiesTags(final Scope scope, WorldProxy self, final String targetSelector) {
    List<? extends Entity> entities = getSelectedEntities(self, targetSelector);
    if (entities == null) {
      return null;
    }
    MCMap tags = new MCMap();
    for (Entity entity : entities) {
      tags.put(entity.getId() + "", new MCSet(entity.getTags()));
    }
    return tags;
  }

  @Method(name = "sb_add_tag_to_entities",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector."),
          @ParameterMeta(name = "tag", doc = "Name of the tag to add to selected entities.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of entities to which the tag was added or #null if an error occured."),
      doc = "Adds a tag to selected entities.")
  public Long addTagToEntities(final Scope scope, WorldProxy self, final String targetSelector, final String tagName) {
    return executeCommand(
        self,
        "tag",
        targetSelector, "add", tagName
    ).orElse(null);
  }

  @Method(name = "sb_remove_tag_from_entities",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector."),
          @ParameterMeta(name = "tag", doc = "Name of the tag to remove from selected entities.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of entities from which the tag was removed or #null if an error occured."),
      doc = "Removes a tag from selected entities.")
  public Long removeTagFromEntities(final Scope scope, WorldProxy self, final String targetSelector, final String tagName) {
    return executeCommand(
        self,
        "tag",
        targetSelector, "remove", tagName
    ).orElse(null);
  }

  /*
   * /team command
   */

  @Method(name = "sb_get_teams",
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "A `map that contains data for each team. Keys correspond to team names. or #null if an error occured"),
      doc = "Returns data for all currently defined teams.")
  public MCMap getTeams(final Scope scope, WorldProxy self) {
    return new MCMap(self.getWorld().getScoreboard().getPlayerTeams().stream()
        .collect(Collectors.toMap(
            PlayerTeam::getName,
            t -> {
              MCMap map = new MCMap();
              map.put("name", t.getName());
              map.put("friendly_fire", t.isAllowFriendlyFire());
              map.put("see_friendly_invisible", t.canSeeFriendlyInvisibles());
              map.put("name_tag_visibility", t.getNameTagVisibility().name);
              map.put("death_message_visibility", t.getDeathMessageVisibility().name);
              map.put("color", t.getColor().getName());
              map.put("collision_rule", t.getCollisionRule().name);
              map.put("players", new MCSet(t.getPlayers()));
              return map;
            }
        )));
  }

  @Method(name = "sb_create_team",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "The name of the team."),
          @ParameterMeta(name = "display_name", mayBeNull = true,
              doc = "A `map that represents a JSON objects containing data to use as the team’s name. May be #null.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of existing teams after execution or #null if an error occured."),
      doc = "Creates a new player team.")
  public Long createTeam(final Scope scope, WorldProxy self, final String teamName, final String displayName) {
    List<String> args = new ArrayList<>(Arrays.asList("add", teamName));
    if (displayName != null) {
      args.add(displayName);
    }
    return executeCommand(
        self,
        "team",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  @Method(name = "sb_delete_team",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "The name of the team to delete.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of existing teams after execution or #null if an error occured."),
      doc = "Deletes a team of players.")
  public Long deleteTeam(final Scope scope, WorldProxy self, final String teamName) {
    return executeCommand(
        self,
        "team",
        "remove", teamName
    ).orElse(null);
  }

  @Method(name = "sb_clear_team",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "The name of the team to clear.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of members in the team before execution or #null if an error occured."),
      doc = "Removes all players from a team.")
  public Long clearTeam(final Scope scope, WorldProxy self, final String teamName) {
    return executeCommand(
        self,
        "team",
        "empty", teamName
    ).orElse(null);
  }

  @Method(name = "sb_add_entities_to_team",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "Name of the team to add entities to."),
          @ParameterMeta(name = "targets", doc = "An entity selector that targets entities to add to the team.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of entities that were added to the team or #null if an error occured."),
      doc = "Adds entities to a team.")
  public Long addPlayersToTeam(final Scope scope, WorldProxy self, final String teamName, final String targetSelector) {
    return executeCommand(
        self,
        "team",
        "join", teamName, targetSelector
    ).orElse(null);
  }

  @Method(name = "sb_remove_entities_from_team",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets entities to remove from their team.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of entities that were added to the team or #null if an error occured."),
      doc = "Removes entities from their team.")
  public Long removePlayersFromTeam(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "team",
        "leave", targetSelector
    ).orElse(null);
  }

  @Method(name = "sb_set_team_display_name",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "Name of the team."),
          @ParameterMeta(name = "display_name", doc = "A `map that represents a JSON objects containing data to use as the team’s name.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the display name of a team.")
  public Boolean setTeamDisplayName(final Scope scope, WorldProxy self, final String teamName, final MCMap displayName) {
    return executeCommand(
        self,
        "team",
        "modify", teamName, "displayName", mapToJSON(displayName)
    ).orElse(-1L) > 0;
  }

  @Method(name = "sb_set_team_color",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "Name of the team."),
          @ParameterMeta(name = "color", doc = "The team’s color. One of the 16 chat colors or \"reset\" to reset to the default color.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the color of a team.")
  public Boolean setTeamColor(final Scope scope, WorldProxy self, final String teamName, final String color) {
    return executeCommand(
        self,
        "team",
        "modify", teamName, "color", color
    ).orElse(-1L) > 0;
  }

  @Method(name = "sb_set_team_friendly_fire",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "Name of the team."),
          @ParameterMeta(name = "active", doc = "Whether to activate friendly fire.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Activates/deactivates friendly fire for the specified team.")
  public Boolean setTeamFriendlyFire(final Scope scope, WorldProxy self, final String teamName, final Boolean active) {
    return executeCommand(
        self,
        "team",
        "modify", teamName, "friendlyFire", active.toString()
    ).orElse(-1L) > 0;
  }

  @Method(name = "sb_set_team_see_friendly_invisible",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "Name of the team."),
          @ParameterMeta(name = "active", doc = "Whether members of the team should see invisible members of their team.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets whether players from the specified team should see invisible players from their team.")
  public Boolean setTeamSeeFriendlyInvisible(final Scope scope, WorldProxy self, final String teamName, final Boolean active) {
    return executeCommand(
        self,
        "team",
        "modify", teamName, "seeFriendlyInvisibles", active.toString()
    ).orElse(-1L) > 0;
  }

  @Method(name = "sb_set_team_name_tag_visibility",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "Name of the team."),
          @ParameterMeta(name = "visibility", doc = "Either \"never\", \"hideForOtherTeams\", \"hideForOwnTeam\" or \"always\".")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the visibility of the name tags of players from the specified team.")
  public Boolean setTeamNameTagVisibility(final Scope scope, WorldProxy self, final String teamName, final String nameTagVisiblity) {
    return executeCommand(
        self,
        "team",
        "modify", teamName, "nametagVisibility", nameTagVisiblity
    ).orElse(-1L) > 0;
  }

  @Method(name = "sb_set_team_death_message_visibility",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "Name of the team."),
          @ParameterMeta(name = "visibility", doc = "Either \"never\", \"hideForOtherTeams\", \"hideForOwnTeam\" or \"always\".")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the visibility of the death messages of players from the specified team.")
  public Boolean setTeamDeathMessageVisibility(final Scope scope, WorldProxy self, final String teamName, final String deathMessageVisibility) {
    return executeCommand(
        self,
        "team",
        "modify", teamName, "deathMessageVisibility", deathMessageVisibility
    ).orElse(-1L) > 0;
  }

  @Method(name = "sb_set_team_collision_rule",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "Name of the team."),
          @ParameterMeta(name = "rule", doc = "Either \"never\", \"pushOtherTeams\", \"pushOwnTeam\" or \"always\".")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the collision rule of players from the specified team.")
  public Boolean setTeamCollisionRule(final Scope scope, WorldProxy self, final String teamName, final String collisionRule) {
    return executeCommand(
        self,
        "team",
        "modify", teamName, "collisionRule", collisionRule
    ).orElse(-1L) > 0;
  }

  @Method(name = "sb_set_team_prefix",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "Name of the team."),
          @ParameterMeta(name = "prefix", doc = "A `map that represents a JSON objects containing data to use as the team’s prefix.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the prefix of a team.")
  public Boolean setTeamPrefix(final Scope scope, WorldProxy self, final String teamName, final MCMap prefix) {
    return executeCommand(
        self,
        "team",
        "modify", teamName, "prefix", mapToJSON(prefix)
    ).orElse(-1L) > 0;
  }

  @Method(name = "sb_set_team_suffix",
      parametersMetadata = {
          @ParameterMeta(name = "team", doc = "Name of the team."),
          @ParameterMeta(name = "suffix", doc = "A `map that represents a JSON objects containing data to use as the team’s suffix.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the suffix of a team.")
  public Boolean setTeamSuffix(final Scope scope, WorldProxy self, final String teamName, final MCMap suffix) {
    return executeCommand(
        self,
        "team",
        "modify", teamName, "suffix", mapToJSON(suffix)
    ).orElse(-1L) > 0;
  }

  /*
   * /teleport /tp commands
   */

  @Method(name = "tp_entities_to_entity",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets entities to be teleported."),
          @ParameterMeta(name = "target", doc = "An entity selector that targets a single entity as the destination.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of teleported entities or #null if an error occured."),
      doc = "Teleports the selected entities to the position of the given entity.")
  public Long teleportEntitiesToEntity(final Scope scope, WorldProxy self, final String sourceSelector, final String targetSelector) {
    return executeCommand(
        self,
        "tp",
        sourceSelector, targetSelector
    ).orElse(null);
  }

  @Method(name = "tp_entities_to_pos",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets entities to be teleported."),
          @ParameterMeta(name = "pos", doc = "The destination position."),
          @ParameterMeta(name = "yaw", mayBeNull = true, doc = "The optional yaw angle between -180° and 180°."),
          @ParameterMeta(name = "pitch", mayBeNull = true, doc = "The optional pitch angle between -90° and 90°.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of teleported entities or #null if an error occured."),
      doc = "Teleports the selected entities to the specified position.")
  public Long teleportEntitiesToPos(final Scope scope, WorldProxy self, final String targetSelector,
                                    final Position destination, final Double yawAngle, final Double pitchAngle) {
    List<String> args = new ArrayList<>(Arrays.asList(
        targetSelector,
        "" + destination.getX(),
        "" + destination.getY(),
        "" + destination.getZ()
    ));
    if (yawAngle != null && pitchAngle != null) {
      args.add(yawAngle.toString());
      args.add(pitchAngle.toString());
    }
    return executeCommand(
        self,
        "tp",
        args.toArray(String[]::new)
    ).orElse(null);
  }

  @Method(name = "tp_entities_to_pos_facing_block",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets entities to be teleported."),
          @ParameterMeta(name = "pos", doc = "The destination position. Accepts positions relative to the targetted entities."),
          @ParameterMeta(name = "look_at", doc = "The coordinates the targetted entities should look at once teleported.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of teleported entities or #null if an error occured."),
      doc = "Teleports the selected entities to the specified position, looking at the specified location.")
  public Long teleportEntitiesToPosFacingBlock(final Scope scope, WorldProxy self, final String targetSelector,
                                               final Position destination, final Position lookAtPos) {
    return executeCommand(
        self,
        "tp",
        targetSelector,
        "" + destination.getX(), "" + destination.getY(), "" + destination.getZ(),
        "facing", "" + lookAtPos.getX(), "" + lookAtPos.getY(), "" + lookAtPos.getZ()
    ).orElse(null);
  }

  @Method(name = "tp_entities_to_pos_facing_entity",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets entities to be teleported."),
          @ParameterMeta(name = "pos", doc = "The destination position. Accepts positions relative to the targetted entities."),
          @ParameterMeta(name = "look_at", doc = "An entity selector that targets a single entity the teleported entities should look at once teleported."),
          @ParameterMeta(name = "eyes", doc = "If #true the teleported entities will look at the eyes of the selected entity, otherwise they will look at its feet.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of teleported entities or #null if an error occured."),
      doc = "Teleports the selected entities to the specified position, looking at the specified location.")
  public Long teleportEntitiesToPosFacingEntity(final Scope scope, WorldProxy self, final String targetSelector,
                                                final Position destination, final String targetEntity, final Boolean lookAtEyes) {
    return executeCommand(
        self,
        "tp",
        targetSelector,
        "" + destination.getX(), "" + destination.getY(), "" + destination.getZ(),
        "facing", "entity", targetEntity, lookAtEyes ? "eyes" : "feet"
    ).orElse(null);
  }

  /*
   * /time command
   */

  @Method(name = "set_time",
      parametersMetadata = {
          @ParameterMeta(name = "time", doc = "The new time. May be either a positive integer or one of \"day\", \"night\", \"noon\" or \"midnight\"."),
          @ParameterMeta(name = "time_unit", doc = "The time unit. One of \"d\" for days, \"s\" for seconds or \"t\" for ticks. " +
              "Ignored if the time is not an integer.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The new time of day in ticks or #null if an error occured."),
      doc = "Sets the time for all worlds.")
  public Long setTime(final Scope scope, WorldProxy self, final Object time, final String unit) {
    return executeCommand(
        self,
        "time",
        "set", time.toString() + ((time instanceof Long) ? unit : "")
    ).orElse(null);
  }

  @Method(name = "add_time",
      parametersMetadata = {
          @ParameterMeta(name = "time", doc = "The time to add. Must be positive."),
          @ParameterMeta(name = "time_unit", doc = "The time unit. One of \"d\" for days, \"s\" for seconds or \"t\" for ticks.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The new time of day in ticks or #null if an error occured."),
      doc = "Adds the given amount to the time for all worlds.")
  public Long addTime(final Scope scope, WorldProxy self, final Long amount, final String unit) {
    return executeCommand(
        self,
        "time",
        "add", amount.toString()
    ).orElse(null);
  }

  /*
   * /title command
   */

  @Method(name = "clear_title",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targetted players or #null if an error occured."),
      doc = "Clears the screen title from the screens of the selected players.")
  public Long clearTitle(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "title",
        targetSelector, "clear"
    ).orElse(null);
  }

  @Method(name = "reset_title",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targetted players or #null if an error occured."),
      doc = "Resets the subtitle text for the selected players to blank text, " +
          "and the fade-in, stay and fade-out times to their default values.")
  public Long resetTitle(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "title",
        targetSelector, "reset"
    ).orElse(-1L);
  }

  @Method(name = "display_title",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "title", doc = "A `map representing a valid JSON object that contains data to display as the title.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targetted players or #null if an error occured."),
      doc = "Displays a screen title to the selected players, or changes the current screen title to the specified text. " +
          "After fading out, resets the subtitle back to blank text, but does not reset fade-in, stay, and fade-out times.")
  public Long displayTitle(final Scope scope, WorldProxy self, final String targetSelector, final MCMap title) {
    return executeCommand(
        self,
        "title",
        targetSelector, "title", mapToJSON(title)
    ).orElse(null);
  }

  @Method(name = "set_subtitle",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "subtitle", doc = "A `map representing a valid JSON object that contains data to display as the subtitle.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targetted players or #null if an error occured."),
      doc = "If a screen title is currently being displayed to the specified players, changes the current subtitle to the specified text; " +
          "otherwise, specifies the subtitle for the next screen title to be displayed to the specified players.")
  public Long setSubtitle(final Scope scope, WorldProxy self, final String targetSelector, final MCMap subtitle) {
    return executeCommand(
        self,
        "title",
        targetSelector, "subtitle", mapToJSON(subtitle)
    ).orElse(null);
  }

  @Method(name = "display_action_bar_text",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "text", doc = "A `map representing a valid JSON object that contains data to display as the action bar text.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targetted players or #null if an error occured."),
      doc = "Displays text on the action bar to the specified player(s), or changes the current action bar title.")
  public Long displayActionBarText(final Scope scope, WorldProxy self, final String targetSelector, final MCMap text) {
    return executeCommand(
        self,
        "title",
        targetSelector, "actionbar", mapToJSON(text)
    ).orElse(null);
  }

  @Method(name = "set_title_times",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "fade_in_time", doc = "The fade in time in ticks."),
          @ParameterMeta(name = "stay_time", doc = "The stay time in ticks."),
          @ParameterMeta(name = "fade_out_time", doc = "The fade out time in ticks."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The number of targetted players or #null if an error occured."),
      doc = "Changes the fade-in, stay, and fade-out times (measured in game ticks) of all current and future screen titles for the specified players.")
  public Long setTitleTimes(final Scope scope, WorldProxy self, final String targetSelector,
                            final Long fadeIn, final Long stay, final Long fadeOut) {
    return executeCommand(
        self,
        "title",
        targetSelector, "times", fadeIn.toString(), stay.toString(), fadeOut.toString()
    ).orElse(null);
  }

  /*
   * /weather command
   */

  @Method(name = "set_weather",
      parametersMetadata = {
          @ParameterMeta(name = "weather", doc = "The weather to set. One of \"clear\", \"rain\" or \"thunder\"."),
          @ParameterMeta(name = "duration", doc = "The duration in seconds the new weather should last.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The amount time the specified weather should last in seconds or #null if an error occured."),
      doc = "Sets the weather for this world.")
  public Long setWeather(final Scope scope, WorldProxy self, final String weather, final Long duration) {
    return executeCommand(
        self,
        "weather",
        weather, duration.toString()
    ).orElse(null);
  }

  /*
   * /worldborder command
   */

  @Method(name = "wb_get_diameter",
      returnTypeMetadata = @ReturnMeta(mayBeNull = true),
      doc = "Returns the size of the world border after rounding to the nearest `int or #null if an error occured.")
  public Long getWorldBorderDiameter(final Scope scope, WorldProxy self) {
    return executeCommand(
        self,
        "worldborder",
        "get"
    ).orElse(null);
  }

  @Method(name = "wb_set_center",
      parametersMetadata = {
          @ParameterMeta(name = "center_x", doc = "The x coordinate of the new center."),
          @ParameterMeta(name = "center_z", doc = "The z coordinate of the new center.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the center coordinate of the world border.")
  public Boolean setWorldBorderCenter(final Scope scope, WorldProxy self, final Long centerX, final Long centerZ) {
    return executeCommand(
        self,
        "worldborder",
        "center", centerX.toString(), centerZ.toString()
    ).orElse(-1L) > 0;
  }

  @Method(name = "wb_set_diameter",
      parametersMetadata = {
          @ParameterMeta(name = "diameter", doc = "The new diameter."),
          @ParameterMeta(name = "time", mayBeNull = true, doc = "The optional time in seconds the size transition should last.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the diameter of the world border in the specified number of seconds.")
  public Boolean setWorldBorderDiameter(final Scope scope, WorldProxy self, final Long diameter, final Long time) {
    List<String> args = new ArrayList<>(Arrays.asList("set", diameter.toString()));
    if (time != null) {
      args.add(time.toString());
    }
    return executeCommand(
        self,
        "worldborder",
        args.toArray(String[]::new)
    ).orElse(-1L) > 0;
  }

  @Method(name = "wb_update_diameter",
      parametersMetadata = {
          @ParameterMeta(name = "amount", doc = "The amount to add to the diameter. May be negative."),
          @ParameterMeta(name = "time", mayBeNull = true, doc = "The optional time in seconds the size transition should last.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Adds the given value to the diameter of the world border in the specified number of seconds.")
  public Boolean updateWorldBorderDiameter(final Scope scope, WorldProxy self, final Long amount, final Long time) {
    List<String> args = new ArrayList<>(Arrays.asList("add", amount.toString()));
    if (time != null) {
      args.add(time.toString());
    }
    return executeCommand(
        self,
        "worldborder",
        args.toArray(String[]::new)
    ).isPresent();
  }

  @Method(name = "wb_set_damage",
      parametersMetadata = {
          @ParameterMeta(name = "damage_per_block", doc = "The damage a player should take per second per block past the world border buffer.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the world border damage amount to the specified value.")
  public Boolean setWorldBorderDamage(final Scope scope, WorldProxy self, final Double damagePerBlock) {
    return executeCommand(
        self,
        "worldborder",
        "damage", "amount", damagePerBlock.toString()
    ).isPresent();
  }

  @Method(name = "wb_set_damage_buffer",
      parametersMetadata = {
          @ParameterMeta(name = "distance", doc = "The distance outside the world buffer a player must be before they start taking damage.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the world border buffer distance to the specified value.")
  public Boolean setWorldBorderDamageBuffer(final Scope scope, WorldProxy self, final Long bufferDistance) {
    return executeCommand(
        self,
        "worldborder",
        "damage", "buffer", bufferDistance.toString()
    ).isPresent();
  }

  @Method(name = "wb_set_warn_distance",
      parametersMetadata = {
          @ParameterMeta(name = "distance",
              doc = "The distance from the world border at which players begins to see a visual warning of the world border’s proximity.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the world border warning distance to the specified value.")
  public Boolean setWorldBorderWarnDistance(final Scope scope, WorldProxy self, final Long distance) {
    return executeCommand(
        self,
        "worldborder",
        "warning", "distance", distance.toString()
    ).isPresent();
  }

  @Method(name = "wb_set_warn_time",
      parametersMetadata = {
          @ParameterMeta(name = "time",
              doc = "The number of seconds that a player begins to see a visual warning before a moving world border passes their position.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "#True if the action succeeded, #false otherwise."),
      doc = "Sets the world border warning time to the specified value.")
  public Boolean setWorldBorderWarnTime(final Scope scope, WorldProxy self, final Long time) {
    return executeCommand(
        self,
        "worldborder",
        "warning", "time", time.toString()
    ).isPresent();
  }

  /*
   * Other methods.
   */

  @Method(name = "execute_command",
      parametersMetadata = {
          @ParameterMeta(name = "command", doc = "Name of the command to execute. The '/' character is optional."),
          @ParameterMeta(name = "args", doc = "A `list that contains the arguments of the command.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The result of the command or #null if an error occured."),
      doc = "Executes an arbitrary command. See the Minecraft wiki for more information.")
  public Long executeCommand(final Scope scope, WorldProxy self, final String command, final MCList args) {
    return executeCommand(
        self,
        command.charAt(0) == '/' ? command.substring(1) : command,
        args.stream().map(o -> ProgramManager.getTypeForValue(o).toString(o)).toArray(String[]::new)
    ).orElse(null);
  }

  @Method(name = "entities_match",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true),
      doc = "Returns the number of entities that matched the selector or #null if an error occured.")
  public Long entitiesMatch(final Scope scope, WorldProxy self, final String targetSelector) {
    return Optional.ofNullable(this.getEntitiesData(scope, self, targetSelector))
        .map(list -> (long) list.size()).orElse(null);
  }

  // TEST
  @Method(name = "get_entities_data",
      parametersMetadata = {
          @ParameterMeta(name = "targets", doc = "An entity selector.")
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "A `list of `map objects that each contain the data of an entity that matched the selector or #null if an error occured."),
      doc = "Fetches the data of all entities that match the given selector.")
  public MCList getEntitiesData(final Scope scope, final WorldProxy self, final String targetSelector) {
    List<? extends Entity> entities = getSelectedEntities(self, targetSelector);
    if (entities == null) {
      return null;
    }
    return new MCList(entities.stream().map(e -> nbtTagToMap(e.saveWithoutId(new CompoundTag()))).collect(Collectors.toList()));
  }

  /*
   * Inherited methods.
   */

  @Override
  protected Object __add__(final Scope scope, final WorldProxy self, final Object o, final boolean inPlace) {
    if (o instanceof String s) {
      return this.__str__(self) + s;
    }
    return super.__add__(scope, self, o, inPlace);
  }

  @Override
  protected String __str__(final WorldProxy self) {
    return "<this world>";
  }

  @Override
  public WorldProxy readFromNBT(final Scope scope, final CompoundTag tag) {
    return (WorldProxy) scope.getVariable(Program.WORLD_VAR_NAME, false);
  }

  /*
   * Utility methods
   */

  private static Optional<Long> executeCommand(WorldProxy self, final String commandName, final String... args) {
    ServerLevel serverlevel = self.getWorld();
    MinecraftServer server = serverlevel.getServer();
    String command = commandName + " " + String.join(" ", args);
    CommandSourceStackWrapper commandSourceStack = new CommandSourceStackWrapper(server, serverlevel);
    long result = server.getCommands().performCommand(commandSourceStack, command);
    if (result == 0 && commandSourceStack.anyFailures) {
      return Optional.empty();
    }
    return Optional.of(result);
  }

  /**
   * Custom source stack wrapper.
   */
  private static class CommandSourceStackWrapper extends CommandSourceStack {
    /**
     * Whether any failures occured while executing the last command.
     */
    boolean anyFailures;

    /**
     * Create a wrapper for a command source and level.
     * The new instance has a permission level of 2 in order to
     * prevent op commands from being executed from within scripts.
     */
    CommandSourceStackWrapper(CommandSource source, ServerLevel level) {
      super(source, Vec3.atLowerCornerOf(level.getSharedSpawnPos()), Vec2.ZERO, level, 2,
          "Server", new TextComponent("Server"), level.getServer(), null,
          true, (context, success, result) -> {
          }, EntityAnchorArgument.Anchor.FEET);
    }

    @Override
    public void sendFailure(Component component) {
      super.sendFailure(component);
      this.anyFailures = true;
    }
  }

  /**
   * Returns a list of entities that match the given target selector or null if the selector is invalid.
   */
  private static List<? extends Entity> getSelectedEntities(final WorldProxy world, final String targetSelector) {
    try {
      EntitySelector selector = EntityArgument.entities().parse(new StringReader(targetSelector));
      return selector.findEntities(new CommandSourceStackWrapper(world.getWorld().getServer(), world.getWorld()));
    } catch (CommandSyntaxException e) {
      return null;
    }
  }

  /**
   * Convert a map to a block state string.
   */
  private static String mapToBlockState(final MCMap map) {
    return "[" + map.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(",")) + "]";
  }

  /**
   * Convert a map to a compount NBT tag string.
   */
  private static String mapToNBTString(final MCMap map) {
    return mapToDataTag(map);
  }

  /**
   * Convert a map to a data tag string.
   */
  private static String mapToDataTag(final MCMap map) {
    if (map == null) {
      return "{}";
    }
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    int i = 0;
    for (Map.Entry<String, ?> e : map.entrySet()) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(e.getKey()).append(':').append(serializedDataTagValue(e.getValue()));
      i++;
    }
    sb.append("}");
    return sb.toString();
  }

  /**
   * Returns the data tag string representation of an object.
   */
  private static String serializedDataTagValue(final Object value) {
    StringBuilder sb = new StringBuilder();

    if (value == null) {
      sb.append("null");
    } else if (value instanceof Map<?, ?> m) {
      //noinspection unchecked
      sb.append(mapToDataTag(new MCMap((Map<? extends String, ?>) m)));
    } else if (value instanceof List<?> l) {
      sb.append('[');
      for (int i = 0; i < l.size(); i++) {
        if (i > 0) {
          sb.append(',');
        }
        Object o = l.get(i);
        sb.append(serializedDataTagValue(o));
      }
      sb.append(']');
    }

    return sb.toString();
  }

  /**
   * Return the JSON representation of a map.
   */
  private static String mapToJSON(final MCMap map) {
    if (map == null) {
      return "{}";
    }
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    int i = 0;
    for (Map.Entry<String, Object> e : map.entrySet()) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(Utils.escapeString(e.getKey())).append(':').append(serializeJSON(e.getValue()));
      i++;
    }
    sb.append("}");
    return sb.toString();
  }

  /**
   * Returns the JSON representation of an object.
   */
  private static String serializeJSON(final Object o) {
    if (o instanceof MCMap m) {
      return mapToJSON(m);
    } else if (o instanceof MCList list) {
      StringBuilder sb = new StringBuilder();
      sb.append('[');
      for (int i = 0; i < list.size(); i++) {
        if (i > 0) {
          sb.append(',');
        }
        sb.append(serializeJSON(list.get(i)));
      }
      sb.append(']');
      return sb.toString();
    } else if (o instanceof String s) {
      return Utils.escapeString(s);
    } else {
      return String.valueOf(o);
    }
  }

  /**
   * Convert a {@link CompoundTag} to a map.
   */
  private static MCMap nbtTagToMap(final CompoundTag tag) {
    MCMap map = new MCMap();
    for (String key : tag.getAllKeys()) {
      Object value = deserializeNBTTag(tag.get(key));
      if (value != null) {
        map.put(key, value);
      }
    }
    return map;
  }

  /**
   * Convert a {@link Tag} to an MCCode-compatible object.
   */
  private static Object deserializeNBTTag(final Tag tag) {
    if (tag instanceof ByteTag || tag instanceof ShortTag || tag instanceof IntTag || tag instanceof LongTag) {
      return ((NumericTag) tag).getAsLong();
    } else if (tag instanceof FloatTag || tag instanceof DoubleTag) {
      return ((NumericTag) tag).getAsDouble();
    } else if (tag instanceof StringTag t) {
      return t.getAsString();
    } else if (tag instanceof ByteArrayTag t) {
      MCList list = new MCList();
      for (byte b : t.getAsByteArray()) {
        list.add((long) b);
      }
      return list;
    } else if (tag instanceof IntArrayTag t) {
      MCList list = new MCList();
      Arrays.stream(t.getAsIntArray()).mapToObj(i -> (long) i).forEach(list::add);
      return list;
    } else if (tag instanceof LongArrayTag t) {
      MCList list = new MCList();
      Arrays.stream(t.getAsLongArray()).forEach(list::add);
      return list;
    } else if (tag instanceof ListTag t) {
      MCList list = new MCList();
      t.stream().map(WorldType::deserializeNBTTag).forEach(list::add);
      return list;
    } else if (tag instanceof CompoundTag t) {
      return nbtTagToMap(t);
    }
    return null;
  }
}
