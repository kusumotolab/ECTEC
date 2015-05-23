package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentbrancher;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;

import org.apache.log4j.Logger;

public class CodeFragmentBranchingThread implements Runnable {

	private static final Logger logger = LoggingManager
			.getLogger(CodeFragmentBranchingThread.class.getName());

	private static final Logger eLogger = LoggingManager.getLogger("error");

	private final ConcurrentMap<Long, DBCodeFragmentLinkInfo> detectedLinks;

	private final DBCombinedCommitInfo[] targetCombinedCommits;

	private final CodeFragmentRetriever fragmentRetriever;

	private final CloneSetRetriever cloneRetriever;

	private final CodeFragmentLinkRetriever linkRetriever;

	private final CodeFragmentLinkRegisterer linkRegisterer;

	private final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits;

	private final AtomicInteger index;

	private final int maximumElementsCount;

	public CodeFragmentBranchingThread(
			final ConcurrentMap<Long, DBCodeFragmentLinkInfo> detectedLinks,
			final DBCombinedCommitInfo[] targetCombinedCommits,
			final CodeFragmentRetriever fragmentRetriever,
			final CloneSetRetriever cloneRetriever,
			final CodeFragmentLinkRetriever linkRetriever,
			final CodeFragmentLinkRegisterer linkRegisterer,
			final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits,
			final AtomicInteger index, final int maximumElementsCount) {
		this.detectedLinks = detectedLinks;
		this.targetCombinedCommits = targetCombinedCommits;
		this.fragmentRetriever = fragmentRetriever;
		this.cloneRetriever = cloneRetriever;
		this.linkRetriever = linkRetriever;
		this.linkRegisterer = linkRegisterer;
		this.processedCombinedCommits = processedCombinedCommits;
		this.index = index;
		this.maximumElementsCount = maximumElementsCount;
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

				processCommit(beforeCombinedRevisionId, afterCombinedRevisionId);

				synchronized (detectedLinks) {
					if (detectedLinks.size() > maximumElementsCount) {
						linkRegisterer.register(detectedLinks.values());
						detectedLinks.clear();
					}
				}

			} catch (Exception e) {
				eLogger.warn("something is wrong in processing the combined commit "
						+ targetCombinedCommit.getId());
				e.printStackTrace();
			}
		}
	}

	private void processCommit(final long beforeCombinedRevisionId,
			final long afterCombinedRevisionId) throws Exception {
		final Map<Long, DBCloneSetInfo> clonesInBeforeRevision = new TreeMap<Long, DBCloneSetInfo>();
		clonesInBeforeRevision.putAll(cloneRetriever
				.retrieveElementsInSpecifiedRevision(beforeCombinedRevisionId));

		final Map<Long, DBCloneSetInfo> clonesInAfterRevision = new TreeMap<Long, DBCloneSetInfo>();
		clonesInAfterRevision.putAll(cloneRetriever
				.retrieveElementsInSpecifiedRevision(afterCombinedRevisionId));

		final Map<Long, DBCodeFragmentInfo> beforeFragments = new TreeMap<Long, DBCodeFragmentInfo>();
		beforeFragments
				.putAll(fragmentRetriever
						.retrieveElementsInSpecifiedCombinedRevision(beforeCombinedRevisionId));

		final Map<Long, DBCodeFragmentInfo> afterFragments = new TreeMap<Long, DBCodeFragmentInfo>();
		afterFragments
				.putAll(fragmentRetriever
						.retrieveElementsInSpecifiedCombinedRevision(afterCombinedRevisionId));

		final Set<Long> intersection = new TreeSet<Long>();
		intersection.addAll(beforeFragments.keySet());
		intersection.retainAll(afterFragments.keySet());

		final Map<Long, DBCodeFragmentLinkInfo> linksByAfterFragmentId = new TreeMap<Long, DBCodeFragmentLinkInfo>();
		for (final DBCodeFragmentLinkInfo link : linkRetriever
				.retrieveElementsWithAfterCombinedRevision(
						afterCombinedRevisionId).values()) {
			linksByAfterFragmentId.put(link.getAfterElementId(), link);
		}

		for (final DBCloneSetInfo afterClone : clonesInAfterRevision.values()) {
			processClone(beforeCombinedRevisionId, afterCombinedRevisionId,
					intersection, linksByAfterFragmentId, afterClone);
		}
	}

	private void processClone(final long beforeCombinedRevisionId,
			final long afterCombinedRevisionId, final Set<Long> intersection,
			final Map<Long, DBCodeFragmentLinkInfo> linksByAfterFragmentId,
			final DBCloneSetInfo afterClone) {
		final Set<Long> candidateBeforeFragments = new TreeSet<Long>();
		final Set<Long> newlyAppeared = new TreeSet<Long>();

		for (final long afterFragmentId : afterClone.getElements()) {
			if (intersection.contains(afterFragmentId)) {
				// the fragment had not been changed
				// the candidate before fragment is the same as the
				// after fragment
				candidateBeforeFragments.add(afterFragmentId);
				continue;
			}

			if (linksByAfterFragmentId.containsKey(afterFragmentId)) {
				// the fragment had its ancestor
				final DBCodeFragmentLinkInfo link = linksByAfterFragmentId
						.get(afterFragmentId);
				candidateBeforeFragments.add(link.getBeforeElementId());
				continue;
			}

			newlyAppeared.add(afterFragmentId);
		}

		for (final long newFragment : newlyAppeared) {
			for (final long candidate : candidateBeforeFragments) {
				final DBCodeFragmentLinkInfo newLink = new DBCodeFragmentLinkInfo(
						candidate, newFragment, beforeCombinedRevisionId,
						afterCombinedRevisionId, false);
				detectedLinks.put(newLink.getId(), newLink);
			}
		}
	}

}
