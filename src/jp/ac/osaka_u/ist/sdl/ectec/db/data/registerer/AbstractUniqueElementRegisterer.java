package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElement;

/**
 * An abstract class to register unique elements into the db
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class AbstractUniqueElementRegisterer<T extends AbstractDBElement> extends AbstractElementRegisterer<T> {


	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public AbstractUniqueElementRegisterer(final DBConnectionManager dbManager,
			final int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	/**
	 * register all the specified elements
	 * 
	 * @param elements
	 * @throws SQLException
	 */
	@Override
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
