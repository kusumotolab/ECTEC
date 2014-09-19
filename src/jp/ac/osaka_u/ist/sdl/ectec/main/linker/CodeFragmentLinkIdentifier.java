package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CRDRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.main.linker.similarity.ICRDSimilarityCalculator;

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
	private final Map<Long, DBCombinedCommitInfo> combinedCommits;

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
	 * the retriever for clones
	 */
	private final CloneSetRetriever cloneRetriever;

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

	/**
	 * whether detect cross project links
	 */
	private final boolean detectCrossProjectLinks;

	/**
	 * whether detect links from fragments that are not in any clones in before
	 * revision
	 */
	private final boolean onlyFragmentInClonesInBeforeRevision;

	public CodeFragmentLinkIdentifier(
			final Map<Long, DBCombinedCommitInfo> combinedCommits,
			final int threadsCount,
			final CodeFragmentLinkRegisterer fragmentLinkRegisterer,
			final CodeFragmentRetriever fragmentRetriever,
			final CRDRetriever crdRetriever,
			final CloneSetRetriever cloneRetriever,
			final ICodeFragmentLinker linker, final double similarityThreshold,
			final ICRDSimilarityCalculator similarityCalculator,
			final int maxElementsCount, final boolean detectCrossProjectLinks,
			final boolean onlyFragmentInClonesInBeforeRevision) {
		this.combinedCommits = combinedCommits;
		this.threadsCount = threadsCount;
		this.fragmentLinkRegisterer = fragmentLinkRegisterer;
		this.fragmentRetriever = fragmentRetriever;
		this.crdRetriever = crdRetriever;
		this.cloneRetriever = cloneRetriever;
		this.linker = linker;
		this.similarityThreshold = similarityThreshold;
		this.similarityCalculator = similarityCalculator;
		this.maxElementsCount = maxElementsCount;
		this.detectCrossProjectLinks = detectCrossProjectLinks;
		this.onlyFragmentInClonesInBeforeRevision = onlyFragmentInClonesInBeforeRevision;
	}

	private Map<Long, Collection<Long>> detectCombinedRevisionAndRelatedCombinedCommits() {
		final Map<Long, Collection<Long>> result = new TreeMap<Long, Collection<Long>>();
		for (final Map.Entry<Long, DBCombinedCommitInfo> entry : combinedCommits
				.entrySet()) {
			final DBCombinedCommitInfo combinedCommit = entry.getValue();

			final long beforeCombinedRevisionId = combinedCommit
					.getBeforeCombinedRevisionId();
			if (result.containsKey(beforeCombinedRevisionId)) {
				result.get(beforeCombinedRevisionId)
						.add(combinedCommit.getId());
			} else {
				final Collection<Long> newCollection = new TreeSet<Long>();
				newCollection.add(combinedCommit.getId());
				result.put(beforeCombinedRevisionId, newCollection);
			}

			final long afterCombinedRevisionId = combinedCommit
					.getAfterCombinedRevisionId();
			if (result.containsKey(afterCombinedRevisionId)) {
				result.get(afterCombinedRevisionId).add(combinedCommit.getId());
			} else {
				final Collection<Long> newCollection = new TreeSet<Long>();
				newCollection.add(combinedCommit.getId());
				result.put(afterCombinedRevisionId, newCollection);
			}
		}
		return result;
	}

	public void run() throws Exception {
		final DBCombinedCommitInfo[] combinedCommitsArray = combinedCommits
				.values().toArray(new DBCombinedCommitInfo[0]);
		final Map<Long, Collection<Long>> combinedRevisionAndRelatedCombinedCommits = detectCombinedRevisionAndRelatedCombinedCommits();

		// the minimum number of thread is 2
		final int tailoredThreadsCount = Math.max(
				Math.min(combinedCommits.size(), threadsCount), 2);

		final ConcurrentMap<Long, DBCodeFragmentLinkInfo> detectedLinks = new ConcurrentHashMap<Long, DBCodeFragmentLinkInfo>();
		final ConcurrentMap<Long, Map<Long, DBCodeFragmentInfo>> codeFragments = new ConcurrentHashMap<Long, Map<Long, DBCodeFragmentInfo>>();
		final ConcurrentMap<Long, Map<Long, DBCrdInfo>> crds = new ConcurrentHashMap<Long, Map<Long, DBCrdInfo>>();
		final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits = new ConcurrentHashMap<Long, DBCombinedCommitInfo>();
		final AtomicInteger index = new AtomicInteger(0);

		final ICodeFragmentLinker selectedLinker = (detectCrossProjectLinks) ? linker
				: new ByRepositoryCodeFragmentLinker(linker);

		final Thread[] threads = new Thread[tailoredThreadsCount - 1];
		for (int i = 0; i < tailoredThreadsCount - 1; i++) {
			threads[i] = new Thread(new CodeFragmentLinkDetectingThread(
					detectedLinks, combinedCommitsArray, fragmentRetriever,
					crdRetriever, cloneRetriever, codeFragments, crds,
					processedCombinedCommits, index, selectedLinker,
					similarityThreshold, similarityCalculator,
					onlyFragmentInClonesInBeforeRevision));
			threads[i].start();
		}

		final CodeFragmentLinkDetectingThreadMonitor monitor = new CodeFragmentLinkDetectingThreadMonitor(
				detectedLinks, fragmentLinkRegisterer, codeFragments, crds,
				processedCombinedCommits,
				combinedRevisionAndRelatedCombinedCommits, maxElementsCount,
				threads);
		monitor.monitor();
	}

}
