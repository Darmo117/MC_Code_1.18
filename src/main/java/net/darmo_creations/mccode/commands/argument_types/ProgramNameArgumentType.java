package net.darmo_creations.mccode.commands.argument_types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.darmo_creations.mccode.MCCode;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Command argument type for program names.
 */
@SuppressWarnings("ClassCanBeRecord")
public class ProgramNameArgumentType implements ArgumentType<String> {
  /**
   * Generate an argument type for programs available in save’s data directory only.
   */
  public static ProgramNameArgumentType available() {
    return new ProgramNameArgumentType(false);
  }

  /**
   * Generate an argument type for loaded programs only.
   */
  public static ProgramNameArgumentType loaded() {
    return new ProgramNameArgumentType(true);
  }

  private final boolean loadedOnly;

  private ProgramNameArgumentType(final boolean loadedOnly) {
    this.loadedOnly = loadedOnly;
  }

  public static String getName(final CommandContext<?> context, final String argumentName) {
    return context.getArgument(argumentName, String.class);
  }

  @Override
  public String parse(StringReader reader) {
    return reader.readUnquotedString();
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    S source = context.getSource();
    if (source instanceof CommandSourceStack s) {
      ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(s.getLevel());
      if (this.loadedOnly) {
        File dir = pm.getProgramsDirectory();
        //noinspection ConstantConditions
        List<String> names = Arrays.stream(dir.listFiles(f -> f.getName().endsWith(".mccode")))
            .map(f -> f.getName().substring(0, f.getName().indexOf('.')))
            .collect(Collectors.toList());
        return SharedSuggestionProvider.suggest(names, builder);
      } else {
        return SharedSuggestionProvider.suggest(pm.getLoadedPrograms(), builder);
      }
    } else if (source instanceof SharedSuggestionProvider s) {
      //noinspection unchecked
      return s.customSuggestion((CommandContext<SharedSuggestionProvider>) context, builder);
    }
    return Suggestions.empty();
  }
}
