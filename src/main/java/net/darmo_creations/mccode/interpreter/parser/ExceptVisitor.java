package net.darmo_creations.mccode.interpreter.parser;

import net.darmo_creations.mccode.interpreter.parser.antlr4.MCCodeBaseVisitor;
import net.darmo_creations.mccode.interpreter.parser.antlr4.MCCodeParser;
import net.darmo_creations.mccode.interpreter.statements.Statement;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility visitor for 'except' branche of try-except statement.
 */
public class ExceptVisitor extends MCCodeBaseVisitor<Pair<String, List<Statement>>> {
  private final StatementVisitor statementVisitor;

  public ExceptVisitor(final StatementVisitor statementVisitor) {
    this.statementVisitor = statementVisitor;
  }

  @Override
  public Pair<String, List<Statement>> visitExcept(MCCodeParser.ExceptContext ctx) {
    String variable = ctx.IDENT().getText();
    List<Statement> statements = ctx.statement().stream()
        .map(this.statementVisitor::visit)
        .collect(Collectors.toList());
    return new ImmutablePair<>(variable, statements);
  }
}
