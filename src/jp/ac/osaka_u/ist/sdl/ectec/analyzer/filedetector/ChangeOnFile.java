package jp.ac.osaka_u.ist.sdl.ectec.analyzer.filedetector;

/**
 * A class that represents a change on a file
 * 
 * @author k-hotta
 * 
 */
public class ChangeOnFile {

	/**
	 * the path of the file
	 */
	private final String path;

	/**
	 * the id of the revision where this file was changed
	 */
	private final long changedRevisionId;

	/**
	 * the type of the change
	 */
	private final ChangeTypeOnFile type;

	public ChangeOnFile(final String path, final long changedRevisionId,
			final ChangeTypeOnFile type) {
		this.path = path;
		this.changedRevisionId = changedRevisionId;
		this.type = type;
	}

	/**
	 * get the path of the file
	 * 
	 * @return
	 */
	public final String getPath() {
		return path;
	}

	/**
	 * get the id of the revision where this file was changed
	 * 
	 * @return
	 */
	public final long getChagnedRevisionId() {
		return this.changedRevisionId;
	}

	/**
	 * get the type of the change
	 * 
	 * @return
	 */
	public final ChangeTypeOnFile getChangeType() {
		return type;
	}

}
