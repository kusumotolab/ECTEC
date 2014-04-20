package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CRDRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.similarity.ICRDSimilarityCalculator;

import org.apache.log4j.Logger;

/**
 * A thread class for detecting links of code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkDetectingThread implements Runnable {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CodeFragmentLinkDetectingThread.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * a map having detected links
	 */
	private final ConcurrentMap<Long, DBCodeFragmentLinkInfo> detectedLinks;

	/**
	 * the target combined commits
	 */
	private final DBCombinedCommitInfo[] targetCombinedCommits;

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
	 * the map between revision id and code fragments included in the revision
	 */
	private final ConcurrentMap<Long, Map<Long, DBCodeFragmentInfo>> codeFragments;

	/**
	 * the map between revision id and crds included in the revision
	 */
	private final ConcurrentMap<Long, Map<Long, DBCrdInfo>> crds;

	/**
	 * already processed combined commits
	 */
	private final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits;

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

	/**
	 * whether detect links from fragments that are not in any clones in before
	 * revision
	 */
	private final boolean onlyFragmentInClonesInBeforeRevision;

	public CodeFragmentLinkDetectingThread(
			final ConcurrentMap<Long, DBCodeFragmentLinkInfo> detectedLinks,
			final DBCombinedCommitInfo[] targetCombinedCommits,
			final CodeFragmentRetriever fragmentRetriever,
			final CRDRetriever crdRetriever,
			final CloneSetRetriever cloneRetriever,
			final ConcurrentMap<Long, Map<Long, DBCodeFragmentInfo>> codeFragments,
			final ConcurrentMap<Long, Map<Long, DBCrdInfo>> crds,
			final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits,
			final AtomicInteger index, final ICodeFragmentLinker linker,
			final double similarityThreshold,
			final ICRDSimilarityCalculator similarityCalculator,
			final boolean onlyFragmentInClonesInBeforeRevision) {
		this.detectedLinks = detectedLinks;
		this.targetCombinedCommits = targetCombinedCommits;
		this.fragmentRetriever = fragmentRetriever;
		this.crdRetriever = crdRetriever;
		this.cloneRetriever = cloneRetriever;
		this.codeFragments = codeFragments;
		this.crds = crds;
		this.processedCombinedCommits = processedCombinedCommits;
		this.index = index;
		this.linker = linker;
		this.similarityThreshold = similarityThreshold;
		this.similarityCalculator = similarityCalculator;
		this.onlyFragmentInClonesInBeforeRevision = onlyFragmentInClonesInBeforeRevision;
	}

	@Override
	public void run() {
		while (true) {
			final int currentIndex = index.getAndIncrement();

			if (currentIndex >= targetCombinedCommits.length) {
				break;
			}

			final DBCombinedCommitInfo targetCombinedCommit = targetCombinedCommits[currentIndex];

			try {
				final long beforeCombinedRevisionId = targetCombinedCommit
						.getBeforeCombinedRevisionId();
				if (beforeCombinedRevisionId == -1) {
					processedCombinedCommits.put(targetCombinedCommit.getId(),
							targetCombinedCommit);
					logger.info("[" + processedCombinedCommits.size() + "/"
							+ targetCombinedCommits.length
							+ "] processed the combined commit "
							+ targetCombinedCommit.getId());
					continue;
				}
				final long afterCombinedRevisionId = targetCombinedCommit
						.getAfterCombinedRevisionId();

				// retrieve necessary elements
				retrieveElements(beforeCombinedRevisionId);
				retrieveElements(afterCombinedRevisionId);

				final Map<Long, DBCrdInfo> currentCrds = new TreeMap<Long, DBCrdInfo>();
				currentCrds.putAll(crds.get(beforeCombinedRevisionId));
				currentCrds.putAll(crds.get(afterCombinedRevisionId));

				final Map<Long, DBCloneSetInfo> clonesInBeforeRevision = new TreeMap<Long, DBCloneSetInfo>();

				if (onlyFragmentInClonesInBeforeRevision) {
					clonesInBeforeRevision
							.putAll(cloneRetriever
									.retrieveElementsInSpecifiedRevision(beforeCombinedRevisionId));
				}

				final Map<Long, DBCodeFragmentInfo> beforeFragments = new TreeMap<Long, DBCodeFragmentInfo>();
				beforeFragments.putAll(codeFragments
						.get(beforeCombinedRevisionId));
				final Map<Long, DBCodeFragmentInfo> afterFragments = new TreeMap<Long, DBCodeFragmentInfo>();
				afterFragments.putAll(codeFragments
						.get(afterCombinedRevisionId));

				final Map<Long, DBCodeFragmentLinkInfo> links = linker
						.detectFragmentPairs(beforeFragments, afterFragments,
								similarityCalculator, similarityThreshold,
								currentCrds, beforeCombinedRevisionId,
								afterCombinedRevisionId,
								onlyFragmentInClonesInBeforeRevision,
								clonesInBeforeRevision);

				detectedLinks.putAll(links);

				processedCombinedCommits.put(targetCombinedCommit.getId(),
						targetCombinedCommit);
				logger.info("[" + processedCombinedCommits.size() + "/"
						+ targetCombinedCommits.length
						+ "] processed the combined commit "
						+ targetCombinedCommit.getId());

			} catch (Exception e) {
				eLogger.warn("something is wrong in processing the combined commit "
						+ targetCombinedCommit.getId());
			}

		}
	}

	/**
	 * retrieve elements if they have not been stored into the maps
	 * 
	 * @param combinedRevisionId
	 * @throws SQLException
	 */
	protected void retrieveElements(final long combinedRevisionId)
			throws SQLException {
		synchronized (codeFragments) {
			if (!codeFragments.containsKey(combinedRevisionId)) {
				final Map<Long, DBCodeFragmentInfo> retrievedFragments = fragmentRetriever
						.retrieveElementsInSpecifiedCombinedRevision(combinedRevisionId);
				final Map<Long, DBCodeFragmentInfo> concurrentRetrievedFragments = new ConcurrentHashMap<Long, DBCodeFragmentInfo>();
				concurrentRetrievedFragments.putAll(retrievedFragments);
				codeFragments.put(combinedRevisionId,
						concurrentRetrievedFragments);
			}
		}

		synchronized (crds) {
			if (!crds.containsKey(combinedRevisionId)) {
				final Map<Long, DBCodeFragmentInfo> fragments = codeFragments
						.get(combinedRevisionId);
				final List<Long> crdIds = new ArrayList<Long>();
				for (final Map.Entry<Long, DBCodeFragmentInfo> entry : fragments
						.entrySet()) {
					crdIds.add(entry.getValue().getCrdId());
				}

				final Map<Long, DBCrdInfo> retrievedCrds = crdRetriever
						.retrieveWithIds(crdIds);
				final Map<Long, DBCrdInfo> concurrentRetrievedCrds = new ConcurrentHashMap<Long, DBCrdInfo>();
				concurrentRetrievedCrds.putAll(retrievedCrds);
				crds.put(combinedRevisionId, concurrentRetrievedCrds);
			}
		}
	}

	public Map<Long, DBCodeFragmentInfo> getTargetFragmentsInBeforeRevision(
			final long beforeCombinedRevisionId) throws SQLException {
		final Map<Long, DBCodeFragmentInfo> result = new TreeMap<Long, DBCodeFragmentInfo>();

		if (onlyFragmentInClonesInBeforeRevision) {
			final Map<Long, DBCloneSetInfo> clones = cloneRetriever
					.retrieveElementsInSpecifiedRevision(beforeCombinedRevisionId);

			final Set<Long> fragmentIdsInClones = new TreeSet<Long>();
			for (final Map.Entry<Long, DBCloneSetInfo> cloneEntry : clones
					.entrySet()) {
				fragmentIdsInClones.addAll(cloneEntry.getValue().getElements());
			}

			for (final Map.Entry<Long, DBCodeFragmentInfo> fragmentEntry : codeFragments
					.get(beforeCombinedRevisionId).entrySet()) {
				final long fragmentId = fragmentEntry.getKey();
				if (fragmentIdsInClones.contains(fragmentId)) {
					result.put(fragmentEntry.getKey(), fragmentEntry.getValue());
				}
			}

		} else {
			result.putAll(codeFragments.get(beforeCombinedRevisionId));
		}

		return Collections.unmodifiableMap(result);
	}

}
