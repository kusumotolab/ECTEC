package jp.ac.osaka_u.ist.sdl.ectec.main.filedetector;

import java.util.Date;

/**
 * A class that represents a change on a file
 * 
 * @author k-hotta
 * 
 */
public class ChangeOnFile implements Comparable<ChangeOnFile> {

	/**
	 * the id of the repository
	 */
	private final long repositoryId;

	/**
	 * the path of the file
	 */
	private final String path;

	/**
	 * the id of the combined commit where this file was changed
	 */
	private final long combinedCommitId;

	/**
	 * the date of the commit where this file was changed
	 */
	private final Date date;

	/**
	 * the type of the change
	 */
	private final ChangeTypeOnFile type;

	public ChangeOnFile(final long repositoryId, final String path,
			final long combinedCommitId, final Date date,
			final ChangeTypeOnFile type) {
		this.repositoryId = repositoryId;
		this.path = path;
		this.combinedCommitId = combinedCommitId;
		this.date = date;
		this.type = type;
	}

	/**
	 * get the id of the repository
	 * 
	 * @return
	 */
	public final long getRepositoryId() {
		return repositoryId;
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
	 * get the id of the combined commit where this file was changed
	 * 
	 * @return
	 */
	public final long getCombinedCommitId() {
		return this.combinedCommitId;
	}

	/**
	 * get the date of the commit where this file was changed
	 * 
	 * @return
	 */
	public final Date getDate() {
		return this.date;
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
		int basedOnDate = this.date.compareTo(another.getDate());
		if (basedOnDate != 0) {
			return basedOnDate;
		} else {
			return ((Long) this.combinedCommitId).compareTo(another
					.getCombinedCommitId());
		}
	}

}
