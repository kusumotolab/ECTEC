package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElement;

/**
 * An abstract class to register elements into the db
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class AbstractUniqueElementRegisterer<T extends AbstractDBElement> {

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
	public AbstractUniqueElementRegisterer(final DBConnectionManager dbManager,
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
	public synchronized void register(final Collection<T> elements) throws SQLException {
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
