grammar MCCode;

WS     : [ \n\r\t]+ -> skip;
COMMENT: '#'.+?[\n\r] -> skip;

LPAREN: '(';
RPAREN: ')';
LBRACK: '[';
RBRACK: ']';
LCURL : '{';
RCURL : '}';
COMMA : ',';
COLON : ':';
SEMIC : ';';
DOT   : '.';

ASSIGN: ':=';
PLUSA : '+=';
MINUSA: '-=';
MULA  : '*=';
DIVA  : '/=';
INTDIVA: '//=';
MODA  : '%=';
POWERA: '^=';

PLUS  : '+';
MINUS : '-';
MUL   : '*';
DIV   : '/';
INTDIV: '//';
MOD   : '%';
POWER : '^';

EQUAL : '==';
NEQUAL: '!=';
GT    : '>';
GE    : '>=';
LT    : '<';
LE    : '<=';
IN    : 'in';

NOT   : 'not';
AND   : 'and';
OR    : 'or';

IMPORT : 'import';
AS     : 'as';
SCHED  : 'schedule';
VAR    : 'var';
CONST  : 'const';
EDITABLE: 'editable';
PUBLIC : 'public';
FUNC   : 'function';
RETURN : 'return';
IF     : 'if';
THEN   : 'then';
ELSE   : 'else';
ELIF   : 'elseif';
WHILE  : 'while';
FOR    : 'for';
DO     : 'do';
END    : 'end';
DELETE : 'del';
BREAK  : 'break';
CONTINUE: 'continue';
WAIT   : 'wait';
REPEAT : 'repeat';
FOREVER: 'forever';
TRY    : 'try';
EXCEPT : 'except';

NULL  : 'null';
TRUE  : 'true';
FALSE : 'false';
INT   : [0-9]+;
FLOAT : ([0-9]+'.'[0-9]*|'.'?[0-9]+)([eE]'-'?[0-9]+)?;
STRING: '"'('\\'["\\n]|~["\\\n\r])*?'"';
IDENT : [a-zA-Z_][a-zA-Z0-9_]*;
CMDARG: '$'('$'|[0-9]+);

module:
  (SCHED ticks=INT (REPEAT times=(INT | FOREVER))? SEMIC)?
  (import_statement)*
  global_statement* EOF;

expression: expr EOF;

import_statement: IMPORT IDENT (DOT IDENT)* (AS alias=IDENT)? SEMIC; // ID: 0

global_statement:
    PUBLIC EDITABLE? VAR name=IDENT ASSIGN value=expr SEMIC # DeclareGlobalVariable // ID: 10
  | PUBLIC CONST name=IDENT ASSIGN value=expr SEMIC         # DeclareGlobalConstant // ID: 10
  | PUBLIC? FUNC name=IDENT LPAREN (IDENT (COMMA IDENT)* COMMA?)? RPAREN statement* END # DefineFunctionStatement // ID: 11
  | statement # Stmt
;

statement:
    (VAR | CONST) name=IDENT ASSIGN value=expr SEMIC # DeclareVariableStatement // ID: 10
  | DELETE name=IDENT SEMIC                          # DeleteStatement // ID: 20
  | DELETE target=expr LBRACK key=expr RBRACK SEMIC  # DeleteItemStatement // ID: 21
  | IF cond=expr THEN statement* (elseif)* (else_)? END # IfStatement // ID: 40
  | WHILE cond=expr DO statement* END                   # WhileLoopStatement // ID: 41
  | FOR variable=IDENT IN range=expr DO statement* END  # ForLoopStatement // ID: 42
  | TRY statement* except END                           # TryExceptStatement // ID: 43
  | WAIT expr SEMIC # WaitStatement // ID: 50 Raises an error if present in a function
  | BREAK SEMIC     # BreakStatement // ID: 60 Raises an error if outside of a loop
  | CONTINUE SEMIC  # ContinueStatement // ID: 61 Raises an error if outside of a loop
  | RETURN (returned=expr)? SEMIC # ReturnStatement // ID: 62 Raises an error if outside of a function
  | name=IDENT operator=(ASSIGN | PLUSA | MINUSA | MULA | DIVA | INTDIVA | MODA | POWERA) value=expr SEMIC                         # VariableAssignmentStatement // ID: 12
  | target=expr LBRACK key=expr RBRACK operator=(ASSIGN | PLUSA | MINUSA | MULA | DIVA | INTDIVA | MODA | POWERA) value=expr SEMIC # SetItemStatement // ID: 13
  | target=expr DOT name=IDENT operator=(ASSIGN | PLUSA | MINUSA | MULA | DIVA | INTDIVA | MODA | POWERA) value=expr SEMIC         # SetPropertyStatement // ID: 14
  | expr SEMIC # ExpressionStatement // ID: 30
;

// Split if-elseif-else statement for easier parsing
elseif: ELIF cond=expr THEN statement*;
else_: ELSE statement*;

// Split try-except statement for easier parsing
except: EXCEPT IDENT THEN statement*;

expr:
    LPAREN exp=expr RPAREN # Parentheses
  | NULL   # NullLiteral // ID: 0
  | TRUE   # BoolLiteral // ID: 1
  | FALSE  # BoolLiteral // ID: 1
  | INT    # IntLiteral // ID: 2
  | FLOAT  # FloatLiteral // ID: 3
  | STRING # StringLiteral // ID: 4
  | LBRACK (expr (COMMA expr)* COMMA?)? RBRACK                       # ListLiteral // ID: 5
  | LCURL (STRING COLON expr (COMMA STRING COLON expr)* COMMA?)? RCURL # MapLiteral // ID: 6
  | LCURL expr (COMMA expr)* COMMA? RCURL                            # SetLiteral // ID: 7
  | object=expr DOT property=IDENT LPAREN (expr (COMMA expr)* COMMA?)? RPAREN # MethodCall // ID: 102
  | object=expr DOT property=IDENT                  # GetProperty // ID: 101
  | expr LPAREN (expr (COMMA expr)* COMMA?)? RPAREN # FunctionCall // ID: 103
  | operator=(MINUS | NOT) operand=expr             # UnaryOperator // IDs: 200
  | source=expr LBRACK key=expr RBRACK              # GetItem // ID: 201
  | left=expr operator=POWER right=expr             # BinaryOperator // ID: 201
  | left=expr operator=(MUL | DIV | INTDIV | MOD) right=expr # BinaryOperator // ID: 201
  | left=expr operator=(PLUS | MINUS) right=expr    # BinaryOperator // ID: 201
  | left=expr NOT? IN right=expr                    # BinaryOperator // ID: 201
  | left=expr operator=(EQUAL | NEQUAL | GT | GE | LT | LE) right=expr # BinaryOperator // ID: 201
  | left=expr operator=AND right=expr               # BinaryOperator // ID: 201
  | left=expr operator=OR right=expr                # BinaryOperator // ID: 201
  | IDENT  # Variable // ID: 100
  | CMDARG # Variable // ID: 100
;
