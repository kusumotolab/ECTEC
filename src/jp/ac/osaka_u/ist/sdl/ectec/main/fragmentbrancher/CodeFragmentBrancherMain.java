package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentbrancher;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;

import org.apache.log4j.Logger;

/**
 * This is the main class for branching code fragments.
 * 
 * @author k-hotta
 *
 */
public class CodeFragmentBrancherMain {

	private static final Logger logger = LoggingManager
			.getLogger(CodeFragmentBrancherMain.class.getName());

	private static final Logger eLogger = LoggingManager.getLogger("error");

	private static DBConnectionManager dbManager = null;

	public static void main(String[] args) {
		try {
			final CodeFragmentBrancherMainSettings settings = loadSettings(args);

			preprocess(settings);

			final CodeFragmentBrancher brancher = new CodeFragmentBrancher(
					settings, dbManager);
			brancher.run();

			postprocess();

		} catch (Exception e) {
			eLogger.fatal("operations failed.\n" + e.toString());
			e.printStackTrace();

			if (dbManager != null) {
				dbManager.rollback();
			}
			postprocess();
		}
	}

	private static final CodeFragmentBrancherMainSettings loadSettings(
			final String[] args) throws Exception {
		final CodeFragmentBrancherMainSettings settings = new CodeFragmentBrancherMainSettings();
		settings.load(args);
		return settings;
	}

	private static void preprocess(
			final CodeFragmentBrancherMainSettings settings) throws Exception {
		// make a connection between the db file
		dbManager = new DBConnectionManager(settings.getDBConfig(),
				settings.getMaxBatchCount());
		logger.info("connected to the db");

		dbManager.initializeElementCounters(settings.getHeaderOfId());
		logger.info("initialized counters of elements");
	}

	private static void postprocess() {
		if (dbManager != null) {
			dbManager.close();
		}
	}

}
