package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;

import org.apache.log4j.Logger;

public class CodeFragmentLinkDetectorMain {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CodeFragmentLinkDetectorMain.class.getName());

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
			final CodeFragmentLinkDetectorMainSettings settings = loadSettings(args);

			// pre processing
			preprocess(settings);

			// main processing
			final CodeFragmentLinkDetector detector = new CodeFragmentLinkDetector(
					settings, dbManager);
			detector.run();

			// post processing
			postprocess();

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
	private static final CodeFragmentLinkDetectorMainSettings loadSettings(
			final String[] args) throws Exception {
		final CodeFragmentLinkDetectorMainSettings settings = new CodeFragmentLinkDetectorMainSettings();
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
			final CodeFragmentLinkDetectorMainSettings settings)
			throws Exception {
		// make a connection between the db file
		dbManager = new DBConnectionManager(settings.getDbPath(),
				settings.getMaxBatchCount());
		logger.info("connected to the db");

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
