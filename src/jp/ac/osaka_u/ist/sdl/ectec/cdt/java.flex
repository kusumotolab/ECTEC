/* This is a lexer for java */

package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java_cup.runtime.*;
import java.util.List;
import java.util.ArrayList;
import jp.ac.osaka_u.ist.sdl.ectec.cdt.Token;
import jp.ac.osaka_u.ist.sdl.ectec.cdt.Symbol;
import jp.ac.osaka_u.ist.sdl.ectec.cdt.Lexer;

%%

/* The name of class created from this file is JavaLexer */
%public
%class JavaLexer

%extends Lexer

%type Token

%line
%column

%{

  public JavaLexer(java.io.Reader in) {
    this.zzReader = in;
  }

  StringBuffer string = new StringBuffer();

  private Token createToken(String str, Symbol sym) {
  	return new Token(str, sym, yyline+1, yycolumn+1);
  }
  
  private Token createToken(Symbol sym) {
  	return new Token(sym.getStr(), sym, yyline+1, yycolumn+1);
  }
  
  public List<Token> runLexicalAnalysis() {
  	List<Token> result = new ArrayList<Token>();
  	try{
  		while (!this.zzAtEOF) {
  			Token token = this.yylex();
  			if (token != null) {
  				result.add(token);
  			}
  		}
  	} catch(Exception e) {
  		e.printStackTrace();
  		return null;
  	}
  	return result;
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*
Annotation			 = "@" {InputCharacter}*

/* identifier */
Identifier = [:jletter:] [:jletterdigit:]*

/* numerical literals */
DecIntegerLiteral = 0 ("l"|"L")? | [1-9][0-9]* ("l"|"L")?

HexDigit = [0-9] | [a-f] | [A-F]
HexIntegerLiteral = 0 ("x"|"X") {HexDigit}+ ("l"|"L")?

OctalDigit = [0-7]
OctalIntegerLiteral = 0 {OctalDigit}+ ("l"|"L")?

ExponentIndicator = "e"|"E"
Sign = -|\+
SignedInteger = {Sign}? [0-9]+
ExponentPart = {ExponentIndicator} {SignedInteger}
FloatTypeSuffix = f|F|d|D
FloatingPointLiteral1 = [0-9]+ "." [0-9]* {ExponentPart}? {FloatTypeSuffix}?
FloatingPointLiteral2 = "." [0-9]+ {ExponentPart}? {FloatTypeSuffix}?
FloatingPointLiteral3 = [0-9]+ {ExponentPart} {FloatTypeSuffix}?
FloatingPointLiteral4 = [0-9]+ {ExponentPart}? {FloatTypeSuffix}
FloatingPointLiteral = {FloatingPointLiteral1} | {FloatingPointLiteral2} | {FloatingPointLiteral3} | {FloatingPointLiteral4} 

/* character literal */
CharacterLiteral = "'" ({SingleCharacter}|{EscapeSequence}) "'"
SingleCharacter = [^\r\n'\\] | {UnicodeCharacter}
UnicodeCharacter = \\ u+ {HexDigit} {HexDigit} {HexDigit} {HexDigit}
EscapeSequence = \\b | \\t | \\n | \\f | \\r | \\ \" | \\ ' | \\ \\ | {OctalEscape}
OctalEscape1 = \\ {OctalDigit} {OctalDigit}?
OctalEscape2 = \\ [0-3] {OctalDigit} {OctalDigit}
OctalEscape = {OctalEscape1} | {OctalEscape2}

%state STRING

%%
/* null */
<YYINITIAL> "null"			{ return createToken(Symbol.NULL); }

/* instanceof */
<YYINITIAL> "instanceof"	{ return createToken(Symbol.INSTANCEOF); }

/* keywords of primitive types */
<YYINITIAL> "byte"			{ return createToken(Symbol.BYTE); }
<YYINITIAL> "char"			{ return createToken(Symbol.CHAR); }
<YYINITIAL> "short"			{ return createToken(Symbol.SHORT); }
<YYINITIAL> "int"			{ return createToken(Symbol.INT); }
<YYINITIAL> "long"			{ return createToken(Symbol.LONG); }
<YYINITIAL> "float"			{ return createToken(Symbol.FLOAT); }
<YYINITIAL> "double"		{ return createToken(Symbol.DOUBLE); }
<YYINITIAL> "boolean"		{ return createToken(Symbol.BOOLEAN); }
<YYINITIAL> "true"			{ return createToken(Symbol.TRUE); }
<YYINITIAL> "false"			{ return createToken(Symbol.FALSE); }
<YYINITIAL> "void"			{ return createToken(Symbol.VOID); }

/* keywords of control instructions */
<YYINITIAL> "if"			{ return createToken(Symbol.IF); }
<YYINITIAL> "else"			{ return createToken(Symbol.ELSE); }
<YYINITIAL> "switch"		{ return createToken(Symbol.SWITCH); }
<YYINITIAL> "case"			{ return createToken(Symbol.CASE); }
<YYINITIAL> "default"		{ return createToken(Symbol.DEFAULT); }
<YYINITIAL> "for"			{ return createToken(Symbol.FOR); }
<YYINITIAL> "while"			{ return createToken(Symbol.WHILE); }
<YYINITIAL> "do"			{ return createToken(Symbol.DO); }
<YYINITIAL> "continue"		{ return createToken(Symbol.CONTINUE); }
<YYINITIAL> "break"			{ return createToken(Symbol.BREAK); }
<YYINITIAL> "return"		{ return createToken(Symbol.RETURN); }

/* keywords about classes or packages */
<YYINITIAL> "package"		{ return createToken(Symbol.PACKAGE); }
<YYINITIAL> "import"		{ return createToken(Symbol.IMPORT); }
<YYINITIAL> "class"			{ return createToken(Symbol.CLASS); }
<YYINITIAL> "interface"		{ return createToken(Symbol.INTERFACE); }
<YYINITIAL> "extends"		{ return createToken(Symbol.EXTENDS); }
<YYINITIAL> "implements"	{ return createToken(Symbol.IMPLEMENTS); }
<YYINITIAL> "this"			{ return createToken(Symbol.THIS); }
<YYINITIAL> "super"			{ return createToken(Symbol.SUPER); }
<YYINITIAL> "new"			{ return createToken(Symbol.NEW); }

/* keywords of modifiers */
<YYINITIAL> "public"		{ return createToken(Symbol.PUBLIC); }
<YYINITIAL> "protected"		{ return createToken(Symbol.PROTECTED); }
<YYINITIAL> "private"		{ return createToken(Symbol.PRIVATE); }
<YYINITIAL> "final"			{ return createToken(Symbol.FINAL); }
<YYINITIAL> "static"		{ return createToken(Symbol.STATIC); }
<YYINITIAL> "abstract"		{ return createToken(Symbol.ABSTRACT); }
<YYINITIAL> "native"		{ return createToken(Symbol.NATIVE); }
<YYINITIAL> "synchronized"	{ return createToken(Symbol.SYNCHRONIZED); }
<YYINITIAL> "volatile"		{ return createToken(Symbol.VOLATILE); }
<YYINITIAL> "transient"		{ return createToken(Symbol.TRANSIENT); }

/* keywords for exception handling */
<YYINITIAL> "try"			{ return createToken(Symbol.TRY); }
<YYINITIAL> "catch"			{ return createToken(Symbol.CATCH); }
<YYINITIAL> "finally"		{ return createToken(Symbol.FINALLY); }
<YYINITIAL> "throw"			{ return createToken(Symbol.THROW); }
<YYINITIAL> "throws"		{ return createToken(Symbol.THROWS); }

/* keywords that can be used after version 1.4 */
<YYINITIAL> "assert"		{ return createToken(Symbol.ASSERT); }
<YYINITIAL> "enum"			{ return createToken(Symbol.ENUM); }

<YYINITIAL> {
	/* identifiers */ 
	{Identifier}			{ return createToken(yytext(), Symbol.IDENTIFIER); }
 
	/* literals */
	{DecIntegerLiteral}		{ return createToken(yytext(), Symbol.INTEGERLITERAL); }
	{HexIntegerLiteral}		{ return createToken(yytext(), Symbol.HEXINTEGERLITERAL); }
	{OctalIntegerLiteral}	{ return createToken(yytext(), Symbol.OCTALINTEGERLITERAL); }
	{FloatingPointLiteral}	{ return createToken(yytext(), Symbol.FLOATINGPOINTLITERAL); }
	{CharacterLiteral}		{ return createToken(yytext(), Symbol.CHARACTERLITERAL); }
	\"						{ string.setLength(0); yybegin(STRING); }

	/* separators */
	"("						{ return createToken(Symbol.LPAREN); }
	")"						{ return createToken(Symbol.RPAREN); }
	"{"						{ return createToken(Symbol.LBRACE); }
	"}"						{ return createToken(Symbol.RBRACE); }
	"["						{ return createToken(Symbol.LBLACKET); }
	"]"						{ return createToken(Symbol.RBLACKET); }
	";"						{ return createToken(Symbol.SEMICOLON); }
	","						{ return createToken(Symbol.COMMA); }

	/* operators */
	"="						{ return createToken(Symbol.EQUAL); }
	">"						{ return createToken(Symbol.GREATER); }
	"<"						{ return createToken(Symbol.LESS); }
	"!"						{ return createToken(Symbol.NOT); }
	"~"						{ return createToken(Symbol.TILDE); }
	"?"						{ return createToken(Symbol.QUESTIONMARK); }
	":"						{ return createToken(Symbol.COLON); }
	"=="					{ return createToken(Symbol.EQUALEQUAL); }
	"<="					{ return createToken(Symbol.LESSEQUAL); }
	">="					{ return createToken(Symbol.GREATEREQUAL); }
	"!="					{ return createToken(Symbol.NOTEQUAL); }
	"&&"					{ return createToken(Symbol.ANDAND); }
	"||"					{ return createToken(Symbol.OR); }
	"++"					{ return createToken(Symbol.PLUSPLUS); }
	"--"					{ return createToken(Symbol.MINUSMINUS); }
	"+"						{ return createToken(Symbol.PLUS); }
	"-"						{ return createToken(Symbol.MINUS); }
	"*"						{ return createToken(Symbol.ASTERISK); }
	"/"						{ return createToken(Symbol.SLASH); }
	"&"						{ return createToken(Symbol.AND); }
	"|"						{ return createToken(Symbol.VERTICALVAR); }
	"^"						{ return createToken(Symbol.CIRCUMFLEX); }
	"%"						{ return createToken(Symbol.PERCENT); }
	"<<"					{ return createToken(Symbol.LDOUBLEANGLEBRACKET); }
	">>"					{ return createToken(Symbol.RDOUBLEANGLEBRACKET); }
	">>>"					{ return createToken(Symbol.RTRIPLEANGLEBRACKET); }
	"+="					{ return createToken(Symbol.PLUSEQUAL); }
	"-="					{ return createToken(Symbol.MINUSEQUAL); }
	"*="					{ return createToken(Symbol.ASTERISKEQUAL); }
	"/="					{ return createToken(Symbol.SLASHEQUAL); }
	"&="					{ return createToken(Symbol.ANDEQUAL); }
	"|="					{ return createToken(Symbol.VERTICALVAREQUAL); }
	"^="					{ return createToken(Symbol.CIRCUMFLEXEQUAL); }
	"%="					{ return createToken(Symbol.PERCENTEQUAL); }
	"<<="					{ return createToken(Symbol.LDOUBLEANGLEBRACKETEQUAL); }
	">>="					{ return createToken(Symbol.RDOUBLEANGLEBRACKETEQUAL); }
	">>>="					{ return createToken(Symbol.RTRIPLEANGLEBRACKETEQUAL); }

	/* dot */
	"."						{ return createToken(Symbol.DOT); }

	/* comments */
	{Comment}				{ /* ignore */ }
	
	/* annotations */
	{Annotation}			{ /* ignore */ }
 
	/* whitespace */
	{WhiteSpace}			{ /* ignore */ }
}

<STRING> {
	\" 						{ yybegin(YYINITIAL); 
								return createToken(string.toString(), Symbol.STRINGLITERAL); }
	[^\n\r\"\\]+			{ string.append( yytext() ); }
	\\t						{ string.append('\t'); }
	\\n						{ string.append('\n'); }

	\\r						{ string.append('\r'); }
	\\\"					{ string.append('\"'); }
	\\\\					{ string.append('\\'); }
}

/* unknown elements */
.|\n						{ return createToken(yytext(), Symbol.UNKNOWN); }