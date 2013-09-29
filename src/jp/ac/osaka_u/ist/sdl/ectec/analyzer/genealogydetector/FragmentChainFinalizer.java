package jp.ac.osaka_u.ist.sdl.ectec.analyzer.genealogydetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.LinkElementRetriever;

/**
 * A class to create instances of code fragment genealogies from the given
 * chains
 * 
 * @author k-hotta
 * 
 */
public class FragmentChainFinalizer extends
		ElementChainFinalizer<CodeFragmentLinkInfo, CodeFragmentGenealogyInfo> {

	public FragmentChainFinalizer(
			LinkElementRetriever<CodeFragmentLinkInfo> retriever) {
		super(retriever);
	}

	@Override
	protected CodeFragmentGenealogyInfo createInstanceFromChain(
			ElementChain<CodeFragmentLinkInfo> chain) throws Exception {
		final long startRevisionId = chain.getFirstRevision();
		final long endRevisionId = chain.getLastRevision();
		final List<Long> elements = new ArrayList<Long>();
		final List<Long> links = new ArrayList<Long>();

		elements.addAll(chain.getElements());
		links.addAll(chain.getLinks());

		final Map<Long, CodeFragmentLinkInfo> linksMap = retriever
				.retrieveWithIds(links);
		int changedCount = 0;
		for (final CodeFragmentLinkInfo link : linksMap.values()) {
			if (link.isChanged()) {
				changedCount++;
			}
		}

		return new CodeFragmentGenealogyInfo(startRevisionId, endRevisionId,
				elements, links, changedCount);
	}

}
