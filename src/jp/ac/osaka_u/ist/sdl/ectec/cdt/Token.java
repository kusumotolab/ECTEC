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

	private final int position;
	
	public Token(final String str, final Symbol symbol, final int line,
			final int column, final int position) {
		this.str = str;
		this.symbol = symbol;
		this.line = line;
		this.column = column;
		this.position = position;
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
	
	public final int getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return this.str;
	}

}
