package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.detector.vcs.IRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.detector.vcs.RepositoryManagerManager;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

/**
 * A class that manages genealogy analyzer
 * 
 * @author k-hotta
 * 
 */
public class GenealogyAnalyzer {

	/**
	 * the manager of db connection
	 */
	private final DBConnectionManager dbManager;

	/**
	 * the manager of repository
	 */
	private final IRepositoryManager repositoryManager;

	private GenealogyAnalyzer(final DBConnectionManager dbManager,
			final IRepositoryManager repositoryManager) {
		this.dbManager = dbManager;
		this.repositoryManager = repositoryManager;
	}

	/**
	 * set up the manager and return the initialized one
	 * 
	 * @param dbPath
	 * @param repositoryPath
	 * @param userName
	 * @param passwd
	 * @param versionControlSystem
	 * @return
	 */
	public static GenealogyAnalyzer setup(final String dbPath,
			final String repositoryPath, final String userName,
			final String passwd, final VersionControlSystem versionControlSystem) {
		boolean troubled = false;

		DBConnectionManager dbManager = null;

		try {
			dbManager = new DBConnectionManager(dbPath,
					Constants.MAX_BATCH_COUNT);

			// the additional path is always null
			final RepositoryManagerManager repositoryManagerManager = new RepositoryManagerManager(
					versionControlSystem, repositoryPath, userName, passwd,
					null);
			final IRepositoryManager repositoryManager = repositoryManagerManager
					.getRepositoryManager();

			return new GenealogyAnalyzer(dbManager, repositoryManager);

		} catch (Exception e) {
			e.printStackTrace();
			troubled = true;
			return null;
		} finally {
			if (troubled) {
				if (dbManager != null) {
					dbManager.close();
				}
			}
		}
	}

	/**
	 * set up the manager and return the initialized one
	 * 
	 * @param dbPath
	 * @param repositoryPath
	 * @param versionControlSystem
	 * @return
	 */
	public static GenealogyAnalyzer setup(final String dbPath,
			final String repositoryPath,
			final VersionControlSystem versionControlSystem) {
		return setup(dbPath, repositoryPath, null, null, versionControlSystem);
	}

	/**
	 * close the manager
	 */
	public void close() {
		dbManager.close();
	}

}
