package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;

import org.apache.log4j.Logger;

/**
 * A class to perform the main process of fragment linker
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkDetector {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CodeFragmentLinkDetector.class.getName());

	/**
	 * the settings
	 */
	private final CodeFragmentLinkDetectorMainSettings settings;

	/**
	 * the manager of db
	 */
	private final DBConnectionManager dbManager;

	public CodeFragmentLinkDetector(
			final CodeFragmentLinkDetectorMainSettings settings,
			final DBConnectionManager dbManager) {
		this.settings = settings;
		this.dbManager = dbManager;
	}

	/**
	 * perform the main process
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception {
		logger.info("retrieving combined commits ... ");
		final List<Long> targetCombinedCommitIds = settings
				.getCombinedCommitIds();
		final Map<Long, DBCombinedCommitInfo> targetCombinedCommits = (targetCombinedCommitIds
				.isEmpty()) ? dbManager.getCombinedCommitRetriever()
				.retrieveAll() : dbManager.getCombinedCommitRetriever()
				.retrieveWithIds(targetCombinedCommitIds);
		logger.info(targetCombinedCommits.size()
				+ " combined commits have been retrieved");

		logger.info("detecting fragment links ... ");
		final CodeFragmentLinkIdentifier identifier = new CodeFragmentLinkIdentifier(
				targetCombinedCommits, settings.getThreads(),
				dbManager.getFragmentLinkRegisterer(),
				dbManager.getFragmentRetriever(), dbManager.getCrdRetriever(),
				dbManager.getCloneRetriever(), settings.getFragmentLinkMode()
						.getLinker(), settings.getSimilarityThreshold(),
				settings.getCrdSimilarityMode().getCalculator(),
				settings.getMaxBatchCount(),
				settings.isDetectCrossProjectLinks(),
				settings.isOnlyFragmentInClonesInBeforeRevision());
		identifier.run();
		logger.info("complete");
	}

}
