package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentbrancher;

import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;

import org.apache.log4j.Logger;

public class CodeFragmentBrancher {

	private static final Logger logger = LoggingManager
			.getLogger(CodeFragmentBrancher.class.getName());

	private final CodeFragmentBrancherMainSettings settings;

	private final DBConnectionManager dbManager;

	public CodeFragmentBrancher(
			final CodeFragmentBrancherMainSettings settings,
			final DBConnectionManager dbManager) {
		this.settings = settings;
		this.dbManager = dbManager;
	}

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

		logger.info("detecting fragment branches ... ");
		final CodeFragmentBranchIdentifier identifier = new CodeFragmentBranchIdentifier(
				targetCombinedCommits, settings.getThreads(),
				dbManager.getFragmentLinkRegisterer(),
				dbManager.getFragmentLinkRetriever(),
				dbManager.getCloneRetriever(),
				dbManager.getFragmentRetriever(), settings.getMaxBatchCount());
		identifier.run();
		logger.info("complete");
	}

}
