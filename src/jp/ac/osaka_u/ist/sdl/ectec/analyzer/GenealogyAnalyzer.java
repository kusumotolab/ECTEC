package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer.Concretizer;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer.NotConcretizedException;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.manager.DBDataManagerManager;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.manager.DataManagerManager;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.selector.CloneGenealogySelector;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.selector.IConstraint;
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
	 * the concretizer under control
	 */
	private final Concretizer concretizer;

	private GenealogyAnalyzer(final DBConnectionManager dbManager,
			final DataManagerManager dataManagerManager,
			final DBDataManagerManager dbDataManagerManager,
			final IRepositoryManager repositoryManager,
			final boolean isBlockMode) {
		this.dbManager = dbManager;
		this.dataManagerManager = dataManagerManager;
		this.dbDataManagerManager = dbDataManagerManager;
		this.concretizer = new Concretizer(dataManagerManager,
				dbDataManagerManager, dbManager, repositoryManager, isBlockMode);
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
					null, 1);
			final IRepositoryManager repositoryManager = repositoryManagerManager
					.getRepositoryManager();
			final DataManagerManager dataManagerManager = new DataManagerManager();
			final DBDataManagerManager dbDataManagerManager = new DBDataManagerManager();

			return new GenealogyAnalyzer(dbManager, dataManagerManager,
					dbDataManagerManager, repositoryManager, isBlockMode);

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
	 * get the db connection manager
	 * 
	 * @return
	 */
	public final DBConnectionManager getDBConnectionManager() {
		return dbManager;
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
	 * concretize a clone genealogy
	 * 
	 * @param genealogyId
	 * @return
	 * @throws NotConcretizedException
	 */
	public CloneGenealogyInfo concretizeCloneGenealogy(final long genealogyId)
			throws NotConcretizedException {
		return concretizer.concretizeCloneGenealogy(genealogyId);
	}

	/**
	 * concretize clone genealogies
	 * 
	 * @param genealogyIds
	 * @return
	 * @throws NotConcretizedException
	 */
	public Map<Long, CloneGenealogyInfo> concretizeCloneGenealogies(
			final Collection<Long> genealogyIds) throws NotConcretizedException {
		final Map<Long, CloneGenealogyInfo> result = new TreeMap<Long, CloneGenealogyInfo>();

		for (final long genealogyId : genealogyIds) {
			result.put(genealogyId,
					concretizer.concretizeCloneGenealogy(genealogyId));
		}

		return Collections.unmodifiableMap(result);
	}

	/**
	 * select clone genealogies that satisfy the given constraint and concretize
	 * them
	 * 
	 * @param constraint
	 * @return
	 * @throws NotConcretizedException
	 */
	public Map<Long, CloneGenealogyInfo> selectAndConcretizeCloneGenealogies(
			final IConstraint constraint) throws NotConcretizedException {
		final CloneGenealogySelector selector = new CloneGenealogySelector(
				dbManager, constraint);
		final Set<Long> selectedIds = selector.select().keySet();

		return concretizeCloneGenealogies(selectedIds);
	}

}
