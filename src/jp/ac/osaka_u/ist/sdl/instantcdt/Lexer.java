package jp.ac.osaka_u.ist.sdl.instantcdt;

import java.util.List;

/**
 * An abstract class that represents a lexer
 * 
 * @author k-hotta
 * 
 */
public abstract class Lexer {

	protected int revision;

	public Lexer() {
		this.revision = -1;
	}

	public Lexer(final int revision) {
		this.revision = revision;
	}

	public final int getRevision() {
		return revision;
	}

	public final void setRevision(final int revision) {
		this.revision = revision;
	}

	public abstract List<Token> runLexicalAnalysis();

}
