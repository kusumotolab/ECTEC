package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;

/**
 * A class for retrieving links of clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkRetriever
		extends
		AbstractNonuniqueElementRetriever<DBCloneSetLinkInfo, CloneSetLinkRowData>
		implements ILinkElementRetriever<DBCloneSetLinkInfo> {

	public CloneSetLinkRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected CloneSetLinkRowData makeRowInstance(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long beforeElementId = rs.getLong(++column);
		final long afterElementId = rs.getLong(++column);
		final long beforeCombinedRevisionId = rs.getLong(++column);
		final long afterCombinedRevisionId = rs.getLong(++column);
		final long codeFragmentLinkId = rs.getLong(++column);

		return new CloneSetLinkRowData(id, beforeElementId, afterElementId,
				beforeCombinedRevisionId, afterCombinedRevisionId,
				codeFragmentLinkId);
	}

	@Override
	protected DBCloneSetLinkInfo createElement(
			Collection<CloneSetLinkRowData> rows) {
		CloneSetLinkRowData aRow = null;
		final Set<Long> codeFragmentLinkIds = new TreeSet<Long>();

		for (final CloneSetLinkRowData row : rows) {
			if (aRow == null) {
				aRow = row;
			}

			codeFragmentLinkIds.add(row.getCodeFragmentLinkId());
		}

		if (aRow == null) {
			return null;
		}

		final long id = aRow.getId();
		final long beforeElementId = aRow.getBeforeElementId();
		final long afterElementId = aRow.getAfterElementId();
		final long beforeCombinedRevisionId = aRow
				.getBeforeCombinedRevisionId();
		final long afterCombinedRevisionId = aRow.getAfterCombinedRevisionId();
		final List<Long> listOfCodeFragmentLinkIds = new ArrayList<Long>(
				codeFragmentLinkIds);

		return new DBCloneSetLinkInfo(id, beforeElementId, afterElementId,
				beforeCombinedRevisionId, afterCombinedRevisionId,
				listOfCodeFragmentLinkIds);
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
	@Override
	public synchronized SortedMap<Long, DBCloneSetLinkInfo> retrieveElementsWithBeforeCombinedRevision(
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
	@Override
	public synchronized SortedMap<Long, DBCloneSetLinkInfo> retrieveElementsWithAfterCombinedRevision(
			final long afterRevisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getAfterRevisionIdColumnName() + " = " + afterRevisionId;

		return retrieve(query);
	}

}
