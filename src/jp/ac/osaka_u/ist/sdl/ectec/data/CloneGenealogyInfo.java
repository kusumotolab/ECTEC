package jp.ac.osaka_u.ist.sdl.ectec.data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a genalogy of clones
 * 
 * @author k-hotta
 * 
 */
public class CloneGenealogyInfo extends AbstractElement implements
		Comparable<CloneGenealogyInfo> {

	/**
	 * a counter to keep the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * the id of the start revision
	 */
	private final long startRevisionId;

	/**
	 * the id of the end revision
	 */
	private final long endRevisionId;

	/**
	 * the list of the ids of clones included in this genealogy
	 */
	private final List<Long> clones;

	/**
	 * the list of the ids of clone links related to this genealogy
	 */
	private final List<Long> cloneLinks;

	/**
	 * the number of changes
	 */
	private final int numberOfChanges;

	/**
	 * the number of additions
	 */
	private final int numberOfAdditions;

	/**
	 * the number of deletions
	 */
	private final int numberOfDeletions;

	/**
	 * whether this genealogy is dead or not in the latest revision
	 */
	private final boolean dead;

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param startRevisionId
	 * @param endRevisionId
	 * @param clones
	 * @param cloneLinks
	 * @param numberOfChanges
	 * @param numberOfAdditions
	 * @param numberOfDeletions
	 * @param dead
	 */
	public CloneGenealogyInfo(final long id, final long startRevisionId,
			final long endRevisionId, final List<Long> clones,
			final List<Long> cloneLinks, final int numberOfChanges,
			final int numberOfAdditions, final int numberOfDeletions,
			final boolean dead) {
		super(id);
		this.startRevisionId = startRevisionId;
		this.endRevisionId = endRevisionId;
		this.clones = clones;
		this.cloneLinks = cloneLinks;
		this.numberOfChanges = numberOfChanges;
		this.numberOfAdditions = numberOfAdditions;
		this.numberOfDeletions = numberOfDeletions;
		this.dead = dead;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param startRevisionId
	 * @param endRevisionId
	 * @param clones
	 * @param cloneLinks
	 * @param numberOfChanges
	 * @param numberOfAdditions
	 * @param numberOfDeletions
	 * @param dead
	 */
	public CloneGenealogyInfo(final long startRevisionId,
			final long endRevisionId, final List<Long> clones,
			final List<Long> cloneLinks, final int numberOfChanges,
			final int numberOfAdditions, final int numberOfDeletions,
			final boolean dead) {
		this(count.getAndIncrement(), startRevisionId, endRevisionId, clones,
				cloneLinks, numberOfChanges, numberOfAdditions,
				numberOfDeletions, dead);
	}

	/**
	 * get the id of the start revision
	 * 
	 * @return
	 */
	public final long getStartRevisionId() {
		return this.startRevisionId;
	}

	/**
	 * get the id of the end revision
	 * 
	 * @return
	 */
	public final long getEndRevisionId() {
		return this.endRevisionId;
	}

	/**
	 * get the list of ids of clones included in this genealogy
	 * 
	 * @return
	 */
	public final List<Long> getClones() {
		return Collections.unmodifiableList(this.clones);
	}

	/**
	 * get the list of ids of clone links related to this genealogy
	 * 
	 * @return
	 */
	public final List<Long> getCloneLinks() {
		return Collections.unmodifiableList(this.cloneLinks);
	}

	/**
	 * get the number of changes
	 * 
	 * @return
	 */
	public final int getNumberOfChanges() {
		return this.numberOfChanges;
	}

	/**
	 * get the number of additions
	 * 
	 * @return
	 */
	public final int getNumberOfAdditions() {
		return this.numberOfAdditions;
	}

	/**
	 * get the number of deletions
	 * 
	 * @return
	 */
	public final int getNumberOfDeletions() {
		return this.numberOfDeletions;
	}

	/**
	 * get whether this genealogy is dead in the latest revision
	 * 
	 * @return
	 */
	public final boolean isDead() {
		return dead;
	}

	@Override
	public int compareTo(CloneGenealogyInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
