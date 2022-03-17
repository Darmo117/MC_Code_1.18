// Generated from /home/damien/IdeaProjects/MC_Code/grammar/MCCode.g4 by ANTLR 4.9.2
package net.darmo_creations.mccode.interpreter.parser.antlr4;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MCCodeParser}.
 */
public interface MCCodeListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MCCodeParser#module}.
	 * @param ctx the parse tree
	 */
	void enterModule(MCCodeParser.ModuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link MCCodeParser#module}.
	 * @param ctx the parse tree
	 */
	void exitModule(MCCodeParser.ModuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link MCCodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(MCCodeParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MCCodeParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(MCCodeParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MCCodeParser#import_statement}.
	 * @param ctx the parse tree
	 */
	void enterImport_statement(MCCodeParser.Import_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MCCodeParser#import_statement}.
	 * @param ctx the parse tree
	 */
	void exitImport_statement(MCCodeParser.Import_statementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DeclareGlobalVariable}
	 * labeled alternative in {@link MCCodeParser#global_statement}.
	 * @param ctx the parse tree
	 */
	void enterDeclareGlobalVariable(MCCodeParser.DeclareGlobalVariableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DeclareGlobalVariable}
	 * labeled alternative in {@link MCCodeParser#global_statement}.
	 * @param ctx the parse tree
	 */
	void exitDeclareGlobalVariable(MCCodeParser.DeclareGlobalVariableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DeclareGlobalConstant}
	 * labeled alternative in {@link MCCodeParser#global_statement}.
	 * @param ctx the parse tree
	 */
	void enterDeclareGlobalConstant(MCCodeParser.DeclareGlobalConstantContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DeclareGlobalConstant}
	 * labeled alternative in {@link MCCodeParser#global_statement}.
	 * @param ctx the parse tree
	 */
	void exitDeclareGlobalConstant(MCCodeParser.DeclareGlobalConstantContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DefineFunctionStatement}
	 * labeled alternative in {@link MCCodeParser#global_statement}.
	 * @param ctx the parse tree
	 */
	void enterDefineFunctionStatement(MCCodeParser.DefineFunctionStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DefineFunctionStatement}
	 * labeled alternative in {@link MCCodeParser#global_statement}.
	 * @param ctx the parse tree
	 */
	void exitDefineFunctionStatement(MCCodeParser.DefineFunctionStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Stmt}
	 * labeled alternative in {@link MCCodeParser#global_statement}.
	 * @param ctx the parse tree
	 */
	void enterStmt(MCCodeParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Stmt}
	 * labeled alternative in {@link MCCodeParser#global_statement}.
	 * @param ctx the parse tree
	 */
	void exitStmt(MCCodeParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DeclareVariableStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterDeclareVariableStatement(MCCodeParser.DeclareVariableStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DeclareVariableStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitDeclareVariableStatement(MCCodeParser.DeclareVariableStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DeleteStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterDeleteStatement(MCCodeParser.DeleteStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DeleteStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitDeleteStatement(MCCodeParser.DeleteStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DeleteItemStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterDeleteItemStatement(MCCodeParser.DeleteItemStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DeleteItemStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitDeleteItemStatement(MCCodeParser.DeleteItemStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(MCCodeParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(MCCodeParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code WhileLoopStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileLoopStatement(MCCodeParser.WhileLoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code WhileLoopStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileLoopStatement(MCCodeParser.WhileLoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForLoopStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForLoopStatement(MCCodeParser.ForLoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForLoopStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForLoopStatement(MCCodeParser.ForLoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TryExceptStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterTryExceptStatement(MCCodeParser.TryExceptStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TryExceptStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitTryExceptStatement(MCCodeParser.TryExceptStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code WaitStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWaitStatement(MCCodeParser.WaitStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code WaitStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWaitStatement(MCCodeParser.WaitStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BreakStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStatement(MCCodeParser.BreakStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BreakStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStatement(MCCodeParser.BreakStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ContinueStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStatement(MCCodeParser.ContinueStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ContinueStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStatement(MCCodeParser.ContinueStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReturnStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(MCCodeParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReturnStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(MCCodeParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VariableAssignmentStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterVariableAssignmentStatement(MCCodeParser.VariableAssignmentStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VariableAssignmentStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitVariableAssignmentStatement(MCCodeParser.VariableAssignmentStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SetItemStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterSetItemStatement(MCCodeParser.SetItemStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SetItemStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitSetItemStatement(MCCodeParser.SetItemStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SetPropertyStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterSetPropertyStatement(MCCodeParser.SetPropertyStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SetPropertyStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitSetPropertyStatement(MCCodeParser.SetPropertyStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpressionStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(MCCodeParser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpressionStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(MCCodeParser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MCCodeParser#elseif}.
	 * @param ctx the parse tree
	 */
	void enterElseif(MCCodeParser.ElseifContext ctx);
	/**
	 * Exit a parse tree produced by {@link MCCodeParser#elseif}.
	 * @param ctx the parse tree
	 */
	void exitElseif(MCCodeParser.ElseifContext ctx);
	/**
	 * Enter a parse tree produced by {@link MCCodeParser#else_}.
	 * @param ctx the parse tree
	 */
	void enterElse_(MCCodeParser.Else_Context ctx);
	/**
	 * Exit a parse tree produced by {@link MCCodeParser#else_}.
	 * @param ctx the parse tree
	 */
	void exitElse_(MCCodeParser.Else_Context ctx);
	/**
	 * Enter a parse tree produced by {@link MCCodeParser#except}.
	 * @param ctx the parse tree
	 */
	void enterExcept(MCCodeParser.ExceptContext ctx);
	/**
	 * Exit a parse tree produced by {@link MCCodeParser#except}.
	 * @param ctx the parse tree
	 */
	void exitExcept(MCCodeParser.ExceptContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Variable}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterVariable(MCCodeParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Variable}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitVariable(MCCodeParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FloatLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFloatLiteral(MCCodeParser.FloatLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FloatLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFloatLiteral(MCCodeParser.FloatLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MethodCall}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMethodCall(MCCodeParser.MethodCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MethodCall}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMethodCall(MCCodeParser.MethodCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GetItem}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterGetItem(MCCodeParser.GetItemContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GetItem}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitGetItem(MCCodeParser.GetItemContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ListLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterListLiteral(MCCodeParser.ListLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ListLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitListLiteral(MCCodeParser.ListLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MapLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMapLiteral(MCCodeParser.MapLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MapLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMapLiteral(MCCodeParser.MapLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BoolLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBoolLiteral(MCCodeParser.BoolLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BoolLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBoolLiteral(MCCodeParser.BoolLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(MCCodeParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(MCCodeParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIntLiteral(MCCodeParser.IntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIntLiteral(MCCodeParser.IntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GetProperty}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterGetProperty(MCCodeParser.GetPropertyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GetProperty}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitGetProperty(MCCodeParser.GetPropertyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(MCCodeParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(MCCodeParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryOperator}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOperator(MCCodeParser.UnaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryOperator}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOperator(MCCodeParser.UnaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BinaryOperator}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBinaryOperator(MCCodeParser.BinaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BinaryOperator}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBinaryOperator(MCCodeParser.BinaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NullLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNullLiteral(MCCodeParser.NullLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NullLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNullLiteral(MCCodeParser.NullLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SetLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterSetLiteral(MCCodeParser.SetLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SetLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitSetLiteral(MCCodeParser.SetLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Parentheses}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParentheses(MCCodeParser.ParenthesesContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Parentheses}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParentheses(MCCodeParser.ParenthesesContext ctx);
}