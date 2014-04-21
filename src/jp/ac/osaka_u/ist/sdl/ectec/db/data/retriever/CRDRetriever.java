package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;

/**
 * A class for retrieving CRDs
 * 
 * @author k-hotta
 * 
 */
public class CRDRetriever extends AbstractUniqueElementRetriever<DBCrdInfo> {

	public CRDRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCrdInfo createElement(ResultSet rs) throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final String typeStr = rs.getString(++column);
		final String head = rs.getString(++column);
		final String anchor = rs.getString(++column);
		final String normalizedAnchor = rs.getString(++column);
		final int cm = rs.getInt(++column);
		final String ancestorsStr = rs.getString(++column);
		final String fullText = rs.getString(++column);

		final BlockType type = BlockType.valueOf(typeStr);
		final List<Long> ancestors = new ArrayList<Long>();
		StringUtils.convertStringToCollection(ancestors, ancestorsStr);

		return new DBCrdInfo(id, type, head, anchor, normalizedAnchor, cm, ancestors,
				fullText);
	}

	@Override
	protected String getTableName() {
		return "CRD";
	}

	@Override
	protected String getIdColumnName() {
		return "CRD_ID";
	}

}
