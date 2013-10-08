package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;

/**
 * A class for retrieving links of code fragments from db
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkRetriever extends
		LinkElementRetriever<DBCodeFragmentLinkInfo> {

	public CodeFragmentLinkRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCodeFragmentLinkInfo createElement(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long beforeElementId = rs.getLong(++column);
		final long afterElementId = rs.getLong(++column);
		final long beforeRevisionId = rs.getLong(++column);
		final long afterRevisionId = rs.getLong(++column);
		final boolean changed = (rs.getInt(++column) == 1) ? true : false;

		return new DBCodeFragmentLinkInfo(id, beforeElementId, afterElementId,
				beforeRevisionId, afterRevisionId, changed);
	}

	@Override
	protected String getBeforeRevisionIdColumnName() {
		return "BEFORE_REVISION_ID";
	}

	@Override
	protected String getAfterRevisionIdColumnName() {
		return "AFTER_REVISION_ID";
	}

	@Override
	protected String getTableName() {
		return "CODE_FRAGMENT_LINK";
	}

	@Override
	protected String getIdColumnName() {
		return "CODE_FRAGMENT_LINK_ID";
	}

}
