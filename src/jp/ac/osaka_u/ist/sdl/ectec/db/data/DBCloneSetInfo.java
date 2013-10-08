package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a clone set
 * 
 * @author k-hotta
 * 
 */
public class DBCloneSetInfo extends AbstractDBElement implements
		Comparable<DBCloneSetInfo> {

	/**
	 * a counter to keep the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * the id of the revision that has this clone set
	 */
	private final long revisionId;

	/**
	 * the list of the ids of code fragments of which this clone set consists
	 */
	private final List<Long> elements;

	/**
	 * the number of elements
	 */
	private final int numberOfElements;

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param revisionId
	 * @param elements
	 */
	public DBCloneSetInfo(final long id, final long revisionId,
			final List<Long> elements) {
		super(id);
		this.revisionId = revisionId;
		this.elements = elements;
		this.numberOfElements = elements.size();
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param revisionId
	 * @param elements
	 */
	public DBCloneSetInfo(final long revisionId, final List<Long> elements) {
		this(count.getAndIncrement(), revisionId, elements);
	}

	/**
	 * get the id of the owner revision
	 * 
	 * @return
	 */
	public final long getRevisionId() {
		return this.revisionId;
	}

	/**
	 * get all the ids of code fragments of which this clone set consists
	 * 
	 * @return
	 */
	public final List<Long> getElements() {
		return Collections.unmodifiableList(elements);
	}

	/**
	 * get the number of elements
	 * 
	 * @return
	 */
	public final int getNumberOfElements() {
		return this.numberOfElements;
	}

	@Override
	public int compareTo(DBCloneSetInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
