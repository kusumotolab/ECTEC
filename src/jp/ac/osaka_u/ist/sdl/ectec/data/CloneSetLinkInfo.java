package jp.ac.osaka_u.ist.sdl.ectec.data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a link between two clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkInfo extends ElementLinkInfo {

	/**
	 * a counter to keep the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * the number of changed elements
	 */
	private final int numberOfChangedElements;

	/**
	 * the number of added elements
	 */
	private final int numberOfAddedElements;

	/**
	 * the number of deleted elements
	 */
	private final int numberOfDeletedElements;

	/**
	 * the number of co-changed elements (in this clone set)
	 */
	private final int numberOfCoChangecElements;

	/**
	 * the list of the ids of code fragment links related to this clone set link
	 */
	private final List<Long> codeFragmentLinks;

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param beforeElementId
	 * @param afterElementId
	 * @param beforeRevisionId
	 * @param afterRevisionId
	 * @param numberOfChangedElements
	 * @param numberOfAddedElements
	 * @param numberOfDeletedElements
	 * @param numberOfCoChangedElements
	 * @param codeFragmentLinks
	 */
	public CloneSetLinkInfo(final long id, final long beforeElementId,
			final long afterElementId, final long beforeRevisionId,
			final long afterRevisionId, final int numberOfChangedElements,
			final int numberOfAddedElements, final int numberOfDeletedElements,
			final int numberOfCoChangedElements,
			final List<Long> codeFragmentLinks) {
		super(id, beforeElementId, afterElementId, beforeRevisionId,
				afterRevisionId);
		this.numberOfChangedElements = numberOfChangedElements;
		this.numberOfAddedElements = numberOfAddedElements;
		this.numberOfDeletedElements = numberOfDeletedElements;
		this.numberOfCoChangecElements = numberOfCoChangedElements;
		this.codeFragmentLinks = codeFragmentLinks;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param beforeElementId
	 * @param afterElementId
	 * @param beforeRevisionId
	 * @param afterRevisionId
	 * @param numberOfChangedElements
	 * @param numberOfAddedElements
	 * @param numberOfDeletedElements
	 * @param numberOfCoChangedElements
	 * @param codeFragmentLinks
	 */
	public CloneSetLinkInfo(final long beforeElementId,
			final long afterElementId, final long beforeRevisionId,
			final long afterRevisionId, final int numberOfChangedElements,
			final int numberOfAddedElements, final int numberOfDeletedElements,
			final int numberOfCoChangedElements,
			final List<Long> codeFragmentLinks) {
		this(count.getAndIncrement(), beforeElementId, afterElementId,
				beforeRevisionId, afterRevisionId, numberOfChangedElements,
				numberOfAddedElements, numberOfDeletedElements,
				numberOfCoChangedElements, codeFragmentLinks);
	}

	/**
	 * get the number of changed elements
	 * 
	 * @return
	 */
	public final int getNumberOfChangedElements() {
		return this.numberOfChangedElements;
	}

	/**
	 * get the number of added elements
	 * 
	 * @return
	 */
	public final int getNumberOfAddedElements() {
		return this.numberOfAddedElements;
	}

	/**
	 * get the number of deleted elements
	 * 
	 * @return
	 */
	public final int getNumberOfDeletedElements() {
		return this.numberOfDeletedElements;
	}

	/**
	 * get the number of co-changed elements
	 * 
	 * @return
	 */
	public final int getNumberOfCoChangedElements() {
		return this.numberOfCoChangecElements;
	}

	/**
	 * get the list of ids of code fragment links related to this clone set link
	 * 
	 * @return
	 */
	public final List<Long> getCodeFragmentLinks() {
		return Collections.unmodifiableList(codeFragmentLinks);
	}

}
