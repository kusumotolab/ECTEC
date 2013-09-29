package jp.ac.osaka_u.ist.sdl.ectec.analyzer.linker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.similarity.ICRDSimilarityCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.Commit;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CRDRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CodeFragmentRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A thread class for detecting links of code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkDetectingThread implements Runnable {

	/**
	 * a map having detected links
	 */
	private final ConcurrentMap<Long, CodeFragmentLinkInfo> detectedLinks;

	/**
	 * the target commits
	 */
	private final Commit[] targetCommits;

	/**
	 * the retriever for code fragments
	 */
	private final CodeFragmentRetriever fragmentRetriever;

	/**
	 * the retriever for crds
	 */
	private final CRDRetriever crdRetriever;

	/**
	 * the map between revision id and code fragments included in the revision
	 */
	private final ConcurrentMap<Long, Map<Long, CodeFragmentInfo>> codeFragments;

	/**
	 * the map between revision id and crds included in the revision
	 */
	private final ConcurrentMap<Long, Map<Long, CRD>> crds;

	/**
	 * already processed commits
	 */
	private final ConcurrentMap<Long, Commit> processedCommits;

	/**
	 * a counter that points the current state of the processing
	 */
	private final AtomicInteger index;

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

	public CodeFragmentLinkDetectingThread(
			final ConcurrentMap<Long, CodeFragmentLinkInfo> detectedLinks,
			final Commit[] targetCommits,
			final CodeFragmentRetriever fragmentRetriever,
			final CRDRetriever crdRetriever,
			final ConcurrentMap<Long, Map<Long, CodeFragmentInfo>> codeFragments,
			final ConcurrentMap<Long, Map<Long, CRD>> crds,
			final ConcurrentMap<Long, Commit> processedCommits,
			final AtomicInteger index, final ICodeFragmentLinker linker,
			final double similarityThreshold,
			final ICRDSimilarityCalculator similarityCalculator) {
		this.detectedLinks = detectedLinks;
		this.targetCommits = targetCommits;
		this.fragmentRetriever = fragmentRetriever;
		this.crdRetriever = crdRetriever;
		this.codeFragments = codeFragments;
		this.crds = crds;
		this.processedCommits = processedCommits;
		this.index = index;
		this.linker = linker;
		this.similarityThreshold = similarityThreshold;
		this.similarityCalculator = similarityCalculator;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= targetCommits.length) {
				break;
			}

			final Commit targetCommit = targetCommits[currentIndex];

			final long beforeRevisionId = targetCommit.getBeforeRevisionId();
			if (beforeRevisionId == -1) {
				processedCommits.put(targetCommit.getId(), targetCommit);
				MessagePrinter.println("\t[" + processedCommits.size() + "/"
						+ targetCommits.length
						+ "] processed the commit from revision "
						+ targetCommit.getBeforeRevisionIdentifier()
						+ " to revision "
						+ targetCommit.getAfterRevisionIdentifier());
				continue;
			}
			final long afterRevisionId = targetCommit.getAfterRevisionId();

			try {
				// retrieve necessary elements
				retrieveElements(beforeRevisionId);
				retrieveElements(afterRevisionId);

				final Map<Long, CRD> currentCrds = new TreeMap<Long, CRD>();
				currentCrds.putAll(crds.get(beforeRevisionId));
				currentCrds.putAll(crds.get(afterRevisionId));

				detectedLinks.putAll(linker.detectFragmentPairs(codeFragments
						.get(beforeRevisionId).values(),
						codeFragments.get(afterRevisionId).values(),
						similarityCalculator, similarityThreshold, currentCrds,
						beforeRevisionId, afterRevisionId));

			} catch (Exception e) {
				MessagePrinter
						.ePrintln("something is wrong in processing the commit from revision"
								+ targetCommit.getBeforeRevisionIdentifier()
								+ " to revision "
								+ targetCommit.getAfterRevisionIdentifier());
			}

			processedCommits.put(targetCommit.getId(), targetCommit);
			MessagePrinter.println("\t[" + processedCommits.size() + "/"
					+ targetCommits.length
					+ "] processed the commit from revision "
					+ targetCommit.getBeforeRevisionIdentifier()
					+ " to revision "
					+ targetCommit.getAfterRevisionIdentifier());
		}
	}

	/**
	 * retrieve elements if they have not been stored into the maps
	 * 
	 * @param revisionId
	 * @throws SQLException
	 */
	protected void retrieveElements(final long revisionId) throws SQLException {
		synchronized (codeFragments) {
			if (!codeFragments.containsKey(revisionId)) {
				final Map<Long, CodeFragmentInfo> retrievedFragments = fragmentRetriever
						.retrieveElementsInSpecifiedRevision(revisionId);
				final Map<Long, CodeFragmentInfo> concurrentRetrievedFragments = new ConcurrentHashMap<Long, CodeFragmentInfo>();
				concurrentRetrievedFragments.putAll(retrievedFragments);
				codeFragments.put(revisionId, concurrentRetrievedFragments);
			}
		}

		synchronized (crds) {
			if (!crds.containsKey(revisionId)) {
				final Map<Long, CodeFragmentInfo> fragments = codeFragments
						.get(revisionId);
				final List<Long> crdIds = new ArrayList<Long>();
				for (final Map.Entry<Long, CodeFragmentInfo> entry : fragments
						.entrySet()) {
					crdIds.add(entry.getValue().getCrdId());
				}

				final Map<Long, CRD> retrievedCrds = crdRetriever
						.retrieveWithIds(crdIds);
				final Map<Long, CRD> concurrentRetrievedCrds = new ConcurrentHashMap<Long, CRD>();
				concurrentRetrievedCrds.putAll(retrievedCrds);
				crds.put(revisionId, concurrentRetrievedCrds);
			}
		}
	}

}
