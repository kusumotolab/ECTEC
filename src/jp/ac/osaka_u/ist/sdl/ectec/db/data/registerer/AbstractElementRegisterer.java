package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElement;

/**
 * An abstract class to register elements into db
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class AbstractElementRegisterer<T extends AbstractDBElement> {

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
	 * get the query to create a prepared statement
	 * 
	 * @return
	 */
	protected abstract String createPreparedStatementQueue();

	/**
	 * the abstract method to perform registeration of elements
	 * 
	 * @param elements
	 * @throws SQLException
	 */
	public abstract void register(final Collection<T> elements)
			throws SQLException;

}
