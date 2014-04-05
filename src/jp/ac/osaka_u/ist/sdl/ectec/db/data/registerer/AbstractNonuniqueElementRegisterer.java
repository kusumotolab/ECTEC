package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElement;

/**
 * An abstract class to register non-unique elements into the db
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class AbstractNonuniqueElementRegisterer<T extends AbstractDBElement>
		extends AbstractElementRegisterer<T> {

	public AbstractNonuniqueElementRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	public synchronized void register(final Collection<T> elements) throws SQLException {
		final PreparedStatement pstmt = dbManager
				.createPreparedStatement(createPreparedStatementQueue());

		int count = 0;

		for (final T element : elements) {
			count = makePreparedStatements(pstmt, element);
			if ((++count % maxBatchCount) == 0) {
				pstmt.executeBatch();
				pstmt.clearBatch();
			}
		}

		pstmt.executeBatch();
		dbManager.commit();

		pstmt.close();
	}

	protected abstract int makePreparedStatements(final PreparedStatement pstmt,
			final T element) throws SQLException;
	
}
