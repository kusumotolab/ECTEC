package jp.ac.osaka_u.ist.sdl.ectec.detector.clonelinker;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.Commit;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CloneSetLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CloneSetRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CodeFragmentLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A class to detect links of clone sets with a single thread
 * 
 * @author k-hotta
 * 
 */
public class SingleThreadCloneSetLinkDetector {

	/**
	 * the target commits
	 */
	private final Commit[] targetCommits;

	/**
	 * a map having detected links of clones
	 */
	private final Map<Long, CloneSetLinkInfo> detectedCloneLinks;

	/**
	 * the map between revision id and clone sets including in the revision
	 */
	private final Map<Long, Map<Long, CloneSetInfo>> cloneSets;

	/**
	 * the retriever for links of code fragments
	 */
	private final CodeFragmentLinkRetriever fragmentLinkRetriever;

	/**
	 * the retriever for clone sets
	 */
	private final CloneSetRetriever cloneRetriever;

	/**
	 * the registerer for clone links
	 */
	private final CloneSetLinkRegisterer cloneLinkRegisterer;

	/**
	 * already processed commits
	 */
	private final Map<Long, Commit> processedCommits;

	/**
	 * id of a revision and a collection of ids of commits that relates to the
	 * revision
	 */
	private final Map<Long, Collection<Long>> revisionAndRelatedCommits;

	/**
	 * the threshold for elements <br>
	 * if the number of stored elements exceeds this threshold, then this
	 * monitor interrupts the other threads and register elements into db with
	 * the registered elements removed from the map
	 */
	private final int maxElementsCount;

	public SingleThreadCloneSetLinkDetector(final Commit[] targetCommits,
			final CodeFragmentLinkRetriever fragmentLinkRetriever,
			final CloneSetRetriever cloneRetriever,
			final CloneSetLinkRegisterer cloneLinkRegisterer,
			final Map<Long, Collection<Long>> revisionAndRelatedCommits,
			final int maxElementsCount) {
		this.targetCommits = targetCommits;
		this.detectedCloneLinks = new TreeMap<Long, CloneSetLinkInfo>();
		this.cloneSets = new TreeMap<Long, Map<Long, CloneSetInfo>>();
		this.fragmentLinkRetriever = fragmentLinkRetriever;
		this.cloneRetriever = cloneRetriever;
		this.cloneLinkRegisterer = cloneLinkRegisterer;
		this.processedCommits = new TreeMap<Long, Commit>();
		this.revisionAndRelatedCommits = revisionAndRelatedCommits;
		this.maxElementsCount = maxElementsCount;
	}

	public void detectAndRegister() throws Exception {
		int numberOfLinks = 0;

		for (int i = 0; i < targetCommits.length; i++) {
			final Commit targetCommit = targetCommits[i];

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

			// retrieve necessary elements
			retrieveElements(beforeRevisionId);
			retrieveElements(afterRevisionId);

			final Map<Long, CodeFragmentLinkInfo> fragmentLinks = fragmentLinkRetriever
					.retrieveElementsWithBeforeRevision(beforeRevisionId);

			final CloneSetLinker linker = new CloneSetLinker();
			detectedCloneLinks.putAll(linker.detectCloneSetLinks(
					cloneSets.get(beforeRevisionId).values(),
					cloneSets.get(afterRevisionId).values(), fragmentLinks,
					beforeRevisionId, afterRevisionId));

			processedCommits.put(targetCommit.getId(), targetCommit);
			MessagePrinter.println("\t[" + processedCommits.size() + "/"
					+ targetCommits.length
					+ "] processed the commit from revision "
					+ targetCommit.getBeforeRevisionIdentifier()
					+ " to revision "
					+ targetCommit.getAfterRevisionIdentifier());

			if (detectedCloneLinks.size() >= maxElementsCount) {
				final Set<CloneSetLinkInfo> currentElements = new HashSet<CloneSetLinkInfo>();
				currentElements.addAll(detectedCloneLinks.values());
				cloneLinkRegisterer.register(currentElements);
				MessagePrinter.println("\t" + currentElements.size()
						+ " links of fragments have been registered into db");
				numberOfLinks += currentElements.size();

				for (final CloneSetLinkInfo link : currentElements) {
					detectedCloneLinks.remove(link.getId());
				}
			}

			final Collection<Long> cloneRevisionIds = new TreeSet<Long>();
			cloneRevisionIds.addAll(cloneSets.keySet());
			for (final long revisionId : cloneRevisionIds) {
				final Collection<Long> relatedCommits = revisionAndRelatedCommits
						.get(revisionId);
				if (processedCommits.keySet().containsAll(relatedCommits)) {
					cloneSets.remove(revisionId);
				}
			}
		}

		MessagePrinter.println();

		MessagePrinter.println("\tall threads have finished their work");
		MessagePrinter
				.println("\tregistering all the remaining elements into db ");
		cloneLinkRegisterer.register(detectedCloneLinks.values());

		numberOfLinks += detectedCloneLinks.size();

		MessagePrinter.println("\t\tOK");

		MessagePrinter.println();

		MessagePrinter.println("the numbers of detected elements are ... ");
		MessagePrinter.println("\tLinks: " + numberOfLinks);
	}

	/**
	 * retrieve elements if they have not been stored into the maps
	 * 
	 * @param revisionId
	 * @throws SQLException
	 */
	private void retrieveElements(final long revisionId) throws Exception {
		if (!cloneSets.containsKey(revisionId)) {
			final Map<Long, CloneSetInfo> retrievedClones = cloneRetriever
					.retrieveElementsInSpecifiedRevision(revisionId);
			cloneSets.put(revisionId, retrievedClones);
		}

	}

}
