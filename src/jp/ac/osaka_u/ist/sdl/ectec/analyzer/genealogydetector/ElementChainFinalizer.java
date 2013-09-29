package jp.ac.osaka_u.ist.sdl.ectec.analyzer.genealogydetector;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.data.AbstractGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.ElementLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.LinkElementRetriever;

/**
 * A class to create genealogies from the given chains
 * 
 * @author k-hotta
 * 
 * @param <L>
 * @param <G>
 */
public abstract class ElementChainFinalizer<L extends ElementLinkInfo, G extends AbstractGenealogyInfo<?, L>> {

	/**
	 * the retriever of links
	 */
	protected final LinkElementRetriever<L> retriever;

	public ElementChainFinalizer(final LinkElementRetriever<L> retriever) {
		this.retriever = retriever;
	}

	/**
	 * create genealogies from the given chains
	 * 
	 * @param chains
	 * @return
	 */
	public Map<Long, G> finalize(final Collection<ElementChain<L>> chains)
			throws Exception {
		final Map<Long, G> result = new TreeMap<Long, G>();
		for (final ElementChain<L> chain : chains) {
			final G genealogy = createInstanceFromChain(chain);
			result.put(genealogy.getId(), genealogy);
		}
		return Collections.unmodifiableMap(result);
	}

	/**
	 * create genealogy instance from the given chain
	 * 
	 * @param chain
	 * @return
	 */
	protected abstract G createInstanceFromChain(final ElementChain<L> chain)
			throws Exception;

}
