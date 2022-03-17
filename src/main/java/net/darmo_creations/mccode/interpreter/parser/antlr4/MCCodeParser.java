// Generated from /home/damien/IdeaProjects/MC_Code/grammar/MCCode.g4 by ANTLR 4.9.2
package net.darmo_creations.mccode.interpreter.parser.antlr4;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MCCodeParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		WS=1, COMMENT=2, LPAREN=3, RPAREN=4, LBRACK=5, RBRACK=6, LCURL=7, RCURL=8, 
		COMMA=9, COLON=10, SEMIC=11, DOT=12, ASSIGN=13, PLUSA=14, MINUSA=15, MULA=16, 
		DIVA=17, INTDIVA=18, MODA=19, POWERA=20, PLUS=21, MINUS=22, MUL=23, DIV=24, 
		INTDIV=25, MOD=26, POWER=27, EQUAL=28, NEQUAL=29, GT=30, GE=31, LT=32, 
		LE=33, IN=34, NOT=35, AND=36, OR=37, IMPORT=38, AS=39, SCHED=40, VAR=41, 
		CONST=42, EDITABLE=43, PUBLIC=44, FUNC=45, RETURN=46, IF=47, THEN=48, 
		ELSE=49, ELIF=50, WHILE=51, FOR=52, DO=53, END=54, DELETE=55, BREAK=56, 
		CONTINUE=57, WAIT=58, REPEAT=59, FOREVER=60, TRY=61, EXCEPT=62, NULL=63, 
		TRUE=64, FALSE=65, INT=66, FLOAT=67, STRING=68, IDENT=69, CMDARG=70;
	public static final int
		RULE_module = 0, RULE_expression = 1, RULE_import_statement = 2, RULE_global_statement = 3, 
		RULE_statement = 4, RULE_elseif = 5, RULE_else_ = 6, RULE_except = 7, 
		RULE_expr = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"module", "expression", "import_statement", "global_statement", "statement", 
			"elseif", "else_", "except", "expr"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'('", "')'", "'['", "']'", "'{'", "'}'", "','", "':'", 
			"';'", "'.'", "':='", "'+='", "'-='", "'*='", "'/='", "'//='", "'%='", 
			"'^='", "'+'", "'-'", "'*'", "'/'", "'//'", "'%'", "'^'", "'=='", "'!='", 
			"'>'", "'>='", "'<'", "'<='", "'in'", "'not'", "'and'", "'or'", "'import'", 
			"'as'", "'schedule'", "'var'", "'const'", "'editable'", "'public'", "'function'", 
			"'return'", "'if'", "'then'", "'else'", "'elseif'", "'while'", "'for'", 
			"'do'", "'end'", "'del'", "'break'", "'continue'", "'wait'", "'repeat'", 
			"'forever'", "'try'", "'except'", "'null'", "'true'", "'false'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "WS", "COMMENT", "LPAREN", "RPAREN", "LBRACK", "RBRACK", "LCURL", 
			"RCURL", "COMMA", "COLON", "SEMIC", "DOT", "ASSIGN", "PLUSA", "MINUSA", 
			"MULA", "DIVA", "INTDIVA", "MODA", "POWERA", "PLUS", "MINUS", "MUL", 
			"DIV", "INTDIV", "MOD", "POWER", "EQUAL", "NEQUAL", "GT", "GE", "LT", 
			"LE", "IN", "NOT", "AND", "OR", "IMPORT", "AS", "SCHED", "VAR", "CONST", 
			"EDITABLE", "PUBLIC", "FUNC", "RETURN", "IF", "THEN", "ELSE", "ELIF", 
			"WHILE", "FOR", "DO", "END", "DELETE", "BREAK", "CONTINUE", "WAIT", "REPEAT", 
			"FOREVER", "TRY", "EXCEPT", "NULL", "TRUE", "FALSE", "INT", "FLOAT", 
			"STRING", "IDENT", "CMDARG"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "MCCode.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MCCodeParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ModuleContext extends ParserRuleContext {
		public Token ticks;
		public Token times;
		public TerminalNode EOF() { return getToken(MCCodeParser.EOF, 0); }
		public TerminalNode SCHED() { return getToken(MCCodeParser.SCHED, 0); }
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public List<Import_statementContext> import_statement() {
			return getRuleContexts(Import_statementContext.class);
		}
		public Import_statementContext import_statement(int i) {
			return getRuleContext(Import_statementContext.class,i);
		}
		public List<Global_statementContext> global_statement() {
			return getRuleContexts(Global_statementContext.class);
		}
		public Global_statementContext global_statement(int i) {
			return getRuleContext(Global_statementContext.class,i);
		}
		public List<TerminalNode> INT() { return getTokens(MCCodeParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(MCCodeParser.INT, i);
		}
		public TerminalNode REPEAT() { return getToken(MCCodeParser.REPEAT, 0); }
		public TerminalNode FOREVER() { return getToken(MCCodeParser.FOREVER, 0); }
		public ModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterModule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitModule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitModule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuleContext module() throws RecognitionException {
		ModuleContext _localctx = new ModuleContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_module);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(25);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SCHED) {
				{
				setState(18);
				match(SCHED);
				setState(19);
				((ModuleContext)_localctx).ticks = match(INT);
				setState(22);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==REPEAT) {
					{
					setState(20);
					match(REPEAT);
					setState(21);
					((ModuleContext)_localctx).times = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==FOREVER || _la==INT) ) {
						((ModuleContext)_localctx).times = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
				}

				setState(24);
				match(SEMIC);
				}
			}

			setState(30);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IMPORT) {
				{
				{
				setState(27);
				import_statement();
				}
				}
				setState(32);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(36);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << VAR) | (1L << CONST) | (1L << PUBLIC) | (1L << FUNC) | (1L << RETURN) | (1L << IF) | (1L << WHILE) | (1L << FOR) | (1L << DELETE) | (1L << BREAK) | (1L << CONTINUE) | (1L << WAIT) | (1L << TRY) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
				{
				{
				setState(33);
				global_statement();
				}
				}
				setState(38);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(39);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExpressionContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode EOF() { return getToken(MCCodeParser.EOF, 0); }
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			expr(0);
			setState(42);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Import_statementContext extends ParserRuleContext {
		public Token alias;
		public TerminalNode IMPORT() { return getToken(MCCodeParser.IMPORT, 0); }
		public List<TerminalNode> IDENT() { return getTokens(MCCodeParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(MCCodeParser.IDENT, i);
		}
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public List<TerminalNode> DOT() { return getTokens(MCCodeParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(MCCodeParser.DOT, i);
		}
		public TerminalNode AS() { return getToken(MCCodeParser.AS, 0); }
		public Import_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_import_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterImport_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitImport_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitImport_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Import_statementContext import_statement() throws RecognitionException {
		Import_statementContext _localctx = new Import_statementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_import_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(IMPORT);
			setState(45);
			match(IDENT);
			setState(50);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOT) {
				{
				{
				setState(46);
				match(DOT);
				setState(47);
				match(IDENT);
				}
				}
				setState(52);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS) {
				{
				setState(53);
				match(AS);
				setState(54);
				((Import_statementContext)_localctx).alias = match(IDENT);
				}
			}

			setState(57);
			match(SEMIC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Global_statementContext extends ParserRuleContext {
		public Global_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_global_statement; }
	 
		public Global_statementContext() { }
		public void copyFrom(Global_statementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DefineFunctionStatementContext extends Global_statementContext {
		public Token name;
		public TerminalNode FUNC() { return getToken(MCCodeParser.FUNC, 0); }
		public TerminalNode LPAREN() { return getToken(MCCodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MCCodeParser.RPAREN, 0); }
		public TerminalNode END() { return getToken(MCCodeParser.END, 0); }
		public List<TerminalNode> IDENT() { return getTokens(MCCodeParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(MCCodeParser.IDENT, i);
		}
		public TerminalNode PUBLIC() { return getToken(MCCodeParser.PUBLIC, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MCCodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MCCodeParser.COMMA, i);
		}
		public DefineFunctionStatementContext(Global_statementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterDefineFunctionStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitDefineFunctionStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitDefineFunctionStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DeclareGlobalVariableContext extends Global_statementContext {
		public Token name;
		public ExprContext value;
		public TerminalNode PUBLIC() { return getToken(MCCodeParser.PUBLIC, 0); }
		public TerminalNode VAR() { return getToken(MCCodeParser.VAR, 0); }
		public TerminalNode ASSIGN() { return getToken(MCCodeParser.ASSIGN, 0); }
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public TerminalNode IDENT() { return getToken(MCCodeParser.IDENT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode EDITABLE() { return getToken(MCCodeParser.EDITABLE, 0); }
		public DeclareGlobalVariableContext(Global_statementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterDeclareGlobalVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitDeclareGlobalVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitDeclareGlobalVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DeclareGlobalConstantContext extends Global_statementContext {
		public Token name;
		public ExprContext value;
		public TerminalNode PUBLIC() { return getToken(MCCodeParser.PUBLIC, 0); }
		public TerminalNode CONST() { return getToken(MCCodeParser.CONST, 0); }
		public TerminalNode ASSIGN() { return getToken(MCCodeParser.ASSIGN, 0); }
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public TerminalNode IDENT() { return getToken(MCCodeParser.IDENT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DeclareGlobalConstantContext(Global_statementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterDeclareGlobalConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitDeclareGlobalConstant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitDeclareGlobalConstant(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtContext extends Global_statementContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public StmtContext(Global_statementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Global_statementContext global_statement() throws RecognitionException {
		Global_statementContext _localctx = new Global_statementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_global_statement);
		int _la;
		try {
			int _alt;
			setState(104);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				_localctx = new DeclareGlobalVariableContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(59);
				match(PUBLIC);
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EDITABLE) {
					{
					setState(60);
					match(EDITABLE);
					}
				}

				setState(63);
				match(VAR);
				setState(64);
				((DeclareGlobalVariableContext)_localctx).name = match(IDENT);
				setState(65);
				match(ASSIGN);
				setState(66);
				((DeclareGlobalVariableContext)_localctx).value = expr(0);
				setState(67);
				match(SEMIC);
				}
				break;
			case 2:
				_localctx = new DeclareGlobalConstantContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(69);
				match(PUBLIC);
				setState(70);
				match(CONST);
				setState(71);
				((DeclareGlobalConstantContext)_localctx).name = match(IDENT);
				setState(72);
				match(ASSIGN);
				setState(73);
				((DeclareGlobalConstantContext)_localctx).value = expr(0);
				setState(74);
				match(SEMIC);
				}
				break;
			case 3:
				_localctx = new DefineFunctionStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(77);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==PUBLIC) {
					{
					setState(76);
					match(PUBLIC);
					}
				}

				setState(79);
				match(FUNC);
				setState(80);
				((DefineFunctionStatementContext)_localctx).name = match(IDENT);
				setState(81);
				match(LPAREN);
				setState(93);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==IDENT) {
					{
					setState(82);
					match(IDENT);
					setState(87);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
					while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(83);
							match(COMMA);
							setState(84);
							match(IDENT);
							}
							} 
						}
						setState(89);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
					}
					setState(91);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(90);
						match(COMMA);
						}
					}

					}
				}

				setState(95);
				match(RPAREN);
				setState(99);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << VAR) | (1L << CONST) | (1L << RETURN) | (1L << IF) | (1L << WHILE) | (1L << FOR) | (1L << DELETE) | (1L << BREAK) | (1L << CONTINUE) | (1L << WAIT) | (1L << TRY) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
					{
					{
					setState(96);
					statement();
					}
					}
					setState(101);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(102);
				match(END);
				}
				break;
			case 4:
				_localctx = new StmtContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(103);
				statement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SetItemStatementContext extends StatementContext {
		public ExprContext target;
		public ExprContext key;
		public Token operator;
		public ExprContext value;
		public TerminalNode LBRACK() { return getToken(MCCodeParser.LBRACK, 0); }
		public TerminalNode RBRACK() { return getToken(MCCodeParser.RBRACK, 0); }
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode ASSIGN() { return getToken(MCCodeParser.ASSIGN, 0); }
		public TerminalNode PLUSA() { return getToken(MCCodeParser.PLUSA, 0); }
		public TerminalNode MINUSA() { return getToken(MCCodeParser.MINUSA, 0); }
		public TerminalNode MULA() { return getToken(MCCodeParser.MULA, 0); }
		public TerminalNode DIVA() { return getToken(MCCodeParser.DIVA, 0); }
		public TerminalNode INTDIVA() { return getToken(MCCodeParser.INTDIVA, 0); }
		public TerminalNode MODA() { return getToken(MCCodeParser.MODA, 0); }
		public TerminalNode POWERA() { return getToken(MCCodeParser.POWERA, 0); }
		public SetItemStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterSetItemStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitSetItemStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitSetItemStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DeleteItemStatementContext extends StatementContext {
		public ExprContext target;
		public ExprContext key;
		public TerminalNode DELETE() { return getToken(MCCodeParser.DELETE, 0); }
		public TerminalNode LBRACK() { return getToken(MCCodeParser.LBRACK, 0); }
		public TerminalNode RBRACK() { return getToken(MCCodeParser.RBRACK, 0); }
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public DeleteItemStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterDeleteItemStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitDeleteItemStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitDeleteItemStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhileLoopStatementContext extends StatementContext {
		public ExprContext cond;
		public TerminalNode WHILE() { return getToken(MCCodeParser.WHILE, 0); }
		public TerminalNode DO() { return getToken(MCCodeParser.DO, 0); }
		public TerminalNode END() { return getToken(MCCodeParser.END, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public WhileLoopStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterWhileLoopStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitWhileLoopStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitWhileLoopStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DeclareVariableStatementContext extends StatementContext {
		public Token name;
		public ExprContext value;
		public TerminalNode ASSIGN() { return getToken(MCCodeParser.ASSIGN, 0); }
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public TerminalNode VAR() { return getToken(MCCodeParser.VAR, 0); }
		public TerminalNode CONST() { return getToken(MCCodeParser.CONST, 0); }
		public TerminalNode IDENT() { return getToken(MCCodeParser.IDENT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DeclareVariableStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterDeclareVariableStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitDeclareVariableStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitDeclareVariableStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ForLoopStatementContext extends StatementContext {
		public Token variable;
		public ExprContext range;
		public TerminalNode FOR() { return getToken(MCCodeParser.FOR, 0); }
		public TerminalNode IN() { return getToken(MCCodeParser.IN, 0); }
		public TerminalNode DO() { return getToken(MCCodeParser.DO, 0); }
		public TerminalNode END() { return getToken(MCCodeParser.END, 0); }
		public TerminalNode IDENT() { return getToken(MCCodeParser.IDENT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ForLoopStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterForLoopStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitForLoopStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitForLoopStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VariableAssignmentStatementContext extends StatementContext {
		public Token name;
		public Token operator;
		public ExprContext value;
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public TerminalNode IDENT() { return getToken(MCCodeParser.IDENT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(MCCodeParser.ASSIGN, 0); }
		public TerminalNode PLUSA() { return getToken(MCCodeParser.PLUSA, 0); }
		public TerminalNode MINUSA() { return getToken(MCCodeParser.MINUSA, 0); }
		public TerminalNode MULA() { return getToken(MCCodeParser.MULA, 0); }
		public TerminalNode DIVA() { return getToken(MCCodeParser.DIVA, 0); }
		public TerminalNode INTDIVA() { return getToken(MCCodeParser.INTDIVA, 0); }
		public TerminalNode MODA() { return getToken(MCCodeParser.MODA, 0); }
		public TerminalNode POWERA() { return getToken(MCCodeParser.POWERA, 0); }
		public VariableAssignmentStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterVariableAssignmentStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitVariableAssignmentStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitVariableAssignmentStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DeleteStatementContext extends StatementContext {
		public Token name;
		public TerminalNode DELETE() { return getToken(MCCodeParser.DELETE, 0); }
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public TerminalNode IDENT() { return getToken(MCCodeParser.IDENT, 0); }
		public DeleteStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterDeleteStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitDeleteStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitDeleteStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WaitStatementContext extends StatementContext {
		public TerminalNode WAIT() { return getToken(MCCodeParser.WAIT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public WaitStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterWaitStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitWaitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitWaitStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BreakStatementContext extends StatementContext {
		public TerminalNode BREAK() { return getToken(MCCodeParser.BREAK, 0); }
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public BreakStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterBreakStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitBreakStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitBreakStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IfStatementContext extends StatementContext {
		public ExprContext cond;
		public TerminalNode IF() { return getToken(MCCodeParser.IF, 0); }
		public TerminalNode THEN() { return getToken(MCCodeParser.THEN, 0); }
		public TerminalNode END() { return getToken(MCCodeParser.END, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public List<ElseifContext> elseif() {
			return getRuleContexts(ElseifContext.class);
		}
		public ElseifContext elseif(int i) {
			return getRuleContext(ElseifContext.class,i);
		}
		public Else_Context else_() {
			return getRuleContext(Else_Context.class,0);
		}
		public IfStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterIfStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitIfStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitIfStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TryExceptStatementContext extends StatementContext {
		public TerminalNode TRY() { return getToken(MCCodeParser.TRY, 0); }
		public ExceptContext except() {
			return getRuleContext(ExceptContext.class,0);
		}
		public TerminalNode END() { return getToken(MCCodeParser.END, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public TryExceptStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterTryExceptStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitTryExceptStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitTryExceptStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SetPropertyStatementContext extends StatementContext {
		public ExprContext target;
		public Token name;
		public Token operator;
		public ExprContext value;
		public TerminalNode DOT() { return getToken(MCCodeParser.DOT, 0); }
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode IDENT() { return getToken(MCCodeParser.IDENT, 0); }
		public TerminalNode ASSIGN() { return getToken(MCCodeParser.ASSIGN, 0); }
		public TerminalNode PLUSA() { return getToken(MCCodeParser.PLUSA, 0); }
		public TerminalNode MINUSA() { return getToken(MCCodeParser.MINUSA, 0); }
		public TerminalNode MULA() { return getToken(MCCodeParser.MULA, 0); }
		public TerminalNode DIVA() { return getToken(MCCodeParser.DIVA, 0); }
		public TerminalNode INTDIVA() { return getToken(MCCodeParser.INTDIVA, 0); }
		public TerminalNode MODA() { return getToken(MCCodeParser.MODA, 0); }
		public TerminalNode POWERA() { return getToken(MCCodeParser.POWERA, 0); }
		public SetPropertyStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterSetPropertyStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitSetPropertyStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitSetPropertyStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ExpressionStatementContext extends StatementContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public ExpressionStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterExpressionStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitExpressionStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitExpressionStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ReturnStatementContext extends StatementContext {
		public ExprContext returned;
		public TerminalNode RETURN() { return getToken(MCCodeParser.RETURN, 0); }
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ReturnStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterReturnStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitReturnStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitReturnStatement(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ContinueStatementContext extends StatementContext {
		public TerminalNode CONTINUE() { return getToken(MCCodeParser.CONTINUE, 0); }
		public TerminalNode SEMIC() { return getToken(MCCodeParser.SEMIC, 0); }
		public ContinueStatementContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterContinueStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitContinueStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitContinueStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_statement);
		int _la;
		try {
			setState(212);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				_localctx = new DeclareVariableStatementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(106);
				_la = _input.LA(1);
				if ( !(_la==VAR || _la==CONST) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(107);
				((DeclareVariableStatementContext)_localctx).name = match(IDENT);
				setState(108);
				match(ASSIGN);
				setState(109);
				((DeclareVariableStatementContext)_localctx).value = expr(0);
				setState(110);
				match(SEMIC);
				}
				break;
			case 2:
				_localctx = new DeleteStatementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(112);
				match(DELETE);
				setState(113);
				((DeleteStatementContext)_localctx).name = match(IDENT);
				setState(114);
				match(SEMIC);
				}
				break;
			case 3:
				_localctx = new DeleteItemStatementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(115);
				match(DELETE);
				setState(116);
				((DeleteItemStatementContext)_localctx).target = expr(0);
				setState(117);
				match(LBRACK);
				setState(118);
				((DeleteItemStatementContext)_localctx).key = expr(0);
				setState(119);
				match(RBRACK);
				setState(120);
				match(SEMIC);
				}
				break;
			case 4:
				_localctx = new IfStatementContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(122);
				match(IF);
				setState(123);
				((IfStatementContext)_localctx).cond = expr(0);
				setState(124);
				match(THEN);
				setState(128);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << VAR) | (1L << CONST) | (1L << RETURN) | (1L << IF) | (1L << WHILE) | (1L << FOR) | (1L << DELETE) | (1L << BREAK) | (1L << CONTINUE) | (1L << WAIT) | (1L << TRY) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
					{
					{
					setState(125);
					statement();
					}
					}
					setState(130);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==ELIF) {
					{
					{
					setState(131);
					elseif();
					}
					}
					setState(136);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(138);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(137);
					else_();
					}
				}

				setState(140);
				match(END);
				}
				break;
			case 5:
				_localctx = new WhileLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(142);
				match(WHILE);
				setState(143);
				((WhileLoopStatementContext)_localctx).cond = expr(0);
				setState(144);
				match(DO);
				setState(148);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << VAR) | (1L << CONST) | (1L << RETURN) | (1L << IF) | (1L << WHILE) | (1L << FOR) | (1L << DELETE) | (1L << BREAK) | (1L << CONTINUE) | (1L << WAIT) | (1L << TRY) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
					{
					{
					setState(145);
					statement();
					}
					}
					setState(150);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(151);
				match(END);
				}
				break;
			case 6:
				_localctx = new ForLoopStatementContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(153);
				match(FOR);
				setState(154);
				((ForLoopStatementContext)_localctx).variable = match(IDENT);
				setState(155);
				match(IN);
				setState(156);
				((ForLoopStatementContext)_localctx).range = expr(0);
				setState(157);
				match(DO);
				setState(161);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << VAR) | (1L << CONST) | (1L << RETURN) | (1L << IF) | (1L << WHILE) | (1L << FOR) | (1L << DELETE) | (1L << BREAK) | (1L << CONTINUE) | (1L << WAIT) | (1L << TRY) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
					{
					{
					setState(158);
					statement();
					}
					}
					setState(163);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(164);
				match(END);
				}
				break;
			case 7:
				_localctx = new TryExceptStatementContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(166);
				match(TRY);
				setState(170);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << VAR) | (1L << CONST) | (1L << RETURN) | (1L << IF) | (1L << WHILE) | (1L << FOR) | (1L << DELETE) | (1L << BREAK) | (1L << CONTINUE) | (1L << WAIT) | (1L << TRY) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
					{
					{
					setState(167);
					statement();
					}
					}
					setState(172);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(173);
				except();
				setState(174);
				match(END);
				}
				break;
			case 8:
				_localctx = new WaitStatementContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(176);
				match(WAIT);
				setState(177);
				expr(0);
				setState(178);
				match(SEMIC);
				}
				break;
			case 9:
				_localctx = new BreakStatementContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(180);
				match(BREAK);
				setState(181);
				match(SEMIC);
				}
				break;
			case 10:
				_localctx = new ContinueStatementContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(182);
				match(CONTINUE);
				setState(183);
				match(SEMIC);
				}
				break;
			case 11:
				_localctx = new ReturnStatementContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(184);
				match(RETURN);
				setState(186);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
					{
					setState(185);
					((ReturnStatementContext)_localctx).returned = expr(0);
					}
				}

				setState(188);
				match(SEMIC);
				}
				break;
			case 12:
				_localctx = new VariableAssignmentStatementContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(189);
				((VariableAssignmentStatementContext)_localctx).name = match(IDENT);
				setState(190);
				((VariableAssignmentStatementContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASSIGN) | (1L << PLUSA) | (1L << MINUSA) | (1L << MULA) | (1L << DIVA) | (1L << INTDIVA) | (1L << MODA) | (1L << POWERA))) != 0)) ) {
					((VariableAssignmentStatementContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(191);
				((VariableAssignmentStatementContext)_localctx).value = expr(0);
				setState(192);
				match(SEMIC);
				}
				break;
			case 13:
				_localctx = new SetItemStatementContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(194);
				((SetItemStatementContext)_localctx).target = expr(0);
				setState(195);
				match(LBRACK);
				setState(196);
				((SetItemStatementContext)_localctx).key = expr(0);
				setState(197);
				match(RBRACK);
				setState(198);
				((SetItemStatementContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASSIGN) | (1L << PLUSA) | (1L << MINUSA) | (1L << MULA) | (1L << DIVA) | (1L << INTDIVA) | (1L << MODA) | (1L << POWERA))) != 0)) ) {
					((SetItemStatementContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(199);
				((SetItemStatementContext)_localctx).value = expr(0);
				setState(200);
				match(SEMIC);
				}
				break;
			case 14:
				_localctx = new SetPropertyStatementContext(_localctx);
				enterOuterAlt(_localctx, 14);
				{
				setState(202);
				((SetPropertyStatementContext)_localctx).target = expr(0);
				setState(203);
				match(DOT);
				setState(204);
				((SetPropertyStatementContext)_localctx).name = match(IDENT);
				setState(205);
				((SetPropertyStatementContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ASSIGN) | (1L << PLUSA) | (1L << MINUSA) | (1L << MULA) | (1L << DIVA) | (1L << INTDIVA) | (1L << MODA) | (1L << POWERA))) != 0)) ) {
					((SetPropertyStatementContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(206);
				((SetPropertyStatementContext)_localctx).value = expr(0);
				setState(207);
				match(SEMIC);
				}
				break;
			case 15:
				_localctx = new ExpressionStatementContext(_localctx);
				enterOuterAlt(_localctx, 15);
				{
				setState(209);
				expr(0);
				setState(210);
				match(SEMIC);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ElseifContext extends ParserRuleContext {
		public ExprContext cond;
		public TerminalNode ELIF() { return getToken(MCCodeParser.ELIF, 0); }
		public TerminalNode THEN() { return getToken(MCCodeParser.THEN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ElseifContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseif; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterElseif(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitElseif(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitElseif(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElseifContext elseif() throws RecognitionException {
		ElseifContext _localctx = new ElseifContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_elseif);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			match(ELIF);
			setState(215);
			((ElseifContext)_localctx).cond = expr(0);
			setState(216);
			match(THEN);
			setState(220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << VAR) | (1L << CONST) | (1L << RETURN) | (1L << IF) | (1L << WHILE) | (1L << FOR) | (1L << DELETE) | (1L << BREAK) | (1L << CONTINUE) | (1L << WAIT) | (1L << TRY) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
				{
				{
				setState(217);
				statement();
				}
				}
				setState(222);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Else_Context extends ParserRuleContext {
		public TerminalNode ELSE() { return getToken(MCCodeParser.ELSE, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public Else_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_else_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterElse_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitElse_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitElse_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Else_Context else_() throws RecognitionException {
		Else_Context _localctx = new Else_Context(_ctx, getState());
		enterRule(_localctx, 12, RULE_else_);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			match(ELSE);
			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << VAR) | (1L << CONST) | (1L << RETURN) | (1L << IF) | (1L << WHILE) | (1L << FOR) | (1L << DELETE) | (1L << BREAK) | (1L << CONTINUE) | (1L << WAIT) | (1L << TRY) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
				{
				{
				setState(224);
				statement();
				}
				}
				setState(229);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExceptContext extends ParserRuleContext {
		public TerminalNode EXCEPT() { return getToken(MCCodeParser.EXCEPT, 0); }
		public TerminalNode IDENT() { return getToken(MCCodeParser.IDENT, 0); }
		public TerminalNode THEN() { return getToken(MCCodeParser.THEN, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ExceptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_except; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterExcept(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitExcept(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitExcept(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExceptContext except() throws RecognitionException {
		ExceptContext _localctx = new ExceptContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_except);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
			match(EXCEPT);
			setState(231);
			match(IDENT);
			setState(232);
			match(THEN);
			setState(236);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << VAR) | (1L << CONST) | (1L << RETURN) | (1L << IF) | (1L << WHILE) | (1L << FOR) | (1L << DELETE) | (1L << BREAK) | (1L << CONTINUE) | (1L << WAIT) | (1L << TRY) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
				{
				{
				setState(233);
				statement();
				}
				}
				setState(238);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class VariableContext extends ExprContext {
		public TerminalNode IDENT() { return getToken(MCCodeParser.IDENT, 0); }
		public TerminalNode CMDARG() { return getToken(MCCodeParser.CMDARG, 0); }
		public VariableContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FloatLiteralContext extends ExprContext {
		public TerminalNode FLOAT() { return getToken(MCCodeParser.FLOAT, 0); }
		public FloatLiteralContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterFloatLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitFloatLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitFloatLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MethodCallContext extends ExprContext {
		public ExprContext object;
		public Token property;
		public TerminalNode DOT() { return getToken(MCCodeParser.DOT, 0); }
		public TerminalNode LPAREN() { return getToken(MCCodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MCCodeParser.RPAREN, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode IDENT() { return getToken(MCCodeParser.IDENT, 0); }
		public List<TerminalNode> COMMA() { return getTokens(MCCodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MCCodeParser.COMMA, i);
		}
		public MethodCallContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterMethodCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitMethodCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitMethodCall(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class GetItemContext extends ExprContext {
		public ExprContext source;
		public ExprContext key;
		public TerminalNode LBRACK() { return getToken(MCCodeParser.LBRACK, 0); }
		public TerminalNode RBRACK() { return getToken(MCCodeParser.RBRACK, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public GetItemContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterGetItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitGetItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitGetItem(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ListLiteralContext extends ExprContext {
		public TerminalNode LBRACK() { return getToken(MCCodeParser.LBRACK, 0); }
		public TerminalNode RBRACK() { return getToken(MCCodeParser.RBRACK, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MCCodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MCCodeParser.COMMA, i);
		}
		public ListLiteralContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterListLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitListLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitListLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MapLiteralContext extends ExprContext {
		public TerminalNode LCURL() { return getToken(MCCodeParser.LCURL, 0); }
		public TerminalNode RCURL() { return getToken(MCCodeParser.RCURL, 0); }
		public List<TerminalNode> STRING() { return getTokens(MCCodeParser.STRING); }
		public TerminalNode STRING(int i) {
			return getToken(MCCodeParser.STRING, i);
		}
		public List<TerminalNode> COLON() { return getTokens(MCCodeParser.COLON); }
		public TerminalNode COLON(int i) {
			return getToken(MCCodeParser.COLON, i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MCCodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MCCodeParser.COMMA, i);
		}
		public MapLiteralContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterMapLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitMapLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitMapLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BoolLiteralContext extends ExprContext {
		public TerminalNode TRUE() { return getToken(MCCodeParser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(MCCodeParser.FALSE, 0); }
		public BoolLiteralContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterBoolLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitBoolLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitBoolLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringLiteralContext extends ExprContext {
		public TerminalNode STRING() { return getToken(MCCodeParser.STRING, 0); }
		public StringLiteralContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterStringLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitStringLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitStringLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IntLiteralContext extends ExprContext {
		public TerminalNode INT() { return getToken(MCCodeParser.INT, 0); }
		public IntLiteralContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterIntLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitIntLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitIntLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class GetPropertyContext extends ExprContext {
		public ExprContext object;
		public Token property;
		public TerminalNode DOT() { return getToken(MCCodeParser.DOT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(MCCodeParser.IDENT, 0); }
		public GetPropertyContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterGetProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitGetProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitGetProperty(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FunctionCallContext extends ExprContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode LPAREN() { return getToken(MCCodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MCCodeParser.RPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(MCCodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MCCodeParser.COMMA, i);
		}
		public FunctionCallContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UnaryOperatorContext extends ExprContext {
		public Token operator;
		public ExprContext operand;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode MINUS() { return getToken(MCCodeParser.MINUS, 0); }
		public TerminalNode NOT() { return getToken(MCCodeParser.NOT, 0); }
		public UnaryOperatorContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterUnaryOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitUnaryOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitUnaryOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BinaryOperatorContext extends ExprContext {
		public ExprContext left;
		public Token operator;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode POWER() { return getToken(MCCodeParser.POWER, 0); }
		public TerminalNode MUL() { return getToken(MCCodeParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(MCCodeParser.DIV, 0); }
		public TerminalNode INTDIV() { return getToken(MCCodeParser.INTDIV, 0); }
		public TerminalNode MOD() { return getToken(MCCodeParser.MOD, 0); }
		public TerminalNode PLUS() { return getToken(MCCodeParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(MCCodeParser.MINUS, 0); }
		public TerminalNode IN() { return getToken(MCCodeParser.IN, 0); }
		public TerminalNode NOT() { return getToken(MCCodeParser.NOT, 0); }
		public TerminalNode EQUAL() { return getToken(MCCodeParser.EQUAL, 0); }
		public TerminalNode NEQUAL() { return getToken(MCCodeParser.NEQUAL, 0); }
		public TerminalNode GT() { return getToken(MCCodeParser.GT, 0); }
		public TerminalNode GE() { return getToken(MCCodeParser.GE, 0); }
		public TerminalNode LT() { return getToken(MCCodeParser.LT, 0); }
		public TerminalNode LE() { return getToken(MCCodeParser.LE, 0); }
		public TerminalNode AND() { return getToken(MCCodeParser.AND, 0); }
		public TerminalNode OR() { return getToken(MCCodeParser.OR, 0); }
		public BinaryOperatorContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterBinaryOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitBinaryOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitBinaryOperator(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NullLiteralContext extends ExprContext {
		public TerminalNode NULL() { return getToken(MCCodeParser.NULL, 0); }
		public NullLiteralContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterNullLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitNullLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitNullLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SetLiteralContext extends ExprContext {
		public TerminalNode LCURL() { return getToken(MCCodeParser.LCURL, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode RCURL() { return getToken(MCCodeParser.RCURL, 0); }
		public List<TerminalNode> COMMA() { return getTokens(MCCodeParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MCCodeParser.COMMA, i);
		}
		public SetLiteralContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterSetLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitSetLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitSetLiteral(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenthesesContext extends ExprContext {
		public ExprContext exp;
		public TerminalNode LPAREN() { return getToken(MCCodeParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(MCCodeParser.RPAREN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParenthesesContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).enterParentheses(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MCCodeListener ) ((MCCodeListener)listener).exitParentheses(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MCCodeVisitor ) return ((MCCodeVisitor<? extends T>)visitor).visitParentheses(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 16;
		enterRecursionRule(_localctx, 16, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(302);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				{
				_localctx = new ParenthesesContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(240);
				match(LPAREN);
				setState(241);
				((ParenthesesContext)_localctx).exp = expr(0);
				setState(242);
				match(RPAREN);
				}
				break;
			case 2:
				{
				_localctx = new NullLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(244);
				match(NULL);
				}
				break;
			case 3:
				{
				_localctx = new BoolLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(245);
				match(TRUE);
				}
				break;
			case 4:
				{
				_localctx = new BoolLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(246);
				match(FALSE);
				}
				break;
			case 5:
				{
				_localctx = new IntLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(247);
				match(INT);
				}
				break;
			case 6:
				{
				_localctx = new FloatLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(248);
				match(FLOAT);
				}
				break;
			case 7:
				{
				_localctx = new StringLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(249);
				match(STRING);
				}
				break;
			case 8:
				{
				_localctx = new ListLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(250);
				match(LBRACK);
				setState(262);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
					{
					setState(251);
					expr(0);
					setState(256);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
					while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(252);
							match(COMMA);
							setState(253);
							expr(0);
							}
							} 
						}
						setState(258);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
					}
					setState(260);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(259);
						match(COMMA);
						}
					}

					}
				}

				setState(264);
				match(RBRACK);
				}
				break;
			case 9:
				{
				_localctx = new MapLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(265);
				match(LCURL);
				setState(281);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==STRING) {
					{
					setState(266);
					match(STRING);
					setState(267);
					match(COLON);
					setState(268);
					expr(0);
					setState(275);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
					while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1 ) {
							{
							{
							setState(269);
							match(COMMA);
							setState(270);
							match(STRING);
							setState(271);
							match(COLON);
							setState(272);
							expr(0);
							}
							} 
						}
						setState(277);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,27,_ctx);
					}
					setState(279);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==COMMA) {
						{
						setState(278);
						match(COMMA);
						}
					}

					}
				}

				setState(283);
				match(RCURL);
				}
				break;
			case 10:
				{
				_localctx = new SetLiteralContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(284);
				match(LCURL);
				setState(285);
				expr(0);
				setState(290);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
				while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(286);
						match(COMMA);
						setState(287);
						expr(0);
						}
						} 
					}
					setState(292);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,30,_ctx);
				}
				setState(294);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(293);
					match(COMMA);
					}
				}

				setState(296);
				match(RCURL);
				}
				break;
			case 11:
				{
				_localctx = new UnaryOperatorContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(298);
				((UnaryOperatorContext)_localctx).operator = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==MINUS || _la==NOT) ) {
					((UnaryOperatorContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(299);
				((UnaryOperatorContext)_localctx).operand = expr(11);
				}
				break;
			case 12:
				{
				_localctx = new VariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(300);
				match(IDENT);
				}
				break;
			case 13:
				{
				_localctx = new VariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(301);
				match(CMDARG);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(372);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,41,_ctx);
			while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(370);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,40,_ctx) ) {
					case 1:
						{
						_localctx = new BinaryOperatorContext(new ExprContext(_parentctx, _parentState));
						((BinaryOperatorContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(304);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(305);
						((BinaryOperatorContext)_localctx).operator = match(POWER);
						setState(306);
						((BinaryOperatorContext)_localctx).right = expr(10);
						}
						break;
					case 2:
						{
						_localctx = new BinaryOperatorContext(new ExprContext(_parentctx, _parentState));
						((BinaryOperatorContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(307);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(308);
						((BinaryOperatorContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << INTDIV) | (1L << MOD))) != 0)) ) {
							((BinaryOperatorContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(309);
						((BinaryOperatorContext)_localctx).right = expr(9);
						}
						break;
					case 3:
						{
						_localctx = new BinaryOperatorContext(new ExprContext(_parentctx, _parentState));
						((BinaryOperatorContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(310);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(311);
						((BinaryOperatorContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((BinaryOperatorContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(312);
						((BinaryOperatorContext)_localctx).right = expr(8);
						}
						break;
					case 4:
						{
						_localctx = new BinaryOperatorContext(new ExprContext(_parentctx, _parentState));
						((BinaryOperatorContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(313);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(315);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if (_la==NOT) {
							{
							setState(314);
							match(NOT);
							}
						}

						setState(317);
						match(IN);
						setState(318);
						((BinaryOperatorContext)_localctx).right = expr(7);
						}
						break;
					case 5:
						{
						_localctx = new BinaryOperatorContext(new ExprContext(_parentctx, _parentState));
						((BinaryOperatorContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(319);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(320);
						((BinaryOperatorContext)_localctx).operator = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQUAL) | (1L << NEQUAL) | (1L << GT) | (1L << GE) | (1L << LT) | (1L << LE))) != 0)) ) {
							((BinaryOperatorContext)_localctx).operator = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(321);
						((BinaryOperatorContext)_localctx).right = expr(6);
						}
						break;
					case 6:
						{
						_localctx = new BinaryOperatorContext(new ExprContext(_parentctx, _parentState));
						((BinaryOperatorContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(322);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(323);
						((BinaryOperatorContext)_localctx).operator = match(AND);
						setState(324);
						((BinaryOperatorContext)_localctx).right = expr(5);
						}
						break;
					case 7:
						{
						_localctx = new BinaryOperatorContext(new ExprContext(_parentctx, _parentState));
						((BinaryOperatorContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(325);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(326);
						((BinaryOperatorContext)_localctx).operator = match(OR);
						setState(327);
						((BinaryOperatorContext)_localctx).right = expr(4);
						}
						break;
					case 8:
						{
						_localctx = new MethodCallContext(new ExprContext(_parentctx, _parentState));
						((MethodCallContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(328);
						if (!(precpred(_ctx, 14))) throw new FailedPredicateException(this, "precpred(_ctx, 14)");
						setState(329);
						match(DOT);
						setState(330);
						((MethodCallContext)_localctx).property = match(IDENT);
						setState(331);
						match(LPAREN);
						setState(343);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
							{
							setState(332);
							expr(0);
							setState(337);
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
							while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
								if ( _alt==1 ) {
									{
									{
									setState(333);
									match(COMMA);
									setState(334);
									expr(0);
									}
									} 
								}
								setState(339);
								_errHandler.sync(this);
								_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
							}
							setState(341);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la==COMMA) {
								{
								setState(340);
								match(COMMA);
								}
							}

							}
						}

						setState(345);
						match(RPAREN);
						}
						break;
					case 9:
						{
						_localctx = new GetPropertyContext(new ExprContext(_parentctx, _parentState));
						((GetPropertyContext)_localctx).object = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(346);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(347);
						match(DOT);
						setState(348);
						((GetPropertyContext)_localctx).property = match(IDENT);
						}
						break;
					case 10:
						{
						_localctx = new FunctionCallContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(349);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(350);
						match(LPAREN);
						setState(362);
						_errHandler.sync(this);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << LBRACK) | (1L << LCURL) | (1L << MINUS) | (1L << NOT) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (TRUE - 64)) | (1L << (FALSE - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (IDENT - 64)) | (1L << (CMDARG - 64)))) != 0)) {
							{
							setState(351);
							expr(0);
							setState(356);
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
							while ( _alt!=2 && _alt!= ATN.INVALID_ALT_NUMBER ) {
								if ( _alt==1 ) {
									{
									{
									setState(352);
									match(COMMA);
									setState(353);
									expr(0);
									}
									} 
								}
								setState(358);
								_errHandler.sync(this);
								_alt = getInterpreter().adaptivePredict(_input,37,_ctx);
							}
							setState(360);
							_errHandler.sync(this);
							_la = _input.LA(1);
							if (_la==COMMA) {
								{
								setState(359);
								match(COMMA);
								}
							}

							}
						}

						setState(364);
						match(RPAREN);
						}
						break;
					case 11:
						{
						_localctx = new GetItemContext(new ExprContext(_parentctx, _parentState));
						((GetItemContext)_localctx).source = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(365);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(366);
						match(LBRACK);
						setState(367);
						((GetItemContext)_localctx).key = expr(0);
						setState(368);
						match(RBRACK);
						}
						break;
					}
					} 
				}
				setState(374);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,41,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 8:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 9);
		case 1:
			return precpred(_ctx, 8);
		case 2:
			return precpred(_ctx, 7);
		case 3:
			return precpred(_ctx, 6);
		case 4:
			return precpred(_ctx, 5);
		case 5:
			return precpred(_ctx, 4);
		case 6:
			return precpred(_ctx, 3);
		case 7:
			return precpred(_ctx, 14);
		case 8:
			return precpred(_ctx, 13);
		case 9:
			return precpred(_ctx, 12);
		case 10:
			return precpred(_ctx, 10);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3H\u017a\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2"+
		"\3\2\3\2\5\2\31\n\2\3\2\5\2\34\n\2\3\2\7\2\37\n\2\f\2\16\2\"\13\2\3\2"+
		"\7\2%\n\2\f\2\16\2(\13\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\4\7\4\63\n"+
		"\4\f\4\16\4\66\13\4\3\4\3\4\5\4:\n\4\3\4\3\4\3\5\3\5\5\5@\n\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5P\n\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\7\5X\n\5\f\5\16\5[\13\5\3\5\5\5^\n\5\5\5`\n\5\3\5\3\5\7\5"+
		"d\n\5\f\5\16\5g\13\5\3\5\3\5\5\5k\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\7\6\u0081\n\6\f\6\16"+
		"\6\u0084\13\6\3\6\7\6\u0087\n\6\f\6\16\6\u008a\13\6\3\6\5\6\u008d\n\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\7\6\u0095\n\6\f\6\16\6\u0098\13\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\7\6\u00a2\n\6\f\6\16\6\u00a5\13\6\3\6\3\6\3\6\3\6"+
		"\7\6\u00ab\n\6\f\6\16\6\u00ae\13\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\5\6\u00bd\n\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u00d7\n\6"+
		"\3\7\3\7\3\7\3\7\7\7\u00dd\n\7\f\7\16\7\u00e0\13\7\3\b\3\b\7\b\u00e4\n"+
		"\b\f\b\16\b\u00e7\13\b\3\t\3\t\3\t\3\t\7\t\u00ed\n\t\f\t\16\t\u00f0\13"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u0101"+
		"\n\n\f\n\16\n\u0104\13\n\3\n\5\n\u0107\n\n\5\n\u0109\n\n\3\n\3\n\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\7\n\u0114\n\n\f\n\16\n\u0117\13\n\3\n\5\n\u011a"+
		"\n\n\5\n\u011c\n\n\3\n\3\n\3\n\3\n\3\n\7\n\u0123\n\n\f\n\16\n\u0126\13"+
		"\n\3\n\5\n\u0129\n\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u0131\n\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u013e\n\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u0152\n\n\f\n\16"+
		"\n\u0155\13\n\3\n\5\n\u0158\n\n\5\n\u015a\n\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\7\n\u0165\n\n\f\n\16\n\u0168\13\n\3\n\5\n\u016b\n\n\5\n\u016d"+
		"\n\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u0175\n\n\f\n\16\n\u0178\13\n\3\n\2\3"+
		"\22\13\2\4\6\b\n\f\16\20\22\2\t\4\2>>DD\3\2+,\3\2\17\26\4\2\30\30%%\3"+
		"\2\31\34\3\2\27\30\3\2\36#\2\u01bd\2\33\3\2\2\2\4+\3\2\2\2\6.\3\2\2\2"+
		"\bj\3\2\2\2\n\u00d6\3\2\2\2\f\u00d8\3\2\2\2\16\u00e1\3\2\2\2\20\u00e8"+
		"\3\2\2\2\22\u0130\3\2\2\2\24\25\7*\2\2\25\30\7D\2\2\26\27\7=\2\2\27\31"+
		"\t\2\2\2\30\26\3\2\2\2\30\31\3\2\2\2\31\32\3\2\2\2\32\34\7\r\2\2\33\24"+
		"\3\2\2\2\33\34\3\2\2\2\34 \3\2\2\2\35\37\5\6\4\2\36\35\3\2\2\2\37\"\3"+
		"\2\2\2 \36\3\2\2\2 !\3\2\2\2!&\3\2\2\2\" \3\2\2\2#%\5\b\5\2$#\3\2\2\2"+
		"%(\3\2\2\2&$\3\2\2\2&\'\3\2\2\2\')\3\2\2\2(&\3\2\2\2)*\7\2\2\3*\3\3\2"+
		"\2\2+,\5\22\n\2,-\7\2\2\3-\5\3\2\2\2./\7(\2\2/\64\7G\2\2\60\61\7\16\2"+
		"\2\61\63\7G\2\2\62\60\3\2\2\2\63\66\3\2\2\2\64\62\3\2\2\2\64\65\3\2\2"+
		"\2\659\3\2\2\2\66\64\3\2\2\2\678\7)\2\28:\7G\2\29\67\3\2\2\29:\3\2\2\2"+
		":;\3\2\2\2;<\7\r\2\2<\7\3\2\2\2=?\7.\2\2>@\7-\2\2?>\3\2\2\2?@\3\2\2\2"+
		"@A\3\2\2\2AB\7+\2\2BC\7G\2\2CD\7\17\2\2DE\5\22\n\2EF\7\r\2\2Fk\3\2\2\2"+
		"GH\7.\2\2HI\7,\2\2IJ\7G\2\2JK\7\17\2\2KL\5\22\n\2LM\7\r\2\2Mk\3\2\2\2"+
		"NP\7.\2\2ON\3\2\2\2OP\3\2\2\2PQ\3\2\2\2QR\7/\2\2RS\7G\2\2S_\7\5\2\2TY"+
		"\7G\2\2UV\7\13\2\2VX\7G\2\2WU\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z]"+
		"\3\2\2\2[Y\3\2\2\2\\^\7\13\2\2]\\\3\2\2\2]^\3\2\2\2^`\3\2\2\2_T\3\2\2"+
		"\2_`\3\2\2\2`a\3\2\2\2ae\7\6\2\2bd\5\n\6\2cb\3\2\2\2dg\3\2\2\2ec\3\2\2"+
		"\2ef\3\2\2\2fh\3\2\2\2ge\3\2\2\2hk\78\2\2ik\5\n\6\2j=\3\2\2\2jG\3\2\2"+
		"\2jO\3\2\2\2ji\3\2\2\2k\t\3\2\2\2lm\t\3\2\2mn\7G\2\2no\7\17\2\2op\5\22"+
		"\n\2pq\7\r\2\2q\u00d7\3\2\2\2rs\79\2\2st\7G\2\2t\u00d7\7\r\2\2uv\79\2"+
		"\2vw\5\22\n\2wx\7\7\2\2xy\5\22\n\2yz\7\b\2\2z{\7\r\2\2{\u00d7\3\2\2\2"+
		"|}\7\61\2\2}~\5\22\n\2~\u0082\7\62\2\2\177\u0081\5\n\6\2\u0080\177\3\2"+
		"\2\2\u0081\u0084\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083"+
		"\u0088\3\2\2\2\u0084\u0082\3\2\2\2\u0085\u0087\5\f\7\2\u0086\u0085\3\2"+
		"\2\2\u0087\u008a\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089"+
		"\u008c\3\2\2\2\u008a\u0088\3\2\2\2\u008b\u008d\5\16\b\2\u008c\u008b\3"+
		"\2\2\2\u008c\u008d\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008f\78\2\2\u008f"+
		"\u00d7\3\2\2\2\u0090\u0091\7\65\2\2\u0091\u0092\5\22\n\2\u0092\u0096\7"+
		"\67\2\2\u0093\u0095\5\n\6\2\u0094\u0093\3\2\2\2\u0095\u0098\3\2\2\2\u0096"+
		"\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u0099\3\2\2\2\u0098\u0096\3\2"+
		"\2\2\u0099\u009a\78\2\2\u009a\u00d7\3\2\2\2\u009b\u009c\7\66\2\2\u009c"+
		"\u009d\7G\2\2\u009d\u009e\7$\2\2\u009e\u009f\5\22\n\2\u009f\u00a3\7\67"+
		"\2\2\u00a0\u00a2\5\n\6\2\u00a1\u00a0\3\2\2\2\u00a2\u00a5\3\2\2\2\u00a3"+
		"\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a6\3\2\2\2\u00a5\u00a3\3\2"+
		"\2\2\u00a6\u00a7\78\2\2\u00a7\u00d7\3\2\2\2\u00a8\u00ac\7?\2\2\u00a9\u00ab"+
		"\5\n\6\2\u00aa\u00a9\3\2\2\2\u00ab\u00ae\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ac"+
		"\u00ad\3\2\2\2\u00ad\u00af\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af\u00b0\5\20"+
		"\t\2\u00b0\u00b1\78\2\2\u00b1\u00d7\3\2\2\2\u00b2\u00b3\7<\2\2\u00b3\u00b4"+
		"\5\22\n\2\u00b4\u00b5\7\r\2\2\u00b5\u00d7\3\2\2\2\u00b6\u00b7\7:\2\2\u00b7"+
		"\u00d7\7\r\2\2\u00b8\u00b9\7;\2\2\u00b9\u00d7\7\r\2\2\u00ba\u00bc\7\60"+
		"\2\2\u00bb\u00bd\5\22\n\2\u00bc\u00bb\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd"+
		"\u00be\3\2\2\2\u00be\u00d7\7\r\2\2\u00bf\u00c0\7G\2\2\u00c0\u00c1\t\4"+
		"\2\2\u00c1\u00c2\5\22\n\2\u00c2\u00c3\7\r\2\2\u00c3\u00d7\3\2\2\2\u00c4"+
		"\u00c5\5\22\n\2\u00c5\u00c6\7\7\2\2\u00c6\u00c7\5\22\n\2\u00c7\u00c8\7"+
		"\b\2\2\u00c8\u00c9\t\4\2\2\u00c9\u00ca\5\22\n\2\u00ca\u00cb\7\r\2\2\u00cb"+
		"\u00d7\3\2\2\2\u00cc\u00cd\5\22\n\2\u00cd\u00ce\7\16\2\2\u00ce\u00cf\7"+
		"G\2\2\u00cf\u00d0\t\4\2\2\u00d0\u00d1\5\22\n\2\u00d1\u00d2\7\r\2\2\u00d2"+
		"\u00d7\3\2\2\2\u00d3\u00d4\5\22\n\2\u00d4\u00d5\7\r\2\2\u00d5\u00d7\3"+
		"\2\2\2\u00d6l\3\2\2\2\u00d6r\3\2\2\2\u00d6u\3\2\2\2\u00d6|\3\2\2\2\u00d6"+
		"\u0090\3\2\2\2\u00d6\u009b\3\2\2\2\u00d6\u00a8\3\2\2\2\u00d6\u00b2\3\2"+
		"\2\2\u00d6\u00b6\3\2\2\2\u00d6\u00b8\3\2\2\2\u00d6\u00ba\3\2\2\2\u00d6"+
		"\u00bf\3\2\2\2\u00d6\u00c4\3\2\2\2\u00d6\u00cc\3\2\2\2\u00d6\u00d3\3\2"+
		"\2\2\u00d7\13\3\2\2\2\u00d8\u00d9\7\64\2\2\u00d9\u00da\5\22\n\2\u00da"+
		"\u00de\7\62\2\2\u00db\u00dd\5\n\6\2\u00dc\u00db\3\2\2\2\u00dd\u00e0\3"+
		"\2\2\2\u00de\u00dc\3\2\2\2\u00de\u00df\3\2\2\2\u00df\r\3\2\2\2\u00e0\u00de"+
		"\3\2\2\2\u00e1\u00e5\7\63\2\2\u00e2\u00e4\5\n\6\2\u00e3\u00e2\3\2\2\2"+
		"\u00e4\u00e7\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6\17"+
		"\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e8\u00e9\7@\2\2\u00e9\u00ea\7G\2\2\u00ea"+
		"\u00ee\7\62\2\2\u00eb\u00ed\5\n\6\2\u00ec\u00eb\3\2\2\2\u00ed\u00f0\3"+
		"\2\2\2\u00ee\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\21\3\2\2\2\u00f0"+
		"\u00ee\3\2\2\2\u00f1\u00f2\b\n\1\2\u00f2\u00f3\7\5\2\2\u00f3\u00f4\5\22"+
		"\n\2\u00f4\u00f5\7\6\2\2\u00f5\u0131\3\2\2\2\u00f6\u0131\7A\2\2\u00f7"+
		"\u0131\7B\2\2\u00f8\u0131\7C\2\2\u00f9\u0131\7D\2\2\u00fa\u0131\7E\2\2"+
		"\u00fb\u0131\7F\2\2\u00fc\u0108\7\7\2\2\u00fd\u0102\5\22\n\2\u00fe\u00ff"+
		"\7\13\2\2\u00ff\u0101\5\22\n\2\u0100\u00fe\3\2\2\2\u0101\u0104\3\2\2\2"+
		"\u0102\u0100\3\2\2\2\u0102\u0103\3\2\2\2\u0103\u0106\3\2\2\2\u0104\u0102"+
		"\3\2\2\2\u0105\u0107\7\13\2\2\u0106\u0105\3\2\2\2\u0106\u0107\3\2\2\2"+
		"\u0107\u0109\3\2\2\2\u0108\u00fd\3\2\2\2\u0108\u0109\3\2\2\2\u0109\u010a"+
		"\3\2\2\2\u010a\u0131\7\b\2\2\u010b\u011b\7\t\2\2\u010c\u010d\7F\2\2\u010d"+
		"\u010e\7\f\2\2\u010e\u0115\5\22\n\2\u010f\u0110\7\13\2\2\u0110\u0111\7"+
		"F\2\2\u0111\u0112\7\f\2\2\u0112\u0114\5\22\n\2\u0113\u010f\3\2\2\2\u0114"+
		"\u0117\3\2\2\2\u0115\u0113\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0119\3\2"+
		"\2\2\u0117\u0115\3\2\2\2\u0118\u011a\7\13\2\2\u0119\u0118\3\2\2\2\u0119"+
		"\u011a\3\2\2\2\u011a\u011c\3\2\2\2\u011b\u010c\3\2\2\2\u011b\u011c\3\2"+
		"\2\2\u011c\u011d\3\2\2\2\u011d\u0131\7\n\2\2\u011e\u011f\7\t\2\2\u011f"+
		"\u0124\5\22\n\2\u0120\u0121\7\13\2\2\u0121\u0123\5\22\n\2\u0122\u0120"+
		"\3\2\2\2\u0123\u0126\3\2\2\2\u0124\u0122\3\2\2\2\u0124\u0125\3\2\2\2\u0125"+
		"\u0128\3\2\2\2\u0126\u0124\3\2\2\2\u0127\u0129\7\13\2\2\u0128\u0127\3"+
		"\2\2\2\u0128\u0129\3\2\2\2\u0129\u012a\3\2\2\2\u012a\u012b\7\n\2\2\u012b"+
		"\u0131\3\2\2\2\u012c\u012d\t\5\2\2\u012d\u0131\5\22\n\r\u012e\u0131\7"+
		"G\2\2\u012f\u0131\7H\2\2\u0130\u00f1\3\2\2\2\u0130\u00f6\3\2\2\2\u0130"+
		"\u00f7\3\2\2\2\u0130\u00f8\3\2\2\2\u0130\u00f9\3\2\2\2\u0130\u00fa\3\2"+
		"\2\2\u0130\u00fb\3\2\2\2\u0130\u00fc\3\2\2\2\u0130\u010b\3\2\2\2\u0130"+
		"\u011e\3\2\2\2\u0130\u012c\3\2\2\2\u0130\u012e\3\2\2\2\u0130\u012f\3\2"+
		"\2\2\u0131\u0176\3\2\2\2\u0132\u0133\f\13\2\2\u0133\u0134\7\35\2\2\u0134"+
		"\u0175\5\22\n\f\u0135\u0136\f\n\2\2\u0136\u0137\t\6\2\2\u0137\u0175\5"+
		"\22\n\13\u0138\u0139\f\t\2\2\u0139\u013a\t\7\2\2\u013a\u0175\5\22\n\n"+
		"\u013b\u013d\f\b\2\2\u013c\u013e\7%\2\2\u013d\u013c\3\2\2\2\u013d\u013e"+
		"\3\2\2\2\u013e\u013f\3\2\2\2\u013f\u0140\7$\2\2\u0140\u0175\5\22\n\t\u0141"+
		"\u0142\f\7\2\2\u0142\u0143\t\b\2\2\u0143\u0175\5\22\n\b\u0144\u0145\f"+
		"\6\2\2\u0145\u0146\7&\2\2\u0146\u0175\5\22\n\7\u0147\u0148\f\5\2\2\u0148"+
		"\u0149\7\'\2\2\u0149\u0175\5\22\n\6\u014a\u014b\f\20\2\2\u014b\u014c\7"+
		"\16\2\2\u014c\u014d\7G\2\2\u014d\u0159\7\5\2\2\u014e\u0153\5\22\n\2\u014f"+
		"\u0150\7\13\2\2\u0150\u0152\5\22\n\2\u0151\u014f\3\2\2\2\u0152\u0155\3"+
		"\2\2\2\u0153\u0151\3\2\2\2\u0153\u0154\3\2\2\2\u0154\u0157\3\2\2\2\u0155"+
		"\u0153\3\2\2\2\u0156\u0158\7\13\2\2\u0157\u0156\3\2\2\2\u0157\u0158\3"+
		"\2\2\2\u0158\u015a\3\2\2\2\u0159\u014e\3\2\2\2\u0159\u015a\3\2\2\2\u015a"+
		"\u015b\3\2\2\2\u015b\u0175\7\6\2\2\u015c\u015d\f\17\2\2\u015d\u015e\7"+
		"\16\2\2\u015e\u0175\7G\2\2\u015f\u0160\f\16\2\2\u0160\u016c\7\5\2\2\u0161"+
		"\u0166\5\22\n\2\u0162\u0163\7\13\2\2\u0163\u0165\5\22\n\2\u0164\u0162"+
		"\3\2\2\2\u0165\u0168\3\2\2\2\u0166\u0164\3\2\2\2\u0166\u0167\3\2\2\2\u0167"+
		"\u016a\3\2\2\2\u0168\u0166\3\2\2\2\u0169\u016b\7\13\2\2\u016a\u0169\3"+
		"\2\2\2\u016a\u016b\3\2\2\2\u016b\u016d\3\2\2\2\u016c\u0161\3\2\2\2\u016c"+
		"\u016d\3\2\2\2\u016d\u016e\3\2\2\2\u016e\u0175\7\6\2\2\u016f\u0170\f\f"+
		"\2\2\u0170\u0171\7\7\2\2\u0171\u0172\5\22\n\2\u0172\u0173\7\b\2\2\u0173"+
		"\u0175\3\2\2\2\u0174\u0132\3\2\2\2\u0174\u0135\3\2\2\2\u0174\u0138\3\2"+
		"\2\2\u0174\u013b\3\2\2\2\u0174\u0141\3\2\2\2\u0174\u0144\3\2\2\2\u0174"+
		"\u0147\3\2\2\2\u0174\u014a\3\2\2\2\u0174\u015c\3\2\2\2\u0174\u015f\3\2"+
		"\2\2\u0174\u016f\3\2\2\2\u0175\u0178\3\2\2\2\u0176\u0174\3\2\2\2\u0176"+
		"\u0177\3\2\2\2\u0177\23\3\2\2\2\u0178\u0176\3\2\2\2,\30\33 &\649?OY]_"+
		"ej\u0082\u0088\u008c\u0096\u00a3\u00ac\u00bc\u00d6\u00de\u00e5\u00ee\u0102"+
		"\u0106\u0108\u0115\u0119\u011b\u0124\u0128\u0130\u013d\u0153\u0157\u0159"+
		"\u0166\u016a\u016c\u0174\u0176";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
