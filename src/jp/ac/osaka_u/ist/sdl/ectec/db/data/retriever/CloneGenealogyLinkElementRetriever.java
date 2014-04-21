package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyLinkElementInfo;

public class CloneGenealogyLinkElementRetriever extends
		AbstractDBSubTableElementRetriever<DBCloneGenealogyLinkElementInfo> {

	public CloneGenealogyLinkElementRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCloneGenealogyLinkElementInfo makeInstance(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long genealogyId = rs.getLong(++column);
		final long linkId = rs.getLong(++column);

		return new DBCloneGenealogyLinkElementInfo(genealogyId, linkId);
	}

	@Override
	protected String getTableName() {
		return "CLONE_GENEALOGY_LINK_ELEMENT";
	}

	@Override
	protected String getIdColumnName() {
		return "CLONE_GENEALOGY_ID";
	}

}
