package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;

/**
 * A class for retrieving files from db
 * 
 * @author k-hotta
 * 
 */
public class FileRetriever extends RangedElementRetriever<DBFileInfo> {

	public FileRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBFileInfo createElement(ResultSet rs) throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final String path = rs.getString(++column);
		final long startRevisionId = rs.getLong(++column);
		final long endRevisionId = rs.getLong(++column);

		return new DBFileInfo(id, path, startRevisionId, endRevisionId);
	}

	@Override
	protected String getStartRevisionIdColumnName() {
		return "START_REVISION_ID";
	}

	@Override
	protected String getEndRevisionIdColumnName() {
		return "END_REVISION_ID";
	}

	@Override
	protected String getTableName() {
		return "FILE";
	}

	@Override
	protected String getIdColumnName() {
		return "FILE_ID";
	}

}
