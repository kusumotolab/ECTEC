package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkFragmentLinkInfo;

public class CloneSetLinkFragmentLinkRetriever extends
		AbstractDBSubTableElementRetriever<DBCloneSetLinkFragmentLinkInfo> {

	public CloneSetLinkFragmentLinkRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCloneSetLinkFragmentLinkInfo makeInstance(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long cloneSetLinkId = rs.getLong(++column);
		final long fragmentLinkId = rs.getLong(++column);

		return new DBCloneSetLinkFragmentLinkInfo(cloneSetLinkId,
				fragmentLinkId);
	}

	@Override
	protected String getTableName() {
		return "CLONE_SET_LINK_FRAGMENT_LINK";
	}

	@Override
	protected String getIdColumnName() {
		return "CLONE_SET_LINK_ID";
	}

}
