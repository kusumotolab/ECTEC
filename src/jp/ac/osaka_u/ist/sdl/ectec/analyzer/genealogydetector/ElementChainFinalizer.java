package jp.ac.osaka_u.ist.sdl.ectec.analyzer.genealogydetector;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.data.AbstractGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.ElementLinkInfo;

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
	 * create genealogies from the given chains
	 * 
	 * @param chains
	 * @return
	 */
	public Map<Long, G> finalize(final Set<ElementChain<L>> chains) {
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
	protected abstract G createInstanceFromChain(final ElementChain<L> chain);

}
