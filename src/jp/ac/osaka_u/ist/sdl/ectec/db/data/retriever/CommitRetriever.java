package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
				afterRevisionId, date);
	}

	@Override
	protected String getTableName() {
		return "VCS_COMMIT";
	}

	@Override
	protected String getIdColumnName() {
		return "VCS_COMMIT_ID";
	}

}
