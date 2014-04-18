package jp.ac.osaka_u.ist.sdl.ectec.detector.linker;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CRDRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.similarity.ICRDSimilarityCalculator;

/**
 * A class for managing threads that detects links of code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkIdentifier {

	/**
	 * the target commits
	 */
	private final Map<Long, DBCommitInfo> commits;

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

	public CodeFragmentLinkIdentifier(final Map<Long, DBCommitInfo> commits,
			final int threadsCount,
			final CodeFragmentLinkRegisterer fragmentLinkRegisterer,
			final CodeFragmentRetriever fragmentRetriever,
			final CRDRetriever crdRetriever, final ICodeFragmentLinker linker,
			final double similarityThreshold,
			final ICRDSimilarityCalculator similarityCalculator,
			final int maxElementsCount) {
		this.commits = commits;
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

	private void runWithSingleThread() throws Exception {
		assert threadsCount == 1;

		final Map<Long, Collection<Long>> revisionAndRelatedCommits = detectRevisionAndRelatedCommits();
		final DBCommitInfo[] commitsArray = commits.values().toArray(new DBCommitInfo[0]);

		final CodeFragmentLinkDetector detector = new CodeFragmentLinkDetector(
				commitsArray, fragmentLinkRegisterer, fragmentRetriever,
				crdRetriever, revisionAndRelatedCommits, linker,
				similarityThreshold, similarityCalculator, maxElementsCount);
		detector.detectAndRegister();
	}

	private void runWithMultiThread() throws Exception {
		assert threadsCount > 1;

		final DBCommitInfo[] commitsArray = commits.values().toArray(new DBCommitInfo[0]);
		final Map<Long, Collection<Long>> revisionAndRelatedCommits = detectRevisionAndRelatedCommits();

		final ConcurrentMap<Long, DBCodeFragmentLinkInfo> detectedLinks = new ConcurrentHashMap<Long, DBCodeFragmentLinkInfo>();
		final ConcurrentMap<Long, Map<Long, DBCodeFragmentInfo>> codeFragments = new ConcurrentHashMap<Long, Map<Long, DBCodeFragmentInfo>>();
		final ConcurrentMap<Long, Map<Long, DBCrdInfo>> crds = new ConcurrentHashMap<Long, Map<Long, DBCrdInfo>>();
		final ConcurrentMap<Long, DBCommitInfo> processedCommits = new ConcurrentHashMap<Long, DBCommitInfo>();
		final AtomicInteger index = new AtomicInteger(0);

		final Thread[] threads = new Thread[threadsCount - 1];
		for (int i = 0; i < threadsCount - 1; i++) {
			threads[i] = new Thread(new CodeFragmentLinkDetectingThread(
					detectedLinks, commitsArray, fragmentRetriever,
					crdRetriever, codeFragments, crds, processedCommits, index,
					linker, similarityThreshold, similarityCalculator));
			threads[i].start();
		}

		final CodeFragmentLinkDetectingThreadMonitor monitor = new CodeFragmentLinkDetectingThreadMonitor(
				detectedLinks, fragmentLinkRegisterer, codeFragments, crds,
				processedCommits, revisionAndRelatedCommits, maxElementsCount);
		monitor.monitor();
	}

}
