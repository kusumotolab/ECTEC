package jp.ac.osaka_u.ist.sdl.ectec.main.clonedetector;

import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;

import org.apache.log4j.Logger;

/**
 * A class to perform the main process of clone detection
 * 
 * @author k-hotta
 * 
 */
public class CloneDetector {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CloneDetector.class.getName());

	/**
	 * the settings
	 */
	private final CloneDetectorMainSettings settings;

	/**
	 * the manager of db
	 */
	private final DBConnectionManager dbManager;

	public CloneDetector(final CloneDetectorMainSettings settings,
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
		logger.info("retrieving combined revisions ...");
		final List<Long> targetCombinedRevisionIds = settings
				.getCombinedRevisionIds();
		final Map<Long, DBCombinedRevisionInfo> targetCombinedRevisions = (targetCombinedRevisionIds
				.isEmpty()) ? dbManager.getCombinedRevisionRetriever()
				.retrieveAll() : dbManager.getCombinedRevisionRetriever()
				.retrieveWithIds(targetCombinedRevisionIds);
		logger.info(targetCombinedRevisions.size()
				+ " combined revisions have been retrieved");

		logger.info("detecting clones ... ");
		final BlockBasedCloneIdentifier identifier = new BlockBasedCloneIdentifier(
				targetCombinedRevisions, settings.getThreads(),
				dbManager.getFragmentRetriever(),
				dbManager.getCloneRegisterer(), settings.getMaxBatchCount(),
				settings.getCloneSizeThreshold(),
				settings.isDetectCrossProjectClones());
		identifier.run();
		logger.info("complete");
	}
}
