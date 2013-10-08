package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import java.util.Collections;
import java.util.List;

public abstract class AbstractDBGenealogyInfo<T extends AbstractDBElement, U extends DBElementLinkInfo>
		extends AbstractDBElement {

	/**
	 * the id of the start revision
	 */
	protected final long startRevisionId;

	/**
	 * the id of the end revision
	 */
	protected final long endRevisionId;

	/**
	 * the list of ids of elements included in this genealogy
	 */
	protected final List<Long> elements;

	/**
	 * the list of ids of links included in this genealogy
	 */
	protected final List<Long> links;

	public AbstractDBGenealogyInfo(final long id, final long startRevisionId,
			final long endRevisionId, final List<Long> elements,
			final List<Long> links) {
		super(id);
		this.startRevisionId = startRevisionId;
		this.endRevisionId = endRevisionId;
		this.elements = elements;
		this.links = links;
	}

	/**
	 * get the id of the start revision
	 * 
	 * @return
	 */
	public final long getStartRevisionId() {
		return startRevisionId;
	}

	/**
	 * get the id of the end revision
	 * 
	 * @return
	 */
	public final long getEndRevisionId() {
		return endRevisionId;
	}

	/**
	 * get the list of ids of elements
	 * 
	 * @return
	 */
	public final List<Long> getElements() {
		return Collections.unmodifiableList(elements);
	}

	/**
	 * get the list of ids of element links
	 * 
	 * @return
	 */
	public final List<Long> getLinks() {
		return Collections.unmodifiableList(links);
	}

}
