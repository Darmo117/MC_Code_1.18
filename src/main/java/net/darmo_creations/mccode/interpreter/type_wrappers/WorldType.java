package net.darmo_creations.mccode.interpreter.type_wrappers;

import net.darmo_creations.mccode.interpreter.Program;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.Utils;
import net.darmo_creations.mccode.interpreter.annotations.*;
import net.darmo_creations.mccode.interpreter.types.MCList;
import net.darmo_creations.mccode.interpreter.types.MCMap;
import net.darmo_creations.mccode.interpreter.types.Position;
import net.darmo_creations.mccode.interpreter.types.WorldProxy;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;

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
      parametersMetadata = {@ParameterMeta(name = "target", doc = "An entity selector that targets players.")},
      returnTypeMetadata = @ReturnMeta(doc = "The number of advancements granted to all targetted players or -1 if the action failed."),
      doc = "Grants all advancements to the selected players.")
  public Long grantAllAdvancements(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "advancement",
        "grant", targetSelector, "everything"
    ).orElse(-1L);
  }

  @Method(name = "grant_advancement",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The advancement to grant."),
          @ParameterMeta(name = "criterion", mayBeNull = true, doc = "An optional criterion for the advancement.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of advancements granted to all targetted players or -1 if the action failed."),
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
    ).orElse(-1L);
  }

  @Method(name = "grant_advancements_from",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The root advancement.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of advancements granted to all targetted players or -1 if the action failed."),
      doc = "Grants all advancements down from the specified one to the selected players.")
  public Long grantAdvancementsFrom(final Scope scope, WorldProxy self, final String targetSelector, final String advancement) {
    return executeCommand(
        self,
        "advancement",
        "grant", targetSelector, "from", advancement
    ).orElse(-1L);
  }

  @Method(name = "grant_advancements_through",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The root advancement.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of advancements granted to all targetted players or -1 if the action failed."),
      doc = "Grants all advancements whose tree contains the specified one to the selected players.")
  public Long grantAdvancementsThrough(final Scope scope, WorldProxy self, final String targetSelector, final String advancement) {
    return executeCommand(
        self,
        "advancement",
        "grant", targetSelector, "through", advancement
    ).orElse(-1L);
  }

  @Method(name = "grant_advancements_until",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The root advancement.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of advancements granted to all targetted players or -1 if the action failed."),
      doc = "Grants all advancements from the root to the specified to the selected players.")
  public Long grantAdvancementsUntil(final Scope scope, WorldProxy self, final String targetSelector, final String advancement) {
    return executeCommand(
        self,
        "advancement",
        "grant", targetSelector, "until", advancement
    ).orElse(-1L);
  }

  @Method(name = "revoke_all_advancements",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of advancements revoked from all targetted players or -1 if the action failed."),
      doc = "Revokes all advancements from the selected players.")
  public Long revokeAllAdvancements(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "advancement",
        "revoke", targetSelector, "everything"
    ).orElse(-1L);
  }

  @Method(name = "revoke_advancement",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The advancement to revoke.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of advancements revoked from all targetted players or -1 if the action failed."),
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
    ).orElse(-1L);
  }

  @Method(name = "revoke_advancements_from",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The root advancement.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of advancements revoked from all targetted players or -1 if the action failed."),
      doc = "Revokes all advancements down from the specified one from the selected players.")
  public Long revokeAdvancementsFrom(final Scope scope, WorldProxy self, final String targetSelector, final String advancement) {
    return executeCommand(
        self,
        "advancement",
        "revoke", targetSelector, "from", advancement
    ).orElse(-1L);
  }

  @Method(name = "revoke_advancements_through",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The root advancement.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of advancements revoked from all targetted players or -1 if the action failed."),
      doc = "Revokes all advancements whose tree contains the specified one from the selected players.")
  public Long revokeAdvancementsThrough(final Scope scope, WorldProxy self, final String targetSelector, final String advancement) {
    return executeCommand(
        self,
        "advancement",
        "revoke", targetSelector, "through", advancement
    ).orElse(-1L);
  }

  @Method(name = "revoke_advancements_until",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The root advancement.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of advancements revoked from all targetted players or -1 if the action failed."),
      doc = "Revokes all advancements from the root to the specified from the selected players.")
  public Long revokeAdvancementsUntil(final Scope scope, WorldProxy self, final String targetSelector, final String advancement) {
    return executeCommand(
        self,
        "advancement",
        "revoke", targetSelector, "until", advancement
    ).orElse(-1L);
  }

  // FIXME test option has been removed
  @Method(name = "has_advancement",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "advancement", doc = "The advancement to check."),
          @ParameterMeta(name = "criterion", mayBeNull = true, doc = "An optional criterion for the advancement.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "True if all the targetted players have the specified advancement, false otherwise."),
      doc = "Returns whether the selected players have the given advancement.")
  public Boolean hasAdvancement(final Scope scope, WorldProxy self, final String targetSelector, final String advancement, final String criterion) {
    List<String> args = new ArrayList<>(Arrays.asList("test", targetSelector, advancement));
    if (criterion != null) {
      args.add(criterion);
    }
    return executeCommand(
        self,
        "advancement",
        args.toArray(new String[0])
    ).orElse(-1L) > 0;
  }

  /*
   * /attribute command
   */

  // TEST
  @Method(name = "get_attribute_value",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets a single player, mob or armor stand."),
          @ParameterMeta(name = "attribute", doc = "The attribute’s name."),
          @ParameterMeta(name = "scale", doc = "A value to multiply the attribute’s value by."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The attribute’s value for the entity, scaled by the given value then cast to an int. Null if the action failed."),
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
          @ParameterMeta(name = "target", doc = "An entity selector that targets a single player, mob or armor stand."),
          @ParameterMeta(name = "attribute", doc = "The attribute’s name."),
          @ParameterMeta(name = "scale", doc = "A value to multiply the attribute’s base value by."),
      },
      returnTypeMetadata = @ReturnMeta(mayBeNull = true,
          doc = "The attribute’s base value for the entity, scaled by the given value then cast to an int. Null if the action failed."),
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
          @ParameterMeta(name = "target", doc = "An entity selector that targets a single player, mob or armor stand."),
          @ParameterMeta(name = "attribute", doc = "The attribute’s name."),
          @ParameterMeta(name = "value", doc = "The new base value."),
      },
      returnTypeMetadata = @ReturnMeta(doc = "True if the action succeeded, false otherwise."),
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
          @ParameterMeta(name = "target", doc = "An entity selector that targets a single player, mob or armor stand."),
          @ParameterMeta(name = "attribute", doc = "The attribute’s name."),
          @ParameterMeta(name = "modifier_uuid", doc = "The modifier’s UUID."),
          @ParameterMeta(name = "modifier_name", doc = "The modifier’s name."),
          @ParameterMeta(name = "modifier_value", doc = "The modifier’s value."),
          @ParameterMeta(name = "operation", doc = "The modifier’s operation. One of \"add\", \"multiply\" or \"multiply_base\"."),
      },
      returnTypeMetadata = @ReturnMeta(doc = "True if the action succeeded, false otherwise."),
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
          @ParameterMeta(name = "target", doc = "An entity selector that targets a single player, mob or armor stand."),
          @ParameterMeta(name = "attribute", doc = "The attribute’s name."),
          @ParameterMeta(name = "modifier_uuid", doc = "The modifier’s UUID.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "True if the action succeeded, false otherwise."),
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
          @ParameterMeta(name = "target", doc = "An entity selector that targets a single player, mob or armor stand."),
          @ParameterMeta(name = "attribute", doc = "The attribute’s name."),
          @ParameterMeta(name = "modifier_uuid", doc = "The modifier’s UUID."),
          @ParameterMeta(name = "scale", doc = "A value to multiply the modifier’s value by."),
      },
      returnTypeMetadata = @ReturnMeta(doc = "The modifier’s value for the entity, scaled by the given value then cast to an int. Null if the action failed."),
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
   * /clear command
   */

  @Method(name = "clear_inventory",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of removed items or -1 if the action failed."),
      doc = "Removes all items from the inventory of the selected players.")
  public Long clearInventory(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "clear",
        targetSelector
    ).orElse(-1L);
  }

  @Method(name = "clear_items",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "item", doc = "The item to delete."),
          @ParameterMeta(name = "data_tags", mayBeNull = true, doc = "Optional data tags for the item."),
          @ParameterMeta(name = "max_count", doc = "The maximum number of items to delete.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "Returns true if the action succeeded, false otherwise."),
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
    ).orElse(0L) != 0;
  }

  @Method(name = "get_items_count",
      parametersMetadata = {
          @ParameterMeta(name = "target", doc = "An entity selector that targets players."),
          @ParameterMeta(name = "item", doc = "The item to query the number of occurences."),
          @ParameterMeta(name = "data_tags", mayBeNull = true, doc = "Optional data tags for the item.")
      },
      returnTypeMetadata = @ReturnMeta(doc = "The number of matching items or null if the operation failed."),
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

  // TODO update
  @Method(name = "clone")
  @Doc("Clones blocks from one region to another. " +
      "Returns the number of affected blocks or -1 if the action failed.")
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
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "clone_filtered")
  @Doc("Clones blocks from one region to another with the \"filter\" mask. " +
      "Returns the number of affected blocks or -1 if the action failed.")
  public Long clone(final Scope scope, WorldProxy self, final Position pos1, final Position pos2, final Position destination,
                    final String blockToClone, final Object metaOrStateToClone, final String cloneMode) {
    BlockPos p1 = pos1.toBlockPos();
    BlockPos p2 = pos2.toBlockPos();
    BlockPos dest = destination.toBlockPos();
    return executeCommand(
        self,
        "clone",
        "" + p1.getX(), "" + p1.getY(), "" + p1.getZ(),
        "" + p2.getX(), "" + p2.getY(), "" + p2.getZ(),
        "" + dest.getX(), "" + dest.getY(), "" + dest.getZ(),
        blockToClone, metaOrStateToString(scope, metaOrStateToClone), cloneMode
    ).orElse(-1L);
  }

  /*
   * /defaultgamemode command
   */

  // TODO update
  @Method(name = "set_default_gamemode")
  @Doc("Sets the default game mode for new players. " +
      "Returns true if the action was successful, false otherwise.")
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

  // TODO update
  @Method(name = "set_difficulty")
  @Doc("Sets the difficulty level. " +
      "Returns true if the action was successful, false otherwise.")
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

  // TODO update
  @Method(name = "clear_all_effects")
  @Doc("Removes all effects from the selected entities. " +
      "Returns the number of affected entities or -1 if the action failed.")
  public Long clearAllEffects(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "effect",
        targetSelector, "clear"
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "give_effect")
  @Doc("Gives an effect to the selected entities. " +
      "Returns the number of affected entities or -1 if the action failed.")
  public Long giveEffect(final Scope scope, WorldProxy self, final String targetSelector, final String effect,
                         final Long seconds, final Long amplifier, final Boolean hideParticles) {
    return executeCommand(
        self,
        "effect",
        targetSelector, effect, seconds.toString(), amplifier.toString(), hideParticles.toString()
    ).orElse(-1L);
  }

  /*
   * /enchant command
   */

  // TODO update
  @Method(name = "enchant_selected_item")
  @Doc("Enchants the active item of all selected entities with the given enchantment. " +
      "Returns the number of affected items or -1 if the action failed.")
  public Long enchantSelectedItem(final Scope scope, WorldProxy self, final String targetSelector, final String enchantment, final Long level) {
    return executeCommand(
        self,
        "enchant",
        targetSelector, enchantment, level.toString()
    ).orElse(-1L);
  }

  /*
   * /execute command
   */

  // TODO update
  @Method(name = "execute_command")
  @Doc("Executes a command relatively to the selected entities. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean executeCommand(final Scope scope, WorldProxy self, final String targetSelector, final Position pos,
                                final String command) {
    List<String> args = new ArrayList<>(Arrays.asList(
        targetSelector, pos.getXCommandRepresentation(), pos.getYCommandRepresentation(), pos.getZCommandRepresentation()
    ));
    args.addAll(Arrays.asList(command.split(" ")));
    return executeCommand(
        self,
        "execute",
        args.toArray(new String[0])
    ).orElse(-1L) > 0;
  }

  /*
   * /experience /xp commands
   */

  // TODO update
  @Method(name = "give_xp")
  @Doc("Gives the indicated amount of XP points or levels to the selected players. " +
      "Returns the number of XP levels/points before the action was executed or -1 if the action failed.")
  public Long giveXP(final Scope scope, WorldProxy self, final String targetSelector, final Long amount, final Boolean levels) {
    return executeCommand(
        self,
        "xp",
        amount.toString() + (levels ? "L" : ""), targetSelector
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "remove_xp_levels")
  @Doc("Removes the indicated amount of XP levels from the selected players. " +
      "Returns the number of XP levels before the action was executed or -1 if the action failed.")
  public Long removeXPLevels(final Scope scope, WorldProxy self, final String targetSelector, final Long amount) {
    return executeCommand(
        self,
        "xp",
        "-" + amount.toString() + "L", targetSelector
    ).orElse(-1L);
  }

  /*
   * /function command
   */

  // TODO update
  @Method(name = "run_mcfunction")
  @Doc("Runs the given mcfunction. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean runMCFunction(final Scope scope, WorldProxy self, final String function) {
    return executeCommand(
        self,
        "function",
        function
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "run_mcfunction_if_entities_match")
  @Doc("Runs the given mcfunction only if entities match the given selector. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean runMCFunctionIfEntitiesMatch(final Scope scope, WorldProxy self, final String function, final String targetSelector) {
    return executeCommand(
        self,
        "function",
        function, "if", targetSelector
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "run_mcfunction_unless_entities_match")
  @Doc("Runs the given mcfunction only if no entities match the given selector. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean runMCFunctionUnlessEntitiesMatch(final Scope scope, WorldProxy self, final String function, final String targetSelector) {
    return executeCommand(
        self,
        "function",
        function, "unless", targetSelector
    ).orElse(-1L) > 0;
  }

  /*
   * /fill command
   */

  // TODO update
  @Method(name = "fill")
  @Doc("Fills the region between the given positions in this world with the specified block and metadata. " +
      "Returns the number of affected blocks or -1 if the action failed.")
  public Long fill(final Scope scope, WorldProxy self, final Position pos1, final Position pos2,
                   final String block, final Object metaOrState,
                   final String mode, final MCMap dataTags) {
    BlockPos p1 = pos1.toBlockPos();
    BlockPos p2 = pos2.toBlockPos();
    List<String> args = new ArrayList<>(Arrays.asList(
        "" + p1.getX(), "" + p1.getY(), "" + p1.getZ(),
        "" + p2.getX(), "" + p2.getY(), "" + p2.getZ(),
        block, metaOrStateToString(scope, metaOrState), mode
    ));
    if (!"replace".equals(mode)) {
      args.add(mapToJSON(dataTags));
    }
    return executeCommand(
        self,
        "fill",
        args.toArray(new String[0])
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "fill_replace")
  @Doc("Fills the region between the given positions in this world with the specified block and metadata. " +
      "Returns the number of affected blocks or -1 if the action failed.")
  public Long fill(final Scope scope, WorldProxy self, final Position pos1, final Position pos2,
                   final String block, final Object metaOrState,
                   final String blockToReplace, final Object metaOrStateToReplace, final MCMap dataTags) {
    BlockPos p1 = pos1.toBlockPos();
    BlockPos p2 = pos2.toBlockPos();
    return executeCommand(
        self,
        "fill",
        "" + p1.getX(), "" + p1.getY(), "" + p1.getZ(),
        "" + p2.getX(), "" + p2.getY(), "" + p2.getZ(),
        block, metaOrStateToString(scope, metaOrState),
        "replace", blockToReplace, metaOrStateToString(scope, metaOrStateToReplace),
        mapToJSON(dataTags)
    ).orElse(-1L);
  }

  /*
   * /gamemode command
   */

  // TODO update
  @Method(name = "set_gamemode")
  @Doc("Sets the game mode of the selected players. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long setGameMode(final Scope scope, WorldProxy self, final String targetSelector, final String gameMode) {
    return executeCommand(
        self,
        "gamemode",
        gameMode, targetSelector
    ).orElse(-1L);
  }

  /*
   * Get block info
   */

  // TODO update
  @Method(name = "get_block")
  @Doc("Returns the block at the given position in this world.")
  public Block getBlock(final Scope scope, final WorldProxy self, final Position pos) {
    return self.getWorld().getBlockState(pos.toBlockPos()).getBlock();
  }

  // TODO update
  @Method(name = "get_block_state")
  @Doc("Returns the block state at the given position in this world.")
  public MCMap getBlockState(final Scope scope, final WorldProxy self, final Position pos) {
    ImmutableMap<IProperty<?>, Comparable<?>> properties = self.getWorld().getBlockState(pos.toBlockPos())
        .getActualState(self.getWorld(), pos.toBlockPos()).getProperties();
    return new MCMap(properties.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getName(), e -> {
      Comparable<?> value = e.getValue();
      if (value instanceof Enum || value instanceof Character) {
        return value.toString();
      } else if (value instanceof Byte || value instanceof Short || value instanceof Integer) {
        return ((Number) value).longValue();
      } else if (value instanceof Float f) {
        return f.doubleValue();
      }
      return value;
    })));
  }

  // TODO update
  @Method(name = "is_block_loaded")
  @Doc("Returns whether the block at the given position is currently loaded.")
  public Boolean isBlockLoaded(final Scope scope, final WorldProxy self, final Position pos) {
    return self.getWorld().isLoaded(pos.toBlockPos());
  }

  /*
   * /kill command
   */

  // TODO update
  @Method(name = "kill_entities")
  @Doc("Kills all selected entities. " +
      "Returns the number of affected entities or -1 if the action failed.")
  public Long killEntities(final Scope scope, final WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "kill",
        targetSelector
    ).orElse(-1L);
  }

  /*
   * /locate command
   */

  // TODO update
  @Method(name = "locate_structure")
  @Doc("Returns the coordinates of the closest structure around the given point. " +
      "If the last parameter is true, unexplored structures will also be scanned. " +
      "Result may be null.")
  public Position locateStructure(final Scope scope, final WorldProxy self, final String structureName,
                                  final Position around, final Boolean findUnexplored) {
    BlockPos pos = self.getWorld().findNearestStructure(structureName, around.toBlockPos(), findUnexplored);
    if (pos == null) {
      return null;
    }
    return new Position(pos);
  }

  /*
   * /msg /tell /w /tellraw commands
   */

  // TODO update
  @Method(name = "send_message")
  @Doc("Sends a private message to the selected players. The message can be either a string or a map." +
      "Returns the number of affected players or -1 if the action failed.")
  public Long sendMessage(final Scope scope, WorldProxy self, final String targetSelector, final Object message) {
    String msg;
    if (message instanceof String) {
      msg = (String) message;
    } else {
      msg = mapToJSON(ProgramManager.getTypeInstance(MapType.class).implicitCast(scope, message));
    }
    return executeCommand(
        self,
        "msg",
        targetSelector, msg
    ).orElse(-1L);
  }

  /*
   * /particle command
   */

  // TODO update
  @Method(name = "spawn_particles")
  @Doc("Spawns particles at the given position. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean spawnParticles(final Scope scope, WorldProxy self, final String name, final Position pos,
                                final Double deltaX, final Double deltaY, final Double deltaZ,
                                final Double speed, final Long count,
                                final Boolean force, final String targetSelector) {
    return executeCommand(
        self,
        "particle",
        name, "" + pos.getX(), "" + pos.getY(), "" + pos.getZ(),
        deltaX.toString(), deltaY.toString(), deltaZ.toString(),
        speed.toString(), count.toString(),
        force ? "force" : "normal", targetSelector
    ).orElse(-1L) > 0;
  }

  /*
   * /playsound command
   */

  // TODO update
  @Method(name = "play_sound")
  @Doc("Plays the specified sound. Position may be null. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long playSound(final Scope scope, WorldProxy self, final String sound, final String source,
                        final String targetSelector, final Position pos) {
    List<String> args = new ArrayList<>(Arrays.asList(
        sound, source, targetSelector,
        pos.getXCommandRepresentation(), pos.getYCommandRepresentation(), pos.getZCommandRepresentation()
    ));
    return executeCommand(
        self,
        "playsound",
        args.toArray(new String[0])
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "play_sound_with_volume")
  @Doc("Plays the specified sound. Pitch and volume may be null. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long playSoundWithVolume(final Scope scope, WorldProxy self, final String sound, final String source,
                                  final String targetSelector, final Position pos,
                                  final Double volume, final Double pitch, final Double minVolume) {
    List<String> args = new ArrayList<>(Arrays.asList(
        sound, source, targetSelector,
        pos.getXCommandRepresentation(), pos.getYCommandRepresentation(), pos.getZCommandRepresentation()
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
        args.toArray(new String[0])
    ).orElse(-1L);
  }

  /*
   * /recipe command
   */

  // TODO update
  @Method(name = "unlock_all_recipes")
  @Doc("Unlocks all recipes for the selected players. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long unlockAllRecipes(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "recipe",
        "give", targetSelector, "*"
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "unlock_recipe")
  @Doc("Unlocks the given recipe for the selected players. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long unlockRecipe(final Scope scope, WorldProxy self, final String targetSelector, final String recipe) {
    return executeCommand(
        self,
        "recipe",
        "give", targetSelector, recipe
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "lock_all_recipes")
  @Doc("Locks all recipes for the selected players. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long lockAllRecipes(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "recipe",
        "take", targetSelector, "*"
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "lock_recipe")
  @Doc("Locks the given recipe for the selected players. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long lockRecipe(final Scope scope, WorldProxy self, final String targetSelector, final String recipe) {
    return executeCommand(
        self,
        "recipe",
        "take", targetSelector, recipe
    ).orElse(-1L);
  }

  /*
   * /scoreboard command
   */

  // /scoreboard objectives

  // TODO update
  @Method(name = "sb_get_objectives")
  @Doc("Returns the list of defined scoreboard objectives.")
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

  // TODO update
  @Method(name = "sb_create_objective")
  @Doc("Adds an objective to the scoreboard. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean createScoreboardObjective(final Scope scope, WorldProxy self, final String name, final String criteria, final String displayName) {
    List<String> args = new ArrayList<>(Arrays.asList(
        "objectives", "add", name, criteria
    ));
    if (displayName != null) {
      args.add(displayName);
    }
    return executeCommand(
        self,
        "scoreboard",
        args.toArray(new String[0])
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_delete_objective")
  @Doc("Removes an objective from the scoreboard. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean deleteScoreboardObjective(final Scope scope, WorldProxy self, final String name) {
    return executeCommand(
        self,
        "scoreboard",
        "objectives", "remove", name
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_set_objective_display_slot")
  @Doc("Sets the display slot of an objective from the scoreboard. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setScoreboardObjectiveDisplaySlot(final Scope scope, WorldProxy self, final String slot, final String name) {
    return executeCommand(
        self,
        "scoreboard",
        "objectives", "setdisplay", slot, name
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_clear_display_slot")
  @Doc("Clears a display slot of the scoreboard. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean clearScoreboardDisplaySlot(final Scope scope, WorldProxy self, final String slot) {
    return executeCommand(
        self,
        "scoreboard",
        "objectives", "setdisplay", slot
    ).orElse(-1L) > 0;
  }

  // /scoreboard players

  // TODO update
  @Method(name = "sb_get_tracked_players")
  @Doc("Returns the names of all players tracked by the scoreboard. " +
      "Names are sorted alphabetically.")
  public MCList getPlayersInScoreboard(final Scope scope, WorldProxy self) {
    MCList list = new MCList(self.getWorld().getScoreboard().getObjectiveNames());
    list.sort(null);
    return list;
  }

  // TODO update
  @Method(name = "sb_get_player_scores")
  @Doc("Returns the scores for the given player.")
  public MCMap getPlayerScores(final Scope scope, WorldProxy self, final String name) {
    return new MCMap(self.getWorld().getScoreboard().getPlayerScores(name).entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey().getName(), e -> e.getValue().getScore())));
  }

  // TODO update
  @Method(name = "sb_set_player_score")
  @Doc("Sets the score of an objective on selected players. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setPlayerScore(final Scope scope, WorldProxy self, final String targetSelector,
                                final String objective, final Long score, final MCMap dataTag) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "set", targetSelector, objective, score.toString(), mapToJSON(dataTag)
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_update_player_score")
  @Doc("Updates the score of an objective on selected players. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean updatePlayerScore(final Scope scope, WorldProxy self, final String targetSelector,
                                   final String objective, Long amount, final MCMap dataTag) {
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
        "players", action, targetSelector, objective, amount.toString(), mapToJSON(dataTag)
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_reset_player_scores")
  @Doc("Resets all scores of selected players. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean resetPlayerScores(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "reset", targetSelector
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_enable_trigger_for_players")
  @Doc("Enables the specified trigger of selected players. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean enableTriggerForPlayers(final Scope scope, WorldProxy self, final String targetSelector, final String trigger) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "enable", targetSelector, trigger
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_is_player_score_within_range")
  @Doc("Returns true if the objective score of the given player is within specified range. " +
      "Min and max may be null.")
  public Boolean isPlayerScoreWithinRange(final Scope scope, WorldProxy self, final String targetSelector,
                                          final String objective, final Long min, final Long max) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "test", targetSelector, objective, min.toString(), max.toString()
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_swap_objective_scores")
  @Doc("Swaps the scores of objectives between two players. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean swapObjectiveScores(final Scope scope, WorldProxy self,
                                     final String targetSelector1, final String objective1,
                                     final String targetSelector2, final String objective2) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "operation", targetSelector1, objective1, "><", targetSelector2, objective2
    ).orElse(-1L) > 0;
  }

  // /scoreboard players tag

  // TODO update
  @Method(name = "sb_get_player_tags")
  @Doc("Returns the tags of selected player or null if an error occurs. " +
      "Tags are sorted alphabetically.")
  public MCList getPlayerTags(final Scope scope, WorldProxy self, final String targetSelector) {
    MinecraftServer server = self.getWorld().getServer();
    Entity entity;
    try {
      entity = CommandBase.getEntity(server, server, targetSelector);
    } catch (CommandException e) {
      Utils.consoleLogTranslated(server, e.getMessage(), e.getErrorObjects());
      return null;
    }
    MCList list = new MCList(entity.getTags());
    list.sort(null);
    return list;
  }

  // TODO update
  @Method(name = "sb_add_tag_to_players")
  @Doc("Adds a tag to selected players. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean addTagToPlayers(final Scope scope, WorldProxy self,
                                 final String targetSelector, final String tagName, final MCMap dataTag) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "tag", targetSelector, "add", tagName, mapToJSON(dataTag)
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_remove_tag_from_players")
  @Doc("Removes a tag from selected players. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean removeTagFromPlayers(final Scope scope, WorldProxy self,
                                      final String targetSelector, final String tagName, final MCMap dataTag) {
    return executeCommand(
        self,
        "scoreboard",
        "players", "tag", targetSelector, "remove", tagName, mapToJSON(dataTag)
    ).orElse(-1L) > 0;
  }

  // /scoreboard teams

  // TODO update
  @Method(name = "sb_get_teams")
  @Doc("Returns a list of all currently defined teams. Teams are sorted by name alphabetically.")
  public MCList getTeams(final Scope scope, WorldProxy self) {
    return new MCList(self.getWorld().getScoreboard().getPlayerTeams().stream()
        .sorted(Comparator.comparing(PlayerTeam::getName))
        .map(t -> {
          MCMap map = new MCMap();
          map.put("name", t.getName());
          map.put("display_name", t.getDisplayName());
          map.put("prefix", t.getPlayerPrefix());
          map.put("suffix", t.getPlayerSuffix());
          map.put("friendly_fire", t.isAllowFriendlyFire());
          map.put("see_friendly_invisible", t.canSeeFriendlyInvisibles());
          map.put("name_tag_visibility", t.getNameTagVisibility().name);
          map.put("death_message_visibility", t.getDeathMessageVisibility().name);
          map.put("color", t.getColor().getName());
          map.put("collision_rule", t.getCollisionRule().name);
          return map;
        })
        .collect(Collectors.toList()));
  }

  // TODO update
  @Method(name = "sb_create_team")
  @Doc("Creates a new team of players. Display name may be null. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean createTeam(final Scope scope, WorldProxy self, final String teamName, final String displayName) {
    List<String> args = new ArrayList<>(Arrays.asList("teams", "add", teamName, displayName));
    if (displayName != null) {
      args.add(displayName);
    }
    return executeCommand(
        self,
        "scoreboard",
        args.toArray(new String[0])
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_delete_team")
  @Doc("Deletes a team of players. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean deleteTeam(final Scope scope, WorldProxy self, final String teamName) {
    return executeCommand(
        self,
        "scoreboard",
        "teams", "remove", teamName
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_clear_team")
  @Doc("Removes all players from a team. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean clearTeam(final Scope scope, WorldProxy self, final String teamName) {
    return executeCommand(
        self,
        "scoreboard",
        "teams", "empty", teamName
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_add_players_to_team")
  @Doc("Adds players to a team. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean addPlayersToTeam(final Scope scope, WorldProxy self, final String teamName, final MCList names) {
    return executeCommand(
        self,
        "scoreboard",
        "teams", "join", teamName, names.stream().map(String::valueOf).collect(Collectors.joining(" "))
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_remove_players_from_team")
  @Doc("Removes players from their team. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean removePlayersFromTeam(final Scope scope, WorldProxy self, final MCList names) {
    return executeCommand(
        self,
        "scoreboard",
        "teams", "leave", names.stream().map(String::valueOf).collect(Collectors.joining(" "))
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_set_team_color")
  @Doc("Sets the color of a team. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setTeamColor(final Scope scope, WorldProxy self, final String teamName, final String color) {
    return executeCommand(
        self,
        "scoreboard",
        "teams", "option", teamName, "color", color
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_set_team_friendly_fire")
  @Doc("Sets whether friendly fire is activated for the specified team. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setTeamFriendlyFire(final Scope scope, WorldProxy self, final String teamName, final Boolean friendlyFire) {
    return executeCommand(
        self,
        "scoreboard",
        "teams", "option", teamName, "friendlyFire", friendlyFire.toString()
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_set_team_see_friendly_invisible")
  @Doc("Sets whether players from the specified team should see invisible players from their team. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setTeamSeeFriendlyInvisible(final Scope scope, WorldProxy self, final String teamName, final Boolean seeFriendlyInvisible) {
    return executeCommand(
        self,
        "scoreboard",
        "teams", "option", teamName, "seeFriendlyInvisibles", seeFriendlyInvisible.toString()
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_set_team_name_tag_visibility")
  @Doc("Sets the visibility of the name tags of players from the specified team. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setTeamNameTagVisibility(final Scope scope, WorldProxy self, final String teamName, final String nameTagVisible) {
    return executeCommand(
        self,
        "scoreboard",
        "teams", "option", teamName, "nametagVisibility", nameTagVisible
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_set_team_death_message_visibility")
  @Doc("Sets the visibility of the death messages of players from the specified team. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setTeamDeathMessageVisibility(final Scope scope, WorldProxy self, final String teamName, final String deathMessageVisibility) {
    return executeCommand(
        self,
        "scoreboard",
        "teams", "option", teamName, "deathMessageVisibility", deathMessageVisibility
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_set_team_collision_rule")
  @Doc("Sets the collision rule of players from the specified team. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setTeamCollisionRule(final Scope scope, WorldProxy self, final String teamName, final String collisionRule) {
    return executeCommand(
        self,
        "scoreboard",
        "teams", "option", teamName, "collisionRule", collisionRule
    ).orElse(-1L) > 0;
  }

  /*
   * /trigger command
   */

  // TODO update
  @Method(name = "sb_update_trigger_objective")
  @Doc("Updates the score of the given trigger objective. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean updateTriggerObjective(final Scope scope, WorldProxy self, final String objectiveName, final Long amount) {
    return executeCommand(
        self,
        "trigger",
        objectiveName, "add", amount.toString()
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "sb_set_trigger_objective")
  @Doc("Sets the score of the given trigger objective. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setTriggerObjective(final Scope scope, WorldProxy self, final String objectiveName, final Long value) {
    return executeCommand(
        self,
        "trigger",
        objectiveName, "set", value.toString()
    ).orElse(-1L) > 0;
  }

  /*
   * /setblock command
   */

  // TODO update
  @Method(name = "set_block")
  @Doc("Sets the block at the given position in this world with the given block and metadata. " +
      "Returns the number of affected blocks or -1 if the action failed.")
  public Long setBlock(final Scope scope, WorldProxy self, final Position pos, final String block, final Object metaOrState,
                       final String mode, final MCMap dataTags) {
    BlockPos p = pos.toBlockPos();
    return executeCommand(
        self,
        "setblock",
        "" + p.getX(), "" + p.getY(), "" + p.getZ(),
        block, metaOrStateToString(scope, metaOrState),
        mode, mapToJSON(dataTags)
    ).orElse(-1L);
  }

  /*
   * /setworldspawn command
   */

  // TODO update
  @Method(name = "set_world_spawn")
  @Doc("Sets the world's spawn. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setWorldSpawn(final Scope scope, WorldProxy self, final Position pos) {
    BlockPos p = pos.toBlockPos();
    return executeCommand(
        self,
        "setworldspawn",
        "" + p.getX(), "" + p.getY(), "" + p.getZ()
    ).orElse(-1L) > 0;
  }

  /*
   * /spawnpoint command
   */

  // TODO update
  @Method(name = "set_spawn")
  @Doc("Sets the spawn point of the selected players. Position is relative to the targetted players." +
      "Returns the number of affected players or -1 if the action failed.")
  public Long setSpawn(final Scope scope, WorldProxy self, final String targetSelector, final Position pos) {
    return executeCommand(
        self,
        "spawnpoint",
        targetSelector,
        pos.getXCommandRepresentation(), pos.getYCommandRepresentation(), pos.getZCommandRepresentation()
    ).orElse(-1L);
  }

  /*
   * /spreadplayers command
   */

  // TODO update
  @Method(name = "spread_players")
  @Doc("Teleports players to a random location in the given area. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long spreadPlayers(final Scope scope, WorldProxy self, final Double centerX, final Double centerZ,
                            final Double spreadDistance, final Double maxRange, final Boolean respectTeams,
                            final String targetSelector) {
    return executeCommand(
        self,
        "spreadplayers",
        centerX.toString(), centerZ.toString(), spreadDistance.toString(), maxRange.toString(),
        respectTeams.toString(), targetSelector
    ).orElse(-1L);
  }

  /*
   * /stopsound command
   */

  // TODO update
  @Method(name = "stop_sound")
  @Doc("Stops the selected or all sounds. If source or sound are null, all sounds will be stopped." +
      "Returns the number of affected players or -1 if the action failed.")
  public Long spreadPlayers(final Scope scope, WorldProxy self, final String targetSelector, final String source, final String sound) {
    List<String> args = new ArrayList<>();
    args.add(targetSelector);
    if (source != null && sound != null) {
      args.add(source);
      args.add(sound);
    }
    return executeCommand(
        self,
        "spreadplayers",
        args.toArray(new String[0])
    ).orElse(-1L);
  }

  /*
   * /summon command
   */

  // TODO update
  @Method(name = "summon")
  @Doc("Summons an entity. Returns true if the action was successful, false otherwise.")
  public Boolean summonEntity(final Scope scope, WorldProxy self, final String entityType,
                              final Double posX, final Double posY, final Double posZ, final MCMap nbtData) {
    return executeCommand(
        self,
        "summon",
        entityType, posX.toString(), posY.toString(), posZ.toString(), mapToJSON(nbtData)
    ).orElse(-1L) > 0;
  }

  /*
   * /teleport /tp commands
   */

  // TODO update
  @Method(name = "tp_entities_to_player")
  @Doc("Teleports the selected entities to the given player. " +
      "Returns the number of affected entities or -1 if the action failed")
  public Long teleportEntitiesToPlayer(final Scope scope, WorldProxy self, final String targetSelector, final String playerName) {
    return executeCommand(
        self,
        "tp",
        targetSelector, playerName
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "tp_entities_to_pos")
  @Doc("Teleports the selected entities to the provided position (relative to the targetted entities). " +
      "Returns the number of affected entities or -1 if the action failed")
  public Long teleportEntitiesToPos(final Scope scope, WorldProxy self, final String targetSelector,
                                    final Position destination,
                                    final Double yawAngle, final Boolean yawRelative,
                                    final Double pitchAngle, final Boolean pitchRelative) {
    List<String> args = new ArrayList<>(Arrays.asList(
        targetSelector,
        destination.getXCommandRepresentation(),
        destination.getYCommandRepresentation(),
        destination.getZCommandRepresentation()
    ));
    if (yawAngle != null && pitchAngle != null) {
      args.add((yawRelative ? "~" + (yawAngle != 0 ? "" + yawAngle : "") : "" + yawAngle));
      args.add((pitchRelative ? "~" + (pitchAngle != 0 ? "" + pitchAngle : "") : "" + pitchAngle));
    }
    return executeCommand(
        self,
        "tp",
        args.toArray(new String[0])
    ).orElse(-1L);
  }

  /*
   * /testfor command
   */

  // FIXME testfor does not exist anymore
  @Method(name = "entities_match")
  @Doc("Tests whether entities match the given target selector.")
  public Long entitiesMatch(final Scope scope, WorldProxy self, final String targetSelector, final MCMap dataTags) {
    return executeCommand(
        self,
        "testfor",
        targetSelector, mapToJSON(dataTags)
    ).orElse(-1L);
  }

  /*
   * /time command
   */

  // TODO update
  @Method(name = "set_time")
  @Doc("Sets the time for all worlds. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setTime(final Scope scope, WorldProxy self, final Long value) {
    return executeCommand(
        self,
        "time",
        "set", value.toString()
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "add_time")
  @Doc("Adds the given amount to the time for all worlds. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean addTime(final Scope scope, WorldProxy self, final Long amount) {
    return executeCommand(
        self,
        "time",
        "add", amount.toString()
    ).orElse(-1L) > 0;
  }

  /*
   * /title command
   */

  // TODO update
  @Method(name = "clear_title")
  @Doc("Clears the screen title from the screens of the selected players. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long clearTitle(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "title",
        targetSelector, "clear"
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "reset_title")
  @Doc("Resets the subtitle text for the selected players to blank text, " +
      "and the fade-in, stay and fade-out times to their default values. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long resetTitle(final Scope scope, WorldProxy self, final String targetSelector) {
    return executeCommand(
        self,
        "title",
        targetSelector, "reset"
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "display_title")
  @Doc("Displays a screen title to the selected players, or changes the current screen title to the specified text. " +
      "After fading out, resets the subtitle back to blank text, but does not reset fade-in, stay, and fade-out times. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long displayTitle(final Scope scope, WorldProxy self, final String targetSelector, final MCMap title) {
    return executeCommand(
        self,
        "title",
        targetSelector, "title", mapToJSON(title)
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "set_subtitle")
  @Doc("Sets the subtitle for the current title or the next one if none is currently displayed. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long setSubtitle(final Scope scope, WorldProxy self, final String targetSelector, final MCMap subtitle) {
    return executeCommand(
        self,
        "title",
        targetSelector, "subtitle", mapToJSON(subtitle)
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "display_action_bar_text")
  @Doc("Displays text in the action bar slot of the selected players. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long displayActionBarText(final Scope scope, WorldProxy self, final String targetSelector, final MCMap text) {
    return executeCommand(
        self,
        "title",
        targetSelector, "actionbar", mapToJSON(text)
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "set_title_times")
  @Doc("Sets the fade-in, stay, and fade-out times (measured in game ticks) " +
      "of all current and future screen titles for the selected players. " +
      "Returns the number of affected players or -1 if the action failed.")
  public Long setTitleTimes(final Scope scope, WorldProxy self, final String targetSelector,
                            final Long fadeIn, final Long stay, final Long fadeOut) {
    return executeCommand(
        self,
        "title",
        targetSelector, "times", fadeIn.toString(), stay.toString(), fadeOut.toString()
    ).orElse(-1L);
  }

  /*
   * /weather command
   */

  // TODO update
  @Method(name = "set_weather")
  @Doc("Sets the weather for this world. Returns true if the action was successful, false otherwise.")
  public Boolean setWeather(final Scope scope, WorldProxy self, final String weather, final Long duration) {
    return executeCommand(
        self,
        "weather",
        weather, duration.toString()
    ).orElse(-1L) > 0;
  }

  /*
   * /worldborder command
   */

  // TODO update
  @Method(name = "wb_get_diameter")
  @Doc("Returns the world border's diameter.")
  public Long getWorldBorderDiameter(final Scope scope, WorldProxy self) {
    return executeCommand(
        self,
        "worldborder",
        "get"
    ).orElse(-1L);
  }

  // TODO update
  @Method(name = "wb_set_center")
  @Doc("Sets the center coordinate of the world border. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setWorldBorderCenter(final Scope scope, WorldProxy self, final Long centerX, final Long centerZ) {
    return executeCommand(
        self,
        "worldborder",
        "center", centerX.toString(), centerZ.toString()
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "wb_set_diameter")
  @Doc("Sets the world border's diameter in the specified number of seconds. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setWorldBorderDiameter(final Scope scope, WorldProxy self, final Long diameter, final Long time) {
    return executeCommand(
        self,
        "worldborder",
        "set", diameter.toString(), time.toString()
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "wb_update_diameter")
  @Doc("Add the given distance to the world border's diameter in the specified number of seconds. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean updateWorldBorderDiameter(final Scope scope, WorldProxy self, final Long amount, final Long time) {
    return executeCommand(
        self,
        "worldborder",
        "add", amount.toString(), time.toString()
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "wb_set_damage")
  @Doc("Sets the amount of damage per block to deal to players outside the border. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setWorldBorderDamage(final Scope scope, WorldProxy self, final Double damagePerBlock) {
    return executeCommand(
        self,
        "worldborder",
        "damage", "amount", damagePerBlock.toString()
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "wb_set_damage_buffer")
  @Doc("Sets distance outside the border over which the players will take damage. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setWorldBorderDamageBuffer(final Scope scope, WorldProxy self, final Long bufferDistance) {
    return executeCommand(
        self,
        "worldborder",
        "damage", "buffer", bufferDistance.toString()
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "wb_set_warn_distance")
  @Doc("Sets the world border's warning distance.")
  public Boolean setWorldBorderWarnDistance(final Scope scope, WorldProxy self, final Long distance) {
    return executeCommand(
        self,
        "worldborder",
        "warning", "distance", distance.toString()
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "wb_set_warn_time")
  @Doc("Sets the world border's warning time. " +
      "Returns true if the action was successful, false otherwise.")
  public Boolean setWorldBorderWarnTime(final Scope scope, WorldProxy self, final Long time) {
    return executeCommand(
        self,
        "worldborder",
        "warning", "time", time.toString()
    ).orElse(-1L) > 0;
  }

  // TODO update
  @Method(name = "get_entities_data")
  @Doc("Returns data of all entities that match the given selector. Returns null if an error occurs.")
  public MCList getEntitiesData(final Scope scope, final WorldProxy self, final String targetSelector) {
    List<Entity> entities;
    try {
      entities = CommandBase.getEntityList(self.getWorld().getServer(), self.getWorld().getServer(), targetSelector);
    } catch (CommandException e) {
      e.printStackTrace();
      return null;
    }
    return new MCList(entities.stream().map(e -> toMap(e.saveWithoutId(new CompoundTag()))).collect(Collectors.toList()));
  }

  private static MCMap toMap(final CompoundTag tag) {
    MCMap map = new MCMap();
    for (String key : tag.getAllKeys()) {
      Object value = deserializeNBTTag(tag.get(key));
      if (value != null) {
        map.put(key, value);
      }
    }
    return map;
  }

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
      return toMap(t);
    }
    return null;
  }

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

  /*
   * Utility methods
   */

  private static Optional<Long> executeCommand(WorldProxy self, final String commandName, final String... args) {
    MinecraftServer server = self.getWorld().getServer();
    String command = commandName + " " + String.join(" ", args);
    ServerLevel serverlevel = self.getWorld();
    CommandSourceStackWrapper commandSourceStack = new CommandSourceStackWrapper(server, serverlevel);
    long result = server.getCommands().performCommand(commandSourceStack, command);
    if (result == 0 && commandSourceStack.anyFailures) {
      return Optional.empty();
    }
    return Optional.of(result);
  }

  private static class CommandSourceStackWrapper extends CommandSourceStack {
    boolean anyFailures;

    public CommandSourceStackWrapper(CommandSource source, ServerLevel level) {
      super(source, Vec3.atLowerCornerOf(level.getSharedSpawnPos()), Vec2.ZERO, level, 4,
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

  private static String metaOrStateToString(final Scope scope, final Object metaOrState) {
    if (metaOrState instanceof Long) {
      return "" + metaOrState;
    }
    return mapToBlockState(ProgramManager.getTypeInstance(MapType.class).implicitCast(scope, metaOrState));
  }

  private static String mapToBlockState(final MCMap map) {
    return map.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(","));
  }

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

  private static String mapToDataTag(final Map<String, ?> map) {
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

  private static String serializedDataTagValue(final Object value) {
    StringBuilder sb = new StringBuilder();

    if (value == null) {
      sb.append("null");
    } else if (value instanceof Map<?, ?> m) {
      //noinspection unchecked
      sb.append(mapToDataTag((Map<String, ?>) m));
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

  @Override
  public WorldProxy readFromNBT(final Scope scope, final CompoundTag tag) {
    return (WorldProxy) scope.getVariable(Program.WORLD_VAR_NAME, false);
  }
}
