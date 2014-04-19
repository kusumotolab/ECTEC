package jp.ac.osaka_u.ist.sdl.ectec.detector.linker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CRDRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.similarity.ICRDSimilarityCalculator;
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
	private final ConcurrentMap<Long, DBCodeFragmentLinkInfo> detectedLinks;

	/**
	 * the target commits
	 */
	private final DBCommitInfo[] targetCommits;

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
	private final ConcurrentMap<Long, Map<Long, DBCodeFragmentInfo>> codeFragments;

	/**
	 * the map between revision id and crds included in the revision
	 */
	private final ConcurrentMap<Long, Map<Long, DBCrdInfo>> crds;

	/**
	 * already processed commits
	 */
	private final ConcurrentMap<Long, DBCommitInfo> processedCommits;

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
			final ConcurrentMap<Long, DBCodeFragmentLinkInfo> detectedLinks,
			final DBCommitInfo[] targetCommits,
			final CodeFragmentRetriever fragmentRetriever,
			final CRDRetriever crdRetriever,
			final ConcurrentMap<Long, Map<Long, DBCodeFragmentInfo>> codeFragments,
			final ConcurrentMap<Long, Map<Long, DBCrdInfo>> crds,
			final ConcurrentMap<Long, DBCommitInfo> processedCommits,
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

			final DBCommitInfo targetCommit = targetCommits[currentIndex];

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

				final Map<Long, DBCrdInfo> currentCrds = new TreeMap<Long, DBCrdInfo>();
				currentCrds.putAll(crds.get(beforeRevisionId));
				currentCrds.putAll(crds.get(afterRevisionId));

				final Map<Long, DBCodeFragmentLinkInfo> links = linker
						.detectFragmentPairs(codeFragments
								.get(beforeRevisionId).values(), codeFragments
								.get(afterRevisionId).values(),
								similarityCalculator, similarityThreshold,
								currentCrds, beforeRevisionId, afterRevisionId);

				detectedLinks.putAll(links);

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
				final Map<Long, DBCodeFragmentInfo> retrievedFragments = fragmentRetriever
						.retrieveElementsInSpecifiedCombinedRevision(revisionId);
				final Map<Long, DBCodeFragmentInfo> concurrentRetrievedFragments = new ConcurrentHashMap<Long, DBCodeFragmentInfo>();
				concurrentRetrievedFragments.putAll(retrievedFragments);
				codeFragments.put(revisionId, concurrentRetrievedFragments);
			}
		}

		synchronized (crds) {
			if (!crds.containsKey(revisionId)) {
				final Map<Long, DBCodeFragmentInfo> fragments = codeFragments
						.get(revisionId);
				final List<Long> crdIds = new ArrayList<Long>();
				for (final Map.Entry<Long, DBCodeFragmentInfo> entry : fragments
						.entrySet()) {
					crdIds.add(entry.getValue().getCrdId());
				}

				final Map<Long, DBCrdInfo> retrievedCrds = crdRetriever
						.retrieveWithIds(crdIds);
				final Map<Long, DBCrdInfo> concurrentRetrievedCrds = new ConcurrentHashMap<Long, DBCrdInfo>();
				concurrentRetrievedCrds.putAll(retrievedCrds);
				crds.put(revisionId, concurrentRetrievedCrds);
			}
		}
	}

}
