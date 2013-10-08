package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.SQLException;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElement;

/**
 * An abstract class for retrieving elements which have start/end revisions
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class RangedElementRetriever<T extends AbstractDBElement> extends
		AbstractElementRetriever<T> {

	public RangedElementRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	/**
	 * retrieve elements that exist in the specified revision
	 * 
	 * @param revisionId
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, T> retrieveElementsInSpecifiedRevision(
			final long revisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getStartRevisionIdColumnName() + " <= " + revisionId
				+ " AND " + getEndRevisionIdColumnName() + " >= " + revisionId;

		return retrieve(query);
	}

	/**
	 * get the name of column having start revisions
	 * 
	 * @return
	 */
	protected abstract String getStartRevisionIdColumnName();

	/**
	 * get the name of column having end revisions
	 * 
	 * @return
	 */
	protected abstract String getEndRevisionIdColumnName();

}
