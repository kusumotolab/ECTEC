package jp.ac.osaka_u.ist.sdl.ectec.main.combiner;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;

import org.apache.log4j.Logger;

/**
 * The main class to make combined revisions and combined commits. <br>
 * This process MUST run with the revision table and commit table made.
 * 
 * @author k-hotta
 * 
 */
public class CombinerMain {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CombinerMain.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * the db manager
	 */
	private static DBConnectionManager dbManager = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// load the settings
			final CombinerMainSettings settings = loadSettings(args);

			// pre process
			preprocess(settings);

			// main process
			final Combiner combiner = new Combiner(dbManager);
			combiner.perform();

			// post process
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
	private static CombinerMainSettings loadSettings(final String[] args)
			throws Exception {
		final CombinerMainSettings settings = new CombinerMainSettings();
		settings.load(args);
		return settings;
	}

	/**
	 * perform pre-processing
	 * 
	 * @param settings
	 * @throws Exception
	 */
	private static void preprocess(final CombinerMainSettings settings)
			throws Exception {
		dbManager = new DBConnectionManager(settings.getDbPath(),
				settings.getMaxBatchCount());
		logger.info("connected to the database");
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
