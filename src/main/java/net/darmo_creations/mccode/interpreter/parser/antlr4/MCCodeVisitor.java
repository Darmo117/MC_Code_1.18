// Generated from /home/damien/IdeaProjects/MC_Code/grammar/MCCode.g4 by ANTLR 4.9.2
package net.darmo_creations.mccode.interpreter.parser.antlr4;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MCCodeParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MCCodeVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MCCodeParser#module}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule(MCCodeParser.ModuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCCodeParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(MCCodeParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCCodeParser#import_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImport_statement(MCCodeParser.Import_statementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DeclareGlobalVariable}
	 * labeled alternative in {@link MCCodeParser#global_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclareGlobalVariable(MCCodeParser.DeclareGlobalVariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DeclareGlobalConstant}
	 * labeled alternative in {@link MCCodeParser#global_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclareGlobalConstant(MCCodeParser.DeclareGlobalConstantContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DefineFunctionStatement}
	 * labeled alternative in {@link MCCodeParser#global_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefineFunctionStatement(MCCodeParser.DefineFunctionStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Stmt}
	 * labeled alternative in {@link MCCodeParser#global_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(MCCodeParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DeclareVariableStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclareVariableStatement(MCCodeParser.DeclareVariableStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DeleteStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeleteStatement(MCCodeParser.DeleteStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DeleteItemStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeleteItemStatement(MCCodeParser.DeleteItemStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(MCCodeParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WhileLoopStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileLoopStatement(MCCodeParser.WhileLoopStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ForLoopStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForLoopStatement(MCCodeParser.ForLoopStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TryExceptStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTryExceptStatement(MCCodeParser.TryExceptStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WaitStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWaitStatement(MCCodeParser.WaitStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BreakStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStatement(MCCodeParser.BreakStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ContinueStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStatement(MCCodeParser.ContinueStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReturnStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(MCCodeParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VariableAssignmentStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableAssignmentStatement(MCCodeParser.VariableAssignmentStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SetItemStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetItemStatement(MCCodeParser.SetItemStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SetPropertyStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetPropertyStatement(MCCodeParser.SetPropertyStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpressionStatement}
	 * labeled alternative in {@link MCCodeParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionStatement(MCCodeParser.ExpressionStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCCodeParser#elseif}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseif(MCCodeParser.ElseifContext ctx);
	/**
	 * Visit a parse tree produced by {@link MCCodeParser#else_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElse_(MCCodeParser.Else_Context ctx);
	/**
	 * Visit a parse tree produced by {@link MCCodeParser#except}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExcept(MCCodeParser.ExceptContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Variable}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(MCCodeParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FloatLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatLiteral(MCCodeParser.FloatLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MethodCall}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCall(MCCodeParser.MethodCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GetItem}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGetItem(MCCodeParser.GetItemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ListLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListLiteral(MCCodeParser.ListLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MapLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapLiteral(MCCodeParser.MapLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BoolLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolLiteral(MCCodeParser.BoolLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(MCCodeParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntLiteral(MCCodeParser.IntLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GetProperty}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGetProperty(MCCodeParser.GetPropertyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionCall}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(MCCodeParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnaryOperator}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOperator(MCCodeParser.UnaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BinaryOperator}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryOperator(MCCodeParser.BinaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NullLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullLiteral(MCCodeParser.NullLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SetLiteral}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetLiteral(MCCodeParser.SetLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Parentheses}
	 * labeled alternative in {@link MCCodeParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParentheses(MCCodeParser.ParenthesesContext ctx);
}