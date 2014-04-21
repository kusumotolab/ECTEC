package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;

/**
 * A class for retrieving combined revisions
 * 
 * @author k-hotta
 * 
 */
public class CombinedRevisionRetriever
		extends
		AbstractNonuniqueElementRetriever<DBCombinedRevisionInfo, CombinedRevisionRowData> {

	public CombinedRevisionRetriever(final DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected String getTableName() {
		return "COMBINED_REVISION";
	}

	@Override
	protected String getIdColumnName() {
		return "COMBINED_REVISION_ID";
	}

	@Override
	protected CombinedRevisionRowData makeRowInstance(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long originalRevisionId = rs.getLong(++column);

		return new CombinedRevisionRowData(id, originalRevisionId);
	}

	@Override
	protected DBCombinedRevisionInfo createElement(
			Collection<CombinedRevisionRowData> rows) {
		CombinedRevisionRowData aRow = null;
		final Set<Long> originalRevisionIds = new TreeSet<Long>();

		for (final CombinedRevisionRowData row : rows) {
			if (aRow == null) {
				aRow = row;
			}

			originalRevisionIds.add(row.getOriginalRevisionId());
		}
		
		if (aRow == null) {
			return null;
		}

		final long id = aRow.getId();
		final List<Long> listOfOriginalRevisionIds = new ArrayList<Long>(
				originalRevisionIds);

		return new DBCombinedRevisionInfo(id, listOfOriginalRevisionIds);
	}

}
