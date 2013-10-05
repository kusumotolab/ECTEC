package jp.ac.osaka_u.ist.sdl.ectec.analyzer.clonedetector;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CodeFragmentRetriever;
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
	private final RevisionInfo[] targetRevisions;

	/**
	 * a map having detected clones
	 */
	private final ConcurrentMap<Long, CloneSetInfo> detectedClones;

	/**
	 * the retriever for code fragments
	 */
	private final CodeFragmentRetriever retriever;

	/**
	 * the index
	 */
	private final AtomicInteger index;

	public BlockBasedCloneDetectingThread(final RevisionInfo[] targetRevisions,
			final ConcurrentMap<Long, CloneSetInfo> detectedClones,
			final CodeFragmentRetriever retriever, final AtomicInteger index) {
		this.targetRevisions = targetRevisions;
		this.detectedClones = detectedClones;
		this.retriever = retriever;
		this.index = index;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= targetRevisions.length) {
				break;
			}

			final RevisionInfo targetRevision = targetRevisions[currentIndex];

			MessagePrinter.println("\t[" + currentIndex + "/"
					+ targetRevisions.length + "] analyzing revision "
					+ targetRevision.getIdentifier());

			try {
				final Map<Long, CodeFragmentInfo> codeFragments = retriever
						.retrieveElementsInSpecifiedRevision(targetRevision
								.getId());
				final BlockBasedCloneDetector detector = new BlockBasedCloneDetector(
						targetRevision.getId());
				detectedClones.putAll(detector.detectClones(codeFragments));
			} catch (Exception e) {
				MessagePrinter
						.ePrintln("something is wrong when analyzing revision "
								+ targetRevision.getIdentifier());
			}
		}
	}
}
