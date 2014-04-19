package jp.ac.osaka_u.ist.sdl.ectec.main.clonedetector;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A thread class to detect clones
 * 
 * @author k-hotta
 * 
 */
public class BlockBasedCloneDetectingThread implements Runnable {

	/**
	 * the target revisions
	 */
	private final DBRevisionInfo[] targetRevisions;

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

	public BlockBasedCloneDetectingThread(final DBRevisionInfo[] targetRevisions,
			final ConcurrentMap<Long, DBCloneSetInfo> detectedClones,
			final CodeFragmentRetriever retriever, final AtomicInteger index,
			final int cloneSizeThreshold) {
		this.targetRevisions = targetRevisions;
		this.detectedClones = detectedClones;
		this.retriever = retriever;
		this.index = index;
		this.cloneSizeThreshold = cloneSizeThreshold;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= targetRevisions.length) {
				break;
			}

			final DBRevisionInfo targetRevision = targetRevisions[currentIndex];

			MessagePrinter.println("\t[" + currentIndex + "/"
					+ targetRevisions.length + "] analyzing revision "
					+ targetRevision.getIdentifier());

			try {
				final Map<Long, DBCodeFragmentInfo> codeFragments = retriever
						.retrieveElementsInSpecifiedRevision(targetRevision
								.getId());
				final BlockBasedCloneDetector detector = new BlockBasedCloneDetector(
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
