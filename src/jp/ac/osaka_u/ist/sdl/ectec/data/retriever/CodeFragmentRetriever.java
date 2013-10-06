package jp.ac.osaka_u.ist.sdl.ectec.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;

/**
 * A class for retrieving code fragments from db
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentRetriever extends
		RangedElementRetriever<CodeFragmentInfo> {

	public CodeFragmentRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected CodeFragmentInfo createElement(ResultSet rs) throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long ownerFileId = rs.getLong(++column);
		final long crdId = rs.getLong(++column);
		final long startRevisionId = rs.getLong(++column);
		final long endRevisionId = rs.getLong(++column);
		final long hash = rs.getLong(++column);
		final long hashForClone = rs.getLong(++column);
		final int startLine = rs.getInt(++column);
		final int endLine = rs.getInt(++column);
		final int size = rs.getInt(++column);

		return new CodeFragmentInfo(id, ownerFileId, crdId, startRevisionId,
				endRevisionId, hash, hashForClone, startLine, endLine, size);
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
		return "CODE_FRAGMENT";
	}

	@Override
	protected String getIdColumnName() {
		return "CODE_FRAGMENT_ID";
	}

}
