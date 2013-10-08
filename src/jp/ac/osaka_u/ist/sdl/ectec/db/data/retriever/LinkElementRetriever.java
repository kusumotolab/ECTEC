package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.SQLException;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBElementLinkInfo;

/**
 * An abstract class for retrieving link elements
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class LinkElementRetriever<T extends DBElementLinkInfo> extends
		AbstractElementRetriever<T> {

	public LinkElementRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	/**
	 * retrieve elements by specifying their before revision
	 * 
	 * @param beforeRevisionId
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, T> retrieveElementsWithBeforeRevision(
			final long beforeRevisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getBeforeRevisionIdColumnName() + " = " + beforeRevisionId;

		return retrieve(query);
	}

	/**
	 * retrieve elements by specifying their before revision
	 * 
	 * @param afterRevisionId
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, T> retrieveElementsWithAfterRevision(
			final long afterRevisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getAfterRevisionIdColumnName() + " = " + afterRevisionId;

		return retrieve(query);
	}

	/**
	 * get the name of column having before revision id
	 * 
	 * @return
	 */
	protected abstract String getBeforeRevisionIdColumnName();

	/**
	 * get the name of column having after revision id
	 * 
	 * @return
	 */
	protected abstract String getAfterRevisionIdColumnName();

}
