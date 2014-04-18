package jp.ac.osaka_u.ist.sdl.ectec.detector.linker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CRDRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.similarity.ICRDSimilarityCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A class to detect links of code fragments with a single thread
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkDetector {

	private final Map<Long, DBCodeFragmentLinkInfo> detectedLinks;

	/**
	 * the target commits
	 */
	private final DBCommitInfo[] targetCommits;

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
	 * the map between revision id and code fragments included in the revision
	 */
	private final Map<Long, Map<Long, DBCodeFragmentInfo>> codeFragments;

	/**
	 * the map between revision id and crds included in the revision
	 */
	private final Map<Long, Map<Long, DBCrdInfo>> crds;

	/**
	 * already processed commits
	 */
	private final Map<Long, DBCommitInfo> processedCommits;

	/**
	 * id of a revision and a collection of ids of commits that relates to the
	 * revision
	 */
	private final Map<Long, Collection<Long>> revisionAndRelatedCommits;

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
	 * the threshold for element
	 */
	private final int maxElementsCount;

	public CodeFragmentLinkDetector(final DBCommitInfo[] targetCommits,
			final CodeFragmentLinkRegisterer fragmentLinkRegisterer,
			final CodeFragmentRetriever fragmentRetriever,
			final CRDRetriever crdRetriever,
			final Map<Long, Collection<Long>> revisionAndRelatedCommits,
			final ICodeFragmentLinker linker, final double similarityThreshold,
			final ICRDSimilarityCalculator similarityCalculator,
			final int maxElementsCount) {
		this.detectedLinks = new TreeMap<Long, DBCodeFragmentLinkInfo>();
		this.targetCommits = targetCommits;
		this.fragmentLinkRegisterer = fragmentLinkRegisterer;
		this.fragmentRetriever = fragmentRetriever;
		this.crdRetriever = crdRetriever;
		this.codeFragments = new TreeMap<Long, Map<Long, DBCodeFragmentInfo>>();
		this.crds = new TreeMap<Long, Map<Long, DBCrdInfo>>();
		this.processedCommits = new TreeMap<Long, DBCommitInfo>();
		this.revisionAndRelatedCommits = revisionAndRelatedCommits;
		this.linker = linker;
		this.similarityThreshold = similarityThreshold;
		this.similarityCalculator = similarityCalculator;
		this.maxElementsCount = maxElementsCount;
	}

	/**
	 * detect links and register them into db
	 * 
	 * @throws Exception
	 */
	public void detectAndRegister() throws Exception {
		int numberOfLinks = 0;

		for (int i = 0; i < targetCommits.length; i++) {
			final DBCommitInfo targetCommit = targetCommits[i];

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

			retrieveElements(beforeRevisionId);
			retrieveElements(afterRevisionId);

			final Map<Long, DBCrdInfo> currentCrds = new TreeMap<Long, DBCrdInfo>();
			currentCrds.putAll(crds.get(beforeRevisionId));
			currentCrds.putAll(crds.get(afterRevisionId));

			final Map<Long, DBCodeFragmentLinkInfo> links = linker
					.detectFragmentPairs(codeFragments.get(beforeRevisionId)
							.values(), codeFragments.get(afterRevisionId)
							.values(), similarityCalculator,
							similarityThreshold, currentCrds, beforeRevisionId,
							afterRevisionId);

			detectedLinks.putAll(links);

			processedCommits.put(targetCommit.getId(), targetCommit);
			MessagePrinter.println("\t[" + processedCommits.size() + "/"
					+ targetCommits.length
					+ "] processed the commit from revision "
					+ targetCommit.getBeforeRevisionIdentifier()
					+ " to revision "
					+ targetCommit.getAfterRevisionIdentifier());

			if (detectedLinks.size() >= maxElementsCount) {
				final Set<DBCodeFragmentLinkInfo> currentElements = new HashSet<DBCodeFragmentLinkInfo>();
				currentElements.addAll(detectedLinks.values());
				fragmentLinkRegisterer.register(currentElements);
				MessagePrinter.println("\t" + currentElements.size()
						+ " links of fragments have been registered into db");
				numberOfLinks += currentElements.size();

				for (final DBCodeFragmentLinkInfo link : currentElements) {
					detectedLinks.remove(link.getId());
				}
			}

			// remove fragments if they are no longer needed
			final Collection<Long> fragmentRevisionIds = new TreeSet<Long>();
			fragmentRevisionIds.addAll(codeFragments.keySet());
			for (final long revisionId : fragmentRevisionIds) {
				final Collection<Long> relatedCommits = revisionAndRelatedCommits
						.get(revisionId);
				if (processedCommits.keySet().containsAll(relatedCommits)) {
					codeFragments.remove(revisionId);
				}
			}

			// remove crds if they are no longer needed
			final Collection<Long> crdRevisionIds = new TreeSet<Long>();
			fragmentRevisionIds.addAll(crds.keySet());
			for (final long revisionId : crdRevisionIds) {
				final Collection<Long> relatedCommits = revisionAndRelatedCommits
						.get(revisionId);
				if (processedCommits.keySet().containsAll(relatedCommits)) {
					crds.remove(revisionId);
				}
			}
		}

		MessagePrinter.println();

		MessagePrinter
				.println("\tregistering all the remaining elements into db ");
		fragmentLinkRegisterer.register(detectedLinks.values());

		numberOfLinks += detectedLinks.size();

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
	protected void retrieveElements(final long revisionId) throws SQLException {
		if (!codeFragments.containsKey(revisionId)) {
			final Map<Long, DBCodeFragmentInfo> retrievedFragments = fragmentRetriever
					.retrieveElementsInSpecifiedRevision(revisionId);
			codeFragments.put(revisionId, retrievedFragments);
		}

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
			crds.put(revisionId, retrievedCrds);
		}

	}

}
