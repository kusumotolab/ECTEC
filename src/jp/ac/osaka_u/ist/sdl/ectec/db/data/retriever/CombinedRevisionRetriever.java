package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;

/**
 * A class for retrieving combined revisions
 * 
 * @author k-hotta
 * 
 */
public class CombinedRevisionRetriever extends
		AbstractElementRetriever<DBCombinedRevisionInfo> {

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
	public SortedMap<Long, DBCombinedRevisionInfo> instantiate(ResultSet rs)
			throws SQLException {
		final SortedMap<Long, DBCombinedRevisionInfo> result = new TreeMap<Long, DBCombinedRevisionInfo>();

		long previousId = -1;
		List<Long> originalRevisions = new ArrayList<Long>();
		while (rs.next()) {
			int column = 0;
			final long id = rs.getLong(++column);
			final long originalRevisionId = rs.getLong(++column);

			if (id != previousId) {
				if (!originalRevisions.isEmpty()) {
					final DBCombinedRevisionInfo newInstance = new DBCombinedRevisionInfo(
							previousId, originalRevisions);
					result.put(newInstance.getId(), newInstance);
					originalRevisions = new ArrayList<Long>();
				}
			}

			previousId = id;
			originalRevisions.add(originalRevisionId);
		}

		if (!originalRevisions.isEmpty()) {
			final DBCombinedRevisionInfo newInstance = new DBCombinedRevisionInfo(
					previousId, originalRevisions);
			result.put(newInstance.getId(), newInstance);
		}

		return Collections.unmodifiableSortedMap(result);
	}

}
