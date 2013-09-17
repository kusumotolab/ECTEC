package jp.ac.osaka_u.ist.sdl.ectec.data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a clone set
 * 
 * @author k-hotta
 * 
 */
public class CloneSetInfo extends AbstractElement {

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
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param revisionId
	 * @param elements
	 */
	public CloneSetInfo(final long id, final long revisionId,
			final List<Long> elements) {
		super(id);
		this.revisionId = revisionId;
		this.elements = elements;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param revisionId
	 * @param elements
	 */
	public CloneSetInfo(final long revisionId, final List<Long> elements) {
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

}
