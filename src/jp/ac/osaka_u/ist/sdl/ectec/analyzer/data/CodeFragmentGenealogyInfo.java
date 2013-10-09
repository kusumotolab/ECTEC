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
	 * the start revision
	 */
	private final RevisionInfo startRevision;

	/**
	 * the end revision
	 */
	private final RevisionInfo endRevision;

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
			final RevisionInfo startRevision, final RevisionInfo endRevision,
			final List<CodeFragmentInfo> fragments,
			final List<CodeFragmentLinkInfo> links, final int changeCount) {
		super(id);
		this.startRevision = startRevision;
		this.endRevision = endRevision;
		this.fragments = fragments;
		this.links = links;
		this.changeCount = changeCount;
	}

	/**
	 * get the start revision
	 * 
	 * @return
	 */
	public final RevisionInfo getStartRevision() {
		return startRevision;
	}

	/**
	 * get the end revision
	 * 
	 * @return
	 */
	public final RevisionInfo getEndRevision() {
		return endRevision;
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
		final int compareWithStartRev = startRevision.compareTo(another
				.getStartRevision());
		if (compareWithStartRev != 0) {
			return compareWithStartRev;
		}

		final int compareWithEndRev = endRevision.compareTo(another
				.getEndRevision());
		if (compareWithEndRev != 0) {
			return compareWithEndRev;
		}

		return ((Long) id).compareTo(another.getId());
	}

}