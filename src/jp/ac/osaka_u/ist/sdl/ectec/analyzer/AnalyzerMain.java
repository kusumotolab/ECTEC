package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.clonedetector.BlockBasedCloneIdentifier;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.clonelinker.CloneSetLinkIdentifier;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.filedetector.ChangedFilesIdentifier;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.genealogydetector.CloneGenealogyIdentifier;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.genealogydetector.FragmentGenealogyIdentifier;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.linker.CodeFragmentLinkIdentifier;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.CodeFragmentIdentifier;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash.DefaultHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.normalizer.NormalizerCreator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.RepositoryManagerManager;
import jp.ac.osaka_u.ist.sdl.ectec.data.Commit;
import jp.ac.osaka_u.ist.sdl.ectec.data.FileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBMaker;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;
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

	/*
	 * preprocessors follow
	 */

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
		MessagePrinter
				.println("\thow to calculate hash values for clone detection: "
						+ settings.getCloneHashCalculateMode().toString());
		MessagePrinter.println("\thow to calculate similarity of CRDs: "
				+ settings.getCrdSimilarityMode().toString());
		MessagePrinter.println("\thow to link code fragments: "
				+ settings.getFragmentLinkMode().toString());
		MessagePrinter.println("\tthe granularity of the analysis: "
				+ settings.getGranularity().toString());

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

	/*
	 * preprocessors end
	 */

	/*
	 * the main process follows
	 */

	/**
	 * the main function
	 * 
	 * @param settings
	 * @throws Exception
	 */
	private static void main(final AnalyzerSettings settings) throws Exception {
		MessagePrinter.stronglyPrintln("start main operations");
		MessagePrinter.stronglyPrintln("\tfrom revision "
				+ settings.getStartRevisionIdentifier());
		MessagePrinter.stronglyPrintln("\tto revision "
				+ settings.getEndRevisionIdentifier());
		MessagePrinter.stronglyPrintln();

		detectAndRegisterTargetRevisions(settings);

		detectAndRegisterFiles(settings);

		detectAndRegisterFragments(settings);

		detectAndRegisterFragmentLinks(settings);

		detectAndRegisterFragmentGenealogies(settings);

		detectAndRegisterClones(settings);

		detectAndRegisterCloneLinks(settings);

		detectAndRegisterCloneGenealogies(settings);
	}

	private static void detectAndRegisterTargetRevisions(
			final AnalyzerSettings settings) throws Exception {
		MessagePrinter.stronglyPrintln("detecting target revisions ... ");

		final RevisionIdentifier identifier = new RevisionIdentifier(
				repositoryManagerManager.getRepositoryManager()
						.getTargetRevisionDetector(),
				dbManager.getRevisionRegisterer(),
				dbManager.getCommitRegisterer());
		identifier.detectAndRegister(settings.getLanguage(),
				settings.getStartRevisionIdentifier(),
				settings.getEndRevisionIdentifier());

		MessagePrinter.stronglyPrintln();

	}

	private static void detectAndRegisterFiles(final AnalyzerSettings settings)
			throws Exception {
		final Map<Long, Commit> commits = dbManager.getCommitRetriever()
				.retrieveAll();
		final ChangedFilesIdentifier identifier = new ChangedFilesIdentifier(
				repositoryManagerManager.getRepositoryManager(),
				dbManager.getFileRegisterer(), settings.getLanguage(),
				settings.getThreads());
		identifier.detectAndRegister(commits);
	}

	private static void detectAndRegisterFragments(
			final AnalyzerSettings settings) throws Exception {
		MessagePrinter
				.stronglyPrintln("detecting and registering code fragments and their crds ... ");

		final Collection<FileInfo> files = dbManager.getFileRetriever()
				.retrieveAll().values();

		final Collection<RevisionInfo> revisions = dbManager
				.getRevisionRetriever().retrieveAll().values();

		final NormalizerCreator blockAnalyzerCreator = new NormalizerCreator(
				settings.getCloneHashCalculateMode());

		final IHashCalculator hashCalculator = new DefaultHashCalculator();

		final CodeFragmentIdentifier identifier = new CodeFragmentIdentifier(
				files, revisions, settings.getThreads(),
				dbManager.getCrdRegisterer(),
				dbManager.getFragmentRegisterer(),
				Constants.MAX_ELEMENTS_COUNT,
				repositoryManagerManager.getRepositoryManager(),
				settings.getGranularity(), blockAnalyzerCreator, hashCalculator);

		identifier.run();

		MessagePrinter.stronglyPrintln();
	}

	private static void detectAndRegisterFragmentLinks(
			final AnalyzerSettings settings) throws Exception {
		MessagePrinter
				.stronglyPrintln("detecting and registering links of code fragments ... ");

		final Map<Long, Commit> commits = dbManager.getCommitRetriever()
				.retrieveAll();

		final CodeFragmentLinkIdentifier identifier = new CodeFragmentLinkIdentifier(
				commits, settings.getThreads(),
				dbManager.getFragmentLinkRegisterer(),
				dbManager.getFragmentRetriever(), dbManager.getCrdRetriever(),
				settings.getFragmentLinkMode().getLinker(),
				settings.getSimilarityThreshold(), settings
						.getCrdSimilarityMode().getCalculator(),
				Constants.MAX_ELEMENTS_COUNT);
		identifier.run();

		MessagePrinter.stronglyPrintln();
	}

	private static void detectAndRegisterFragmentGenealogies(
			final AnalyzerSettings settings) throws Exception {
		MessagePrinter
				.stronglyPrintln("detecting and registering genealogies of code fragments ... ");

		final Map<Long, RevisionInfo> targetRevisions = dbManager
				.getRevisionRetriever().retrieveAll();

		final FragmentGenealogyIdentifier identifier = new FragmentGenealogyIdentifier(
				targetRevisions, settings.getThreads(),
				dbManager.getFragmentRetriever(),
				dbManager.getFragmentLinkRetriever(),
				dbManager.getFragmentGenealogyRegisterer());
		identifier.detectAndRegister();

		MessagePrinter.stronglyPrintln();
	}

	private static void detectAndRegisterClones(final AnalyzerSettings settings)
			throws Exception {
		MessagePrinter
				.stronglyPrintln("detecting and registering clone sets ... ");

		final Map<Long, RevisionInfo> targetRevisions = dbManager
				.getRevisionRetriever().retrieveAll();

		final BlockBasedCloneIdentifier identifier = new BlockBasedCloneIdentifier(
				targetRevisions, settings.getThreads(),
				dbManager.getFragmentRetriever(),
				dbManager.getCloneRegisterer(), Constants.MAX_ELEMENTS_COUNT);
		identifier.run();

		MessagePrinter.stronglyPrintln();
	}

	private static void detectAndRegisterCloneLinks(
			final AnalyzerSettings settings) throws Exception {
		MessagePrinter
				.stronglyPrintln("detecting and registering links of clone sets ... ");

		final Map<Long, Commit> commits = dbManager.getCommitRetriever()
				.retrieveAll();

		final CloneSetLinkIdentifier identifier = new CloneSetLinkIdentifier(
				commits, settings.getThreads(),
				dbManager.getFragmentLinkRetriever(),
				dbManager.getCloneRetriever(),
				dbManager.getCloneLinkRegisterer(),
				Constants.MAX_ELEMENTS_COUNT);
		identifier.run();

		MessagePrinter.stronglyPrintln();
	}

	private static void detectAndRegisterCloneGenealogies(
			final AnalyzerSettings settings) throws Exception {
		MessagePrinter
				.stronglyPrintln("detecting and registering genealogies of clones ... ");

		final SortedMap<Long, RevisionInfo> targetRevisions = dbManager
				.getRevisionRetriever().retrieveAll();
		final long lastRevisionId = targetRevisions.lastKey();

		final CloneGenealogyIdentifier identifier = new CloneGenealogyIdentifier(
				targetRevisions, settings.getThreads(),
				dbManager.getCloneRetriever(),
				dbManager.getCloneLinkRetriever(),
				dbManager.getCloneGenealogyRegisterer(), lastRevisionId);
		identifier.detectAndRegister();

		MessagePrinter.stronglyPrintln();
	}

	/*
	 * main process ends
	 */

	/*
	 * post processors follow
	 */

	private static void postprocess() {
		if (dbManager != null) {
			dbManager.close();
		}
	}

}
