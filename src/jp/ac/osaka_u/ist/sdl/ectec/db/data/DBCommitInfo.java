package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents commits
 *
 * @author k-hotta
 *
 */
public class DBCommitInfo extends AbstractDBElement implements
		Comparable<DBCommitInfo> {

	/**
	 * the counter for having the number of created instances
	 */
	private static AtomicLong count = new AtomicLong(0);

	/**
	 * the repository id
	 */
	private final long repositoryId;

	/**
	 * the before revision id
	 */
	private final long beforeRevisionId;

	/**
	 * the after revision id
	 */
	private final long afterRevisionId;

	/**
	 * the identifier of the before revision
	 */
	private final String beforeRevisionIdentifier;

	/**
	 * the identifier of the after revision
	 */
	private final String afterRevisionIdentifier;

	/**
	 * the name of committer
	 */
	private final String committer;

	/**
	 *  the e-mail address of committer
	 */
	private final String committerEmail;

	/**
	 * the date of the commit
	 */
	private final Date date;

	public DBCommitInfo(final long repositoryId, final long beforeRevisionId,
			final long afterRevisionId, final String beforeRevisionIdentifier,
			final String afterRevisionIdentifier, final String committer,
			final String committerEmail, final Date date) {
		this(count.getAndIncrement(), repositoryId, beforeRevisionId,
				afterRevisionId, beforeRevisionIdentifier,
				afterRevisionIdentifier, committer, committerEmail, date);
	}

	public DBCommitInfo(final long id, final long repositoryId,
			final long beforeRevisionId, final long afterRevisionId,
			final String beforeRevisionIdentifier,
			final String afterRevisionIdentifier, final String committer,
			final String committerEmail, final Date date) {
		super(id);
		this.repositoryId = repositoryId;
		this.beforeRevisionId = beforeRevisionId;
		this.afterRevisionId = afterRevisionId;
		this.beforeRevisionIdentifier = beforeRevisionIdentifier;
		this.afterRevisionIdentifier = afterRevisionIdentifier;
		this.committer = committer;
		this.committerEmail = committerEmail;
		this.date = date;
	}

	/**
	 * reset the count with the given long value
	 *
	 * @param l
	 */
	public static void resetCount(final long l) {
		count = new AtomicLong(l);
	}

	/**
	 * get the repository id
	 *
	 * @return
	 */
	public final long getRepositoryId() {
		return this.repositoryId;
	}

	/**
	 * get the before revision id
	 *
	 * @return
	 */
	public final long getBeforeRevisionId() {
		return this.beforeRevisionId;
	}

	/**
	 * get the after revision id
	 *
	 * @return
	 */
	public final long getAfterRevisionId() {
		return this.afterRevisionId;
	}

	/**
	 * get the identifier of the before revision
	 *
	 * @return
	 */
	public final String getBeforeRevisionIdentifier() {
		return this.beforeRevisionIdentifier;
	}

	/**
	 * get the identifier of the after revision
	 *
	 * @return
	 */
	public final String getAfterRevisionIdentifier() {
		return this.afterRevisionIdentifier;
	}

	/**
	 * get the committer
	 *
	 * @return
	 */
	public final String getCommitter() {
		return this.committer;
	}

	/**
	 * get the committerEmail
	 *
	 * @return
	 */
	public final String getCommitterEmail() {
		return this.committerEmail;
	}
	/**
	 * get the date of the commit
	 *
	 * @return
	 */
	public final Date getDate() {
		return this.date;
	}

	@Override
	public int compareTo(DBCommitInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
