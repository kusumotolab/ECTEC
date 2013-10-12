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

	private final int endLine;

	private final long hash;

	public InstantCodeFragmentInfo(final String filePath, final int startLine,
			final int endLine, final long hash) {
		this.filePath = filePath;
		this.startLine = startLine;
		this.endLine = endLine;
		this.hash = hash;
	}

	public final String getFilePath() {
		return filePath;
	}

	public final int getStartLine() {
		return startLine;
	}

	public final int getEndLine() {
		return endLine;
	}

	public final long getHash() {
		return hash;
	}

}
