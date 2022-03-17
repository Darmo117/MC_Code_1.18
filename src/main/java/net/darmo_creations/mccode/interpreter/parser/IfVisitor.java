package net.darmo_creations.mccode.interpreter.parser;

import net.darmo_creations.mccode.interpreter.nodes.Node;
import net.darmo_creations.mccode.interpreter.parser.antlr4.MCCodeBaseVisitor;
import net.darmo_creations.mccode.interpreter.parser.antlr4.MCCodeParser;
import net.darmo_creations.mccode.interpreter.statements.Statement;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility visitor for elseif-else branches of 'if' statement.
 */
public class IfVisitor extends MCCodeBaseVisitor<Pair<Node, List<Statement>>> {
  private final StatementVisitor statementVisitor;
  private final NodeVisitor nodeVisitor;

  public IfVisitor(final StatementVisitor statementVisitor, final NodeVisitor nodeVisitor) {
    this.statementVisitor = statementVisitor;
    this.nodeVisitor = nodeVisitor;
  }

  @Override
  public Pair<Node, List<Statement>> visitElseif(MCCodeParser.ElseifContext ctx) {
    return new ImmutablePair<>(
        this.nodeVisitor.visit(ctx.cond),
        ctx.statement().stream().map(this.statementVisitor::visit).collect(Collectors.toList())
    );
  }

  @Override
  public Pair<Node, List<Statement>> visitElse_(MCCodeParser.Else_Context ctx) {
    return new ImmutablePair<>(null, ctx.statement().stream().map(this.statementVisitor::visit).collect(Collectors.toList()));
  }
}
