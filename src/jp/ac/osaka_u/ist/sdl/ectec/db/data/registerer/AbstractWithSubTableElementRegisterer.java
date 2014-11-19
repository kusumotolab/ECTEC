package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.AbstractDBElement;

/**
 * An abstract class to register non-unique elements into the db
 * 
 * @author k-hotta
 * 
 * @param <T>
 */
public abstract class AbstractWithSubTableElementRegisterer<T extends AbstractDBElement>
		extends AbstractElementRegisterer<T> {

	private final List<String> subElements;

	private final Map<String, String> preparedStatementQueries;

	public AbstractWithSubTableElementRegisterer(DBConnectionManager dbManager,
			int maxBatchCount, final List<String> subElements,
			final Map<String, String> preparedStatementQueries) {
		super(dbManager, maxBatchCount);
		this.subElements = subElements;
		this.preparedStatementQueries = preparedStatementQueries;
	}

	@Override
	public synchronized void register(final Collection<T> elements)
			throws SQLException {
		final PreparedStatement mainPstmt = dbManager
				.createPreparedStatement(createMainPreparedStatementQuery());

		final Map<String, PreparedStatement> subPstmts = makePreparedStatementsForSubElements();

		int mainCount = 0;
		final Map<String, Integer> currentCount = new HashMap<String, Integer>();
		for (final String subElement : subElements) {
			currentCount.put(subElement, 0);
		}

		for (final T element : elements) {
			fillMainElementPreparedStatement(mainPstmt, element);
			final Map<String, Integer> currentCountCopy = fillSubElementPreparedStatements(
					element, subPstmts, currentCount);

			if ((mainCount++ % maxBatchCount) == 0) {
				mainPstmt.executeBatch();
				mainPstmt.clearBatch();
			}
			for (final Map.Entry<String, Integer> countEntry : currentCountCopy
					.entrySet()) {
				final int subCount = countEntry.getValue();
				if (subCount >= maxBatchCount) {
					mainPstmt.executeBatch();
					mainPstmt.clearBatch();

					final PreparedStatement subPstmt = subPstmts.get(countEntry
							.getKey());
					subPstmt.executeBatch();
					subPstmt.clearBatch();
					currentCount.put(countEntry.getKey(), subCount
							- maxBatchCount);
				} else {
					currentCount.put(countEntry.getKey(), subCount);
				}
			}
		}

		mainPstmt.executeBatch();

		for (final Map.Entry<String, PreparedStatement> subPstmtEntry : subPstmts
				.entrySet()) {
			subPstmtEntry.getValue().executeBatch();
		}

		dbManager.commit();

		mainPstmt.close();
		for (final Map.Entry<String, PreparedStatement> subPstmtEntry : subPstmts
				.entrySet()) {
			subPstmtEntry.getValue().close();
		}
	}

	private Map<String, PreparedStatement> makePreparedStatementsForSubElements() {
		final Map<String, PreparedStatement> subPstmts = new HashMap<String, PreparedStatement>();
		for (final Map.Entry<String, String> subPstmtQueryEntry : preparedStatementQueries
				.entrySet()) {
			final PreparedStatement subPstmt = dbManager
					.createPreparedStatement(subPstmtQueryEntry.getValue());
			subPstmts.put(subPstmtQueryEntry.getKey(), subPstmt);
		}

		return subPstmts;
	}

	protected abstract String createMainPreparedStatementQuery();

	protected abstract void fillMainElementPreparedStatement(
			final PreparedStatement mainPstmt, final T element)
			throws SQLException;

	protected abstract Map<String, Integer> fillSubElementPreparedStatements(
			final T element, final Map<String, PreparedStatement> subPstmts,
			final Map<String, Integer> currentCount) throws SQLException;

}
