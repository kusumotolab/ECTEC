package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a repository
 * 
 * @author k-hotta
 * 
 */
public class DBRepositoryInfo extends AbstractDBElement implements
		Comparable<DBRepositoryInfo> {

	/**
	 * a counter to keep the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * the name of the repository
	 */
	private final String name;

	/**
	 * the url of the repository
	 */
	private final String url;

	/**
	 * the constructor for elements that are retrieved from db
	 * 
	 * @param id
	 * @param name
	 * @param url
	 */
	public DBRepositoryInfo(final long id, final String name, final String url) {
		super(id);
		this.name = name;
		this.url = url;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param name
	 * @param url
	 */
	public DBRepositoryInfo(final String name, final String url) {
		this(count.getAndIncrement(), name, url);
	}

	/**
	 * get the name of the repository
	 * 
	 * @return
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * get the url of the repository
	 * 
	 * @return
	 */
	public final String getUrl() {
		return this.url;
	}

	@Override
	public int compareTo(DBRepositoryInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
