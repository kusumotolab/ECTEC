package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import java.util.Collections;
import java.util.List;

/**
 * A class that represents genealogies of clones
 * 
 * @author k-hotta
 * 
 */
public class CloneGenealogyInfo extends AbstractElement implements
		Comparable<CloneGenealogyInfo> {

	/**
	 * the start combined revision
	 */
	private final CombinedRevisionInfo startCombinedRevision;

	/**
	 * the end combined revision
	 */
	private final CombinedRevisionInfo endCombinedRevision;

	/**
	 * the list of clones
	 */
	private final List<CloneSetInfo> clones;

	/**
	 * the list of clone links
	 */
	private final List<CloneSetLinkInfo> links;

	/**
	 * the number of revisions where any changes on fragments occurred
	 */
	private final int numberOfChanges;

	/**
	 * the number of revisions where any additions of fragments occurred
	 */
	private final int numberOfAdditions;

	/**
	 * the number of revisions where any deletions of elements occurred
	 */
	private final int numberOfDeletions;

	public CloneGenealogyInfo(final long id, final CombinedRevisionInfo startCombinedRevision,
			final CombinedRevisionInfo endCombinedRevision, final List<CloneSetInfo> clones,
			final List<CloneSetLinkInfo> links, final int numberOfChanges,
			final int numberOfAdditions, final int numberOfDeletions) {
		super(id);
		this.startCombinedRevision = startCombinedRevision;
		this.endCombinedRevision = endCombinedRevision;
		this.clones = clones;
		this.links = links;
		this.numberOfChanges = numberOfChanges;
		this.numberOfAdditions = numberOfAdditions;
		this.numberOfDeletions = numberOfDeletions;
	}

	/**
	 * get the start combined revision
	 * 
	 * @return
	 */
	public final CombinedRevisionInfo getStartRevision() {
		return startCombinedRevision;
	}

	/**
	 * get the end combined revision
	 * 
	 * @return
	 */
	public final CombinedRevisionInfo getEndRevision() {
		return endCombinedRevision;
	}

	/**
	 * get the list of clones
	 * 
	 * @return
	 */
	public final List<CloneSetInfo> getClones() {
		return Collections.unmodifiableList(clones);
	}

	/**
	 * get the list of links
	 * 
	 * @return
	 */
	public final List<CloneSetLinkInfo> getLinks() {
		return Collections.unmodifiableList(links);
	}

	/**
	 * get the number of revisions where any changes on fragments occurred
	 * 
	 * @return
	 */
	public final int getNumberOfChanges() {
		return numberOfChanges;
	}

	/**
	 * get the number of revisions where any additions of fragments occurred
	 * 
	 * @return
	 */
	public final int getNumberOfAdditions() {
		return numberOfAdditions;
	}

	/**
	 * get number of revisions where any deletions of elements occurred
	 * 
	 * @return
	 */
	public final int getNumberOfDeletions() {
		return numberOfDeletions;
	}

	/**
	 * get whether this genealogy still be alive at the latest revision or not
	 * 
	 * @return true if it is NOT alive at the latest revision
	 */
	public final boolean isDead(final long combinedRevisionId) {
		return this.endCombinedRevision.getId() < combinedRevisionId;
	}

	@Override
	public int compareTo(CloneGenealogyInfo another) {
		final int compareWithStartRev = startCombinedRevision.compareTo(another
				.getStartRevision());
		if (compareWithStartRev != 0) {
			return compareWithStartRev;
		}

		final int compareWithEndRev = endCombinedRevision.compareTo(another
				.getEndRevision());
		if (compareWithEndRev != 0) {
			return compareWithEndRev;
		}

		return ((Long) id).compareTo(another.getId());
	}

}
