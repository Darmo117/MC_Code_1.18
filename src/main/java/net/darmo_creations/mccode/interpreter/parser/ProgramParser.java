package net.darmo_creations.mccode.interpreter.parser;

import net.darmo_creations.mccode.interpreter.Program;
import net.darmo_creations.mccode.interpreter.ProgramManager;
import net.darmo_creations.mccode.interpreter.exceptions.SyntaxErrorException;
import net.darmo_creations.mccode.interpreter.parser.antlr4.MCCodeLexer;
import net.darmo_creations.mccode.interpreter.parser.antlr4.MCCodeParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;
import java.util.regex.Pattern;

/**
 * Parser that returns a {@link Program} instance for the given code.
 */
public final class ProgramParser {
  /**
   * Pattern for variables, functions, methods, properties and types names.
   */
  public static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[a-zA-Z_]\\w*$");

  /**
   * Return a {@link Program} instance for the given code.
   *
   * @param programManager The manager that requests the program.
   * @param programName    Name of the program.
   * @param script         Code of the program.
   * @param asModule       Whether the program should be parsed as a module.
   * @param args           Optional command arguments for the program.
   * @return The program instance.
   * @throws SyntaxErrorException If a syntax error is encountered.
   */
  public static Program parse(final ProgramManager programManager, final String programName, final String script, boolean asModule, final String... args)
      throws SyntaxErrorException {
    MCCodeLexer lexer = new MCCodeLexer(CharStreams.fromString(script));
    MCCodeParser parser = new MCCodeParser(new CommonTokenStream(lexer));
    ErrorListener errorListener = new ErrorListener();
    parser.addErrorListener(errorListener);
    return new ProgramVisitor(programManager, programName, asModule, args).visit(parser.module());
  }

  /**
   * Simple error listener to throw syntax errors instead of just logging them.
   */
  private static class ErrorListener implements ANTLRErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
      throw new SyntaxErrorException(line, charPositionInLine, "mccode.interpreter.error.syntax_error", msg);
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

  private ProgramParser() {
  }
}
