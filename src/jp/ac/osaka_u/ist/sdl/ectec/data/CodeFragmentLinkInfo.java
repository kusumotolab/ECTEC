package jp.ac.osaka_u.ist.sdl.ectec.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents a link between two code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkInfo extends ElementLinkInfo {

	/**
	 * a counter to keep the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * whether the code fragment was changed or not
	 */
	private final boolean changed;

	/**
	 * the constructor for elements that are retrieved from the db
	 * 
	 * @param id
	 * @param beforeElementId
	 * @param afterElementId
	 * @param beforeRevisionId
	 * @param afterRevisionId
	 * @param changed
	 */
	public CodeFragmentLinkInfo(final long id, final long beforeElementId,
			final long afterElementId, final long beforeRevisionId,
			final long afterRevisionId, final boolean changed) {
		super(id, beforeElementId, afterElementId, beforeRevisionId,
				afterRevisionId);
		this.changed = changed;
	}

	/**
	 * the constructor for newly created elements
	 * 
	 * @param beforeElementId
	 * @param afterElementId
	 * @param beforeRevisionId
	 * @param afterRevisionId
	 * @param changed
	 */
	public CodeFragmentLinkInfo(final long beforeElementId,
			final long afterElementId, final long beforeRevisionId,
			final long afterRevisionId, final boolean changed) {
		this(count.getAndIncrement(), beforeElementId, afterElementId,
				beforeRevisionId, afterRevisionId, changed);
	}

}
