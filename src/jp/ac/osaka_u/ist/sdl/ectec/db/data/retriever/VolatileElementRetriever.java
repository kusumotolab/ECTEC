package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.SQLException;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElement;

/**
 * An abstract class for retrieving elements that exist only at one revision
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class VolatileElementRetriever<T extends AbstractDBElement>
		extends AbstractElementRetriever<T> {

	public VolatileElementRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	/**
	 * retrieve elements that exist at the specified revision
	 * 
	 * @param revisionId
	 * @return
	 * @throws SQLException
	 */
	public SortedMap<Long, T> retrieveElementsInSpecifiedRevision(
			final long revisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getRevisionIdColumnName() + " = " + revisionId;

		return retrieve(query);
	}

	/**
	 * get the name of column having revision
	 * 
	 * @return
	 */
	protected abstract String getRevisionIdColumnName();

}
