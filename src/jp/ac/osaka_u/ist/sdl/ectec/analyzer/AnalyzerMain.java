package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.RepositoryManagerManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBMaker;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * The main class of the analyzer
 * 
 * @author k-hotta
 * 
 */
public class AnalyzerMain {

	/**
	 * the manager of the repository manager
	 */
	private static RepositoryManagerManager repositoryManagerManager = null;

	/**
	 * the manager of the db
	 */
	private static DBConnectionManager dbManager = null;

	public static void main(String[] args) {
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);

			preprocess(settings);

			main(settings);

			postprocess();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void preprocess(final AnalyzerSettings settings)
			throws Exception {
		// set the level of verbose output
		MessagePrinter.setLevel(settings.getVerboseLevel());

		// print for starting operations
		initialPrint(settings);

		// initialize the repository
		initializeRepository(settings);

		// initialize the db
		initializeDb(settings);

		// detect start/end revisions if they are not specified
		resetRevisionIdentifiers(settings);
	}

	private static void initialPrint(final AnalyzerSettings settings) {
		MessagePrinter.stronglyPrint("operations start");
		MessagePrinter.print(" with the configurations below");
		MessagePrinter.stronglyPrintln();

		MessagePrinter.println("\ttarget repository: "
				+ settings.getRepositoryPath());
		MessagePrinter.println("\toutput database file: "
				+ settings.getDbPath());
		MessagePrinter
				.println("\taddtitional path: "
						+ ((settings.getAdditionalPath() == null) ? "nothing is specified"
								: settings.getAdditionalPath()));
		MessagePrinter.println("\ttarget language: "
				+ settings.getLanguage().toString());
		MessagePrinter.println("\tthe number of threads: "
				+ settings.getThreads());
		MessagePrinter.println("\tuser name for accessing the repository: "
				+ ((settings.getUserName() == null) ? "nothing is specified"
						: settings.getUserName()));
		MessagePrinter.println("\tpassword for accessing the repository: "
				+ ((settings.getPasswd() == null) ? "nothing is specified"
						: "*********"));
		MessagePrinter
				.println("\tstart revision: "
						+ ((settings.getUserName() == null) ? "nothing is specified, will start at the earliest revision"
								: settings.getStartRevisionIdentifier()));
		MessagePrinter
				.println("\tend revision: "
						+ ((settings.getUserName() == null) ? "nothing is specified, will end at the latest revision"
								: settings.getStartRevisionIdentifier()));
		MessagePrinter.println("\tversion control system targeted: "
				+ settings.getVersionControlSystem().toString());
		MessagePrinter
				.println("\tthe loaded properties file: "
						+ ((settings.getPropertiesFilePath() == null) ? "the default one"
								: settings.getPropertiesFilePath()));
		MessagePrinter.println("\toverwrite the db if it already exists: "
				+ ((settings.isOverwriteDb()) ? "yes" : "no"));
		MessagePrinter.println("\tthe maximum nuber of batched statements: "
				+ settings.getMaxBatchCount());
		MessagePrinter.stronglyPrintln();
	}

	private static void initializeRepository(final AnalyzerSettings settings)
			throws Exception {
		MessagePrinter.stronglyPrintln("initializing the repository ... ");
		repositoryManagerManager = new RepositoryManagerManager(
				settings.getVersionControlSystem(),
				settings.getRepositoryPath(), settings.getUserName(),
				settings.getPasswd(), settings.getAdditionalPath());
		MessagePrinter.stronglyPrintln("\tOK");
		MessagePrinter.stronglyPrintln();
	}

	private static void initializeDb(final AnalyzerSettings settings)
			throws Exception {
		MessagePrinter.stronglyPrintln("initializing the database ... ");
		dbManager = new DBConnectionManager(settings.getDbPath(),
				settings.getMaxBatchCount());
		final DBMaker dbMaker = new DBMaker(dbManager);
		dbMaker.makeDb(settings.isOverwriteDb());
		MessagePrinter.stronglyPrintln("\tOK");
		MessagePrinter.stronglyPrintln();
	}

	private static void resetRevisionIdentifiers(final AnalyzerSettings settings)
			throws Exception {
		if (settings.getStartRevisionIdentifier() == null) {
			settings.setStartRevisionIdentifier(repositoryManagerManager
					.getRepositoryManager().getFirstRevision());
		}
		if (settings.getEndRevisionIdentifier() == null) {
			settings.setEndRevisionIdentifier(repositoryManagerManager
					.getRepositoryManager().getLatestRevision());
		}
	}

	private static void main(final AnalyzerSettings settings) throws Exception {
		MessagePrinter.stronglyPrintln("start main operations");
		MessagePrinter.stronglyPrintln("\tfrom revision "
				+ settings.getStartRevisionIdentifier());
		MessagePrinter.stronglyPrintln("\tto revision "
				+ settings.getEndRevisionIdentifier());
		MessagePrinter.stronglyPrintln(
				);
		detectAndRegisterTargetRevisions(settings);
	}

	private static void detectAndRegisterTargetRevisions(
			final AnalyzerSettings settings) throws Exception {
		MessagePrinter.stronglyPrintln("detecting target revisions ... ");

		final RevisionIdentifier identifier = new RevisionIdentifier(
				repositoryManagerManager.getRepositoryManager()
						.getTargetRevisionDetector(),
				dbManager.getRevisionRegisterer());
		identifier.detectAndRegister(settings.getLanguage(),
				settings.getStartRevisionIdentifier(),
				settings.getEndRevisionIdentifier());

		MessagePrinter.stronglyPrintln();
	}

	private static void postprocess() {
		if (dbManager != null) {
			dbManager.close();
		}
	}

}
