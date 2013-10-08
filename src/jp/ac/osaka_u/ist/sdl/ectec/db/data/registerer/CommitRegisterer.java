package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;

/**
 * A class that represents the registerer of commits
 * 
 * @author k-hotta
 * 
 */
public class CommitRegisterer extends AbstractElementRegisterer<DBCommitInfo> {

	public CommitRegisterer(DBConnectionManager dbManager, int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into VCS_COMMIT values (?,?,?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt, DBCommitInfo element)
			throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setLong(++column, element.getBeforeRevisionId());
		pstmt.setLong(++column, element.getAfterRevisionId());
		pstmt.setString(++column, element.getBeforeRevisionIdentifier());
		pstmt.setString(++column, element.getAfterRevisionIdentifier());
	}

}
