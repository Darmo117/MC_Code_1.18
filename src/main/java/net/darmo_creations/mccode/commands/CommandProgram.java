package net.darmo_creations.mccode.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.darmo_creations.mccode.MCCode;
import net.darmo_creations.mccode.commands.argument_types.ProgramElementNameArgumentType;
import net.darmo_creations.mccode.commands.argument_types.ProgramNameArgumentType;
import net.darmo_creations.mccode.commands.argument_types.ProgramVariableNameArgumentType;
import net.darmo_creations.mccode.interpreter.MemberFunction;
import net.darmo_creations.mccode.interpreter.ObjectProperty;
import net.darmo_creations.mccode.interpreter.Program;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.darmo_creations.mccode.interpreter.exceptions.ProgramStatusException;
import net.darmo_creations.mccode.interpreter.exceptions.SyntaxErrorException;
import net.darmo_creations.mccode.interpreter.nodes.Node;
import net.darmo_creations.mccode.interpreter.parser.ExpressionParser;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraftforge.server.command.EnumArgument;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Command to interact with programs.
 */
public class CommandProgram {
  public static final String PROGRAM_NAME_ARG = "program_name";
  public static final String PROGRAM_ALIAS_ARG = "alias";
  public static final String PROGRAM_ARGUMENTS_ARG = "arguments";
  public static final String VARIABLE_NAME_ARG = "variable_name";
  public static final String VARIABLE_VALUE_ARG = "value";
  public static final String DOC_TYPE_ARG = "type";
  public static final String ELEMENT_NAME_ARG = "name";

  /**
   * Register this command in the given dispatcher.
   */
  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    LiteralArgumentBuilder<CommandSourceStack> loadProgramOption = Commands.literal("load")
        .then(buildLoadProgramBranch(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.available()), false))
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.available())
            .then(Commands.literal("as")
                .then(buildLoadProgramBranch(Commands.argument(PROGRAM_ALIAS_ARG, StringArgumentType.word()), true))));

    LiteralArgumentBuilder<CommandSourceStack> unloadProgramOption = Commands.literal("unload")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.loaded())
            .executes(CommandProgram::unloadProgram));

    LiteralArgumentBuilder<CommandSourceStack> resetProgramOption = Commands.literal("reset")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.loaded())
            .executes(CommandProgram::resetProgram));

    LiteralArgumentBuilder<CommandSourceStack> runProgramOption = Commands.literal("run")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.loaded())
            .executes(CommandProgram::runProgram));

    LiteralArgumentBuilder<CommandSourceStack> pauseProgramOption = Commands.literal("pause")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.loaded())
            .executes(CommandProgram::pauseProgram));

    LiteralArgumentBuilder<CommandSourceStack> getVariableOption = Commands.literal("get")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.loaded())
            .then(Commands.argument(VARIABLE_NAME_ARG, ProgramVariableNameArgumentType.variableName())
                .executes(CommandProgram::getVariableValue)));

    LiteralArgumentBuilder<CommandSourceStack> setVariableOption = Commands.literal("set")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.loaded())
            .then(Commands.argument(VARIABLE_NAME_ARG, ProgramVariableNameArgumentType.editableVariableName())
                .then(Commands.argument(VARIABLE_VALUE_ARG, StringArgumentType.greedyString())
                    .executes(CommandProgram::setVariableValue))));

    LiteralArgumentBuilder<CommandSourceStack> deleteVariableOption = Commands.literal("delete")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.loaded())
            .then(Commands.argument(VARIABLE_NAME_ARG, ProgramVariableNameArgumentType.deletableVariableName())
                .executes(CommandProgram::deleteVariable)));

    LiteralArgumentBuilder<CommandSourceStack> listProgramsOption = Commands.literal("list")
        .executes(CommandProgram::listPrograms);

    LiteralArgumentBuilder<CommandSourceStack> docOption = Commands.literal("doc")
        .then(Commands.argument(DOC_TYPE_ARG, EnumArgument.enumArgument(DocType.class))
            .then(Commands.argument(ELEMENT_NAME_ARG, ProgramElementNameArgumentType.create())
                .executes(CommandProgram::showDoc)));

    dispatcher.register(
        Commands.literal("program")
            .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
            .then(loadProgramOption)
            .then(unloadProgramOption)
            .then(resetProgramOption)
            .then(runProgramOption)
            .then(pauseProgramOption)
            .then(getVariableOption)
            .then(setVariableOption)
            .then(deleteVariableOption)
            .then(listProgramsOption)
            .then(docOption)
    );
  }

  private static ArgumentBuilder<CommandSourceStack, ?> buildLoadProgramBranch(
      ArgumentBuilder<CommandSourceStack, ?> root, final boolean hasAlias) {
    return root.executes(context -> loadProgram(context, hasAlias, false))
        .then(Commands.argument(PROGRAM_ARGUMENTS_ARG, StringArgumentType.greedyString())
            .executes(context -> loadProgram(context, hasAlias, true)));
  }

  private static int loadProgram(CommandContext<CommandSourceStack> context, final boolean hasAlias, final boolean hasArgs) {
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = ProgramNameArgumentType.getName(context, PROGRAM_NAME_ARG);
    String alias = hasAlias ? StringArgumentType.getString(context, PROGRAM_ALIAS_ARG) : null;
    String[] args = hasArgs ? StringArgumentType.getString(context, PROGRAM_ARGUMENTS_ARG).split(" ") : new String[0];
    try {
      pm.loadProgram(programName, alias, false, args);
    } catch (SyntaxErrorException e) {
      Object[] a = new Object[e.getArgs().length + 3];
      a[0] = programName;
      a[1] = e.getLine();
      a[2] = e.getColumn();
      System.arraycopy(e.getArgs(), 0, a, 3, e.getArgs().length);
      context.getSource().sendFailure(new TranslatableComponent(e.getTranslationKey(), a));
      return 0;
    } catch (ProgramStatusException e) {
      context.getSource().sendFailure(new TranslatableComponent(e.getTranslationKey(), e.getProgramName()));
      return 0;
    }
    context.getSource().sendSuccess(
        new TranslatableComponent("commands.program.feedback.program_loaded", programName), true);
    return args.length;
  }

  private static int unloadProgram(CommandContext<CommandSourceStack> context) {
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = ProgramNameArgumentType.getName(context, PROGRAM_NAME_ARG);
    try {
      pm.unloadProgram(programName);
    } catch (ProgramStatusException e) {
      context.getSource().sendFailure(new TranslatableComponent(e.getTranslationKey(), e.getProgramName()));
      return 0;
    }
    context.getSource().sendSuccess(
        new TranslatableComponent("commands.program.feedback.program_unloaded", programName), true);
    return 1;
  }

  private static int resetProgram(CommandContext<CommandSourceStack> context) {
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = ProgramNameArgumentType.getName(context, PROGRAM_NAME_ARG);
    try {
      pm.resetProgram(programName);
    } catch (ProgramStatusException e) {
      context.getSource().sendFailure(new TranslatableComponent(e.getTranslationKey(), e.getProgramName()));
      return 0;
    }
    context.getSource().sendSuccess(
        new TranslatableComponent("commands.program.feedback.program_reset", programName), true);
    return 1;
  }

  private static int runProgram(final CommandContext<CommandSourceStack> context) {
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = ProgramNameArgumentType.getName(context, PROGRAM_NAME_ARG);
    try {
      pm.runProgram(programName);
    } catch (ProgramStatusException e) {
      context.getSource().sendFailure(new TranslatableComponent(e.getTranslationKey(), e.getProgramName()));
      return 0;
    }
    context.getSource().sendSuccess(
        new TranslatableComponent("commands.program.feedback.program_launched", programName), true);
    return 1;
  }

  private static int pauseProgram(CommandContext<CommandSourceStack> context) {
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = ProgramNameArgumentType.getName(context, PROGRAM_NAME_ARG);
    try {
      pm.pauseProgram(programName);
    } catch (ProgramStatusException e) {
      context.getSource().sendFailure(new TranslatableComponent(e.getTranslationKey(), e.getProgramName()));
      return 0;
    }
    context.getSource().sendSuccess(
        new TranslatableComponent("commands.program.feedback.program_paused", programName), true);
    return 1;
  }

  private static int listPrograms(CommandContext<CommandSourceStack> context) {
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    List<String> loadedPrograms = pm.getLoadedPrograms();
    if (loadedPrograms.isEmpty()) {
      context.getSource().sendFailure(new TranslatableComponent("commands.program.error.no_loaded_programs"));
      return 0;
    } else {
      context.getSource().sendSuccess(
          new TranslatableComponent("commands.program.feedback.loaded_programs",
              String.join(", ", loadedPrograms)), true);
      return loadedPrograms.size();
    }
  }

  private static int getVariableValue(CommandContext<CommandSourceStack> context) {
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = ProgramNameArgumentType.getName(context, PROGRAM_NAME_ARG);
    Optional<Program> program = pm.getProgram(programName);
    if (program.isPresent()) {
      String variableName = ProgramVariableNameArgumentType.getName(context, VARIABLE_NAME_ARG);
      Object value;
      try {
        value = program.get().getScope().getVariable(variableName, true);
      } catch (EvaluationException e) {
        context.getSource().sendFailure(new TranslatableComponent(e.getTranslationKey(), e.getArgs()));
        return 0;
      }
      context.getSource().sendSuccess(
          new TranslatableComponent("commands.program.feedback.get_variable_value", variableName, value), true);
      return 1;
    } else {
      context.getSource().sendFailure(
          new TranslatableComponent("mccode.interpreter.error.program_not_found", programName));
      return 0;
    }
  }

  private static int setVariableValue(CommandContext<CommandSourceStack> context) {
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = ProgramNameArgumentType.getName(context, PROGRAM_NAME_ARG);
    Optional<Program> program = pm.getProgram(programName);
    if (program.isPresent()) {
      String variableName = ProgramVariableNameArgumentType.getName(context, VARIABLE_NAME_ARG);
      Node node;
      try {
        node = ExpressionParser.parse(StringArgumentType.getString(context, VARIABLE_VALUE_ARG));
      } catch (SyntaxErrorException e) {
        context.getSource().sendFailure(new TranslatableComponent(e.getTranslationKey(), e.getArgs()));
        return 0;
      }

      Object value;
      try {
        value = node.evaluate(program.get().getScope());
        program.get().getScope().setVariable(variableName, value, true);
      } catch (EvaluationException e) {
        context.getSource().sendFailure(new TranslatableComponent(e.getTranslationKey(), e.getArgs()));
        return 0;
      }
      context.getSource().sendSuccess(
          new TranslatableComponent("commands.program.feedback.set_variable_value", variableName, node), true);
      return 1;
    } else {
      context.getSource().sendFailure(
          new TranslatableComponent("mccode.interpreter.error.program_not_found", programName));
      return 0;
    }
  }

  private static int deleteVariable(CommandContext<CommandSourceStack> context) {
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = ProgramNameArgumentType.getName(context, PROGRAM_NAME_ARG);
    Optional<Program> program = pm.getProgram(programName);
    if (program.isPresent()) {
      String variableName = ProgramVariableNameArgumentType.getName(context, VARIABLE_NAME_ARG);
      try {
        program.get().getScope().deleteVariable(variableName, true);
      } catch (EvaluationException e) {
        context.getSource().sendFailure(new TranslatableComponent(e.getTranslationKey(), e.getArgs()));
        return 0;
      }
      context.getSource().sendSuccess(
          new TranslatableComponent("commands.program.feedback.variable_delete", variableName), true);
      return 1;
    } else {
      context.getSource().sendFailure(
          new TranslatableComponent("mccode.interpreter.error.program_not_found", programName));
      return 0;
    }
  }

  private static int showDoc(CommandContext<CommandSourceStack> context) {
    DocType docType = context.getArgument(DOC_TYPE_ARG, DocType.class);
    String name = ProgramElementNameArgumentType.getName(context, ELEMENT_NAME_ARG);

    Optional<Pair<String, Object[]>> doc = switch (docType) {
      case type -> getTypeDoc(context, name);
      case property -> getPropertyDoc(context, name);
      case method -> getMethodDoc(context, name);
      case function -> getFunctionDoc(context, name);
    };

    if (doc.isPresent()) {
      Pair<String, Object[]> d = doc.get();
      context.getSource().sendSuccess(
          new TranslatableComponent("commands.program.feedback.doc_" + docType.name(), d.getRight())
              .withStyle(ChatFormatting.GREEN),
          true);
      context.getSource().sendSuccess(parseDoc(d.getLeft()), true);
      return 1;
    }
    return 0;
  }

  /**
   * Formats the given doc string.
   *
   * @param rawDoc The raw documentation string.
   * @return The resulting chat components.
   */
  private static Component parseDoc(final String rawDoc) {
    BaseComponent component = new TextComponent("");
    StringBuilder sb = new StringBuilder();
    boolean escapeNext = false;

    for (int i = 0; i < rawDoc.length(); i++) {
      String c = rawDoc.charAt(i) + "";
      if ("\\".equals(c) && !escapeNext) {
        escapeNext = true;
      } else if (ProgramManager.DOC_PARAM_PREFIX.equals(c)) {
        i = consumePrefixedWord(rawDoc, component, sb, i,
            param -> new TextComponent(param).withStyle(ChatFormatting.ITALIC));
      } else if (ProgramManager.DOC_TYPE_PREFIX.equals(c)) {
        i = consumePrefixedWord(rawDoc, component, sb, i,
            type -> new TextComponent(type).withStyle(Style.EMPTY
                .withColor(ChatFormatting.AQUA)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/program doc type " + type.toLowerCase()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.type_doc.tooltip", type.toLowerCase())))));
      } else if (ProgramManager.DOC_TAG_PREFIX.equals(c)) {
        i = consumePrefixedWord(rawDoc, component, sb, i,
            tag -> new TextComponent(tag).withStyle(ChatFormatting.UNDERLINE));
      } else if (ProgramManager.DOC_FUNCTION_PREFIX.equals(c)) {
        i = consumePrefixedWord(rawDoc, component, sb, i,
            function -> new TextComponent(function).withStyle(Style.EMPTY
                .withColor(ChatFormatting.DARK_GREEN)
                .withItalic(true)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/program doc function " + function.toLowerCase()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.function_doc.tooltip", function.toLowerCase())))));
      } else if (ProgramManager.DOC_LITERAL_PREFIX.equals(c)) {
        i = consumePrefixedWord(rawDoc, component, sb, i,
            value -> new TextComponent(value).withStyle(ChatFormatting.DARK_PURPLE));
      } else {
        sb.append(c);
      }
    }

    if (!sb.isEmpty()) {
      component.append(sb.toString());
    }

    return component;
  }

  private static final Pattern WORD_PATTERN = Pattern.compile("\\w", Pattern.UNICODE_CHARACTER_CLASS);

  private static int consumePrefixedWord(final String rawDoc, BaseComponent component, StringBuilder sb, final int start,
                                         final Function<String, Component> getComponent) {
    StringBuilder word = new StringBuilder();

    for (int i = start + 1; i < rawDoc.length(); i++) {
      char c = rawDoc.charAt(i);
      if (WORD_PATTERN.asPredicate().test("" + c)) {
        word.append(c);
      } else {
        break;
      }
    }

    if (word.length() != 0) {
      component.append(sb.toString());
      component.append(getComponent.apply(word.toString()));
      sb.setLength(0);
      return start + word.length();
    } else {
      sb.append(rawDoc.charAt(start));
    }

    return start;
  }

  private static Optional<Pair<String, Object[]>> getTypeDoc(CommandContext<CommandSourceStack> context, final String typeName) {
    TypeBase<?> type = ProgramManager.getTypeForName(typeName);
    if (type == null) {
      context.getSource().sendFailure(
          new TranslatableComponent("mccode.interpreter.error.no_type_for_name", typeName));
      return Optional.empty();
    }
    Optional<String> d = type.getDoc();
    if (d.isEmpty()) {
      context.getSource().sendFailure(
          new TranslatableComponent("commands.program.error.no_doc_for_type", typeName));
      return Optional.empty();
    }
    return Optional.of(new ImmutablePair<>(d.get(), new Object[]{typeName}));
  }

  private static Optional<Pair<String, Object[]>> getPropertyDoc(CommandContext<CommandSourceStack> context, final String prefixedPropertyName) {
    if (!prefixedPropertyName.contains(".")) {
      context.getSource().sendFailure(
          new TranslatableComponent("commands.program.error.invalid_property_name", prefixedPropertyName));
      return Optional.empty();
    }

    String[] parts = prefixedPropertyName.split("\\.", 2);
    String typeName = parts[0];
    String propertyName = parts[1];
    ObjectProperty property = ProgramManager.getTypeForName(typeName).getProperty(propertyName);

    if (property != null) {
      Optional<String> d = property.getDoc();
      if (d.isEmpty()) {
        context.getSource().sendFailure(
            new TranslatableComponent("commands.program.error.no_doc_for_property", typeName, propertyName));
        return Optional.empty();
      }
      return Optional.of(new ImmutablePair<>(d.get(), new Object[]{typeName, propertyName}));
    }

    context.getSource().sendFailure(
        new TranslatableComponent("commands.program.error.no_property_for_type", typeName, propertyName));
    return Optional.empty();
  }

  private static Optional<Pair<String, Object[]>> getMethodDoc(CommandContext<CommandSourceStack> context, final String prefixedMethodName) {
    if (!prefixedMethodName.contains(".")) {
      context.getSource().sendFailure(
          new TranslatableComponent("commands.program.error.invalid_method_name", prefixedMethodName));
      return Optional.empty();
    }

    String[] parts = prefixedMethodName.split("\\.", 2);
    String typeName = parts[0];
    String methodName = parts[1];
    MemberFunction method = ProgramManager.getTypeForName(typeName).getMethod(methodName);

    if (method != null) {
      Optional<String> d = method.getDoc();
      if (d.isEmpty()) {
        context.getSource().sendFailure(
            new TranslatableComponent("commands.program.error.no_doc_for_method", typeName, methodName));
        return Optional.empty();
      }
      return Optional.of(new ImmutablePair<>(d.get(), new Object[]{typeName, methodName}));
    }

    context.getSource().sendFailure(
        new TranslatableComponent("mccode.interpreter.error.no_method_for_type", typeName, methodName));
    return Optional.empty();
  }

  private static Optional<Pair<String, Object[]>> getFunctionDoc(CommandContext<CommandSourceStack> context, final String functionName) {
    BuiltinFunction function = ProgramManager.getBuiltinFunction(functionName);

    if (function != null) {
      Optional<String> d = function.getDoc();
      if (d.isEmpty()) {
        context.getSource().sendFailure(
            new TranslatableComponent("commands.program.error.no_doc_for_function", functionName));
        return Optional.empty();
      }
      return Optional.of(new ImmutablePair<>(d.get(), new Object[]{functionName}));
    }

    context.getSource().sendFailure(
        new TranslatableComponent("commands.program.error.no_function", functionName));
    return Optional.empty();
  }

  public enum DocType {
    type, property, method, function
  }
}
