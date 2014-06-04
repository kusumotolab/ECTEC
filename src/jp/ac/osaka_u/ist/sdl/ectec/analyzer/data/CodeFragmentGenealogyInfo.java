package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import java.util.Collections;
import java.util.List;

/**
 * A class that represents genealogies of clones
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentGenealogyInfo extends AbstractElement implements
		Comparable<CodeFragmentGenealogyInfo> {

	/**
	 * the start combined revision
	 */
	private final CombinedRevisionInfo startCombinedRevision;

	/**
	 * the end combined revision
	 */
	private final CombinedRevisionInfo endCombinedRevision;

	/**
	 * the list of fragments
	 */
	private final List<CodeFragmentInfo> fragments;

	/**
	 * the list of fragment links
	 */
	private final List<CodeFragmentLinkInfo> links;

	/**
	 * the number of changes
	 */
	private final int changeCount;

	public CodeFragmentGenealogyInfo(final long id,
			final CombinedRevisionInfo startCombinedRevision,
			final CombinedRevisionInfo endCombinedRevision,
			final List<CodeFragmentInfo> fragments,
			final List<CodeFragmentLinkInfo> links, final int changeCount) {
		super(id);
		this.startCombinedRevision = startCombinedRevision;
		this.endCombinedRevision = endCombinedRevision;
		this.fragments = fragments;
		this.links = links;
		this.changeCount = changeCount;
	}

	/**
	 * get the start combined revision
	 * 
	 * @return
	 */
	public final CombinedRevisionInfo getStartCombinedRevision() {
		return startCombinedRevision;
	}

	/**
	 * get the end combined revision
	 * 
	 * @return
	 */
	public final CombinedRevisionInfo getEndCombinedRevision() {
		return endCombinedRevision;
	}

	/**
	 * get the list of fragments
	 * 
	 * @return
	 */
	public final List<CodeFragmentInfo> getFragments() {
		return Collections.unmodifiableList(fragments);
	}

	/**
	 * get the list of fragment links
	 * 
	 * @return
	 */
	public final List<CodeFragmentLinkInfo> getLinks() {
		return Collections.unmodifiableList(links);
	}

	/**
	 * get the number of changes
	 * 
	 * @return
	 */
	public final int getChangeCount() {
		return changeCount;
	}

	@Override
	public int compareTo(CodeFragmentGenealogyInfo another) {
		final int compareWithStartRev = startCombinedRevision.compareTo(another
				.getStartCombinedRevision());
		if (compareWithStartRev != 0) {
			return compareWithStartRev;
		}

		final int compareWithEndRev = endCombinedRevision.compareTo(another
				.getEndCombinedRevision());
		if (compareWithEndRev != 0) {
			return compareWithEndRev;
		}

		return ((Long) id).compareTo(another.getId());
	}

}