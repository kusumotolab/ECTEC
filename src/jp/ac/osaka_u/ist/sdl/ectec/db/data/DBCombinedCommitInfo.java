package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents combined commits
 * 
 * @author k-hotta
 * 
 */
public class DBCombinedCommitInfo extends AbstractDBElement implements
		Comparable<DBCombinedCommitInfo> {

	/**
	 * a counter to keep the nubmer of created elements
	 */
	private static AtomicLong count = new AtomicLong(0);

	/**
	 * the id of the before combined revision
	 */
	private final long beforeCombinedRevisionId;

	/**
	 * the id of the after combined revision
	 */
	private final long afterCombinedRevisionId;

	/**
	 * the id of the original commit
	 */
	private final long originalCommitId;

	/**
	 * the constructor for elements retrieved from db
	 * 
	 * @param id
	 * @param beforeCombinedRevisionId
	 * @param afterCombinedRevisionId
	 * @param originalCommitId
	 */
	public DBCombinedCommitInfo(final long id,
			final long beforeCombinedRevisionId,
			final long afterCombinedRevisionId, final long originalCommitId) {
		super(id);
		this.beforeCombinedRevisionId = beforeCombinedRevisionId;
		this.afterCombinedRevisionId = afterCombinedRevisionId;
		this.originalCommitId = originalCommitId;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param beforeCombinedRevisionId
	 * @param afterCombinedRevisionId
	 * @param originalCommitId
	 */
	public DBCombinedCommitInfo(final long beforeCombinedRevisionId,
			final long afterCombinedRevisionId, final long originalCommitId) {
		this(count.getAndIncrement(), beforeCombinedRevisionId,
				afterCombinedRevisionId, originalCommitId);
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
	 * get the id of the before combined revision
	 * 
	 * @return
	 */
	public final long getBeforeCombinedRevisionId() {
		return this.beforeCombinedRevisionId;
	}

	/**
	 * get the id of the after combined revision
	 * 
	 * @return
	 */
	public final long getAfterCombinedRevisionId() {
		return this.afterCombinedRevisionId;
	}

	/**
	 * get the id of the original commit
	 * 
	 * @return
	 */
	public final long getOriginalCommitId() {
		return this.originalCommitId;
	}

	@Override
	public int compareTo(DBCombinedCommitInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
