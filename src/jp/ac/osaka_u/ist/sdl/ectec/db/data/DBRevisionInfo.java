package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a revision
 * 
 * @author k-hotta
 * 
 */
public class DBRevisionInfo extends AbstractDBElement implements
		Comparable<DBRevisionInfo> {

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
	public DBRevisionInfo(final String identifier) {
		this(count.getAndIncrement(), identifier);
	}

	/**
	 * the constructor for elements which are retrieved from the db
	 * 
	 * @param id
	 * @param identifier
	 */
	public DBRevisionInfo(final long id, final String identifier) {
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

	@Override
	public int compareTo(DBRevisionInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
