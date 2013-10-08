package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a file
 * 
 * @author k-hotta
 * 
 */
public class DBFileInfo extends AbstractDBElement implements Comparable<DBFileInfo> {

	/**
	 * a counter to keep the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * the path of this file
	 */
	private final String path;

	/**
	 * the id of the start revision
	 */
	private final long startRevisionId;

	/**
	 * the id of the end revision
	 */
	private final long endRevisionid;

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param path
	 * @param startRevisionId
	 * @param endRevisionId
	 */
	public DBFileInfo(final long id, final String path,
			final long startRevisionId, final long endRevisionId) {
		super(id);
		this.path = path;
		this.startRevisionId = startRevisionId;
		this.endRevisionid = endRevisionId;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param path
	 * @param startRevisionId
	 * @param endRevisionId
	 */
	public DBFileInfo(final String path, final long startRevisionId,
			final long endRevisionId) {
		this(count.getAndIncrement(), path, startRevisionId, endRevisionId);
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
	 * get the id of the start revision
	 * 
	 * @return
	 */
	public final long getStartRevisionId() {
		return this.startRevisionId;
	}

	/**
	 * get the id of the end revision
	 * 
	 * @return
	 */
	public final long getEndRevisionId() {
		return this.endRevisionid;
	}

	@Override
	public int compareTo(DBFileInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
