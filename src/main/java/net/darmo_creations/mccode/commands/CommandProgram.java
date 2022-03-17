package net.darmo_creations.mccode.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.darmo_creations.mccode.MCCode;
import net.darmo_creations.mccode.interpreter.*;
import net.darmo_creations.mccode.interpreter.exceptions.EvaluationException;
import net.darmo_creations.mccode.interpreter.exceptions.ProgramStatusException;
import net.darmo_creations.mccode.interpreter.exceptions.SyntaxErrorException;
import net.darmo_creations.mccode.interpreter.nodes.Node;
import net.darmo_creations.mccode.interpreter.parser.ExpressionParser;
import net.darmo_creations.mccode.interpreter.type_wrappers.TypeBase;
import net.darmo_creations.mccode.interpreter.types.BuiltinFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.EnumArgument;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    LiteralArgumentBuilder<CommandSourceStack> loadProgramOption = Commands.literal("load")
        .then(buildLoadProgramBranch(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.create())))
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.create())
            .then(Commands.literal("as")
                .then(buildLoadProgramBranch(Commands.argument(PROGRAM_ALIAS_ARG, StringArgumentType.word())))));
    LiteralArgumentBuilder<CommandSourceStack> runProgramOption = Commands.literal("run")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.create())
            .executes(context -> {
              CommandProgram.runProgram(context);
              return 1;
            }));
    LiteralArgumentBuilder<CommandSourceStack> pauseProgramOption = Commands.literal("pause")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.create())
            .executes(context -> {
              CommandProgram.pauseProgram(context);
              return 1;
            }));
    LiteralArgumentBuilder<CommandSourceStack> resetProgramOption = Commands.literal("reset")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.create())
            .executes(context -> {
              CommandProgram.resetProgram(context);
              return 1;
            }));
    LiteralArgumentBuilder<CommandSourceStack> unloadProgramOption = Commands.literal("unload")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.create())
            .executes(context -> {
              CommandProgram.unloadProgram(context);
              return 1;
            }));
    LiteralArgumentBuilder<CommandSourceStack> getVariableOption = Commands.literal("get")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.create())
            .then(Commands.argument(VARIABLE_NAME_ARG, ProgramVariableNameArgumentType.create())
                .executes(context -> {
                  CommandProgram.getVariableValue(context);
                  return 1;
                })));
    LiteralArgumentBuilder<CommandSourceStack> setVariableOption = Commands.literal("set")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.create())
            .then(Commands.argument(VARIABLE_NAME_ARG, ProgramVariableNameArgumentType.create())
                .then(Commands.argument(VARIABLE_VALUE_ARG, StringArgumentType.word())
                    .executes(context -> {
                      CommandProgram.setVariableValue(context);
                      return 1;
                    }))));
    LiteralArgumentBuilder<CommandSourceStack> deleteVariableOption = Commands.literal("delete")
        .then(Commands.argument(PROGRAM_NAME_ARG, ProgramNameArgumentType.create())
            .then(Commands.argument(VARIABLE_NAME_ARG, ProgramVariableNameArgumentType.create())
                .executes(context -> {
                  CommandProgram.deleteVariable(context);
                  return 1;
                })));
    LiteralArgumentBuilder<CommandSourceStack> listProgramsOption = Commands.literal("list")
        .executes(context -> {
          CommandProgram.listPrograms(context);
          return 1;
        });
    LiteralArgumentBuilder<CommandSourceStack> docOption = Commands.literal("doc")
        .then(Commands.argument(DOC_TYPE_ARG, EnumArgument.enumArgument(DocType.class))
            .then(Commands.argument(ELEMENT_NAME_ARG, ProgramElementNameArgumentType.create())
                .executes(context -> {
                  CommandProgram.showDoc(context);
                  return 1;
                })));
    dispatcher.register(Commands.literal("program")
        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
        .then(loadProgramOption)
        .then(runProgramOption)
        .then(pauseProgramOption)
        .then(resetProgramOption)
        .then(unloadProgramOption)
        .then(getVariableOption)
        .then(setVariableOption)
        .then(deleteVariableOption)
        .then(listProgramsOption)
        .then(docOption));
  }

  private static ArgumentBuilder<CommandSourceStack, ?> buildLoadProgramBranch(ArgumentBuilder<CommandSourceStack, ?> root) {
    return root.executes(context -> {
          CommandProgram.loadProgram(context);
          return 1;
        })
        .then(Commands.argument(PROGRAM_ARGUMENTS_ARG, MessageArgument.message())
            .executes(context -> {
              CommandProgram.loadProgram(context);
              return 1;
            }));
  }

  private static void loadProgram(CommandContext<CommandSourceStack> context) {
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = args[0];
    String alias = null;
    Object[] parsedArgs = new Object[0];
    if (args.length > 1) {
      int argsOffset = 1;
      if (args.length >= 3 && "as".equals(args[1])) {
        alias = args[2];
        argsOffset = 3;
      }
      if (argsOffset < args.length) {
        parsedArgs = this.parseArgs(Arrays.copyOfRange(args, argsOffset, args.length));
      }
    }
    try {
      pm.loadProgram(programName, alias, false, parsedArgs);
    } catch (SyntaxErrorException e) {
      Object[] a = new Object[e.getArgs().length + 3];
      a[0] = programName;
      a[1] = e.getLine();
      a[2] = e.getColumn();
      System.arraycopy(e.getArgs(), 0, a, 3, e.getArgs().length);
      throw new CommandException(e.getTranslationKey(), a);
    } catch (ProgramStatusException e) {
      throw new CommandException(e.getTranslationKey(), e.getProgramName());
    }
    notifyCommandListener(sender, this, "commands.program.feedback.program_loaded", programName);
  }

  private static void unloadProgram(CommandContext<CommandSourceStack> context) {
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = args[0];
    try {
      pm.unloadProgram(programName);
    } catch (ProgramStatusException e) {
      throw new CommandException(e.getTranslationKey(), e.getProgramName());
    }
    notifyCommandListener(sender, this, "commands.program.feedback.program_unloaded", programName);
  }

  private static void resetProgram(CommandContext<CommandSourceStack> context) {
    if (args.length != 1) {
      throw new WrongUsageException(this.getUsage(sender));
    }
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = args[0];
    try {
      pm.resetProgram(programName);
    } catch (ProgramStatusException e) {
      throw new CommandException(e.getTranslationKey(), e.getProgramName());
    }
    notifyCommandListener(sender, this, "commands.program.feedback.program_reset", programName);
  }

  private static void runProgram(CommandContext<CommandSourceStack> context) {
    if (args.length != 1) {
      throw new WrongUsageException(this.getUsage(sender));
    }
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = args[0];
    try {
      pm.runProgram(programName);
    } catch (ProgramStatusException e) {
      throw new CommandException(e.getTranslationKey(), e.getProgramName());
    }
    notifyCommandListener(sender, this, "commands.program.feedback.program_launched", programName);
  }

  private static void pauseProgram(CommandContext<CommandSourceStack> context) {
    if (args.length != 1) {
      throw new WrongUsageException(this.getUsage(sender));
    }
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = args[0];
    try {
      pm.pauseProgram(programName);
    } catch (ProgramStatusException e) {
      throw new CommandException(e.getTranslationKey(), e.getProgramName());
    }
    notifyCommandListener(sender, this, "commands.program.feedback.program_paused", programName);
  }

  private static void listPrograms(CommandContext<CommandSourceStack> context) {
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    List<String> loadedPrograms = pm.getLoadedPrograms();
    if (loadedPrograms.isEmpty()) {
      throw new CommandException("commands.program.error.no_loaded_programs");
    } else {
      notifyCommandListener(sender, this, "commands.program.feedback.loaded_programs",
          String.join(", ", loadedPrograms));
    }
  }

  private static void getVariableValue(CommandContext<CommandSourceStack> context) {
    if (args.length != 2) {
      throw new WrongUsageException(this.getUsage(sender));
    }
    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = args[0];
    Optional<Program> program = pm.getProgram(programName);
    if (program.isPresent()) {
      String variableName = args[1];
      Object value;
      try {
        value = program.get().getScope().getVariable(variableName, true);
      } catch (EvaluationException e) {
        throw new CommandException(e.getTranslationKey(), e.getArgs());
      }
      notifyCommandListener(sender, this, "commands.program.feedback.get_variable_value", variableName, value);
    } else {
      throw new CommandException("mccode.interpreter.error.program_not_found", programName);
    }
  }

  private static void setVariableValue(CommandContext<CommandSourceStack> context) {
    if (args.length < 3) {
      throw new WrongUsageException(this.getUsage(sender));
    }

    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = args[0];
    Optional<Program> program = pm.getProgram(programName);
    if (program.isPresent()) {
      String variableName = args[1];
      String expression = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
      Node node;
      try {
        node = ExpressionParser.parse(expression);
      } catch (SyntaxErrorException e) {
        throw new CommandException(e.getTranslationKey(), e.getArgs());
      }

      Object value;
      try {
        value = node.evaluate(program.get().getScope());
        program.get().getScope().setVariable(variableName, value, true);
      } catch (EvaluationException e) {
        throw new CommandException(e.getTranslationKey(), e.getArgs());
      }
      notifyCommandListener(sender, this, "commands.program.feedback.set_variable_value", variableName, node);
    } else {
      throw new CommandException("mccode.interpreter.error.program_not_found", programName);
    }
  }

  private static void deleteVariable(CommandContext<CommandSourceStack> context) {
    if (args.length != 2) {
      throw new WrongUsageException(this.getUsage(sender));
    }

    ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
    String programName = args[0];
    Optional<Program> program = pm.getProgram(programName);
    if (program.isPresent()) {
      String variableName = args[1];
      try {
        program.get().getScope().deleteVariable(variableName, true);
      } catch (EvaluationException e) {
        throw new CommandException(e.getTranslationKey(), e.getArgs());
      }
    } else {
      throw new CommandException("mccode.interpreter.error.program_not_found", programName);
    }
  }

  private static void showDoc(CommandContext<CommandSourceStack> context) {
    if (args.length != 2) {
      throw new WrongUsageException(this.getUsage(sender));
    }
    DocType docType = DocType.fromString(args[0]).orElseThrow(() -> new WrongUsageException(this.getUsage(sender)));
    String name = args[1];

    if (docType == null) {
      throw new WrongUsageException(this.getUsage(sender));
    }

    String doc;
    Object[] translationArgs;

    switch (docType) {
      case TYPE: {
        TypeBase<?> type = ProgramManager.getTypeForName(name);
        if (type == null) {
          throw new CommandException("mccode.interpreter.error.no_type_for_name", name);
        }
        doc = type.getDoc().orElseThrow(() -> new CommandException("commands.program.error.no_doc_for_type", name));
        translationArgs = new Object[]{name};
        break;
      }

      case PROPERTY: {
        if (!name.contains(".")) {
          throw new WrongUsageException(this.getUsage(sender));
        }
        String[] parts = name.split("\\.", 2);
        String typeName = parts[0];
        String propertyName = parts[1];
        ObjectProperty property = ProgramManager.getTypeForName(typeName).getProperty(propertyName);
        if (property != null) {
          doc = property.getDoc().orElseThrow(() -> new CommandException("commands.program.error.no_doc_for_property", typeName, propertyName));
          translationArgs = new Object[]{typeName, propertyName};
        } else {
          throw new CommandException("mccode.interpreter.error.no_property_for_type", typeName, propertyName);
        }
        break;
      }

      case METHOD: {
        if (!name.contains(".")) {
          throw new WrongUsageException(this.getUsage(sender));
        }
        String[] parts = name.split("\\.", 2);
        String typeName = parts[0];
        String methodName = parts[1];
        MemberFunction method = ProgramManager.getTypeForName(typeName).getMethod(methodName);
        if (method != null) {
          doc = method.getDoc().orElseThrow(() -> new CommandException("commands.program.error.no_doc_for_method", typeName, methodName));
          translationArgs = new Object[]{typeName, methodName};
        } else {
          throw new CommandException("mccode.interpreter.error.no_method_for_type", typeName, methodName);
        }
        break;
      }

      case FUNCTION:
        BuiltinFunction function = ProgramManager.getBuiltinFunction(name);
        if (function != null) {
          doc = function.getDoc().orElseThrow(() -> new CommandException("commands.program.error.no_doc_for_function", name));
          translationArgs = new Object[]{name};
        } else {
          throw new CommandException("commands.program.error.no_function", name);
        }
        break;

      default:
        throw new WrongUsageException(this.getUsage(sender));
    }

    sender.sendMessage(new TextComponentTranslation("commands.program.feedback.doc_" + docType.name().toLowerCase(), translationArgs)
        .setStyle(new Style().setColor(TextFormatting.GREEN)));
    sender.sendMessage(new TextComponentString(doc));
  }

  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
    if (args.length == 1) {
      return getListOfStringsMatchingLastWord(args, Arrays.stream(Option.values()).map(o -> o.name().toLowerCase()).collect(Collectors.toList()));

    } else if (args.length == 2) {
      Optional<Option> option = Option.fromString(args[0]);
      if (option.isPresent()) {
        Option opt = option.get();
        if (opt == Option.DOC) {
          return getListOfStringsMatchingLastWord(args, Arrays.stream(DocType.values()).map(t -> t.name().toLowerCase()).collect(Collectors.toList()));
        } else if (opt != Option.LIST) {
          ProgramManager pm = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel());
          if (opt == Option.LOAD) {
            File dir = pm.getProgramsDirectory();
            //noinspection ConstantConditions
            List<String> names = Arrays.stream(dir.listFiles(f -> f.getName().endsWith(".mccode")))
                .map(f -> f.getName().substring(0, f.getName().indexOf('.')))
                .collect(Collectors.toList());
            return getListOfStringsMatchingLastWord(args, names);
          } else {
            return getListOfStringsMatchingLastWord(args, pm.getLoadedPrograms());
          }
        }
        return Collections.emptyList();
      }

    } else if (args.length == 3) {
      Optional<Option> option = Option.fromString(args[0]);
      if (option.isPresent()) {
        Option opt = option.get();
        if (opt == Option.DOC) {
          Optional<DocType> docType = DocType.fromString(args[1]);
          if (docType.isPresent()) {
            DocType t = docType.get();
            switch (t) {
              case TYPE:
                return getListOfStringsMatchingLastWord(
                    args,
                    ProgramManager.getTypes().stream()
                        .map(TypeBase::getName)
                        .sorted()
                        .collect(Collectors.toList())
                );
              case PROPERTY:
                return getListOfStringsMatchingLastWord(
                    args,
                    ProgramManager.getTypes().stream()
                        .flatMap(type -> type.getProperties().keySet().stream().map(pName -> type.getName() + "." + pName))
                        .sorted()
                        .collect(Collectors.toList())
                );
              case METHOD:
                return getListOfStringsMatchingLastWord(
                    args,
                    ProgramManager.getTypes().stream()
                        .flatMap(type -> type.getMethods().keySet().stream().map(mName -> type.getName() + "." + mName))
                        .sorted()
                        .collect(Collectors.toList())
                );
              case FUNCTION:
                return getListOfStringsMatchingLastWord(
                    args,
                    ProgramManager.getBuiltinFunctions().stream()
                        .map(BuiltinFunction::getName)
                        .sorted()
                        .collect(Collectors.toList())
                );
            }
          }

        } else if (opt == Option.LOAD) {
          return getListOfStringsMatchingLastWord(args, "as");

        } else if (opt == Option.GET_VAR || opt == Option.SET_VAR || opt == Option.DELETE_VAR) {
          Optional<Program> program = MCCode.INSTANCE.PROGRAM_MANAGERS.get(context.getSource().getLevel()).getProgram(args[1]);
          if (program.isPresent()) {
            Predicate<Variable> filter;
            if (opt == Option.GET_VAR) {
              filter = Variable::isPubliclyVisible;
            } else if (opt == Option.SET_VAR) {
              filter = v -> v.isPubliclyVisible() && v.isEditableFromOutside() && !v.isConstant();
            } else {
              filter = v -> v.isPubliclyVisible() && v.isDeletable();
            }
            return getListOfStringsMatchingLastWord(
                args,
                program.get().getScope().getVariables().values().stream()
                    .filter(filter)
                    .map(Variable::getName)
                    .collect(Collectors.toList())
            );
          }
        }
      }
    }

    return Collections.emptyList();
  }

  private enum DocType {
    TYPE, PROPERTY, METHOD, FUNCTION;

    public static Optional<DocType> fromString(final String s) {
      for (DocType value : values()) {
        if (value.name().toLowerCase().equals(s)) {
          return Optional.of(value);
        }
      }
      return Optional.empty();
    }
  }
}
