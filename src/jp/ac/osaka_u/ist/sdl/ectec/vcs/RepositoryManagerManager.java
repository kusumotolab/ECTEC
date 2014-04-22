package jp.ac.osaka_u.ist.sdl.ectec.vcs;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.git.GitRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.svn.SVNRepositoryManager;

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
	private final ConcurrentMap<Long, AbstractRepositoryManager> repositoryManagers;

	public RepositoryManagerManager() {
		this.repositoryManagers = new ConcurrentHashMap<Long, AbstractRepositoryManager>();
	}

	/**
	 * add a repository manager
	 * 
	 * @param repository
	 * @param vcs
	 * @throws Exception
	 */
	public void addRepositoryManager(final DBRepositoryInfo repository)
			throws Exception {
		addRepositoryManager(repository.getId(), repository.getUrl(),
				repository.getUserName(), repository.getPasswd(),
				repository.getName(), repository.getManagingVcs());
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
			final String repositoryName, final VersionControlSystem vcs)
			throws Exception {
		AbstractRepositoryManager repositoryManager = null;

		switch (vcs) {
		case SVN:
			repositoryManager = new SVNRepositoryManager(url, userName, passwd,
					repositoryName, id);
			break;
		case GIT:
			repositoryManager = new GitRepositoryManager(url, userName,
					repositoryName, id);
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
	public AbstractRepositoryManager getRepositoryManager(final long id) {
		return repositoryManagers.get(id);
	}

	/**
	 * get the repository managers
	 * 
	 * @return
	 */
	public final ConcurrentMap<Long, AbstractRepositoryManager> getRepositoryManagers() {
		return repositoryManagers;
	}

}
