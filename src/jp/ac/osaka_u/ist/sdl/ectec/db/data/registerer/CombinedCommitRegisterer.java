package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;

/**
 * A class to register combined commits
 * 
 * @author k-hotta
 * 
 */
public class CombinedCommitRegisterer extends
		AbstractUniqueElementRegisterer<DBCombinedCommitInfo> {

	public CombinedCommitRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into COMBINED_COMMIT values (?,?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt,
			DBCombinedCommitInfo element) throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setLong(++column, element.getBeforeCombinedRevisionId());
		pstmt.setLong(++column, element.getAfterCombinedRevisionId());
		pstmt.setLong(++column, element.getOriginalCommitId());
	}

}
