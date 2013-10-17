package jp.ac.osaka_u.ist.sdl.ectec.cdt;

public class InstantFileInfo {

	private final long fileId;

	private final String path;

	private final int tokens;

	private final int lines;

	public InstantFileInfo(final long fileId, final String path,
			final int tokens, final int lines) {
		this.fileId = fileId;
		this.path = path;
		this.tokens = tokens;
		this.lines = lines;
	}

	public final long getFileId() {
		return fileId;
	}

	public final String getPath() {
		return path;
	}

	public final int getTokens() {
		return tokens;
	}

	public final int getLines() {
		return lines;
	}

}
