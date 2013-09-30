package jp.ac.osaka_u.ist.sdl.ectec.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.data.Commit;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;

public class CommitRetriever extends AbstractElementRetriever<Commit> {

	public CommitRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected Commit createElement(ResultSet rs) throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long beforeRevisionId = rs.getLong(++column);
		final long afterRevisionId = rs.getLong(++column);
		final String beforeRevisionIdentifier = rs.getString(++column);
		final String afterRevisionIdentifier = rs.getString(++column);

		return new Commit(id, beforeRevisionId, afterRevisionId,
				beforeRevisionIdentifier, afterRevisionIdentifier);
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
