package jp.ac.osaka_u.ist.sdl.ectec.db.data;

/**
 * An abstract class that represents a link between two elements
 * 
 * @author k-hotta
 * 
 */
public abstract class DBElementLinkInfo extends AbstractDBElement {

	/**
	 * the id of the before element
	 */
	private final long beforeElementId;

	/**
	 * the id of the after element
	 */
	private final long afterElementId;

	/**
	 * the id of the before revision
	 */
	private final long beforeRevisionId;

	/**
	 * the id of the after revision
	 */
	private final long afterRevisionId;

	/**
	 * the constructor
	 * 
	 * @param id
	 * @param beforeElementId
	 * @param afterElementId
	 * @param beforeRevisionId
	 * @param afterRevisionId
	 */
	public DBElementLinkInfo(final long id, final long beforeElementId,
			final long afterElementId, final long beforeRevisionId,
			final long afterRevisionId) {
		super(id);
		this.beforeElementId = beforeElementId;
		this.afterElementId = afterElementId;
		this.beforeRevisionId = beforeRevisionId;
		this.afterRevisionId = afterRevisionId;
	}

	/**
	 * get the id of the before element
	 * 
	 * @return
	 */
	public final long getBeforeElementId() {
		return this.beforeElementId;
	}

	/**
	 * get the id of the after element
	 * 
	 * @return
	 */
	public final long getAfterElementId() {
		return this.afterElementId;
	}

	/**
	 * get the id of the before revision
	 * 
	 * @return
	 */
	public final long getBeforeRevisionId() {
		return this.beforeRevisionId;
	}

	/**
	 * get the id of the after revision
	 * 
	 * @return
	 */
	public final long getAfterRevisionId() {
		return this.afterRevisionId;
	}

}
