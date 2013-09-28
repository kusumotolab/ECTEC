package jp.ac.osaka_u.ist.sdl.ectec.analyzer.linker;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.similarity.ICRDSimilarityCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CodeFragmentLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CRDRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CodeFragmentRetriever;

/**
 * A class for managing threads that detects links of code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkIdentifier {

	/**
	 * the map between revision and previous revision
	 */
	private final Map<RevisionInfo, RevisionInfo> revisions;

	/**
	 * the number of threads
	 */
	private final int threadsCount;

	/**
	 * the registerer for links of code fragments
	 */
	private final CodeFragmentLinkRegisterer fragmentLinkRegisterer;

	/**
	 * the retriever for code fragments
	 */
	private final CodeFragmentRetriever fragmentRetriever;

	/**
	 * the retriever for crds
	 */
	private final CRDRetriever crdRetriever;

	/**
	 * the linker
	 */
	private final ICodeFragmentLinker linker;

	/**
	 * the threshold for similarities
	 */
	private final double similarityThreshold;

	/**
	 * the similarity calculator for crds
	 */
	private final ICRDSimilarityCalculator similarityCalculator;

	/**
	 * the threshold for storing elements
	 */
	private final int maxElementsCount;

	public CodeFragmentLinkIdentifier(
			final Map<RevisionInfo, RevisionInfo> revisions,
			final int threadsCount,
			final CodeFragmentLinkRegisterer fragmentLinkRegisterer,
			final CodeFragmentRetriever fragmentRetriever,
			final CRDRetriever crdRetriever, final ICodeFragmentLinker linker,
			final double similarityThreshold,
			final ICRDSimilarityCalculator similarityCalculator,
			final int maxElementsCount) {
		this.revisions = revisions;
		this.threadsCount = threadsCount;
		this.fragmentLinkRegisterer = fragmentLinkRegisterer;
		this.fragmentRetriever = fragmentRetriever;
		this.crdRetriever = crdRetriever;
		this.linker = linker;
		this.similarityThreshold = similarityThreshold;
		this.similarityCalculator = similarityCalculator;
		this.maxElementsCount = maxElementsCount;
	}

	public void run() throws Exception {
		if (threadsCount == 1) {
			runWithSingleThread();
		} else {
			runWithMultiThread();
		}
	}

	private void runWithSingleThread() throws Exception {
		assert threadsCount == 1;

		final RevisionInfo[] revisionsArray = revisions.keySet().toArray(
				new RevisionInfo[0]);
		final Map<Long, Long> revisionsMap = new TreeMap<Long, Long>();
		for (final Map.Entry<RevisionInfo, RevisionInfo> entry : revisions
				.entrySet()) {
			revisionsMap.put(entry.getKey().getId(), entry.getValue().getId());
		}

		final CodeFragmentLinkDetector detector = new CodeFragmentLinkDetector(
				revisionsArray, fragmentLinkRegisterer, fragmentRetriever,
				crdRetriever, revisionsMap, linker, similarityThreshold,
				similarityCalculator, maxElementsCount);
		detector.detectAndRegister();
	}

	private void runWithMultiThread() throws Exception {
		assert threadsCount > 1;

		final RevisionInfo[] revisionsArray = revisions.keySet().toArray(
				new RevisionInfo[0]);
		final ConcurrentMap<Long, Long> revisionsMap = new ConcurrentHashMap<Long, Long>();
		for (final Map.Entry<RevisionInfo, RevisionInfo> entry : revisions
				.entrySet()) {
			revisionsMap.put(entry.getKey().getId(), entry.getValue().getId());
		}

		final ConcurrentMap<Long, CodeFragmentLinkInfo> detectedLinks = new ConcurrentHashMap<Long, CodeFragmentLinkInfo>();
		final ConcurrentMap<Long, Map<Long, CodeFragmentInfo>> codeFragments = new ConcurrentHashMap<Long, Map<Long, CodeFragmentInfo>>();
		final ConcurrentMap<Long, Map<Long, CRD>> crds = new ConcurrentHashMap<Long, Map<Long, CRD>>();
		final ConcurrentMap<Long, RevisionInfo> processedRevisions = new ConcurrentHashMap<Long, RevisionInfo>();
		final AtomicInteger index = new AtomicInteger(0);

		final Thread[] threads = new Thread[threadsCount - 1];
		for (int i = 0; i < threadsCount - 1; i++) {
			threads[i] = new Thread(new CodeFragmentLinkDetectingThread(
					detectedLinks, revisionsArray, fragmentRetriever,
					crdRetriever, codeFragments, crds, processedRevisions,
					revisionsMap, index, linker, similarityThreshold,
					similarityCalculator));
			threads[i].start();
		}

		final CodeFragmentLinkDetectingThreadMonitor monitor = new CodeFragmentLinkDetectingThreadMonitor(
				detectedLinks, fragmentLinkRegisterer, codeFragments, crds,
				processedRevisions, revisionsMap, maxElementsCount);
		monitor.monitor();
	}

}
