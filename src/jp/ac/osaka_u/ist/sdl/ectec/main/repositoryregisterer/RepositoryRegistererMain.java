package jp.ac.osaka_u.ist.sdl.ectec.main.repositoryregisterer;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;

import org.apache.log4j.Logger;

/**
 * The main class to register the given repository. <br>
 * This process MUST follow the db making process.
 *
 * @author k-hotta
 *
 */
public class RepositoryRegistererMain {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(RepositoryRegistererMain.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * the db manater
	 */
	private static DBConnectionManager dbManager = null;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			// load the settings
			final RepositoryRegistererMainSettings settings = loadSettings(args);

			// initialize db
			preprocess(settings);
			// main processing
			final RepositoryRegisterer registerer = new RepositoryRegisterer(
					settings, dbManager);
			registerer.perform();
			// post processing
			postprocess();

			logger.info("operations have finished.");

		} catch (Exception e) {
			eLogger.fatal("operations failed.\n" + e.toString());
			
			if (dbManager != null) {
				dbManager.rollback();
			}
			postprocess();
		}
	}

	/**
	 * load the settings
	 *
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private static RepositoryRegistererMainSettings loadSettings(
			final String[] args) throws Exception {
		final RepositoryRegistererMainSettings settings = new RepositoryRegistererMainSettings();
		settings.load(args);
		return settings;
	}

	/**
	 * perform pre-processing
	 *
	 * @param settings
	 * @throws Exception
	 */
	private static void preprocess(
			final RepositoryRegistererMainSettings settings) throws Exception {
		dbManager = new DBConnectionManager(settings.getDBConfig(),
				settings.getMaxBatchCount());
		logger.info("connected to the database");

		dbManager.initializeElementCounters(settings.getHeaderOfId());
		logger.info("initialized counters of elements");
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
