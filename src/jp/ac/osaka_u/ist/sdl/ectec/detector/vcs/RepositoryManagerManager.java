package jp.ac.osaka_u.ist.sdl.ectec.detector.vcs;

import jp.ac.osaka_u.ist.sdl.ectec.detector.vcs.svn.SVNRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

/**
 * A class that manages the repository manager
 * 
 * @author k-hotta
 * 
 */
public class RepositoryManagerManager {

	/**
	 * the repository manager
	 */
	private final IRepositoryManager repositoryManager;

	/**
	 * the constructor
	 * 
	 * @param vcs
	 * @param url
	 * @param userName
	 * @param passwd
	 * @param additionalUrl
	 * @throws Exception
	 *             thrown if it is failed to initialize the repository
	 */
	public RepositoryManagerManager(final VersionControlSystem vcs,
			final String url, final String userName, final String passwd,
			final String additionalUrl, final int threadsCount)
			throws Exception {
		switch (vcs) {
		case SVN:
			repositoryManager = new SVNRepositoryManager(url, userName, passwd,
					additionalUrl);
			break;
		default:
			throw new RepositoryNotInitializedException(
					"the version control system was not specified");
		}
	}

	/**
	 * get the repository manager
	 * 
	 * @return
	 */
	public final IRepositoryManager getRepositoryManager() {
		return this.repositoryManager;
	}

}
