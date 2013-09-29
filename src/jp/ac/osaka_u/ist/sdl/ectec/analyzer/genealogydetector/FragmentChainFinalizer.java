package jp.ac.osaka_u.ist.sdl.ectec.analyzer.genealogydetector;

import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;

/**
 * A class to create instances of code fragment genealogies from the given
 * chains
 * 
 * @author k-hotta
 * 
 */
public class FragmentChainFinalizer extends
		ElementChainFinalizer<CodeFragmentLinkInfo, CodeFragmentGenealogyInfo> {

	@Override
	protected CodeFragmentGenealogyInfo createInstanceFromChain(
			ElementChain<CodeFragmentLinkInfo> chain) {
		final long startRevisionId = chain.getFirstRevision();
		final long endRevisionId = chain.getLastRevision();
		final List<Long> elements = new ArrayList<Long>();
		final List<Long> links = new ArrayList<Long>();

		elements.addAll(chain.getElements());
		links.addAll(chain.getLinks());

		return new CodeFragmentGenealogyInfo(startRevisionId, endRevisionId,
				elements, links);
	}

}
