package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import java.util.Collections;
import java.util.List;

/**
 * A class that represents links of clones
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkInfo extends AbstractElement implements
		Comparable<CloneSetLinkInfo> {

	/**
	 * the before revision
	 */
	private final RevisionInfo beforeRevision;

	/**
	 * the after revision
	 */
	private final RevisionInfo afterRevision;

	/**
	 * the before clone
	 */
	private final CloneSetInfo beforeClone;

	/**
	 * the after clone
	 */
	private final CloneSetInfo afterClone;

	/**
	 * the number of added elements
	 */
	private final int numberOfAddedElements;

	/**
	 * the number of deleted elements
	 */
	private final int numberOfDeletedElements;

	/**
	 * the number of changed elements
	 */
	private final int numberOfChangedElements;

	/**
	 * the number of co-changed elements
	 */
	private final int numberOfCoChangedElements;

	/**
	 * the list of code fragments related to this clone set link
	 */
	private final List<CodeFragmentLinkInfo> fragmentLinks;

	public CloneSetLinkInfo(final long id, final RevisionInfo beforeRevision,
			final RevisionInfo afterRevision, final CloneSetInfo beforeClone,
			final CloneSetInfo afterClone, final int numberOfAddedElements,
			final int numberOfDeletedElements,
			final int numberOfChangedElements,
			final int numberOfCoChangedElements,
			final List<CodeFragmentLinkInfo> fragmentLinks) {
		super(id);
		this.beforeRevision = beforeRevision;
		this.afterRevision = afterRevision;
		this.beforeClone = beforeClone;
		this.afterClone = afterClone;
		this.numberOfAddedElements = numberOfAddedElements;
		this.numberOfDeletedElements = numberOfDeletedElements;
		this.numberOfChangedElements = numberOfChangedElements;
		this.numberOfCoChangedElements = numberOfCoChangedElements;
		this.fragmentLinks = fragmentLinks;
	}

	/**
	 * get the before revision
	 * 
	 * @return
	 */
	public final RevisionInfo getBeforeRevision() {
		return beforeRevision;
	}

	/**
	 * get the after revision
	 * 
	 * @return
	 */
	public final RevisionInfo getAfterRevision() {
		return afterRevision;
	}

	/**
	 * get the before clone
	 * 
	 * @return
	 */
	public final CloneSetInfo getBeforeClone() {
		return beforeClone;
	}

	/**
	 * get the after clone
	 * 
	 * @return
	 */
	public final CloneSetInfo getAfterClone() {
		return afterClone;
	}

	/**
	 * get the number of added elements
	 * 
	 * @return
	 */
	public final int getNumberOfAddedElements() {
		return numberOfAddedElements;
	}

	/**
	 * get the number of deleted elements
	 * 
	 * @return
	 */
	public final int getNumberOfDeletedElements() {
		return numberOfDeletedElements;
	}

	/**
	 * get the number of changed elements
	 * 
	 * @return
	 */
	public final int getNumberOfChangedElements() {
		return numberOfChangedElements;
	}

	/**
	 * get the number of co-changed elements
	 * 
	 * @return
	 */
	public final int getNumberOfCoChangedElements() {
		return numberOfCoChangedElements;
	}

	/**
	 * get the list of fragment links
	 * 
	 * @return
	 */
	public final List<CodeFragmentLinkInfo> getFragmentLinks() {
		return Collections.unmodifiableList(fragmentLinks);
	}

	@Override
	public int compareTo(CloneSetLinkInfo another) {
		final int compareWithBeforeRev = beforeRevision.compareTo(another
				.getBeforeRevision());
		if (compareWithBeforeRev != 0) {
			return compareWithBeforeRev;
		}

		final int compareWithAfterRev = afterRevision.compareTo(another
				.getAfterRevision());
		if (compareWithAfterRev != 0) {
			return compareWithAfterRev;
		}

		final int compareWithBeforeClone = beforeClone.compareTo(another
				.getBeforeClone());
		if (compareWithBeforeClone != 0) {
			return compareWithBeforeClone;
		}

		final int compareWithAfterClone = afterClone.compareTo(another
				.getAfterClone());
		if (compareWithAfterClone != 0) {
			return compareWithAfterClone;
		}

		return ((Long) id).compareTo(another.getId());
	}

}
