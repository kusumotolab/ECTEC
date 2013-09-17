package jp.ac.osaka_u.ist.sdl.ectec.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a file
 * 
 * @author k-hotta
 * 
 */
public class FileInfo extends AbstractElement {

	/**
	 * a counter to keep the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * the path of this file
	 */
	private final String path;

	/**
	 * the id of the start revision
	 */
	private final long startRevision;

	/**
	 * the id of the end revision
	 */
	private final long endRevision;

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param path
	 * @param startRevision
	 * @param endRevision
	 */
	public FileInfo(final long id, final String path, final long startRevision,
			final long endRevision) {
		super(id);
		this.path = path;
		this.startRevision = startRevision;
		this.endRevision = endRevision;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param path
	 * @param startRevision
	 * @param endRevision
	 */
	public FileInfo(final String path, final long startRevision,
			final long endRevision) {
		this(count.getAndIncrement(), path, startRevision, endRevision);
	}

	/**
	 * get the path of this file
	 * 
	 * @return
	 */
	public final String getPath() {
		return this.path;
	}

	/**
	 * get the id of the start revision
	 * 
	 * @return
	 */
	public final long getStartRevision() {
		return this.startRevision;
	}

	/**
	 * get the id of the end revision
	 * 
	 * @return
	 */
	public final long getEndRevision() {
		return this.endRevision;
	}

}
