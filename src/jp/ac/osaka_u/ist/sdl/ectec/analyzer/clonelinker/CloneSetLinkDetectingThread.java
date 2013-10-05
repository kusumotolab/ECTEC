package jp.ac.osaka_u.ist.sdl.ectec.analyzer.clonelinker;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.Commit;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CloneSetRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CodeFragmentLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A thread class to detect links of clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkDetectingThread implements Runnable {

	/**
	 * the target commits
	 */
	private final Commit[] targetCommits;

	/**
	 * a map having detected links of clones
	 */
	private final ConcurrentMap<Long, CloneSetLinkInfo> detectedCloneLinks;

	/**
	 * the map between revision id and clone sets including in the revision
	 */
	private final ConcurrentMap<Long, Map<Long, CloneSetInfo>> cloneSets;

	/**
	 * the retriever for code fragment links
	 */
	private final CodeFragmentLinkRetriever fragmentLinkRetriever;

	/**
	 * the retriever for clones
	 */
	private final CloneSetRetriever cloneRetriever;

	/**
	 * already processed commits
	 */
	private final ConcurrentMap<Long, Commit> processedCommits;

	/**
	 * a counter that points the current state of the processing
	 */
	private final AtomicInteger index;

	public CloneSetLinkDetectingThread(final Commit[] targetCommits,
			final ConcurrentMap<Long, CloneSetLinkInfo> detectedCloneLinks,
			final ConcurrentMap<Long, Map<Long, CloneSetInfo>> cloneSets,
			final CodeFragmentLinkRetriever fragmentLinkRetriever,
			final CloneSetRetriever cloneRetriever,
			final ConcurrentMap<Long, Commit> processedCommits,
			final AtomicInteger index) {
		this.targetCommits = targetCommits;
		this.detectedCloneLinks = detectedCloneLinks;
		this.cloneSets = cloneSets;
		this.fragmentLinkRetriever = fragmentLinkRetriever;
		this.cloneRetriever = cloneRetriever;
		this.processedCommits = processedCommits;
		this.index = index;
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

				final Map<Long, CodeFragmentLinkInfo> fragmentLinks = fragmentLinkRetriever
						.retrieveElementsWithBeforeRevision(beforeRevisionId);

				final CloneSetLinker linker = new CloneSetLinker();
				detectedCloneLinks.putAll(linker.detectCloneSetLinks(cloneSets
						.get(beforeRevisionId).values(),
						cloneSets.get(afterRevisionId).values(), fragmentLinks,
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
	private void retrieveElements(final long revisionId) throws Exception {
		synchronized (cloneSets) {
			if (!cloneSets.containsKey(revisionId)) {
				final Map<Long, CloneSetInfo> retrievedClones = cloneRetriever
						.retrieveElementsInSpecifiedRevision(revisionId);
				final Map<Long, CloneSetInfo> concurrentRetrievedClones = new ConcurrentHashMap<Long, CloneSetInfo>();
				concurrentRetrievedClones.putAll(retrievedClones);
				cloneSets.put(revisionId, concurrentRetrievedClones);
			}
		}
	}
}
