package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents combined revisions
 * 
 * @author k-hotta
 * 
 */
public class DBCombinedRevisionInfo extends AbstractDBElement implements
		Comparable<DBCombinedRevisionInfo> {

	/**
	 * a counter to keep the number of created elements
	 */
	private static AtomicLong count = new AtomicLong(0);

	/**
	 * a list that has ids of revisions included in this combined revision
	 */
	private final List<Long> originalRevisions;

	/**
	 * the constructor for elements that are retrieved from db
	 * 
	 * @param id
	 */
	public DBCombinedRevisionInfo(final long id,
			final List<Long> originalRevisions) {
		super(id);
		this.originalRevisions = originalRevisions;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param originalRevisions
	 */
	public DBCombinedRevisionInfo(final List<Long> originalRevisions) {
		this(count.getAndIncrement(), originalRevisions);
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
	 * get the list of original revisions
	 * 
	 * @return
	 */
	public final List<Long> getOriginalRevisions() {
		return Collections.unmodifiableList(originalRevisions);
	}

	@Override
	public int compareTo(DBCombinedRevisionInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
