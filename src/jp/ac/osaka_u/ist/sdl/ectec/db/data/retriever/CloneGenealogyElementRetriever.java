package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyElementInfo;

public class CloneGenealogyElementRetriever extends
		AbstractDBSubTableElementRetriever<DBCloneGenealogyElementInfo> {

	public CloneGenealogyElementRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCloneGenealogyElementInfo makeInstance(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long genealogyId = rs.getLong(++column);
		final long elementId = rs.getLong(++column);

		return new DBCloneGenealogyElementInfo(genealogyId, elementId);
	}

	@Override
	protected String getTableName() {
		return "CLONE_GENEALOGY_ELEMENT";
	}

	@Override
	protected String getIdColumnName() {
		return "CLONE_GENEALOGY_ID";
	}

}
