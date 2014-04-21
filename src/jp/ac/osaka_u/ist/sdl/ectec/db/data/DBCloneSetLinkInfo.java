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
	 * the list of ids of related fragment links
	 */
	private final List<Long> fragmentLinks;

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param beforeElementId
	 * @param afterElementId
	 * @param beforeCombinedRevisionId
	 * @param afterCombinedRevisionId
	 */
	public DBCloneSetLinkInfo(final long id, final long beforeElementId,
			final long afterElementId, final long beforeCombinedRevisionId,
			final long afterCombinedRevisionId, final List<Long> fragmentLinks) {
		super(id, beforeElementId, afterElementId, beforeCombinedRevisionId,
				afterCombinedRevisionId);
		this.fragmentLinks = fragmentLinks;
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
			final long afterCombinedRevisionId, final List<Long> fragmentLinks) {
		this(count.getAndIncrement(), beforeElementId, afterElementId,
				beforeCombinedRevisionId, afterCombinedRevisionId,
				fragmentLinks);
	}

	/**
	 * get the list of ids of related fragment links
	 * 
	 * @return
	 */
	public final List<Long> getCodeFragmentLinks() {
		return Collections.unmodifiableList(fragmentLinks);
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
	public int compareTo(DBCloneSetLinkInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
