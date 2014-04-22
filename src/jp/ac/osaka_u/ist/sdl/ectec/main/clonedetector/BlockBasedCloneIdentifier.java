package jp.ac.osaka_u.ist.sdl.ectec.main.clonedetector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CloneSetRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;

/**
 * A class for managing threads that detect clones
 * 
 * @author k-hotta
 * 
 */
public class BlockBasedCloneIdentifier {

	/**
	 * the target combined revisions
	 */
	private final Map<Long, DBCombinedRevisionInfo> combinedRevisions;

	/**
	 * the number of threads
	 */
	private final int threadsCount;

	/**
	 * the retriever for code fragments
	 */
	private final CodeFragmentRetriever fragmentRetriever;

	/**
	 * the registerer for clones
	 */
	private final CloneSetRegisterer cloneRegisterer;

	/**
	 * the threshold for storing elements
	 */
	private final int maxElementsCount;

	/**
	 * the size threshold
	 */
	private final int cloneSizeThreshold;

	/**
	 * whether detect cross project clones or not
	 */
	private final boolean detectCrossProjectClones;

	public BlockBasedCloneIdentifier(
			final Map<Long, DBCombinedRevisionInfo> combinedRevisions,
			final int threadsCount,
			final CodeFragmentRetriever fragmentRetriever,
			final CloneSetRegisterer cloneRegisterer,
			final int maxElementsCount, final int cloneSizeThreshold,
			final boolean detectCrossProjectClones) {
		this.combinedRevisions = combinedRevisions;
		this.threadsCount = threadsCount;
		this.fragmentRetriever = fragmentRetriever;
		this.cloneRegisterer = cloneRegisterer;
		this.maxElementsCount = maxElementsCount;
		this.cloneSizeThreshold = cloneSizeThreshold;
		this.detectCrossProjectClones = detectCrossProjectClones;
	}

	public void run() throws Exception {
		final DBCombinedRevisionInfo[] revisionsArray = combinedRevisions
				.values().toArray(new DBCombinedRevisionInfo[0]);

		// the minimum number of thread is 2
		final int tailoredThreadsCount = Math.max(
				Math.min(combinedRevisions.size(), threadsCount), 2);

		final ConcurrentMap<Long, DBCloneSetInfo> detectedClones = new ConcurrentHashMap<Long, DBCloneSetInfo>();
		final AtomicInteger index = new AtomicInteger(0);

		final Thread[] threads = new Thread[tailoredThreadsCount - 1];
		for (int i = 0; i < tailoredThreadsCount - 1; i++) {
			threads[i] = new Thread(new BlockBasedCloneDetectingThread(
					revisionsArray, detectedClones, fragmentRetriever, index,
					cloneSizeThreshold, detectCrossProjectClones));
			threads[i].start();
		}

		final BlockBasedCloneDetectingThreadMonitor monitor = new BlockBasedCloneDetectingThreadMonitor(
				detectedClones, cloneRegisterer, maxElementsCount, threads);
		monitor.monitor();
	}

}
