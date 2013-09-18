package jp.ac.osaka_u.ist.sdl.ectec.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.ectec.data.AbstractElement;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;

/**
 * An abstract class to register elements into the db
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class AbstractElementRegisterer<T extends AbstractElement> {

	/**
	 * the manager of the connection between db
	 */
	protected final DBConnectionManager dbManager;

	/**
	 * the maximum number of statements that are batched
	 */
	protected final int maxBatchCount;

	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public AbstractElementRegisterer(final DBConnectionManager dbManager,
			final int maxBatchCount) {
		this.dbManager = dbManager;
		this.maxBatchCount = maxBatchCount;
	}

	/**
	 * register all the specified elements
	 * 
	 * @param elements
	 * @throws SQLException
	 */
	public void register(final Collection<T> elements) throws SQLException {
		final PreparedStatement pstmt = dbManager
				.createPreparedStatement(createPreparedStatementQueue());

		int count = 0;

		for (final T element : elements) {
			setAttributes(pstmt, element);
			pstmt.addBatch();
			if ((++count % maxBatchCount) == 0) {
				pstmt.executeBatch();
				pstmt.clearBatch();
			}
		}

		pstmt.executeBatch();
		dbManager.commit();

		pstmt.close();
	}

	/**
	 * get the query to create a prepared statement
	 * 
	 * @return
	 */
	protected abstract String createPreparedStatementQueue();

	/**
	 * set attributes in the given prepared statement
	 * 
	 * @param pstmt
	 * @param element
	 * @throws SQLException
	 */
	protected abstract void setAttributes(PreparedStatement pstmt, T element)
			throws SQLException;

}
