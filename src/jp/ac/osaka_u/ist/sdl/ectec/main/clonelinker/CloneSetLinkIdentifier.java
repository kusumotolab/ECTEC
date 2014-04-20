package jp.ac.osaka_u.ist.sdl.ectec.main.clonelinker;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CloneSetLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentLinkRetriever;

/**
 * A class for managing clone set link detectors
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkIdentifier {

	/**
	 * the target commits
	 */
	private final Map<Long, DBCombinedCommitInfo> combinedCommits;

	/**
	 * the number of threads
	 */
	private final int threadsCount;

	/**
	 * the retriever for links of code fragments
	 */
	private final CodeFragmentLinkRetriever fragmentLinkRetriever;

	/**
	 * the retriever for clone sets
	 */
	private final CloneSetRetriever cloneRetriever;

	/**
	 * the registerer for clone links
	 */
	private final CloneSetLinkRegisterer cloneLinkRegisterer;

	/**
	 * the threshold for storing elements
	 */
	private final int maxElementsCount;

	public CloneSetLinkIdentifier(
			final Map<Long, DBCombinedCommitInfo> combinedCommits,
			final int threadsCount,
			final CodeFragmentLinkRetriever fragmentLinkRetriever,
			final CloneSetRetriever cloneRetriever,
			final CloneSetLinkRegisterer cloneLinkRegisterer,
			final int maxElementsCount) {
		this.combinedCommits = combinedCommits;
		this.threadsCount = threadsCount;
		this.fragmentLinkRetriever = fragmentLinkRetriever;
		this.cloneRetriever = cloneRetriever;
		this.cloneLinkRegisterer = cloneLinkRegisterer;
		this.maxElementsCount = maxElementsCount;
	}

	public void run() throws Exception {
		final DBCombinedCommitInfo[] combinedCommitsArray = combinedCommits
				.values().toArray(new DBCombinedCommitInfo[0]);

		// the minimum number of thread is 2
		final int tailoredThreadsCount = Math.min(combinedCommits.size(),
				Math.max(threadsCount, 2));

		final Map<Long, Collection<Long>> combinedRevisionAndRelatedCombinedCommits = detectCombinedRevisionAndRelatedCombinedCommits();

		final ConcurrentMap<Long, DBCloneSetLinkInfo> detectedCloneLinks = new ConcurrentHashMap<Long, DBCloneSetLinkInfo>();
		final ConcurrentMap<Long, Map<Long, DBCloneSetInfo>> cloneSets = new ConcurrentHashMap<Long, Map<Long, DBCloneSetInfo>>();
		final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits = new ConcurrentHashMap<Long, DBCombinedCommitInfo>();
		final AtomicInteger index = new AtomicInteger(0);

		final Thread[] threads = new Thread[tailoredThreadsCount - 1];
		for (int i = 0; i < tailoredThreadsCount - 1; i++) {
			threads[i] = new Thread(new CloneSetLinkDetectingThread(
					combinedCommitsArray, detectedCloneLinks, cloneSets,
					fragmentLinkRetriever, cloneRetriever,
					processedCombinedCommits, index));
			threads[i].start();
		}

		final CloneSetLinkDetectingThreadMonitor monitor = new CloneSetLinkDetectingThreadMonitor(
				detectedCloneLinks, cloneLinkRegisterer, cloneSets,
				processedCombinedCommits,
				combinedRevisionAndRelatedCombinedCommits, maxElementsCount,
				threads);
		monitor.monitor();
	}

	private Map<Long, Collection<Long>> detectCombinedRevisionAndRelatedCombinedCommits() {
		final Map<Long, Collection<Long>> result = new TreeMap<Long, Collection<Long>>();
		for (final Map.Entry<Long, DBCombinedCommitInfo> entry : combinedCommits
				.entrySet()) {
			final DBCombinedCommitInfo combinedCommit = entry.getValue();

			final long beforeCombinedRevisionId = combinedCommit
					.getBeforeCombinedRevisionId();
			if (result.containsKey(beforeCombinedRevisionId)) {
				result.get(beforeCombinedRevisionId).add(
						entry.getValue().getId());
			} else {
				final Collection<Long> newCollection = new TreeSet<Long>();
				newCollection.add(entry.getValue().getId());
				result.put(beforeCombinedRevisionId, newCollection);
			}

			final long afterCombinedRevisionId = entry.getValue()
					.getAfterCombinedRevisionId();
			if (result.containsKey(afterCombinedRevisionId)) {
				result.get(afterCombinedRevisionId).add(
						entry.getValue().getId());
			} else {
				final Collection<Long> newCollection = new TreeSet<Long>();
				newCollection.add(entry.getValue().getId());
				result.put(afterCombinedRevisionId, newCollection);
			}
		}
		return result;
	}

}
