package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;

/**
 * A class for retrieving combined commits
 * 
 * @author k-hotta
 * 
 */
public class CombinedCommitRetriever extends
		AbstractUniqueElementRetriever<DBCombinedCommitInfo> {

	public CombinedCommitRetriever(final DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCombinedCommitInfo createElement(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long beforeCombinedRevisionId = rs.getLong(++column);
		final long afterCombinedRevisionId = rs.getLong(++column);
		final long originalCommitId = rs.getLong(++column);
		
		return new DBCombinedCommitInfo(id, beforeCombinedRevisionId, afterCombinedRevisionId, originalCommitId);
	}

	@Override
	protected String getTableName() {
		return "COMBINED_COMMIT";
	}

	@Override
	protected String getIdColumnName() {
		return "COMBINED_COMMIT_ID";
	}

}
