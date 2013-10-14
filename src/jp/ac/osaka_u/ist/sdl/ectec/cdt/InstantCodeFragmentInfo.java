package jp.ac.osaka_u.ist.sdl.ectec.cdt;

/**
 * A class that represents simplefied code fragments that are used in cdt
 * 
 * @author k-hotta
 * 
 */
public class InstantCodeFragmentInfo {

	private final String filePath;

	private final int startLine;

	private final int startColumn;

	private final int endLine;

	private final int endColumn;

	private final long hash;

	public InstantCodeFragmentInfo(final String filePath, final int startLine,
			final int startColumn, final int endLine, final int endColumn,
			final long hash) {
		this.filePath = filePath;
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
		this.hash = hash;
	}

	public final String getFilePath() {
		return filePath;
	}

	public final int getStartLine() {
		return startLine;
	}
	
	public final int getStartColumn() {
		return startColumn;
	}

	public final int getEndLine() {
		return endLine;
	}
	
	public final int getEndColumn() {
		return endColumn;
	}

	public final long getHash() {
		return hash;
	}

}
