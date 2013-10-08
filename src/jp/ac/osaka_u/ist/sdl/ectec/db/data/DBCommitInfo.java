package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents commits
 * 
 * @author k-hotta
 * 
 */
public class DBCommitInfo extends AbstractDBElement implements Comparable<DBCommitInfo> {

	/**
	 * the counter for having the number of created instances
	 */
	private static final AtomicLong count = new AtomicLong(0);

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

	public DBCommitInfo(final long beforeRevisionId, final long afterRevisionId,
			final String beforeRevisionIdentifier,
			final String afterRevisionIdentifier) {
		super(count.getAndIncrement());
		this.beforeRevisionId = beforeRevisionId;
		this.afterRevisionId = afterRevisionId;
		this.beforeRevisionIdentifier = beforeRevisionIdentifier;
		this.afterRevisionIdentifier = afterRevisionIdentifier;
	}

	public DBCommitInfo(final long id, final long beforeRevisionId,
			final long afterRevisionId, final String beforeRevisionIdentifier,
			final String afterRevisionIdentifier) {
		super(id);
		this.beforeRevisionId = beforeRevisionId;
		this.afterRevisionId = afterRevisionId;
		this.beforeRevisionIdentifier = beforeRevisionIdentifier;
		this.afterRevisionIdentifier = afterRevisionIdentifier;
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
	 * get the before revision identifier
	 * 
	 * @return
	 */
	public final String getBeforeRevisionIdentifier() {
		return this.beforeRevisionIdentifier;
	}

	/**
	 * get the after revision identifier
	 * 
	 * @return
	 */
	public final String getAfterRevisionIdentifier() {
		return this.afterRevisionIdentifier;
	}

	@Override
	public int compareTo(DBCommitInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
