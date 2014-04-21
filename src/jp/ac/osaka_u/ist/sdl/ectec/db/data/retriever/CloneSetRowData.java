package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

/**
 * A class that represents a row in the table of clone sets
 * 
 * @author k-hotta
 * 
 */
class CloneSetRowData extends AbstractRowData {

	private final long ownerCombinedRevisionId;

	private final long elementId;

	CloneSetRowData(final long id, final long ownerCombinedRevisionId,
			final long elementId) {
		super(id);
		this.ownerCombinedRevisionId = ownerCombinedRevisionId;
		this.elementId = elementId;
	}

	final long getOwnerCombinedRevisionId() {
		return ownerCombinedRevisionId;
	}

	final long getElementId() {
		return elementId;
	}

}
