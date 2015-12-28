package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;

/**
 * A class that represents the registerer of commits
 *
 * @author k-hotta
 *
 */
public class CommitRegisterer extends
		AbstractUniqueElementRegisterer<DBCommitInfo> {

	public CommitRegisterer(DBConnectionManager dbManager, int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into VCS_COMMIT values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt, DBCommitInfo element)
			throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setLong(++column, element.getRepositoryId());
		pstmt.setLong(++column, element.getBeforeRevisionId());
		pstmt.setLong(++column, element.getAfterRevisionId());
		pstmt.setString(++column, element.getBeforeRevisionIdentifier());
		pstmt.setString(++column, element.getAfterRevisionIdentifier());
		pstmt.setString(++column, element.getCommitter());
		pstmt.setString(++column, element.getCommitterEmail());

		final Date date = element.getDate();
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		pstmt.setInt(++column, calendar.get(Calendar.YEAR));
		// "+1" is necessary to convert 0-11 to 1-12
		pstmt.setInt(++column, calendar.get(Calendar.MONTH) + 1);
		pstmt.setInt(++column, calendar.get(Calendar.DAY_OF_MONTH));
		pstmt.setInt(++column, calendar.get(Calendar.HOUR_OF_DAY));
		pstmt.setInt(++column, calendar.get(Calendar.MINUTE));
		pstmt.setInt(++column, calendar.get(Calendar.SECOND));
	}

}
