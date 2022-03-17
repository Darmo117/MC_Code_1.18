package net.darmo_creations.mccode;

import net.darmo_creations.mccode.commands.CommandProgram;
import net.darmo_creations.mccode.interpreter.ProgramErrorReport;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Mod’s main class. MCCode adds the possibility to write scripts to load and interact with Minecraft worlds.
 */
@Mod(MCCode.MOD_ID)
public class MCCode {
  public static final String MOD_ID = "mccode";
  public static final Logger LOGGER = LogManager.getLogger();

  public static final GameRules.Key<GameRules.BooleanValue> GR_SHOW_ERROR_MESSAGES;

  public static MCCode INSTANCE;

  static {
    // GameRules.BooleanValue.create(boolean) is package-private… Let’s call it anyway!
    Method method = ObfuscationReflectionHelper.findMethod(GameRules.BooleanValue.class, "create", boolean.class);
    method.setAccessible(true);
    try {
      //noinspection unchecked
      GR_SHOW_ERROR_MESSAGES = GameRules.register("showProgramErrorMessages", GameRules.Category.MISC,
          (GameRules.Type<GameRules.BooleanValue>) method.invoke(null, true));
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Map associating worlds to their program managers.
   */
  public final Map<Level, ProgramManager> PROGRAM_MANAGERS = new HashMap<>();

  public MCCode() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    modEventBus.addListener(this::setup);
    MinecraftForge.EVENT_BUS.register(this);
    INSTANCE = this;
  }

  private void setup(final FMLCommonSetupEvent event) {
    ProgramManager.declareDefaultBuiltinTypes();
    ProgramManager.declareDefaultBuiltinFunctions();
    ProgramManager.initialize();
  }

  /**
   * Forge-related events.
   */
  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
  public static class ForgeEvents {
    @SubscribeEvent
    public static void onCommandsRegistry(final RegisterCommandsEvent event) {
      CommandProgram.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
      LevelAccessor world = event.getWorld();
      if (world instanceof ServerLevel w) {
        INSTANCE.PROGRAM_MANAGERS.put(w, new ProgramManager(w));
      }
    }

    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event) {
      LevelAccessor world = event.getWorld();
      if (world instanceof ServerLevel w) {
        ProgramManager pm = INSTANCE.PROGRAM_MANAGERS.get(w);
        if (pm != null) {
          pm.save();
        }
      }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
      LevelAccessor world = event.getWorld();
      if (world instanceof ServerLevel w) {
        INSTANCE.PROGRAM_MANAGERS.remove(w);
      }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.WorldTickEvent event) {
      if (!event.world.isClientSide() && event.phase == TickEvent.Phase.START) {
        for (ProgramManager programManager : INSTANCE.PROGRAM_MANAGERS.values()) {
          for (ProgramErrorReport report : programManager.executePrograms()) {
            // Log errors in chat and server console
            MutableComponent message;
            if (report.getLine() != -1 && report.getColumn() != -1) {
              message = new TextComponent(
                  String.format("[%s:%d:%d] ", report.getScope().getProgram().getName(), report.getLine(), report.getColumn()))
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
            } else {
              message = new TextComponent(String.format("[%s] ", report.getScope().getProgram().getName()))
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
            }
            message.append(new TranslatableComponent(report.getTranslationKey(), report.getArgs())
                .setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));

            ServerLevel world = report.getScope().getProgram().getProgramManager().getWorld();
            // Only show error messages to players that can use the "program" command
            if (world.getGameRules().getBoolean(GR_SHOW_ERROR_MESSAGES)) {
              world.getPlayers(p -> true).stream()
                  .filter(p -> p.hasPermissions(2))
                  .forEach(player -> player.sendMessage(message, Util.NIL_UUID));
            }
            world.getServer().sendMessage(message, Util.NIL_UUID);
          }
        }
      }
    }
  }
}
