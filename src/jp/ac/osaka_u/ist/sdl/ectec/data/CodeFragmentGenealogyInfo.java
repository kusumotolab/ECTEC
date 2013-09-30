package jp.ac.osaka_u.ist.sdl.ectec.data;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a genealogy of code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentGenealogyInfo extends
		AbstractGenealogyInfo<CodeFragmentInfo, CodeFragmentLinkInfo> implements
		Comparable<CodeFragmentGenealogyInfo> {

	/**
	 * the conter to have the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * the number of changed
	 */
	private final int changedCount;

	public CodeFragmentGenealogyInfo(final long id, final long startRevisionId,
			final long endRevisionId, final List<Long> elements,
			final List<Long> links, final int changedCount) {
		super(id, startRevisionId, endRevisionId, elements, links);
		this.changedCount = changedCount;
	}

	public CodeFragmentGenealogyInfo(final long startRevisionId,
			final long endRevisionId, final List<Long> elements,
			final List<Long> links, final int changedCount) {
		this(count.getAndIncrement(), startRevisionId, endRevisionId, elements,
				links, changedCount);
	}

	public final int getChangedCount() {
		return changedCount;
	}

	@Override
	public int compareTo(CodeFragmentGenealogyInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
