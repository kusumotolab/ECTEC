package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

/**
 * A class that represents a row in a db table
 * 
 * @author k-hotta
 * 
 */
abstract class AbstractRowData {

	/**
	 * the id of the element
	 */
	protected final long id;
	
	AbstractRowData(final long id) {
		this.id = id;
	}
	
	final long getId() {
		return id;
	}
	
}
