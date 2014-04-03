package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a genalogy of clones
 * 
 * @author k-hotta
 * 
 */
public class DBCloneGenealogyInfo extends
		AbstractDBGenealogyInfo<DBCloneSetInfo, DBCloneSetLinkInfo> implements
		Comparable<DBCloneGenealogyInfo> {

	/**
	 * a counter to keep the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param startRevisionId
	 * @param endRevisionId
	 * @param elements
	 * @param links
	 */
	public DBCloneGenealogyInfo(final long id, final long startRevisionId,
			final long endRevisionId, final List<Long> elements,
			final List<Long> links) {
		super(id, startRevisionId, endRevisionId, elements, links);
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param startRevisionId
	 * @param endRevisionId
	 * @param elements
	 * @param links
	 */
	public DBCloneGenealogyInfo(final long startRevisionId,
			final long endRevisionId, final List<Long> elements,
			final List<Long> links) {
		this(count.getAndIncrement(), startRevisionId, endRevisionId, elements,
				links);
	}

	@Override
	public int compareTo(DBCloneGenealogyInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
