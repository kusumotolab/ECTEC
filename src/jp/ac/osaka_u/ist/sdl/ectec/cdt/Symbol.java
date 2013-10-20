package jp.ac.osaka_u.ist.sdl.ectec.cdt;

/**
 * An enum that represents types of tokens
 * 
 * @author k-hotta
 * 
 */
public enum Symbol {

	/*
	 * key words
	 */

	ABSTRACT("abstract"),
	
	ALIGNAS("alignas"),

	ALIGNOF("alignof"),
	
	ASM("asm"),

	ASSERT("assert"),

	AUTO("auto"),

	BOOL("bool"),

	BOOLEAN("boolean"),

	BREAK("break"),

	BYTE("byte"),

	CASE("case"),

	CATCH("catch"),

	CHAR("char"),

	CHAR16_T("char16_t"),

	CHAR32_T("char32_t"),

	CLASS("class"),

	CONST("const"),

	CONST_CAST("const_cast"),

	CONSTEXPR("constexpr"),

	CONTINUE("continue"),

	DECLTYPE("decltype"),

	DEFAULT("default"),
	
	DEFINE("define"),

	DELETE("delete"),

	DO("do"),

	DOUBLE("double"),

	DYNAMIC_CAST("dynamic_cast"),

	ELIF("elif"),
	
	ELSE("else"),

	ENUM("enum"),
	
	ERROR("error"),

	EXPLICIT("explicit"),

	EXPORT("export"),

	EXTENDS("extends"),

	EXTERN("extern"),

	FALSE("false"),

	FINAL("final"),

	FINALLY("finally"),

	FLOAT("float"),

	FOR("for"),

	FRIEND("friend"),

	GOTO("goto"),

	IF("if"),
	
	IFDEF("ifdef"),
	
	IFNDEF("ifndef"),

	IMPLEMENTS("implements"),

	IMPORT("import"),
	
	INCLUDE("include"),

	INLINE("inline"),

	INT("int"),

	INSTANCEOF("instanceof"),

	INTERFACE("interface"),
	
	LINE("line"),

	LONG("long"),

	MUTABLE("mutable"),

	NEW("new"),

	NAMESPACE("namespace"),

	NATIVE("native"),

	NULL("null"),

	NULLPTR("nullptr"),

	OPERATOR("operator"),
	
	OVERRIDE("override"),

	PACKAGE("package"),
	
	PRAGMA("pragma"),

	PRIVATE("private"),

	PROTECTED("protected"),

	PUBLIC("public"),

	REGISTER("register"),

	RETURN("return"),

	REINTERPRET_CAST("reinterpret_cast"),

	SHORT("short"),

	SIGNED("signed"),

	SIZEOF("sizeof"),

	STATIC("static"),

	STATIC_ASSERT("static_assert"),

	STATIC_CAST("static_cast"),

	STRUCT("struct"),

	SUPER("super"),

	SWITCH("switch"),

	SYNCHRONIZED("synchronized"),

	TEMPLATE("template"),

	THIS("this"),

	THREAD_LOCAL("thread_local"),

	THROW("throw"),

	THROWS("throws"),

	TRANSIENT("transient"),

	TRUE("true"),

	TRY("try"),

	TYPEID("typeid"),

	TYPENAME("typename"),

	TYPE_DEF("type_def"),
	
	UNDEF("undef"),

	UNION("union"),

	UNSIGNED("unsigned"),

	USING("using"),

	VIRTUAL("virtual"),

	VOID("void"),

	VOLATILE("volatile"),

	WCHAR_T("wchar_t"),

	WHILE("while"),

	/*
	 * Symbolic characters
	 */

	LPAREN("("),

	RPAREN(")"),

	LBRACE("{"),

	RBRACE("}"),

	LBLACKET("["),

	RBLACKET("]"),

	SEMICOLON(";"),

	COMMA(","),

	EQUAL("="),

	EQUALEQUAL("=="),

	PLUS("+"),

	PLUSEQUAL("+="),

	GREATER(">"),

	GREATEREQUAL(">="),

	LESS("<"),

	LESSEQUAL("<="),

	MINUS("-"),

	MINUSEQUAL("-="),

	ASTERISK("*"),

	ASTERISKEQUAL("*="),

	NOT("!"),

	NOTEQUAL("!="),

	SLASH("/"),

	SLASHEQUAL("/="),

	TILDE("~"),

	ANDAND("&&"),

	AND("&"),

	ANDEQUAL("&="),

	QUESTIONMARK("?"),

	OR("||"),

	VERTICALVAR("|"),

	VERTICALVAREQUAL("|="),

	COLON(":"),

	COLONCOLON("::"),
	
	PLUSPLUS("++"),

	CIRCUMFLEX("^"),

	CIRCUMFLEXEQUAL("^="),

	MINUSMINUS("--"),

	PERCENT("%"),

	PERCENTEQUAL("%="),
	
	PERCENTCOLON("%:"),
	
	DOUBLEPERCENTCOLON("%:%:"),

	LBRACKETCOLON("<:"),
	
	LBRACKETPERCENT("<%"),
	
	LDOUBLEANGLEBRACKET("<<"),

	LDOUBLEANGLEBRACKETEQUAL("<<="),

	RDOUBLEANGLEBRACKET(">>"),

	RDOUBLEANGLEBRACKETEQUAL(">>="),

	RTRIPLEANGLEBRACKET(">>>"),

	RTRIPLEANGLEBRACKETEQUAL(">>>="),
	
	COLONRBRACKET(":>"),
	
	PERCENTRBRACKET("%>"),
	
	RIGHTARROW("->"),
	
	RIGHTARROWASTERISK("->*"),

	IDENTIFIER("IDENTIFIER"),

	STRINGLITERAL("STRING_LITERAL"),

	INTEGERLITERAL("INTEGER_LITERAL"),

	HEXINTEGERLITERAL("HEX_INTEGER_LITERAL"),

	OCTALINTEGERLITERAL("OCTAL_INTEGER_LITERAL"),

	FLOATINGPOINTLITERAL("FLOATING_POINT_LITERAL"),

	CHARACTERLITERAL("CHARACTER_LITERAL"),

	DOT("."),
	
	DOTASTERISK(".*"),
	
	LDOTS("..."),
	
	SHARP("#"),
	
	SHARPSHARP("##"),

	/*
	 * unknown token
	 */

	UNKNOWN("UNKNOWN");

	/*
	 * definitions of methods follow
	 */

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
