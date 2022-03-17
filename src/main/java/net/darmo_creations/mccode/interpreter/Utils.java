package net.darmo_creations.mccode.interpreter;

import net.darmo_creations.mccode.interpreter.statements.Statement;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions for interpreter-related classes.
 */
public final class Utils {
  private static final String INDENT = "  ";

  /**
   * Stringify the given list of statements and indents them.
   * Resulting string starts and ends with a line return.
   *
   * @param statements The statements to format.
   * @return The stringified and indented statements.
   */
  public static String indentStatements(final List<Statement> statements) {
    if (statements.isEmpty()) {
      return "";
    }
    StringJoiner code = new StringJoiner("\n" + INDENT, "\n" + INDENT, "\n");
    for (Statement s : statements) {
      code.add(String.join("\n" + INDENT, s.toString().split("\n")));
    }
    return code.toString();
  }

  /**
   * Performs a true modulo operation using the mathematical definition of "a mod b".
   *
   * @param a Value to get the modulo of.
   * @param b The divisor.
   * @return a mod b
   * @throws ArithmeticException If b = 0.
   */
  public static double trueModulo(final double a, final double b) {
    if (b == 0) {
      throw new ArithmeticException("/ by 0");
    }
    return ((a % b) + b) % b;
  }

  /**
   * Escape all special characters from the given string.
   * <p>
   * Adds double quotes, escapes '"', '\' and '\n' characters.
   *
   * @param s The string to escape.
   * @return The escaped string.
   */
  public static String escapeString(final String s) {
    return String.format("\"%s\"", s.replaceAll("([\"\\\\])", "\\\\$1").replace("\n", "\\n"));
  }

  /**
   * Unescape all escaped special characters from the given string.
   * <p>
   * Removes trailing double quotes, unescapes '\"', '\\' and '\\n' sequences.
   *
   * @param s The string to unescape.
   * @return The unescaped string.
   */
  public static String unescapeString(final String s) {
    return s.substring(1, s.length() - 1).replaceAll("\\\\([\"\\\\])", "$1").replace("\\n", "\n");
  }

  /**
   * Replaces all substrings that match the given pattern by the values returned by the specified callback.
   *
   * @param input    The target string.
   * @param regex    The pattern to apply.
   * @param callback A function that is called for each pattern match and returns a replacement string.
   * @return The resulting string.
   */
  public static String replaceAll(String input, Pattern regex, Function<Matcher, String> callback) {
    StringBuilder resultString = new StringBuilder();
    Matcher regexMatcher = regex.matcher(input);
    while (regexMatcher.find()) {
      regexMatcher.appendReplacement(resultString, callback.apply(regexMatcher));
    }
    regexMatcher.appendTail(resultString);

    return resultString.toString();
  }

  /**
   * Print a message in the server console.
   *
   * @param server  Server instance.
   * @param message Message to displaly.
   */
  public static void consoleLog(MinecraftServer server, final String message) {
    server.sendMessage(new TextComponent(message), Util.NIL_UUID);
  }

  /**
   * Print a translated message in the server console.
   *
   * @param server         Server instance.
   * @param translationKey Translation key.
   * @param args           Translation arguments.
   */
  public static void consoleLogTranslated(MinecraftServer server, final String translationKey, final Object... args) {
    server.sendMessage(new TranslatableComponent(translationKey, args), Util.NIL_UUID);
  }

  private Utils() {
  }
}
