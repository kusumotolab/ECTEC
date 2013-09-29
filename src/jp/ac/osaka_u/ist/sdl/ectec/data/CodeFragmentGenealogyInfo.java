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
		AbstractGenealogyInfo<CodeFragmentInfo, CodeFragmentLinkInfo> {

	/**
	 * the conter to have the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	public CodeFragmentGenealogyInfo(final long id, final long startRevisionId,
			final long endRevisionId, final List<Long> elements,
			final List<Long> links) {
		super(id, startRevisionId, endRevisionId, elements, links);
	}

	public CodeFragmentGenealogyInfo(final long startRevisionId,
			final long endRevisionId, final List<Long> elements,
			final List<Long> links) {
		this(count.getAndIncrement(), startRevisionId, endRevisionId, elements,
				links);
	}
}
