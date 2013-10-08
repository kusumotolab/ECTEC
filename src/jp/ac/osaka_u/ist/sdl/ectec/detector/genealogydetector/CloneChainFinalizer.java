package jp.ac.osaka_u.ist.sdl.ectec.detector.genealogydetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.data.CloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.AbstractElementRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.LinkElementRetriever;

public class CloneChainFinalizer
		extends
		ElementChainFinalizer<CloneSetInfo, CloneSetLinkInfo, CloneGenealogyInfo> {

	/**
	 * the last revision id
	 */
	private final long lastRevisionId;

	public CloneChainFinalizer(
			AbstractElementRetriever<CloneSetInfo> elementRetriever,
			LinkElementRetriever<CloneSetLinkInfo> linkRetriever,
			final long lastRevisionId) {
		super(elementRetriever, linkRetriever);
		this.lastRevisionId = lastRevisionId;
	}

	@Override
	protected CloneGenealogyInfo createInstanceFromChain(
			ElementChain<CloneSetLinkInfo> chain) throws Exception {
		final long startRevisionId = chain.getFirstRevision();
		final long endRevisionId = chain.getLastRevision();
		final List<Long> elements = new ArrayList<Long>();
		elements.addAll(chain.getElements());
		final List<Long> links = new ArrayList<Long>();
		links.addAll(chain.getLinks());

		final Map<Long, CloneSetLinkInfo> linksMap = linkRetriever
				.retrieveWithIds(links);
		int numberOfChanges = 0;
		int numberOfAdditions = 0;
		int numberOfDeletions = 0;

		for (final CloneSetLinkInfo link : linksMap.values()) {
			if (link.getNumberOfChangedElements() > 0) {
				numberOfChanges++;
			}
			if (link.getNumberOfAddedElements() > 0) {
				numberOfAdditions++;
			}
			if (link.getNumberOfDeletedElements() > 0) {
				numberOfDeletions++;
			}
		}

		final boolean dead = (endRevisionId == lastRevisionId);

		return new CloneGenealogyInfo(startRevisionId, endRevisionId, elements,
				links, numberOfChanges, numberOfAdditions, numberOfDeletions,
				dead);
	}

	@Override
	protected CloneGenealogyInfo createInstanceFromElement(CloneSetInfo element) {
		final long startRevisionId = element.getRevisionId();
		final long endRevisionId = element.getRevisionId();
		final List<Long> elements = new ArrayList<Long>();
		final List<Long> links = new ArrayList<Long>();
		elements.add(element.getId());

		final int numberOfChanges = 0;
		final int numberOfAdditions = 0;
		final int numberOfDeletions = 0;

		final boolean dead = (endRevisionId == lastRevisionId);

		return new CloneGenealogyInfo(startRevisionId, endRevisionId, elements,
				links, numberOfChanges, numberOfAdditions, numberOfDeletions,
				dead);
	}

}
