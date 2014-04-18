package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a file
 * 
 * @author k-hotta
 * 
 */
public class DBFileInfo extends AbstractDBElement implements
		Comparable<DBFileInfo> {

	/**
	 * a counter to keep the number of created elements
	 */
	private static AtomicLong count = new AtomicLong(0);

	/**
	 * the id of the owner repository
	 */
	private final long ownerRepositoryId;

	/**
	 * the path of this file
	 */
	private final String path;

	/**
	 * the id of the start combined revision
	 */
	private final long startCombinedRevisionId;

	/**
	 * the id of the end revision
	 */
	private final long endCombinedRevisionid;

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param ownerRepositoryId
	 * @param path
	 * @param startCombinedRevisionId
	 * @param endCombinedRevisionId
	 */
	public DBFileInfo(final long id, final long ownerRepositoryId,
			final String path, final long startCombinedRevisionId,
			final long endCombinedRevisionId) {
		super(id);
		this.ownerRepositoryId = ownerRepositoryId;
		this.path = path;
		this.startCombinedRevisionId = startCombinedRevisionId;
		this.endCombinedRevisionid = endCombinedRevisionId;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param ownerRepositoryId
	 * @param path
	 * @param startCombinedRevisionId
	 * @param endCombinedRevisionId
	 */
	public DBFileInfo(final long ownerRepositoryId, final String path,
			final long startCombinedRevisionId, final long endCombinedRevisionId) {
		this(count.getAndIncrement(), ownerRepositoryId, path,
				startCombinedRevisionId, endCombinedRevisionId);
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
	 * get the id of the owner repository
	 * 
	 * @return
	 */
	public final long getOwnerRepositoryId() {
		return this.ownerRepositoryId;
	}

	/**
	 * get the path of this file
	 * 
	 * @return
	 */
	public final String getPath() {
		return this.path;
	}

	/**
	 * get the id of the start combined revision
	 * 
	 * @return
	 */
	public final long getStartCombinedRevisionId() {
		return this.startCombinedRevisionId;
	}

	/**
	 * get the id of the end combined revision
	 * 
	 * @return
	 */
	public final long getCombinedEndRevisionId() {
		return this.endCombinedRevisionid;
	}

	@Override
	public int compareTo(DBFileInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
