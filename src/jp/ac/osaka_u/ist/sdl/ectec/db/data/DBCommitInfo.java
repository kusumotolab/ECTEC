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
	private static final AtomicLong count = new AtomicLong(0);

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
	 * the date of the commit
	 */
	private final Date date;

	public DBCommitInfo(final long repositoryId, final long beforeRevisionId,
			final long afterRevisionId, final Date date) {
		this(count.getAndIncrement(), repositoryId, beforeRevisionId,
				afterRevisionId, date);
	}

	public DBCommitInfo(final long id, final long repositoryId,
			final long beforeRevisionId, final long afterRevisionId,
			final Date date) {
		super(id);
		this.repositoryId = repositoryId;
		this.beforeRevisionId = beforeRevisionId;
		this.afterRevisionId = afterRevisionId;
		this.date = date;
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
