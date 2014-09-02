package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalStateException;
import jp.ac.osaka_u.ist.sdl.ectec.main.linker.similarity.ContentBasedCRDSimilarityCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.settings.CRDSimilarityCalculateMode;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.RepositoryManagerManager;

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

		if (settings.getCrdSimilarityMode() == CRDSimilarityCalculateMode.CONTENT_LEVENSHTEIN) {
			// initialize the manager of repository managers
			final RepositoryManagerManager repositoryManagerManager = new RepositoryManagerManager();
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

			for (final Map.Entry<Long, DBRepositoryInfo> entry : registeredRepositories
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

			ContentBasedCRDSimilarityCalculator calculator = (ContentBasedCRDSimilarityCalculator) settings
					.getCrdSimilarityMode().getCalculator();
			calculator.setup(repositoryManagerManager,
					dbManager.getFileRetriever(),
					dbManager.getRevisionRetriever(),
					dbManager.getCombinedRevisionRetriever());
		}
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
