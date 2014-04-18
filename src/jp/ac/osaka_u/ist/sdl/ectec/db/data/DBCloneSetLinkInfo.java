package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a link between two clone sets
 * 
 * @author k-hotta
 * 
 */
public class DBCloneSetLinkInfo extends AbstractDBElementLinkInfo implements
		Comparable<DBCloneSetLinkInfo> {

	/**
	 * a counter to keep the number of created elements
	 */
	private static AtomicLong count = new AtomicLong(0);

	/**
	 * the list of the ids of code fragment links related to this clone set link
	 */
	private final List<Long> codeFragmentLinks;

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param beforeElementId
	 * @param afterElementId
	 * @param beforeCombinedRevisionId
	 * @param afterCombinedRevisionId
	 * @param codeFragmentLinks
	 */
	public DBCloneSetLinkInfo(final long id, final long beforeElementId,
			final long afterElementId, final long beforeCombinedRevisionId,
			final long afterCombinedRevisionId,
			final List<Long> codeFragmentLinks) {
		super(id, beforeElementId, afterElementId, beforeCombinedRevisionId,
				afterCombinedRevisionId);
		this.codeFragmentLinks = codeFragmentLinks;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param beforeElementId
	 * @param afterElementId
	 * @param beforeCombinedRevisionId
	 * @param afterCombinedRevisionId
	 * @param codeFragmentLinks
	 */
	public DBCloneSetLinkInfo(final long beforeElementId,
			final long afterElementId, final long beforeCombinedRevisionId,
			final long afterCombinedRevisionId,
			final List<Long> codeFragmentLinks) {
		this(count.getAndIncrement(), beforeElementId, afterElementId,
				beforeCombinedRevisionId, afterCombinedRevisionId,
				codeFragmentLinks);
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
	 * get the list of ids of code fragment links related to this clone set link
	 * 
	 * @return
	 */
	public final List<Long> getCodeFragmentLinks() {
		return Collections.unmodifiableList(codeFragmentLinks);
	}

	@Override
	public int compareTo(DBCloneSetLinkInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
