package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;

/**
 * A class for retrieving links of clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkRetriever extends
		AbstractElementRetriever<DBCloneSetLinkInfo> {

	public CloneSetLinkRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	public SortedMap<Long, DBCloneSetLinkInfo> instantiate(ResultSet rs)
			throws SQLException {
		final SortedMap<Long, DBCloneSetLinkInfo> result = new TreeMap<Long, DBCloneSetLinkInfo>();

		long previousId = -1;
		long id = -1;
		long beforeElementId = -1;
		long afterElementId = -1;
		long beforeCombinedRevisionId = -1;
		long afterCombinedRevisionId = -1;
		long codeFragmentLinkId = -1;
		List<Long> codeFragmentLinkIds = new ArrayList<Long>();

		while (rs.next()) {
			int column = 0;
			id = rs.getLong(++column);
			beforeElementId = rs.getLong(++column);
			afterElementId = rs.getLong(++column);
			beforeCombinedRevisionId = rs.getLong(++column);
			afterCombinedRevisionId = rs.getLong(++column);
			codeFragmentLinkId = rs.getLong(++column);

			if (id != previousId) {
				if (!codeFragmentLinkIds.isEmpty()) {
					final DBCloneSetLinkInfo newInstance = new DBCloneSetLinkInfo(
							id, beforeElementId, afterElementId,
							beforeCombinedRevisionId, afterCombinedRevisionId,
							codeFragmentLinkIds);
					result.put(newInstance.getId(), newInstance);
					codeFragmentLinkIds = new ArrayList<Long>();
				}
			}
			
			previousId = id;
			codeFragmentLinkIds.add(codeFragmentLinkId);
		}
		
		if (!codeFragmentLinkIds.isEmpty()) {
			final DBCloneSetLinkInfo newInstance = new DBCloneSetLinkInfo(
					id, beforeElementId, afterElementId,
					beforeCombinedRevisionId, afterCombinedRevisionId,
					codeFragmentLinkIds);
			result.put(newInstance.getId(), newInstance);
		}

		return Collections.unmodifiableSortedMap(result);
	}

	protected String getBeforeRevisionIdColumnName() {
		return "BEFORE_COMBINED_REVISION_ID";
	}

	protected String getAfterRevisionIdColumnName() {
		return "AFTER_COMBINED_REVISION_ID";
	}

	@Override
	protected String getTableName() {
		return "CLONE_SET_LINK";
	}

	@Override
	protected String getIdColumnName() {
		return "CLONE_SET_LINK_ID";
	}

	/**
	 * retrieve elements by specifying their before revision
	 * 
	 * @param beforeRevisionId
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, DBCloneSetLinkInfo> retrieveElementsWithBeforeRevision(
			final long beforeRevisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getBeforeRevisionIdColumnName() + " = " + beforeRevisionId;

		return retrieve(query);
	}

	/**
	 * retrieve elements by specifying their before revision
	 * 
	 * @param afterRevisionId
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, DBCloneSetLinkInfo> retrieveElementsWithAfterRevision(
			final long afterRevisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getAfterRevisionIdColumnName() + " = " + afterRevisionId;

		return retrieve(query);
	}

}
