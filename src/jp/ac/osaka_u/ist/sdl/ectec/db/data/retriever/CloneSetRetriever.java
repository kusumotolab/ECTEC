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
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;

/**
 * A class for retrieving clone sets from db
 * 
 * @author k-hotta
 * 
 */
public class CloneSetRetriever extends
		AbstractNonuniqueElementRetriever<DBCloneSetInfo, CloneSetRowData> {

	public CloneSetRetriever(DBConnectionManager dbManager) {
		super(dbManager);
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

	@Override
	protected CloneSetRowData makeRowInstance(ResultSet rs) throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long ownerCombinedRevisionId = rs.getLong(++column);
		final long elementId = rs.getLong(++column);

		return new CloneSetRowData(id, ownerCombinedRevisionId, elementId);
	}

	@Override
	protected DBCloneSetInfo createElement(Collection<CloneSetRowData> rows) {
		CloneSetRowData aRow = null;
		final Set<Long> elementIds = new TreeSet<Long>();

		for (final CloneSetRowData row : rows) {
			if (aRow == null) {
				aRow = row;
			}

			elementIds.add(row.getElementId());
		}

		if (aRow == null) {
			return null;
		}

		final long id = aRow.getId();
		final long ownerCombinedRevisionId = aRow.getOwnerCombinedRevisionId();
		final List<Long> listOfElementIds = new ArrayList<Long>(elementIds);

		return new DBCloneSetInfo(id, ownerCombinedRevisionId, listOfElementIds);
	}
}
