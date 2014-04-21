package jp.ac.osaka_u.ist.sdl.ectec.db.data;

/**
 * An abstract class that represents a link between two elements
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractDBElementLinkInfo extends AbstractDBElement {

	/**
	 * the id of the before element
	 */
	private final long beforeElementId;

	/**
	 * the id of the after element
	 */
	private final long afterElementId;

	/**
	 * the id of the before combined revision
	 */
	private final long beforeCombinedRevisionId;

	/**
	 * the id of the after combined revision
	 */
	private final long afterCombinedRevisionId;

	/**
	 * the constructor
	 * 
	 * @param id
	 * @param beforeElementId
	 * @param afterElementId
	 * @param beforeCombinedRevisionId
	 * @param afterCombinedRevisionId
	 */
	public AbstractDBElementLinkInfo(final long id, final long beforeElementId,
			final long afterElementId, final long beforeCombinedRevisionId,
			final long afterCombinedRevisionId) {
		super(id);
		this.beforeElementId = beforeElementId;
		this.afterElementId = afterElementId;
		this.beforeCombinedRevisionId = beforeCombinedRevisionId;
		this.afterCombinedRevisionId = afterCombinedRevisionId;
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
	public final long getBeforeCombinedRevisionId() {
		return this.beforeCombinedRevisionId;
	}

	/**
	 * get the id of the after revision
	 * 
	 * @return
	 */
	public final long getAfterCombinedRevisionId() {
		return this.afterCombinedRevisionId;
	}

}
