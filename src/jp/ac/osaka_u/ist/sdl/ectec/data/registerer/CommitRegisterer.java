package jp.ac.osaka_u.ist.sdl.ectec.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.data.Commit;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;

/**
 * A class that represents the registerer of commits
 * 
 * @author k-hotta
 * 
 */
public class CommitRegisterer extends AbstractElementRegisterer<Commit> {

	public CommitRegisterer(DBConnectionManager dbManager, int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into VCS_COMMIT values (?,?,?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt, Commit element)
			throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setLong(++column, element.getBeforeRevisionId());
		pstmt.setLong(++column, element.getAfterRevisionId());
		pstmt.setString(++column, element.getBeforeRevisionIdentifier());
		pstmt.setString(++column, element.getAfterRevisionIdentifier());
	}

}
