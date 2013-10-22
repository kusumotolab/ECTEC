package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer.ConcretizerController;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.manager.DBDataManagerManager;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.manager.DataManagerManager;
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
	 * the data manager manager
	 */
	private final DataManagerManager dataManagerManager;

	/**
	 * the db data manager manager
	 */
	private final DBDataManagerManager dbDataManagerManager;

	/**
	 * the controller for the concretizer
	 */
	private final ConcretizerController controller;

	private GenealogyAnalyzer(final DBConnectionManager dbManager,
			final DataManagerManager dataManagerManager,
			final DBDataManagerManager dbDataManagerManager,
			final ConcretizerController controller) {
		this.dbManager = dbManager;
		this.dataManagerManager = dataManagerManager;
		this.dbDataManagerManager = dbDataManagerManager;
		this.controller = controller;
	}

	/**
	 * set up the manager and return the initialized one
	 * 
	 * @param dbPath
	 * @param repositoryPath
	 * @param userName
	 * @param passwd
	 * @param versionControlSystem
	 * @param isBlockMode
	 * @return
	 */
	public static GenealogyAnalyzer setup(final String dbPath,
			final String repositoryPath, final String userName,
			final String passwd,
			final VersionControlSystem versionControlSystem,
			final boolean isBlockMode) {
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
			final DataManagerManager dataManagerManager = new DataManagerManager();
			final DBDataManagerManager dbDataManagerManager = new DBDataManagerManager();
			final ConcretizerController controller = new ConcretizerController(
					dataManagerManager, dbDataManagerManager, dbManager,
					repositoryManager, isBlockMode);

			return new GenealogyAnalyzer(dbManager, dataManagerManager,
					dbDataManagerManager, controller);

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
	 * @param isBlockMode
	 * @return
	 */
	public static GenealogyAnalyzer setup(final String dbPath,
			final String repositoryPath,
			final VersionControlSystem versionControlSystem,
			final boolean isBlockMode) {
		return setup(dbPath, repositoryPath, null, null, versionControlSystem,
				isBlockMode);
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
		return setup(dbPath, repositoryPath, userName, passwd,
				versionControlSystem, true);
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
		return setup(dbPath, repositoryPath, null, null, versionControlSystem,
				true);
	}

	/**
	 * close the manager
	 */
	public void close() {
		dataManagerManager.clear();
		dbDataManagerManager.clear();
		dbManager.close();
	}

	/**
	 * get the data manager manager
	 * 
	 * @return
	 */
	public final DataManagerManager getDataManagerManager() {
		return dataManagerManager;
	}

	/**
	 * get the db data manager manager
	 * 
	 * @return
	 */
	public final DBDataManagerManager getDbDataManagerManager() {
		return dbDataManagerManager;
	}

	/**
	 * get the concretizer controller <br>
	 * calling some methods in the controller affects the data manager manager
	 * and the db data manager manager
	 * 
	 * @return
	 */
	public final ConcretizerController getController() {
		return controller;
	}

}
