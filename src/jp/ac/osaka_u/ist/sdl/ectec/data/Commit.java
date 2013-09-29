package jp.ac.osaka_u.ist.sdl.ectec.data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents commits
 * 
 * @author k-hotta
 * 
 */
public class Commit extends AbstractElement {

	/**
	 * the counter for having the number of created instances
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * the before revision id
	 */
	private final long beforeRevisionId;

	/**
	 * the after revision id
	 */
	private final long afterRevisionId;

	public Commit(final long beforeRevisionId, final long afterRevisionId) {
		super(count.getAndIncrement());
		this.beforeRevisionId = beforeRevisionId;
		this.afterRevisionId = afterRevisionId;
	}

	/**
	 * get the before revision id
	 * 
	 * @return
	 */
	public final long getBeforeRevisionId() {
		return this.beforeRevisionId;
	}

	/**
	 * get the after revision id
	 * 
	 * @return
	 */
	public final long getAfterRevisionId() {
		return this.afterRevisionId;
	}

}
