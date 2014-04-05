package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

/**
 * A class that represents a row in the table of combined revisions
 * 
 * @author k-hotta
 * 
 */
class CombinedRevisionRowData extends AbstractRowData {

	private final long id;
	
	private final long originalRevisionId;
	
	CombinedRevisionRowData(final long id, final long originalRevisionId) {
		this.id = id;
		this.originalRevisionId = originalRevisionId;
	}
	
	final long getId() {
		return this.id;
	}
	
	final long getOriginalRevisionId() {
		return originalRevisionId;
	}
	
}
