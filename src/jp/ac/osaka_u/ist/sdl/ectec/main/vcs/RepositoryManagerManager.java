package jp.ac.osaka_u.ist.sdl.ectec.main.vcs;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.vcs.svn.SVNRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

import org.apache.log4j.Logger;

/**
 * A class that manages the repository manager
 * 
 * @author k-hotta
 * 
 */
public class RepositoryManagerManager {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(RepositoryManagerManager.class.getName());

	/**
	 * the repository manager
	 */
	private final ConcurrentMap<Long, IRepositoryManager> repositoryManagers;

	public RepositoryManagerManager() {
		this.repositoryManagers = new ConcurrentHashMap<Long, IRepositoryManager>();
	}

	/**
	 * add a repository manager
	 * 
	 * @param repository
	 * @param vcs
	 * @throws Exception
	 */
	public void addRepositoryManager(final DBRepositoryInfo repository,
			final VersionControlSystem vcs) throws Exception {
		addRepositoryManager(repository.getId(), repository.getUrl(), null,
				null, null, vcs);
	}

	/**
	 * add a repository manager
	 * 
	 * @param id
	 * @param url
	 * @param userName
	 * @param passwd
	 * @param additionalUrl
	 * @param vcs
	 * @throws Exception
	 */
	public void addRepositoryManager(final long id, final String url,
			final String userName, final String passwd,
			final String additionalUrl, final VersionControlSystem vcs)
			throws Exception {
		IRepositoryManager repositoryManager = null;

		switch (vcs) {
		case SVN:
			repositoryManager = new SVNRepositoryManager(url, userName, passwd,
					additionalUrl);
			break;
		default:
			break;
		}

		if (repositoryManager == null) {
			throw new IllegalStateException(
					"cannot create a repository manager for repository " + id);
		}

		repositoryManagers.put(id, repositoryManager);
		logger.info("initialize the repository manager for repository " + id);
	}

	/**
	 * get the repository manager
	 * 
	 * @param id
	 * @return
	 */
	public IRepositoryManager getRepositoryManager(final long id) {
		return repositoryManagers.get(id);
	}

}
