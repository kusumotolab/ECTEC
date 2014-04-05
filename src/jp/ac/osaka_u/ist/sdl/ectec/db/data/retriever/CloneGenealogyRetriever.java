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
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;

/**
 * A class for retrieving clone genealogies
 * 
 * @author k-hotta
 * 
 */
public class CloneGenealogyRetriever
		extends
		AbstractNonuniqueElementRetriever<DBCloneGenealogyInfo, CloneGenealogyRowData> {

	public CloneGenealogyRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected CloneGenealogyRowData makeRowInstance(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long startCombinedRevisionId = rs.getLong(++column);
		final long endCombinedRevisionId = rs.getLong(++column);
		final long cloneSetId = rs.getLong(++column);
		final long cloneSetLinkId = rs.getLong(++column);

		return new CloneGenealogyRowData(id, startCombinedRevisionId,
				endCombinedRevisionId, cloneSetId, cloneSetLinkId);
	}

	@Override
	protected DBCloneGenealogyInfo createElement(
			Collection<CloneGenealogyRowData> rows) {
		final Set<Long> cloneSetIds = new TreeSet<Long>();
		final Set<Long> cloneSetLinkIds = new TreeSet<Long>();
		CloneGenealogyRowData aRow = null;

		for (final CloneGenealogyRowData row : rows) {
			if (aRow == null) {
				aRow = row;
			}
			cloneSetIds.add(row.getCloneSetId());
			cloneSetLinkIds.add(row.getCloneSetLinkId());
		}

		if (aRow == null) {
			return null; // here shouldn't be reached
		}

		final long id = aRow.getId();
		final long startCombinedRevisionId = aRow.getStartCombinedRevisionId();
		final long endCombinedRevisionId = aRow.getEndCombinedRevisionId();
		final List<Long> listOfCloneSetIds = new ArrayList<Long>(cloneSetIds);
		final List<Long> listOfCloneSetLinkIds = new ArrayList<Long>(
				cloneSetLinkIds);

		return new DBCloneGenealogyInfo(id, startCombinedRevisionId,
				endCombinedRevisionId, listOfCloneSetIds, listOfCloneSetLinkIds);
	}

	protected String getStartRevisionIdColumnName() {
		return "START_REVISION_ID";
	}

	protected String getEndRevisionIdColumnName() {
		return "END_REVISION_ID";
	}

	@Override
	protected String getTableName() {
		return "CLONE_GENEALOGY";
	}

	@Override
	protected String getIdColumnName() {
		return "CLONE_GENEALOGY_ID";
	}

	/**
	 * retrieve elements that exist in the specified revision
	 * 
	 * @param revisionId
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, DBCloneGenealogyInfo> retrieveElementsInSpecifiedRevision(
			final long revisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getStartRevisionIdColumnName() + " <= " + revisionId
				+ " AND " + getEndRevisionIdColumnName() + " >= " + revisionId;

		return retrieve(query);
	}

}
