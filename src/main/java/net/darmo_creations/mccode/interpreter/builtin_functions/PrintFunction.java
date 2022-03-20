package net.darmo_creations.mccode.interpreter.builtin_functions;

import net.darmo_creations.mccode.interpreter.Parameter;
import net.darmo_creations.mccode.interpreter.Program;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.Scope;
import net.darmo_creations.mccode.interpreter.annotations.Function;
import net.darmo_creations.mccode.interpreter.type_wrappers.AnyType;
import net.darmo_creations.mccode.interpreter.type_wrappers.NullType;
import net.darmo_creations.mccode.interpreter.type_wrappers.StringType;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

/**
 * A function that prints text to the console and/or chat.
 */
@Function(parametersDoc = {
    "The value to print.",
    "The channel where to print the value. Must be one of $CHAT, $CONSOLE or $BOTH."},
    doc = "Prints a value in the chat and/or server console.")
public class PrintFunction extends BuiltinFunction {
  /**
   * Create a function that prints text to the console and/or chat.
   */
  public PrintFunction() {
    super("print", ProgramManager.getTypeInstance(NullType.class), false,
        new Parameter("message", ProgramManager.getTypeInstance(AnyType.class), true),
        new Parameter("channel", ProgramManager.getTypeInstance(StringType.class)));
  }

  @Override
  public Object apply(final Scope scope) {
    Program program = scope.getProgram();
    MinecraftServer server = program.getProgramManager().getWorld().getServer();
    Object message = this.getParameterValue(scope, 0);
    String text = ProgramManager.getTypeForValue(message).toString(message);
    Channel channel = Channel.fromString(this.getParameterValue(scope, 1));
    if (channel == Channel.CHAT || channel == Channel.BOTH) {
      scope.getProgram().getProgramManager().getWorld().getPlayers(p -> true)
          .forEach(player -> player.sendMessage(new TextComponent(text), Util.NIL_UUID));
    }
    if (channel == Channel.CONSOLE || channel == Channel.BOTH) {
      String dimension = scope.getProgram().getProgramManager().getWorld().dimension().getRegistryName().toString();
      server.sendMessage(new TextComponent("[MCCode:%s][%s] %s".formatted(program.getName(), dimension, text)), Util.NIL_UUID);
    }
    return null;
  }

  /**
   * Available outputs where messages can be printed to.
   */
  public enum Channel {
    CHAT, CONSOLE, BOTH;

    public static Channel fromString(final String s) {
      for (Channel value : values()) {
        if (value.name().toUpperCase().equals(s)) {
          return value;
        }
      }
      return null;
    }
  }
}
