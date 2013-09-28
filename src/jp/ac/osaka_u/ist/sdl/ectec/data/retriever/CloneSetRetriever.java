package jp.ac.osaka_u.ist.sdl.ectec.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;

/**
 * A class for retrieving clone sets from db
 * 
 * @author k-hotta
 * 
 */
public class CloneSetRetriever extends VolatileElementRetriever<CloneSetInfo> {

	public CloneSetRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected CloneSetInfo createElement(ResultSet rs) throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long revisionId = rs.getLong(++column);
		final String elementsStr = rs.getString(++column);
		final List<Long> elements = new ArrayList<Long>();
		StringUtils.convertStringToCollection(elements, elementsStr);

		return new CloneSetInfo(id, revisionId, elements);
	}

	@Override
	protected String getRevisionIdColumnName() {
		return "OWNER_REVISION_ID";
	}

	@Override
	protected String getTableName() {
		return "CLONE_SET";
	}

	@Override
	protected String getIdColumnName() {
		return "CLONE_SET_ID";
	}

}
