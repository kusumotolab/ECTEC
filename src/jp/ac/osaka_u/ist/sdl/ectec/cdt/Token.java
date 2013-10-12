package jp.ac.osaka_u.ist.sdl.ectec.cdt;

/**
 * A class that represents a token
 * 
 * @author k-hotta
 * 
 */
public class Token {

	private final String str;

	private final Symbol symbol;

	private final int line;

	private final int column;

	public Token(final String str, final Symbol symbol, final int line,
			final int column) {
		this.str = str;
		this.symbol = symbol;
		this.line = line;
		this.column = column;
	}

	public final String getStr() {
		return str;
	}

	public final Symbol getSymbol() {
		return symbol;
	}

	public final int getLine() {
		return line;
	}

	public final int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return this.str;
	}

}
