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
	 * @param elements
	 * @param links
	 * @param numberOfChanges
	 * @param numberOfAdditions
	 * @param numberOfDeletions
	 * @param dead
	 */
	public DBCloneGenealogyInfo(final long id, final long startRevisionId,
			final long endRevisionId, final List<Long> elements,
			final List<Long> links, final int numberOfChanges,
			final int numberOfAdditions, final int numberOfDeletions,
			final boolean dead) {
		super(id, startRevisionId, endRevisionId, elements, links);
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
	 * @param elements
	 * @param links
	 * @param numberOfChanges
	 * @param numberOfAdditions
	 * @param numberOfDeletions
	 * @param dead
	 */
	public DBCloneGenealogyInfo(final long startRevisionId,
			final long endRevisionId, final List<Long> elements,
			final List<Long> links, final int numberOfChanges,
			final int numberOfAdditions, final int numberOfDeletions,
			final boolean dead) {
		this(count.getAndIncrement(), startRevisionId, endRevisionId, elements,
				links, numberOfChanges, numberOfAdditions, numberOfDeletions,
				dead);
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
	public int compareTo(DBCloneGenealogyInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
