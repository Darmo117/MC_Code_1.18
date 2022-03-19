package net.darmo_creations.mccode.interpreter.parser;

import net.darmo_creations.mccode.interpreter.Program;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.exceptions.SyntaxErrorException;
import net.darmo_creations.mccode.interpreter.parser.antlr4.MCCodeBaseVisitor;
import net.darmo_creations.mccode.interpreter.parser.antlr4.MCCodeParser;
import net.darmo_creations.mccode.interpreter.statements.ImportStatement;
import net.darmo_creations.mccode.interpreter.statements.Statement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Visitor for {@link Program} class.
 */
public class ProgramVisitor extends MCCodeBaseVisitor<Program> {
  private final ProgramManager programManager;
  private final String programName;
  private final String[] args;
  private final boolean asModule;

  /**
   * Create a visitor for the given program name.
   *
   * @param programManager Program manager that requests the program instance.
   * @param programName    Program’s name.
   * @param asModule       Whether the program should be parsed as a module.
   * @param args           Optional command arguments for the program.
   */
  public ProgramVisitor(final ProgramManager programManager, final String programName, boolean asModule, final String... args) {
    this.programManager = programManager;
    this.asModule = asModule;
    this.programName = programName;
    this.args = args;
  }

  @Override
  public Program visitModule(MCCodeParser.ModuleContext ctx) {
    Long scheduleDelay = null;
    Long repeatAmount = null;
    if (ctx.SCHED() != null) {
      if (this.asModule) {
        throw new SyntaxErrorException(ctx.start.getLine(), ctx.start.getCharPositionInLine() + 1,
            "mccode.interpreter.error.schedule_in_module", this.programName);
      }
      scheduleDelay = Long.parseLong(ctx.ticks.getText());
      if (ctx.REPEAT() != null) {
        String timesText = ctx.times.getText();
        if ("forever".equals(timesText)) {
          repeatAmount = Long.MAX_VALUE;
        } else {
          repeatAmount = Long.parseLong(timesText);
        }
      }
    }

    StatementVisitor statementVisitor = new StatementVisitor();

    Set<String> modules = new HashSet<>();
    List<Statement> statements = ctx.import_statement().stream().map(tree -> {
      ImportStatement stmt = (ImportStatement) statementVisitor.visit(tree);
      String modulePath = stmt.getModulePath();
      if (modules.contains(modulePath)) {
        throw new SyntaxErrorException(stmt.getLine(), stmt.getColumn(),
            "mccode.interpreter.error.duplicate_import", modulePath);
      }
      modules.add(modulePath);
      return stmt;
    }).collect(Collectors.toList());

    ctx.global_statement().stream().map(statementVisitor::visit).forEach(statements::add);

    Program program;
    if (!this.asModule) {
      program = new Program(this.programName, statements, scheduleDelay, repeatAmount, this.programManager, this.args);
    } else {
      program = new Program(this.programName, statements, this.programManager);
    }
    return program;
  }
}
