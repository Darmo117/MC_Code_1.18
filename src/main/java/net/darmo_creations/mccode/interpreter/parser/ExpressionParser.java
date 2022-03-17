package net.darmo_creations.mccode.interpreter.parser;

import net.darmo_creations.mccode.interpreter.exceptions.SyntaxErrorException;
import net.darmo_creations.mccode.interpreter.nodes.Node;
import net.darmo_creations.mccode.interpreter.parser.antlr4.MCCodeLexer;
import net.darmo_creations.mccode.interpreter.parser.antlr4.MCCodeParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;

/**
 * Parser that returns a {@link Node} instance for the given expression.
 */
public final class ExpressionParser {
  /**
   * Return a {@link Node} instance for the given expression.
   *
   * @param expression The expression to parse.
   * @return The node instance.
   * @throws SyntaxErrorException If a syntax error is encountered.
   */
  public static Node parse(final String expression) throws SyntaxErrorException {
    MCCodeLexer lexer = new MCCodeLexer(CharStreams.fromString(expression));
    MCCodeParser parser = new MCCodeParser(new CommonTokenStream(lexer));
    ErrorListener errorListener = new ErrorListener();
    parser.addErrorListener(errorListener);
    return new NodeVisitor().visit(parser.expression());
  }

  /**
   * Simple error listener to throw syntax errors instead of just logging them.
   */
  private static class ErrorListener implements ANTLRErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
      throw new SyntaxErrorException(line, charPositionInLine, "mccode.interpreter.error.expression_syntax_error", msg);
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
    }
  }

  private ExpressionParser() {
  }
}
