package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a genealogy of code fragments
 * 
 * @author k-hotta
 * 
 */
public class DBCodeFragmentGenealogyInfo extends
		AbstractDBGenealogyInfo<DBCodeFragmentInfo, DBCodeFragmentLinkInfo>
		implements Comparable<DBCodeFragmentGenealogyInfo> {

	/**
	 * the counter to have the number of created elements
	 */
	private static AtomicLong count = new AtomicLong(0);

	public DBCodeFragmentGenealogyInfo(final long id,
			final long startRevisionId, final long endRevisionId,
			final List<Long> elements, final List<Long> links) {
		super(id, startRevisionId, endRevisionId, elements, links);
	}

	public DBCodeFragmentGenealogyInfo(final long startRevisionId,
			final long endRevisionId, final List<Long> elements,
			final List<Long> links) {
		this(count.getAndIncrement(), startRevisionId, endRevisionId, elements,
				links);
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
	public int compareTo(DBCodeFragmentGenealogyInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
