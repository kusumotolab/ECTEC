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
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
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
	private final Map<Long, DBCommitInfo> commits;

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

	public CloneSetLinkIdentifier(final Map<Long, DBCommitInfo> commits,
			final int threadsCount,
			final CodeFragmentLinkRetriever fragmentLinkRetriever,
			final CloneSetRetriever cloneRetriever,
			final CloneSetLinkRegisterer cloneLinkRegisterer,
			final int maxElementsCount) {
		this.commits = commits;
		this.threadsCount = threadsCount;
		this.fragmentLinkRetriever = fragmentLinkRetriever;
		this.cloneRetriever = cloneRetriever;
		this.cloneLinkRegisterer = cloneLinkRegisterer;
		this.maxElementsCount = maxElementsCount;
	}

	public void run() throws Exception {
		if (threadsCount == 1) {
			runWithSingleThread();
		} else {
			runWithMultipleThread();
		}
	}

	private void runWithSingleThread() throws Exception {
		assert threadsCount == 1;

		final Map<Long, Collection<Long>> revisionAndRelatedCommits = detectRevisionAndRelatedCommits();
		final DBCommitInfo[] commitsArray = commits.values().toArray(new DBCommitInfo[0]);

		final SingleThreadCloneSetLinkDetector detector = new SingleThreadCloneSetLinkDetector(
				commitsArray, fragmentLinkRetriever, cloneRetriever,
				cloneLinkRegisterer, revisionAndRelatedCommits,
				maxElementsCount);
		detector.detectAndRegister();
	}

	private void runWithMultipleThread() throws Exception {
		assert threadsCount > 1;

		final DBCommitInfo[] commitsArray = commits.values().toArray(new DBCommitInfo[0]);
		final Map<Long, Collection<Long>> revisionAndRelatedCommits = detectRevisionAndRelatedCommits();

		final ConcurrentMap<Long, DBCloneSetLinkInfo> detectedCloneLinks = new ConcurrentHashMap<Long, DBCloneSetLinkInfo>();
		final ConcurrentMap<Long, Map<Long, DBCloneSetInfo>> cloneSets = new ConcurrentHashMap<Long, Map<Long, DBCloneSetInfo>>();
		final ConcurrentMap<Long, DBCommitInfo> processedCommits = new ConcurrentHashMap<Long, DBCommitInfo>();
		final AtomicInteger index = new AtomicInteger(0);

		final Thread[] threads = new Thread[threadsCount - 1];
		for (int i = 0; i < threadsCount - 1; i++) {
			threads[i] = new Thread(new CloneSetLinkDetectingThread(
					commitsArray, detectedCloneLinks, cloneSets,
					fragmentLinkRetriever, cloneRetriever, processedCommits,
					index));
			threads[i].start();
		}

		final CloneSetLinkDetectingThreadMonitor monitor = new CloneSetLinkDetectingThreadMonitor(
				detectedCloneLinks, cloneLinkRegisterer, cloneSets,
				processedCommits, revisionAndRelatedCommits, maxElementsCount);
		monitor.monitor();
	}

	private Map<Long, Collection<Long>> detectRevisionAndRelatedCommits() {
		final Map<Long, Collection<Long>> result = new TreeMap<Long, Collection<Long>>();
		for (final Map.Entry<Long, DBCommitInfo> entry : commits.entrySet()) {
			final DBCommitInfo commit = entry.getValue();

			final long beforeRevisionId = commit.getBeforeRevisionId();
			if (result.containsKey(beforeRevisionId)) {
				result.get(beforeRevisionId).add(entry.getValue().getId());
			} else {
				final Collection<Long> newCollection = new TreeSet<Long>();
				newCollection.add(entry.getValue().getId());
				result.put(beforeRevisionId, newCollection);
			}

			final long afterRevisionId = entry.getValue().getAfterRevisionId();
			if (result.containsKey(afterRevisionId)) {
				result.get(afterRevisionId).add(entry.getValue().getId());
			} else {
				final Collection<Long> newCollection = new TreeSet<Long>();
				newCollection.add(entry.getValue().getId());
				result.put(afterRevisionId, newCollection);
			}
		}
		return result;
	}

}
