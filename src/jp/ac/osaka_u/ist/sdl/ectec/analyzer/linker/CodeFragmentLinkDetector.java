package jp.ac.osaka_u.ist.sdl.ectec.analyzer.linker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.similarity.ICRDSimilarityCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CodeFragmentLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CRDRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CodeFragmentRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A class to detect links of code fragments with a single thread
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkDetector {

	private final Map<Long, CodeFragmentLinkInfo> detectedLinks;

	/**
	 * the target revisions
	 */
	private final RevisionInfo[] targetRevisions;

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
	private final Map<Long, Map<Long, CodeFragmentInfo>> codeFragments;

	/**
	 * the map between revision id and crds included in the revision
	 */
	private final Map<Long, Map<Long, CRD>> crds;

	/**
	 * already processed revisions
	 */
	private final Map<Long, RevisionInfo> processedRevisions;

	/**
	 * the map whose keys are revision ids and whose values are ids of previous
	 * revisions
	 */
	private final Map<Long, Long> revisionsMap;

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

	public CodeFragmentLinkDetector(final RevisionInfo[] targetRevisions,
			final CodeFragmentLinkRegisterer fragmentLinkRegisterer,
			final CodeFragmentRetriever fragmentRetriever,
			final CRDRetriever crdRetriever,
			final Map<Long, Long> revisionsMap,
			final ICodeFragmentLinker linker, final double similarityThreshold,
			final ICRDSimilarityCalculator similarityCalculator,
			final int maxElementsCount) {
		this.detectedLinks = new TreeMap<Long, CodeFragmentLinkInfo>();
		this.targetRevisions = targetRevisions;
		this.fragmentLinkRegisterer = fragmentLinkRegisterer;
		this.fragmentRetriever = fragmentRetriever;
		this.crdRetriever = crdRetriever;
		this.codeFragments = new TreeMap<Long, Map<Long, CodeFragmentInfo>>();
		this.crds = new TreeMap<Long, Map<Long, CRD>>();
		this.processedRevisions = new TreeMap<Long, RevisionInfo>();
		this.revisionsMap = revisionsMap;
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

		for (int i = 0; i < targetRevisions.length; i++) {
			final RevisionInfo targetRevision = targetRevisions[i];
			MessagePrinter.println("\t[" + (i + 1) + "/"
					+ targetRevisions.length
					+ "] processing the commit to revision "
					+ targetRevision.getIdentifier());

			final long beforeRevisionId = revisionsMap.get(targetRevision
					.getId());
			if (beforeRevisionId == -1) {
				processedRevisions.put(targetRevision.getId(), targetRevision);
				continue;
			}
			final long afterRevisionId = targetRevision.getId();

			retrieveElements(beforeRevisionId);
			retrieveElements(afterRevisionId);

			final Map<Long, CRD> currentCrds = new TreeMap<Long, CRD>();
			currentCrds.putAll(crds.get(beforeRevisionId));
			currentCrds.putAll(crds.get(afterRevisionId));

			detectedLinks.putAll(linker.detectFragmentPairs(
					codeFragments.get(beforeRevisionId).values(), codeFragments
							.get(afterRevisionId).values(),
					similarityCalculator, similarityThreshold, currentCrds,
					beforeRevisionId, afterRevisionId));

			processedRevisions.put(targetRevision.getId(), targetRevision);

			if (detectedLinks.size() >= maxElementsCount) {
				final Collection<CodeFragmentLinkInfo> currentElements = detectedLinks
						.values();
				fragmentLinkRegisterer.register(currentElements);
				MessagePrinter.println("\t" + currentElements.size()
						+ " links of fragments have been registered into db");
				numberOfLinks += currentElements.size();

				for (final CodeFragmentLinkInfo link : currentElements) {
					detectedLinks.remove(link.getId());
				}
			}

			for (final Map.Entry<Long, RevisionInfo> entry : processedRevisions
					.entrySet()) {
				if (processedRevisions.containsKey(revisionsMap.get(entry
						.getKey()))) {
					final long removeRevisionId = revisionsMap.get(entry
							.getKey());
					codeFragments.remove(removeRevisionId);
					crds.remove(removeRevisionId);
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
			final Map<Long, CodeFragmentInfo> retrievedFragments = fragmentRetriever
					.retrieveElementsInSpecifiedRevision(revisionId);
			codeFragments.put(revisionId, retrievedFragments);
		}

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
			crds.put(revisionId, retrievedCrds);
		}

	}

}
