package jp.ac.osaka_u.ist.sdl.ectec.main.clonelinker;

import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;

import org.apache.log4j.Logger;

/**
 * A class that performs the main process of clone set link detection
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkDetector {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CloneSetLinkDetector.class.getName());

	/**
	 * the settings
	 */
	private final CloneSetLinkDetectorMainSettings settings;

	/**
	 * the manager of db
	 */
	private final DBConnectionManager dbManager;

	public CloneSetLinkDetector(
			final CloneSetLinkDetectorMainSettings settings,
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

		logger.info("detecting clone set links ... ");
		final CloneSetLinkIdentifier identifier = new CloneSetLinkIdentifier(
				targetCombinedCommits, settings.getThreads(),
				dbManager.getFragmentLinkRetriever(),
				dbManager.getCloneRetriever(),
				dbManager.getCloneLinkRegisterer(), settings.getMaxBatchCount());
		identifier.run();
		logger.info("complete");
	}

}
