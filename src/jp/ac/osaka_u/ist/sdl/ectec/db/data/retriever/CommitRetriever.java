package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;

/**
 * A class for retrieving commits
 * 
 * @author k-hotta
 * 
 */
public class CommitRetriever extends
		AbstractUniqueElementRetriever<DBCommitInfo> {

	public CommitRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCommitInfo createElement(ResultSet rs) throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long repositoryId = rs.getLong(++column);
		final long beforeRevisionId = rs.getLong(++column);
		final long afterRevisionId = rs.getLong(++column);
		final String beforeRevisionIdentifier = rs.getString(++column);
		final String afterRevisionIdentifier = rs.getString(++column);
		final int year = rs.getInt(++column);
		final int month = rs.getInt(++column);
		final int day = rs.getInt(++column);
		final int hour = rs.getInt(++column);
		final int minute = rs.getInt(++column);
		final int second = rs.getInt(++column);

		final Calendar cal = new GregorianCalendar(year, month - 1, day, hour,
				minute, second);
		final Date date = cal.getTime();

		return new DBCommitInfo(id, repositoryId, beforeRevisionId,
				afterRevisionId, beforeRevisionIdentifier,
				afterRevisionIdentifier, date);
	}

	@Override
	protected String getTableName() {
		return "VCS_COMMIT";
	}

	@Override
	protected String getIdColumnName() {
		return "VCS_COMMIT_ID";
	}

	public DBCommitInfo getLatestCommit() throws SQLException {
		final int maxYear = getIntWithQuery("select MAX(YEAR) from "
				+ getTableName());
		final int maxMonth = getIntWithQuery("select MAX(MONTH) from "
				+ getTableName() + " where YEAR = " + maxYear);
		final int maxDay = getIntWithQuery("select MAX(DAY) from "
				+ getTableName() + " where YEAR = " + maxYear + " AND MONTH = "
				+ maxMonth);

		final Map<Long, DBCommitInfo> commitsInTheDay = retrieve("select * from "
				+ getTableName()
				+ " where YEAR = "
				+ maxYear
				+ " AND MONTH = "
				+ maxMonth + " AND DAY = " + maxDay);

		DBCommitInfo result = null;
		for (final Map.Entry<Long, DBCommitInfo> entry : commitsInTheDay
				.entrySet()) {
			final DBCommitInfo currentCommit = entry.getValue();
			if (result == null) {
				result = currentCommit;
				continue;
			}

			if (result.getDate().compareTo(currentCommit.getDate()) < 0) {
				result = currentCommit;
			}
		}

		return result;
	}

	private int getIntWithQuery(final String query) throws SQLException {
		final Statement stmt = dbManager.createStatement();
		final ResultSet rs = stmt.executeQuery(query);

		rs.next();
		final int result = rs.getInt(1);

		stmt.close();
		rs.close();

		return result;
	}

}
