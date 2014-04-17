package jp.ac.osaka_u.ist.sdl.ectec.main.filedetector;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalStateException;
import jp.ac.osaka_u.ist.sdl.ectec.main.vcs.RepositoryManagerManager;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

import org.apache.log4j.Logger;

/**
 * The main class for detecting files
 * 
 * @author k-hotta
 * 
 */
public class FileDetectorMain {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(FileDetectorMain.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * the db manager
	 */
	private static DBConnectionManager dbManager = null;

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
			final FileDetectorMainSettings settings = loadSettings(args);

			// pre processing
			preprocess(settings);

			// main processing
			final FileDetector detector = new FileDetector(settings, dbManager,
					repositoryManagerManager);
			detector.run();

			// post processing
			postprocess();

			logger.info("operations have finished.");

		} catch (Exception e) {
			eLogger.fatal("operations failed.\n" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * load the settings
	 * 
	 * @param args
	 * @return
	 */
	private static final FileDetectorMainSettings loadSettings(
			final String[] args) throws Exception {
		final FileDetectorMainSettings settings = new FileDetectorMainSettings();
		settings.load(args);
		return settings;
	}

	/**
	 * perform pre-processing
	 * 
	 * @param settings
	 * @throws Exception
	 */
	private static void preprocess(final FileDetectorMainSettings settings)
			throws Exception {
		// make a connection between the db file
		dbManager = new DBConnectionManager(settings.getDbPath(),
				settings.getMaxBatchCount());
		logger.info("connected to the db");

		// initialize the manager of repository managers
		repositoryManagerManager = new RepositoryManagerManager();
		logger.info("initialized the manager of repository managers");

		// retrieving all the repositories registered in the db
		logger.info("retrieving repositories ...");
		final Map<Long, DBRepositoryInfo> registeredRepositories = dbManager
				.getRepositoryRetriever().retrieveAll();
		if (registeredRepositories.isEmpty()) {
			throw new IllegalStateException(
					"cannot retrieve any repositories from db");
		}

		logger.info(registeredRepositories.size()
				+ " repositories were retrieved.");

		final VersionControlSystem vcs = settings.getVcs();

		for (final Map.Entry<Long, DBRepositoryInfo> entry : registeredRepositories
				.entrySet()) {
			final DBRepositoryInfo repository = entry.getValue();
			logger.debug("repository " + entry.getKey() + ": "
					+ repository.getName() + " - " + repository.getUrl());

			try {
				repositoryManagerManager.addRepositoryManager(repository, vcs);
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
