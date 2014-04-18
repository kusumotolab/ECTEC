package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a link between two code fragments
 * 
 * @author k-hotta
 * 
 */
public class DBCodeFragmentLinkInfo extends AbstractDBElementLinkInfo implements
		Comparable<DBCodeFragmentLinkInfo> {

	/**
	 * a counter to keep the number of created elements
	 */
	private static AtomicLong count = new AtomicLong(0);

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param beforeElementId
	 * @param afterElementId
	 * @param beforeCombinedRevisionId
	 * @param afterCombinedRevisionId
	 */
	public DBCodeFragmentLinkInfo(final long id, final long beforeElementId,
			final long afterElementId, final long beforeCombinedRevisionId,
			final long afterCombinedRevisionId) {
		super(id, beforeElementId, afterElementId, beforeCombinedRevisionId,
				afterCombinedRevisionId);
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param beforeElementId
	 * @param afterElementId
	 * @param beforeCombinedRevisionId
	 * @param afterCombinedRevisionId
	 */
	public DBCodeFragmentLinkInfo(final long beforeElementId,
			final long afterElementId, final long beforeCombinedRevisionId,
			final long afterCombinedRevisionId) {
		this(count.getAndIncrement(), beforeElementId, afterElementId,
				beforeCombinedRevisionId, afterCombinedRevisionId);
	}
	
	/**
	 * reset the count with the given long value
	 * 
	 * @param l
	 */
	public static void resetCount(final long l) {
		count = new AtomicLong(l);
	}


	@Override
	public int compareTo(DBCodeFragmentLinkInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
