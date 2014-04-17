package jp.ac.osaka_u.ist.sdl.ectec.main.filedetector;

/**
 * A class that represents a change on a file
 * 
 * @author k-hotta
 * 
 */
public class ChangeOnFile implements Comparable<ChangeOnFile> {

	/**
	 * the path of the file
	 */
	private final String path;

	/**
	 * the id of the commit where this file was changed
	 */
	private final long changedCommitId;

	/**
	 * the type of the change
	 */
	private final ChangeTypeOnFile type;

	public ChangeOnFile(final String path, final long changedCommitId,
			final ChangeTypeOnFile type) {
		this.path = path;
		this.changedCommitId = changedCommitId;
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
	 * get the id of the commit where this file was changed
	 * 
	 * @return
	 */
	public final long getChagnedCommitId() {
		return this.changedCommitId;
	}

	/**
	 * get the type of the change
	 * 
	 * @return
	 */
	public final ChangeTypeOnFile getChangeType() {
		return type;
	}

	@Override
	public int compareTo(ChangeOnFile another) {
		return ((Long) this.changedCommitId).compareTo(another
				.getChagnedCommitId());
	}

}
