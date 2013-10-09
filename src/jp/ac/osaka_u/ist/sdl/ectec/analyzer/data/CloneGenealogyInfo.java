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
	 * the start revision
	 */
	private final RevisionInfo startRevision;

	/**
	 * the end revision
	 */
	private final RevisionInfo endRevision;

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

	/**
	 * whether this genealogy still be alive at the latest revision
	 */
	private final boolean dead;

	public CloneGenealogyInfo(final long id, final RevisionInfo startRevision,
			final RevisionInfo endRevision, final List<CloneSetInfo> clones,
			final List<CloneSetLinkInfo> links, final int numberOfChanges,
			final int numberOfAdditions, final int numberOfDeletions,
			final boolean dead) {
		super(id);
		this.startRevision = startRevision;
		this.endRevision = endRevision;
		this.clones = clones;
		this.links = links;
		this.numberOfChanges = numberOfChanges;
		this.numberOfAdditions = numberOfAdditions;
		this.numberOfDeletions = numberOfDeletions;
		this.dead = dead;
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
	public final boolean isDead() {
		return dead;
	}

	@Override
	public int compareTo(CloneGenealogyInfo another) {
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
