package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;

/**
 * A class for retrieving clone sets from db
 * 
 * @author k-hotta
 * 
 */
public class CloneSetRetriever extends AbstractElementRetriever<DBCloneSetInfo> {

	public CloneSetRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	public SortedMap<Long, DBCloneSetInfo> instantiate(ResultSet rs)
			throws SQLException {
		final SortedMap<Long, DBCloneSetInfo> result = new TreeMap<Long, DBCloneSetInfo>();

		List<Long> elementIds = new ArrayList<Long>();
		long previousId = -1;
		long id = -1;
		long ownerCombinedRevisionId = -1;
		long elementId = -1;
		
		while (rs.next()) {
			int column = 0;
			id = rs.getLong(++column);
			ownerCombinedRevisionId = rs.getLong(++column);
			elementId = rs.getLong(++column);

			if (id != previousId) {
				if (!elementIds.isEmpty()) {
					final DBCloneSetInfo newInstance = new DBCloneSetInfo(
							elementId, ownerCombinedRevisionId, elementIds);
					result.put(newInstance.getId(), newInstance);
					elementIds = new ArrayList<Long>();
				}
			}

			previousId = id;
			elementIds.add(elementId);
		}
		
		if (!elementIds.isEmpty()) {
			final DBCloneSetInfo newInstance = new DBCloneSetInfo(
					id, ownerCombinedRevisionId, elementIds);
			result.put(newInstance.getId(), newInstance);
			elementIds = new ArrayList<Long>();
		}

		return Collections.unmodifiableSortedMap(result);
	}

	protected String getRevisionIdColumnName() {
		return "OWNER_COMBINED_REVISION_ID";
	}

	@Override
	protected String getTableName() {
		return "CLONE_SET";
	}

	@Override
	protected String getIdColumnName() {
		return "CLONE_SET_ID";
	}

	/**
	 * retrieve elements that exist at the specified revision
	 * 
	 * @param revisionId
	 * @return
	 * @throws SQLException
	 */
	public SortedMap<Long, DBCloneSetInfo> retrieveElementsInSpecifiedRevision(
			final long revisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getRevisionIdColumnName() + " = " + revisionId;

		return retrieve(query);
	}

}
