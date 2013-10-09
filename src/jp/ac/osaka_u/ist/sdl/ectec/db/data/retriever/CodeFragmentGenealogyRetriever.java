package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;

/**
 * A class for retrieving fragment genealogies
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentGenealogyRetriever extends
		RangedElementRetriever<DBCodeFragmentGenealogyInfo> {

	public CodeFragmentGenealogyRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCodeFragmentGenealogyInfo createElement(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long startRevisionId = rs.getLong(++column);
		final long endRevisionId = rs.getLong(++column);
		final String elementsStr = rs.getString(++column);
		final String linksStr = rs.getString(++column);
		final int changes = rs.getInt(++column);

		final List<Long> elements = new ArrayList<Long>();
		StringUtils.convertStringToCollection(elements, elementsStr);
		final List<Long> links = new ArrayList<Long>();
		StringUtils.convertStringToCollection(links, linksStr);

		return new DBCodeFragmentGenealogyInfo(id, startRevisionId,
				endRevisionId, elements, links, changes);
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
		return "CODE_FRAGMENT_GENEALOGY";
	}

	@Override
	protected String getIdColumnName() {
		return "CODE_FRAGMENT_GENEALOGY_ID";
	}

}