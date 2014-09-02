package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.concurrent.atomic.AtomicLong;

import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

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
	private static AtomicLong count = new AtomicLong(0);

	/**
	 * the name of the repository
	 */
	private final String name;

	/**
	 * the url of the repository
	 */
	private final String url;

	/**
	 * the additional url
	 */
	private final String additionalUrl;

	/**
	 * the version control system
	 */
	private final VersionControlSystem managingVcs;

	/**
	 * the user name to access the repository
	 */
	private final String userName;

	/**
	 * the password to access the repository
	 */
	private final String passwd;

	/**
	 * the constructor for elements that are retrieved from db
	 * 
	 * @param id
	 * @param name
	 * @param url
	 */
	public DBRepositoryInfo(final long id, final String name, final String url,
			final String additionalUrl, final VersionControlSystem managingVcs,
			final String userName, final String passwd) {
		super(id);
		this.name = name;
		this.url = url;
		this.additionalUrl = additionalUrl;
		this.managingVcs = managingVcs;
		this.userName = userName;
		this.passwd = passwd;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param name
	 * @param url
	 */
	public DBRepositoryInfo(final String name, final String url,
			final String additionalUrl, final VersionControlSystem managingVcs,
			final String userName, final String passwd) {
		this(count.getAndIncrement(), name, url, additionalUrl, managingVcs,
				userName, passwd);
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

	/**
	 * get the additional url of the repository
	 * 
	 * @return
	 */
	public final String getAdditionalUrl() {
		return this.additionalUrl;
	}

	/**
	 * get the version control system managing this repository
	 * 
	 * @return
	 */
	public final VersionControlSystem getManagingVcs() {
		return this.managingVcs;
	}

	/**
	 * get the user name to access the repository
	 * 
	 * @return
	 */
	public final String getUserName() {
		return this.userName;
	}

	/**
	 * get the password to access the repository
	 * 
	 * @return
	 */
	public final String getPasswd() {
		return this.passwd;
	}

	@Override
	public int compareTo(DBRepositoryInfo another) {
		return ((Long) this.getId()).compareTo(another.getId());
	}

}
