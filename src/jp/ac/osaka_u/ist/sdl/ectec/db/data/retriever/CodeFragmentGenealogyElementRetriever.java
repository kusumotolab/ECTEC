package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyElementInfo;

public class CodeFragmentGenealogyElementRetriever extends
		AbstractDBSubTableElementRetriever<DBCodeFragmentGenealogyElementInfo> {

	public CodeFragmentGenealogyElementRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCodeFragmentGenealogyElementInfo makeInstance(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long genealogyId = rs.getLong(++column);
		final long elementId = rs.getLong(++column);

		return new DBCodeFragmentGenealogyElementInfo(genealogyId, elementId);
	}

	@Override
	protected String getTableName() {
		return "CODE_FRAGMENT_GENEALOGY_ELEMENT";
	}

	@Override
	protected String getIdColumnName() {
		return "CODE_FRAGMENT_GENEALOGY_ID";
	}

}
