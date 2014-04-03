package jp.ac.osaka_u.ist.sdl.ectec.detector.genealogydetector;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElement;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElementLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.AbstractElementRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.LinkElementRetriever;

/**
 * A class to create genealogies from the given chains
 * 
 * @author k-hotta
 * 
 * @param <L>
 * @param <G>
 */
public abstract class ElementChainFinalizer<E extends AbstractDBElement, L extends AbstractDBElementLinkInfo, G extends AbstractDBGenealogyInfo<?, L>> {

	/**
	 * the retriever of elements
	 */
	protected final AbstractElementRetriever<E> elementRetriever;

	/**
	 * the retriever of links
	 */
	protected final LinkElementRetriever<L> linkRetriever;

	/**
	 * a collection of ids of elements included in any genealogy
	 */
	private final Collection<Long> processedElements;

	public ElementChainFinalizer(
			final AbstractElementRetriever<E> elementRetriever,
			final LinkElementRetriever<L> linkRetriever) {
		this.elementRetriever = elementRetriever;
		this.linkRetriever = linkRetriever;
		this.processedElements = new TreeSet<Long>();
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

			processedElements.addAll(chain.getElements());
		}

		final Map<Long, E> notProcessedElements = elementRetriever
				.retrieveWithoutIds(processedElements);
		for (final Map.Entry<Long, E> notProcessedElement : notProcessedElements
				.entrySet()) {
			final G genealogy = createInstanceFromElement(notProcessedElement
					.getValue());
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

	/**
	 * create genealogy instance from the given element
	 * 
	 * @param element
	 * @return
	 */
	protected abstract G createInstanceFromElement(final E element);

}
