package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyLinkElementInfo;

public class CodeFragmentGenealogyLinkElementRetriever
		extends
		AbstractDBSubTableElementRetriever<DBCodeFragmentGenealogyLinkElementInfo> {

	public CodeFragmentGenealogyLinkElementRetriever(
			DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCodeFragmentGenealogyLinkElementInfo makeInstance(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long genealogyId = rs.getLong(++column);
		final long linkId = rs.getLong(++column);

		return new DBCodeFragmentGenealogyLinkElementInfo(genealogyId, linkId);
	}

	@Override
	protected String getTableName() {
		return "CODE_FRAGMENT_GENEALOGY_LINK_ELEMENT";
	}

	@Override
	protected String getIdColumnName() {
		return "CODE_FRAGMENT_GENEALOGY_ID";
	}

}
