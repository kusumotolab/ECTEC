package jp.ac.osaka_u.ist.sdl.ectec.cdt;

/**
 * A class that represents simplefied code fragments that are used in cdt
 * 
 * @author k-hotta
 * 
 */
public class InstantCodeFragmentInfo {

	private final String filePath;

	private final long fileId;

	private final int startLine;

	private final int startColumn;

	private final int endLine;

	private final int endColumn;

	private final long hash;

	private final int size;

	public InstantCodeFragmentInfo(final String filePath, final long fileId,
			final int startLine, final int startColumn, final int endLine,
			final int endColumn, final long hash, final int size) {
		this.filePath = filePath;
		this.fileId = fileId;
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
		this.hash = hash;
		this.size = size;
	}

	public final String getFilePath() {
		return filePath;
	}

	public final long getFileId() {
		return fileId;
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

	public final int getSize() {
		return size;
	}

	public final boolean subsume(final InstantCodeFragmentInfo another) {
		if (!this.filePath.equals(another.getFilePath())) {
			return false;
		}

		return this.startLine <= another.getStartLine()
				&& this.endLine >= another.getEndLine();

	}

}
