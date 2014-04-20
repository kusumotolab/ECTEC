package jp.ac.osaka_u.ist.sdl.ectec.main.clonelinker;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentLinkRetriever;

import org.apache.log4j.Logger;

/**
 * A thread class to detect links of clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkDetectingThread implements Runnable {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CloneSetLinkDetectingThread.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * the target combined commits
	 */
	private final DBCombinedCommitInfo[] targetCombinedCommits;

	/**
	 * a map having detected links of clones
	 */
	private final ConcurrentMap<Long, DBCloneSetLinkInfo> detectedCloneLinks;

	/**
	 * the map between revision id and clone sets including in the revision
	 */
	private final ConcurrentMap<Long, Map<Long, DBCloneSetInfo>> cloneSets;

	/**
	 * the retriever for code fragment links
	 */
	private final CodeFragmentLinkRetriever fragmentLinkRetriever;

	/**
	 * the retriever for clones
	 */
	private final CloneSetRetriever cloneRetriever;

	/**
	 * already processed combined commits
	 */
	private final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits;

	/**
	 * a counter that points the current state of the processing
	 */
	private final AtomicInteger index;

	public CloneSetLinkDetectingThread(
			final DBCombinedCommitInfo[] targetCombinedCommits,
			final ConcurrentMap<Long, DBCloneSetLinkInfo> detectedCloneLinks,
			final ConcurrentMap<Long, Map<Long, DBCloneSetInfo>> cloneSets,
			final CodeFragmentLinkRetriever fragmentLinkRetriever,
			final CloneSetRetriever cloneRetriever,
			final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits,
			final AtomicInteger index) {
		this.targetCombinedCommits = targetCombinedCommits;
		this.detectedCloneLinks = detectedCloneLinks;
		this.cloneSets = cloneSets;
		this.fragmentLinkRetriever = fragmentLinkRetriever;
		this.cloneRetriever = cloneRetriever;
		this.processedCombinedCommits = processedCombinedCommits;
		this.index = index;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= targetCombinedCommits.length) {
				break;
			}

			final DBCombinedCommitInfo targetCombinedCommit = targetCombinedCommits[currentIndex];

			final long beforeCombinedRevisionId = targetCombinedCommit
					.getBeforeCombinedRevisionId();
			if (beforeCombinedRevisionId == -1) {
				processedCombinedCommits.put(targetCombinedCommit.getId(),
						targetCombinedCommit);
				logger.info("[" + processedCombinedCommits.size() + "/"
						+ targetCombinedCommits.length
						+ "] processed the combined commit "
						+ targetCombinedCommit.getId());
				continue;
			}
			final long afterCombinedRevisionId = targetCombinedCommit
					.getAfterCombinedRevisionId();

			try {
				// retrieve necessary elements
				retrieveElements(beforeCombinedRevisionId);
				retrieveElements(afterCombinedRevisionId);

				final Map<Long, DBCodeFragmentLinkInfo> fragmentLinks = fragmentLinkRetriever
						.retrieveElementsWithBeforeRevision(beforeCombinedRevisionId);

				final CloneSetLinker linker = new CloneSetLinker();
				detectedCloneLinks.putAll(linker.detectCloneSetLinks(cloneSets
						.get(beforeCombinedRevisionId).values(),
						cloneSets.get(afterCombinedRevisionId).values(),
						fragmentLinks, beforeCombinedRevisionId,
						afterCombinedRevisionId));

			} catch (Exception e) {
				eLogger.warn("something is wrong in processing the combined commit "
						+ targetCombinedCommit.getId());
			}

			processedCombinedCommits.put(targetCombinedCommit.getId(),
					targetCombinedCommit);
			logger.info("[" + processedCombinedCommits.size() + "/"
					+ targetCombinedCommits.length
					+ "] processed the combined commit "
					+ targetCombinedCommit.getId());
		}
	}

	/**
	 * retrieve elements if they have not been stored into the maps
	 * 
	 * @param combinedRevisionId
	 * @throws SQLException
	 */
	private void retrieveElements(final long combinedRevisionId)
			throws Exception {
		synchronized (cloneSets) {
			if (!cloneSets.containsKey(combinedRevisionId)) {
				final Map<Long, DBCloneSetInfo> retrievedClones = cloneRetriever
						.retrieveElementsInSpecifiedRevision(combinedRevisionId);
				final Map<Long, DBCloneSetInfo> concurrentRetrievedClones = new ConcurrentHashMap<Long, DBCloneSetInfo>();
				concurrentRetrievedClones.putAll(retrievedClones);
				cloneSets.put(combinedRevisionId, concurrentRetrievedClones);
			}
		}
	}
}
