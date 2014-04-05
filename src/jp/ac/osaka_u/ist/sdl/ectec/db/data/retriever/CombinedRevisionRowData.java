package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

/**
 * A class that represents a row in the table of combined revisions
 * 
 * @author k-hotta
 * 
 */
class CombinedRevisionRowData extends AbstractRowData {
	
	private final long originalRevisionId;
	
	CombinedRevisionRowData(final long id, final long originalRevisionId) {
		super(id);
		this.originalRevisionId = originalRevisionId;
	}
	
	final long getOriginalRevisionId() {
		return originalRevisionId;
	}
	
}
