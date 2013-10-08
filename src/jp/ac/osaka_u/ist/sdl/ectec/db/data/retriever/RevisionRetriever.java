package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;

/**
 * A class for retrieving revisions from db
 * 
 * @author k-hotta
 * 
 */
public class RevisionRetriever extends AbstractElementRetriever<DBRevisionInfo> {

	public RevisionRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBRevisionInfo createElement(ResultSet rs) throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final String identifier = rs.getString(++column);

		return new DBRevisionInfo(id, identifier);
	}

	@Override
	protected String getTableName() {
		return "REVISION";
	}

	@Override
	protected String getIdColumnName() {
		return "REVISION_ID";
	}

}
