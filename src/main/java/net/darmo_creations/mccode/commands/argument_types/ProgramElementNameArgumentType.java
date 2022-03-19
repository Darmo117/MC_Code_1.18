package net.darmo_creations.mccode.commands.argument_types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.darmo_creations.mccode.commands.CommandProgram;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Command argument type for functions, methods, properties and types.
 */
public class ProgramElementNameArgumentType implements ArgumentType<String> {
  /**
   * Generate an argument type.
   */
  public static ProgramElementNameArgumentType create() {
    return new ProgramElementNameArgumentType();
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
    if (source instanceof CommandSourceStack) {
      //noinspection unchecked
      CommandContext<CommandSourceStack> ctx = (CommandContext<CommandSourceStack>) context;
      CommandProgram.DocType docType = ctx.getArgument(CommandProgram.DOC_TYPE_ARG, CommandProgram.DocType.class);
      return switch (docType) {
        case type -> SharedSuggestionProvider.suggest(
            ProgramManager.getTypes().stream()
                .map(TypeBase::getName)
                .sorted()
                .collect(Collectors.toList()),
            builder
        );
        case property -> SharedSuggestionProvider.suggest(
            ProgramManager.getTypes().stream()
                .flatMap(type -> type.getProperties().keySet().stream().map(pName -> type.getName() + "." + pName))
                .sorted()
                .collect(Collectors.toList()),
            builder
        );
        case method -> SharedSuggestionProvider.suggest(
            ProgramManager.getTypes().stream()
                .flatMap(type -> type.getMethods().keySet().stream().map(mName -> type.getName() + "." + mName))
                .sorted()
                .collect(Collectors.toList()),
            builder
        );
        case function -> SharedSuggestionProvider.suggest(
            ProgramManager.getBuiltinFunctions().stream()
                .map(BuiltinFunction::getName)
                .sorted()
                .collect(Collectors.toList()),
            builder
        );
      };
    } else if (source instanceof SharedSuggestionProvider s) {
      //noinspection unchecked
      return s.customSuggestion((CommandContext<SharedSuggestionProvider>) context, builder);
    }
    return Suggestions.empty();
  }
}
