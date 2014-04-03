package jp.ac.osaka_u.ist.sdl.ectec.detector.genealogydetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.AbstractElementRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.LinkElementRetriever;

/**
 * A class to create instances of code fragment genealogies from the given
 * chains
 * 
 * @author k-hotta
 * 
 */
public class FragmentChainFinalizer
		extends
		ElementChainFinalizer<DBCodeFragmentInfo, DBCodeFragmentLinkInfo, DBCodeFragmentGenealogyInfo> {

	public FragmentChainFinalizer(
			AbstractElementRetriever<DBCodeFragmentInfo> elementRetriever,
			LinkElementRetriever<DBCodeFragmentLinkInfo> linkRetriever) {
		super(elementRetriever, linkRetriever);
	}

	@Override
	protected DBCodeFragmentGenealogyInfo createInstanceFromChain(
			ElementChain<DBCodeFragmentLinkInfo> chain) throws Exception {
		final long startRevisionId = chain.getFirstRevision();
		final long endRevisionId = chain.getLastRevision();
		final List<Long> elements = new ArrayList<Long>();
		final List<Long> links = new ArrayList<Long>();

		elements.addAll(chain.getElements());
		links.addAll(chain.getLinks());

		final Map<Long, DBCodeFragmentLinkInfo> linksMap = linkRetriever
				.retrieveWithIds(links);
		int changedCount = 0;
		DBCodeFragmentLinkInfo startLink = null;
		DBCodeFragmentLinkInfo endLink = null;

		for (final DBCodeFragmentLinkInfo link : linksMap.values()) {
			if (link.isChanged()) {
				changedCount++;
			}
			if (link.getBeforeRevisionId() == startRevisionId) {
				startLink = link;
			}
			if (link.getAfterRevisionId() == endRevisionId) {
				endLink = link;
			}
		}

		final Map<Long, DBCodeFragmentInfo> elementsMap = elementRetriever
				.retrieveWithIds(startLink.getBeforeElementId(),
						endLink.getAfterElementId());
		final DBCodeFragmentInfo startElement = elementsMap.get(startLink
				.getBeforeElementId());
		final DBCodeFragmentInfo endElement = elementsMap.get(endLink
				.getAfterElementId());

		return new DBCodeFragmentGenealogyInfo(startElement.getStartCombinedRevisionId(),
				endElement.getEndCombinedRevisionId(), elements, links, changedCount);
	}

	@Override
	protected DBCodeFragmentGenealogyInfo createInstanceFromElement(
			DBCodeFragmentInfo element) {
		final long startRevisionId = element.getStartCombinedRevisionId();
		final long endRevisionId = element.getEndCombinedRevisionId();
		final List<Long> elements = new ArrayList<Long>();
		final List<Long> links = new ArrayList<Long>();
		elements.add(element.getId());
		final int changedCount = 0;

		return new DBCodeFragmentGenealogyInfo(startRevisionId, endRevisionId,
				elements, links, changedCount);
	}

}
