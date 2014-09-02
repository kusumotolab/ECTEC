package jp.ac.osaka_u.ist.sdl.ectec.main.revisiondetector;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalStateException;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.RepositoryManagerManager;

import org.apache.log4j.Logger;

/**
 * The main class to detect and register target revisions. <br>
 * This process MUST follow the repository registering step.
 * 
 * @author k-hotta
 * 
 */
public class RevisionDetectorMain {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(RevisionDetectorMain.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * the db manager
	 */
	private static DBConnectionManager dbManager = null;

	/**
	 * the target repository
	 */
	private static Map<Long, DBRepositoryInfo> repositories = new TreeMap<Long, DBRepositoryInfo>();

	/**
	 * the manager of repository managers
	 */
	private static RepositoryManagerManager repositoryManagerManager = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// load the settings
			final RevisionDetectorMainSettings settings = loadSettings(args);

			// pre processing
			preprocess(settings);

			// main processing
			final RevisionDetector detector = new RevisionDetector(settings,
					dbManager, repositoryManagerManager, repositories);
			detector.run();

			// post processing
			postprocess();

			logger.info("operations have finished.");

		} catch (Exception e) {
			eLogger.fatal("operations failed.\n" + e.toString());
		}
	}

	/**
	 * load the settings
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private static RevisionDetectorMainSettings loadSettings(final String[] args)
			throws Exception {
		final RevisionDetectorMainSettings settings = new RevisionDetectorMainSettings();
		settings.load(args);
		return settings;
	}

	/**
	 * perform pre-processing
	 * 
	 * @param settings
	 * @throws Exception
	 */
	private static void preprocess(final RevisionDetectorMainSettings settings)
			throws Exception {
		// make a connection between the db file
		dbManager = new DBConnectionManager(settings.getDbPath(),
				settings.getMaxBatchCount());
		logger.info("connected to the db");

		dbManager.initializeElementCounters(settings.getHeaderOfId());
		logger.info("initialized counters of elements");

		// initialize the manager of repository managers
		repositoryManagerManager = new RepositoryManagerManager();
		logger.info("initialized the manager of repository managers");

		// retrieving all the repositories registered in the db
		logger.info("retrieving repositories ...");
		final Set<Long> repositoryIds = settings.getRepositoryIds();
		final Map<Long, DBRepositoryInfo> registeredRepositories = dbManager
				.getRepositoryRetriever().retrieveAll();
		if (registeredRepositories.isEmpty()) {
			throw new IllegalStateException(
					"cannot retrieve any repositories from db");
		}

		logger.info(registeredRepositories.size()
				+ " repositories were retrieved.");

		// check the given ids of target repositories
		if (repositoryIds.isEmpty()) {
			repositories.putAll(registeredRepositories);
		} else {
			for (final long repositoryId : repositoryIds) {
				if (registeredRepositories.containsKey(repositoryId)) {
					repositories.put(repositoryId,
							registeredRepositories.get(repositoryId));
				} else {
					eLogger.warn("cannot find the repository " + repositoryId
							+ " (will be ignored)");
				}
			}
		}

		if (repositories.isEmpty()) {
			throw new IllegalStateException(
					"the collection of target repositories is empty");
		}

		logger.info("targets " + repositories.size() + " repositories");

		for (final Map.Entry<Long, DBRepositoryInfo> entry : repositories
				.entrySet()) {
			final DBRepositoryInfo repository = entry.getValue();
			logger.debug("repository " + entry.getKey() + ": "
					+ repository.getName() + " - " + repository.getRootUrl());

			try {
				repositoryManagerManager.addRepositoryManager(repository);
			} catch (Exception e) {
				eLogger.warn(e.toString());
			}
		}

		logger.info("repository managers were initialized");
	}

	/**
	 * perform post-processing
	 */
	private static void postprocess() {
		if (dbManager != null) {
			dbManager.close();
		}
	}

}
