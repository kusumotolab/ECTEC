package jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs;

/**
 * A class that represents commits
 * 
 * @author k-hotta
 * 
 */
public class Commit {

	/**
	 * the before revision id
	 */
	private final long beforeRevisionId;

	/**
	 * the after revision id
	 */
	private final long afterRevisionId;

	public Commit(final long beforeRevisionId, final long afterRevisionId) {
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
