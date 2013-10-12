package jp.ac.osaka_u.ist.sdl.ectec.cdt;

/**
 * An enum that represents types of tokens
 * 
 * @author k-hotta
 * 
 */
public enum Symbol {

	BYTE("byte"), CHAR("char"), SHORT("short"), INT("int"), LONG("long"), FLOAT(
			"float"), DOUBLE("double"), BOOLEAN("boolean"), TRUE("true"), FALSE(
			"false"), VOID("void"), IF("if"), ELSE("else"), SWITCH("switch"), CASE(
			"case"), DEFAULT("default"), FOR("for"), WHILE("while"), DO("do"), CONTINUE(
			"continue"), BREAK("break"), RETURN("return"), PACKAGE("package"), IMPORT(
			"import"), CLASS("class"), INTERFACE("interface"), EXTENDS(
			"extends"), IMPLEMENTS("implements"), THIS("this"), SUPER("super"), NEW(
			"new"), NULL("null"), INSTANCEOF("instanceof"), PUBLIC("public"), PROTECTED(
			"protected"), PRIVATE("private"), FINAL("final"), STATIC("static"), ABSTRACT(
			"abstract"), NATIVE("native"), SYNCHRONIZED("synchronized"), VOLATILE(
			"volatile"), TRANSIENT("transient"), TRY("try"), CATCH("catch"), FINALLY(
			"finally"), THROW("throw"), THROWS("throws"), ASSERT("assert"), ENUM(
			"enum"), LPAREN("("), RPAREN(")"), LBRACE("{"), RBRACE("}"), LBLACKET(
			"["), RBLACKET("]"), SEMICOLON(";"), COMMA(","), EQUAL("="), EQUALEQUAL(
			"=="), PLUS("+"), PLUSEQUAL("+="), GREATER(">"), GREATEREQUAL(">="), LESS(
			"<"), LESSEQUAL("<="), MINUS("-"), MINUSEQUAL("-="), ASTERISK("*"), ASTERISKEQUAL(
			"*="), NOT("!"), NOTEQUAL("!="), SLASH("/"), SLASHEQUAL("/="), TILDE(
			"~"), ANDAND("&&"), AND("&"), ANDEQUAL("&="), QUESTIONMARK("?"), OR(
			"||"), VERTICALVAR("|"), VERTICALVAREQUAL("|="), COLON(":"), PLUSPLUS(
			"++"), CIRCUMFLEX("^"), CIRCUMFLEXEQUAL("^="), MINUSMINUS("--"), PERCENT(
			"%"), PERCENTEQUAL("%="), LDOUBLEANGLEBRACKET("<<"), LDOUBLEANGLEBRACKETEQUAL(
			"<<="), RDOUBLEANGLEBRACKET(">>"), RDOUBLEANGLEBRACKETEQUAL(">>="), RTRIPLEANGLEBRACKET(
			">>>"), RTRIPLEANGLEBRACKETEQUAL(">>>="), IDENTIFIER("IDENTIFIER"), STRINGLITERAL(
			"STRING_LITERAL"), INTEGERLITERAL("INTEGER_LITERAL"), HEXINTEGERLITERAL(
			"HEX_INTEGER_LITERAL"), OCTALINTEGERLITERAL("OCTAL_INTEGER_LITERAL"), FLOATINGPOINTLITERAL(
			"FLOATING_POINT_LITERAL"), CHARACTERLITERAL("CHARACTER_LITERAL"), DOT(
			"."), UNKNOWN("UNKNOWN");

	private final String str;

	private Symbol(final String str) {
		this.str = str;
	}

	private static Symbol[] values = values();

	public static Symbol getCorrespondingSymbol(final String symbolStr) {
		for (final Symbol symbol : values) {
			if (symbol.getStr().equals(symbolStr)) {
				return symbol;
			}
		}

		return UNKNOWN;
	}

	public final String getStr() {
		return str;
	}

	public final String toString() {
		return str;
	}

}
