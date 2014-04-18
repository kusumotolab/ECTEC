package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.hash.DefaultHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.NormalizerCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.vcs.RepositoryManagerManager;

import org.apache.log4j.Logger;

/**
 * A class to detect code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentDetector {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CodeFragmentDetector.class.getName());

	/**
	 * the settings
	 */
	private final CodeFragmentDetectorMainSettings settings;

	/**
	 * the manager of db
	 */
	private final DBConnectionManager dbManager;

	/**
	 * the manager of repository managers
	 */
	private final RepositoryManagerManager repositoryManagerManager;

	public CodeFragmentDetector(
			final CodeFragmentDetectorMainSettings settings,
			final DBConnectionManager dbManager,
			final RepositoryManagerManager repositoryManagerManager) {
		this.settings = settings;
		this.dbManager = dbManager;
		this.repositoryManagerManager = repositoryManagerManager;
	}

	/**
	 * perform the main process
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception {
		logger.info("retrieving files ... ");
		final List<Long> targetFileIds = settings.getFileIds();
		final Map<Long, DBFileInfo> files = (targetFileIds.isEmpty()) ? dbManager
				.getFileRetriever().retrieveAll() : dbManager
				.getFileRetriever().retrieveWithIds(targetFileIds);
		logger.info(files.size() + " files have been retrieved");

		logger.info("retrieving revisions ... ");
		final ConcurrentMap<Long, DBRevisionInfo> originalRevisions = new ConcurrentHashMap<Long, DBRevisionInfo>();
		originalRevisions
				.putAll(dbManager.getRevisionRetriever().retrieveAll());
		logger.info(originalRevisions.size() + " revisions have been retrieved");

		logger.info("retrieving combined revisions ... ");
		final ConcurrentMap<Long, DBCombinedRevisionInfo> combinedRevisions = new ConcurrentHashMap<Long, DBCombinedRevisionInfo>();
		combinedRevisions.putAll(dbManager.getCombinedRevisionRetriever()
				.retrieveAll());
		logger.info(combinedRevisions.size()
				+ " combined revisions have been retrieved");

		final IHashCalculator hashCalculator = new DefaultHashCalculator();

		logger.info("identifying code fragments ... ");
		final CodeFragmentIdentifier identifier = new CodeFragmentIdentifier(
				files.values(), originalRevisions, combinedRevisions,
				settings.getThreads(), dbManager.getCrdRegisterer(),
				dbManager.getFragmentRegisterer(), settings.getMaxBatchCount(),
				repositoryManagerManager.getRepositoryManagers(),
				settings.getGranularity(), new NormalizerCreator(
						settings.getCloneHashMode()), hashCalculator);
		identifier.run();
		logger.info("complete");
	}
}
