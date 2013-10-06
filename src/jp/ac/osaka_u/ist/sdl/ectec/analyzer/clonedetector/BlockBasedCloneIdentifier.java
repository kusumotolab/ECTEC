package jp.ac.osaka_u.ist.sdl.ectec.analyzer.clonedetector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CloneSetRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CodeFragmentRetriever;

/**
 * A class for managing threads that detect clones
 * 
 * @author k-hotta
 * 
 */
public class BlockBasedCloneIdentifier {

	/**
	 * the target revisions
	 */
	private final Map<Long, RevisionInfo> revisions;

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

	public BlockBasedCloneIdentifier(final Map<Long, RevisionInfo> revisions,
			final int threadsCount,
			final CodeFragmentRetriever fragmentRetriever,
			final CloneSetRegisterer cloneRegisterer,
			final int maxElementsCount, final int cloneSizeThreshold) {
		this.revisions = revisions;
		this.threadsCount = threadsCount;
		this.fragmentRetriever = fragmentRetriever;
		this.cloneRegisterer = cloneRegisterer;
		this.maxElementsCount = maxElementsCount;
		this.cloneSizeThreshold = cloneSizeThreshold;
	}

	public void run() throws Exception {
		if (threadsCount == 1) {
			runWithSingleThread();
		} else {
			runWithMultipleThreads();
		}
	}

	private void runWithSingleThread() throws Exception {
		assert threadsCount == 1;

		final RevisionInfo[] revisionsArray = revisions.values().toArray(
				new RevisionInfo[0]);
		final SingleThreadBlockBasedCloneDetector detector = new SingleThreadBlockBasedCloneDetector(
				revisionsArray, fragmentRetriever, cloneRegisterer,
				maxElementsCount, cloneSizeThreshold);
		detector.detectAndRegister();
	}

	private void runWithMultipleThreads() throws Exception {
		assert threadsCount > 1;

		final RevisionInfo[] revisionsArray = revisions.values().toArray(
				new RevisionInfo[0]);
		final ConcurrentMap<Long, CloneSetInfo> detectedClones = new ConcurrentHashMap<Long, CloneSetInfo>();
		final AtomicInteger index = new AtomicInteger(0);

		final Thread[] threads = new Thread[threadsCount - 1];
		for (int i = 0; i < threadsCount - 1; i++) {
			threads[i] = new Thread(new BlockBasedCloneDetectingThread(
					revisionsArray, detectedClones, fragmentRetriever, index,
					cloneSizeThreshold));
			threads[i].start();
		}

		final BlockBasedCloneDetectingThreadMonitor monitor = new BlockBasedCloneDetectingThreadMonitor(
				detectedClones, cloneRegisterer, maxElementsCount);
		monitor.monitor();
	}

}
