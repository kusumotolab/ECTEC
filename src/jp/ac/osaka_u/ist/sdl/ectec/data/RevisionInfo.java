package jp.ac.osaka_u.ist.sdl.ectec.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a revision
 * 
 * @author k-hotta
 * 
 */
public class RevisionInfo extends AbstractElement {

	/**
	 * a counter to keep the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * a string to identify this revision (revision number and so on)
	 */
	private final String identifier;

	/**
	 * the constructor for newly created elements
	 * 
	 * @param identifier
	 */
	public RevisionInfo(final String identifier) {
		this(count.getAndIncrement(), identifier);
	}

	/**
	 * the constructor for elements which are retrieved from the db
	 * 
	 * @param id
	 * @param identifier
	 */
	public RevisionInfo(final long id, final String identifier) {
		super(id);
		this.identifier = identifier;
	}

	/**
	 * get the identifier of this revision
	 * 
	 * @return
	 */
	public final String getIdentifier() {
		return this.identifier;
	}

}
