/* This is a lexer for C/C++ */

package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.util.List;
import java.util.ArrayList;
import jp.ac.osaka_u.ist.sdl.ectec.cdt.Token;
import jp.ac.osaka_u.ist.sdl.ectec.cdt.Symbol;
import jp.ac.osaka_u.ist.sdl.ectec.cdt.Lexer;

%%

/* The name of class created from this file is JavaLexer */
%public
%class CPPLexer

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
  	return new Token(str, sym, yyline+1, yycolumn+1, yychar+1);
  }
  
  private Token createToken(Symbol sym) {
  	return new Token(sym.getStr(), sym, yyline+1, yycolumn+1, yychar+1);
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
Comment = {TraditionalComment} | {EndOfLineComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}
CommentContent       = ( [^*] | \*+ [^/*] )*

/* identifier */
Identifier = {SingleCharacter}{SingleCharacter}*

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
<YYINITIAL> "int"			{ return createToken(Symbol.INT); }
<YYINITIAL> "long"			{ return createToken(Symbol.LONG); }
<YYINITIAL> "short"			{ return createToken(Symbol.SHORT); }
<YYINITIAL> "signed"			{ return createToken(Symbol.SIGNED); }
<YYINITIAL> "unsigned"			{ return createToken(Symbol.UNSIGNED); }
<YYINITIAL> "float"			{ return createToken(Symbol.FLOAT); }
<YYINITIAL> "double"		{ return createToken(Symbol.DOUBLE); }
<YYINITIAL> "bool"		{ return createToken(Symbol.BOOL); }
<YYINITIAL> "true"			{ return createToken(Symbol.TRUE); }
<YYINITIAL> "false"			{ return createToken(Symbol.FALSE); }
<YYINITIAL> "char"			{ return createToken(Symbol.CHAR); }
<YYINITIAL> "wchar_t"			{ return createToken(Symbol.WCHAR_T); }
<YYINITIAL> "char16_t"			{ return createToken(Symbol.CHAR16_T); }
<YYINITIAL> "char32_t"			{ return createToken(Symbol.CHAR32_T); }

/* keywords of control instructions */
<YYINITIAL> "if"			{ return createToken(Symbol.IF); }
<YYINITIAL> "else"			{ return createToken(Symbol.ELSE); }
<YYINITIAL> "for"			{ return createToken(Symbol.FOR); }
<YYINITIAL> "while"			{ return createToken(Symbol.WHILE); }
<YYINITIAL> "do"			{ return createToken(Symbol.DO); }
<YYINITIAL> "switch"		{ return createToken(Symbol.SWITCH); }
<YYINITIAL> "case"			{ return createToken(Symbol.CASE); }
<YYINITIAL> "default"		{ return createToken(Symbol.DEFAULT); }
<YYINITIAL> "continue"		{ return createToken(Symbol.CONTINUE); }
<YYINITIAL> "break"			{ return createToken(Symbol.BREAK); }
<YYINITIAL> "goto"			{ return createToken(Symbol.GOTO); }
<YYINITIAL> "return"		{ return createToken(Symbol.RETURN); }
<YYINITIAL> "try"			{ return createToken(Symbol.TRY); }
<YYINITIAL> "catch"			{ return createToken(Symbol.CATCH); }
<YYINITIAL> "throw"			{ return createToken(Symbol.THROW); }

/* keywords for expressions */
<YYINITIAL> "nullptr"			{ return createToken(Symbol.NULLPTR); }
<YYINITIAL> "new"			{ return createToken(Symbol.NEW); }
<YYINITIAL> "delete"			{ return createToken(Symbol.DELETE); }
<YYINITIAL> "dynamic_cast"			{ return createToken(Symbol.DYNAMIC_CAST); }
<YYINITIAL> "static_cast"			{ return createToken(Symbol.STATIC_CAST); }
<YYINITIAL> "const_cast"			{ return createToken(Symbol.CONST_CAST); }
<YYINITIAL> "reinterpret_cast"			{ return createToken(Symbol.REINTERPRET_CAST); }
<YYINITIAL> "alignof"			{ return createToken(Symbol.ALIGNOF); }
<YYINITIAL> "decltype"			{ return createToken(Symbol.DECLTYPE); }
<YYINITIAL> "sizeof"			{ return createToken(Symbol.SIZEOF); }
<YYINITIAL> "typeid"			{ return createToken(Symbol.TYPEID); }
<YYINITIAL> "static_assert"			{ return createToken(Symbol.STATIC_ASSERT); }

/* keywords about classes or packages */
<YYINITIAL> "class"			{ return createToken(Symbol.CLASS); }
<YYINITIAL> "struct"			{ return createToken(Symbol.STRUCT); }
<YYINITIAL> "union"			{ return createToken(Symbol.UNION); }
<YYINITIAL> "enum"			{ return createToken(Symbol.ENUM); }

/* keywords of modifiers */
<YYINITIAL> "const"			{ return createToken(Symbol.CONST); }
<YYINITIAL> "volatile"		{ return createToken(Symbol.VOLATILE); }
<YYINITIAL> "extern"		{ return createToken(Symbol.EXTERN); }
<YYINITIAL> "register"		{ return createToken(Symbol.REGISTER); }
<YYINITIAL> "static"		{ return createToken(Symbol.STATIC); }
<YYINITIAL> "mutable"		{ return createToken(Symbol.MUTABLE); }
<YYINITIAL> "thread_local"		{ return createToken(Symbol.THREAD_LOCAL); }
<YYINITIAL> "friend"		{ return createToken(Symbol.FRIEND); }
<YYINITIAL> "type_def"		{ return createToken(Symbol.TYPE_DEF); }
<YYINITIAL> "constexpr"		{ return createToken(Symbol.CONSTEXPR); }
<YYINITIAL> "explicit"		{ return createToken(Symbol.EXPLICIT); }
<YYINITIAL> "inline"		{ return createToken(Symbol.INLINE); }
<YYINITIAL> "virtual"		{ return createToken(Symbol.VIRTUAL); }
<YYINITIAL> "public"		{ return createToken(Symbol.PUBLIC); }
<YYINITIAL> "protected"		{ return createToken(Symbol.PROTECTED); }
<YYINITIAL> "private"		{ return createToken(Symbol.PRIVATE); }
<YYINITIAL> "this"		{ return createToken(Symbol.THIS); }
<YYINITIAL> "operator"		{ return createToken(Symbol.OPERATOR); }

/* keywords for template */
<YYINITIAL> "template"		{ return createToken(Symbol.TEMPLATE); }
<YYINITIAL> "typename"		{ return createToken(Symbol.TYPENAME); }
<YYINITIAL> "export"		{ return createToken(Symbol.EXPORT); }

/* keywords for header files */
<YYINITIAL> "define"		{ return createToken(Symbol.DEFINE); }
<YYINITIAL> "undef"		{ return createToken(Symbol.UNDEF); }
<YYINITIAL> "include"		{ return createToken(Symbol.INCLUDE); }
<YYINITIAL> "line"		{ return createToken(Symbol.LINE); }
<YYINITIAL> "error"		{ return createToken(Symbol.ERROR); }
<YYINITIAL> "pragma"		{ return createToken(Symbol.PRAGMA); }
<YYINITIAL> "ifdef"		{ return createToken(Symbol.IFDEF); }
<YYINITIAL> "ifndef"		{ return createToken(Symbol.IFNDEF); }
<YYINITIAL> "elif"		{ return createToken(Symbol.ELIF); }

/* other keywords */
<YYINITIAL> "namespace"		{ return createToken(Symbol.NAMESPACE); }
<YYINITIAL> "using"		{ return createToken(Symbol.USING); }
<YYINITIAL> "asm"		{ return createToken(Symbol.ASM); }
<YYINITIAL> "alignas"		{ return createToken(Symbol.ALIGNAS); }
<YYINITIAL> "final"		{ return createToken(Symbol.FINAL); }
<YYINITIAL> "override"		{ return createToken(Symbol.OVERRIDE); }

/* alternative representation */
<YYINITIAL> "and"		{ return createToken(Symbol.ANDAND); }
<YYINITIAL> "and_eq"		{ return createToken(Symbol.ANDEQUAL); }
<YYINITIAL> "bitand"		{ return createToken(Symbol.AND); }
<YYINITIAL> "bitor"		{ return createToken(Symbol.VERTICALVAR); }
<YYINITIAL> "compl"		{ return createToken(Symbol.TILDE); }
<YYINITIAL> "not"		{ return createToken(Symbol.NOT); }
<YYINITIAL> "not_eq"		{ return createToken(Symbol.NOTEQUAL); }
<YYINITIAL> "or"		{ return createToken(Symbol.OR); }
<YYINITIAL> "or_eq"		{ return createToken(Symbol.VERTICALVAREQUAL); }
<YYINITIAL> "xor"		{ return createToken(Symbol.CIRCUMFLEX); }
<YYINITIAL> "xor_eq"		{ return createToken(Symbol.CIRCUMFLEXEQUAL); }

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
	"::"						{ return createToken(Symbol.COLONCOLON); }
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
	"#"					{ return createToken(Symbol.SHARP); }
	"##"					{ return createToken(Symbol.SHARPSHARP); }
	"<:"					{ return createToken(Symbol.LBRACKETCOLON); }
	":>"					{ return createToken(Symbol.COLONRBRACKET); }
	"<%"					{ return createToken(Symbol.LBRACKETPERCENT); }
	"%>"					{ return createToken(Symbol.PERCENTRBRACKET); }
	"%:"					{ return createToken(Symbol.PERCENTCOLON); }
	"%:%:"					{ return createToken(Symbol.DOUBLEPERCENTCOLON); }
	"->"					{ return createToken(Symbol.RIGHTARROW); }
	"->*"					{ return createToken(Symbol.RIGHTARROWASTERISK); }

	/* dot */
	"."						{ return createToken(Symbol.DOT); }
	".*"						{ return createToken(Symbol.DOTASTERISK); }
	"..."						{ return createToken(Symbol.LDOTS); }

	/* comments */
	{Comment}				{ /* ignore */ }
 
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