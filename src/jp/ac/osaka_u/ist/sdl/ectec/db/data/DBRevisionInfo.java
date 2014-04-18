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
	private static AtomicLong count = new AtomicLong(0);

	/**
	 * a string to identify this revision (revision number and so on)
	 */
	private final String identifier;

	/**
	 * the id of the repository
	 */
	private final long repositoryId;

	/**
	 * the constructor for newly created elements
	 * 
	 * @param identifier
	 */
	public DBRevisionInfo(final String identifier, final long repositoryId) {
		this(count.getAndIncrement(), identifier, repositoryId);
	}

	/**
	 * the constructor for elements which are retrieved from the db
	 * 
	 * @param id
	 * @param identifier
	 */
	public DBRevisionInfo(final long id, final String identifier,
			final long repositoryId) {
		super(id);
		this.identifier = identifier;
		this.repositoryId = repositoryId;
	}

	/**
	 * get the identifier of this revision
	 * 
	 * @return
	 */
	public final String getIdentifier() {
		return this.identifier;
	}

	/**
	 * reset the count with the given long value
	 * 
	 * @param l
	 */
	public static void resetCount(final long l) {
		count = new AtomicLong(l);
	}

	/**
	 * get the id of the repository
	 * 
	 * @return
	 */
	public final long getRepositoryId() {
		return this.repositoryId;
	}

	@Override
	public int compareTo(DBRevisionInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
