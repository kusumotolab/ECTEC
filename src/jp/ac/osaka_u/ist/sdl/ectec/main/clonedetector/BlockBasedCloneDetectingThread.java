package jp.ac.osaka_u.ist.sdl.ectec.main.clonedetector;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;

import org.apache.log4j.Logger;

/**
 * A thread class to detect clones
 * 
 * @author k-hotta
 * 
 */
public class BlockBasedCloneDetectingThread implements Runnable {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(BlockBasedCloneDetectingThread.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * the target combined revisions
	 */
	private final DBCombinedRevisionInfo[] targetCombinedRevisions;

	/**
	 * a map having detected clones
	 */
	private final ConcurrentMap<Long, DBCloneSetInfo> detectedClones;

	/**
	 * the retriever for code fragments
	 */
	private final CodeFragmentRetriever retriever;

	/**
	 * the index
	 */
	private final AtomicInteger index;

	/**
	 * the size threshold
	 */
	private final int cloneSizeThreshold;

	public BlockBasedCloneDetectingThread(
			final DBCombinedRevisionInfo[] targetCombinedRevisions,
			final ConcurrentMap<Long, DBCloneSetInfo> detectedClones,
			final CodeFragmentRetriever retriever, final AtomicInteger index,
			final int cloneSizeThreshold) {
		this.targetCombinedRevisions = targetCombinedRevisions;
		this.detectedClones = detectedClones;
		this.retriever = retriever;
		this.index = index;
		this.cloneSizeThreshold = cloneSizeThreshold;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= targetCombinedRevisions.length) {
				break;
			}

			final DBCombinedRevisionInfo targetCombinedRevision = targetCombinedRevisions[currentIndex];

			logger.info.("[" + currentIndex + "/"
					+ targetCombinedRevisions.length + "] analyzing combined revision "
					+ targetCombinedRevision.getId());

			try {
				final Map<Long, DBCodeFragmentInfo> codeFragments = retriever
						.retrieveElementsInSpecifiedRevision(targetRevision
								.getId());
				final FragmentComparator detector = new FragmentComparator(
						targetRevision.getId(), cloneSizeThreshold);
				detectedClones.putAll(detector.detectClones(codeFragments));
			} catch (Exception e) {
				MessagePrinter
						.ePrintln("something is wrong when analyzing revision "
								+ targetRevision.getIdentifier());
			}
		}
	}
}
