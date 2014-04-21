package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyElementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyLinkElementInfo;

/**
 * A class for retrieving clone genealogies
 * 
 * @author k-hotta
 * 
 */
public class CloneGenealogyRetriever extends
		AbstractUniqueElementRetriever<DBCloneGenealogyInfo> {

	private final CloneGenealogyElementRetriever elementRetriever;

	private final CloneGenealogyLinkElementRetriever linkRetriever;

	public CloneGenealogyRetriever(DBConnectionManager dbManager) {
		super(dbManager);
		this.elementRetriever = dbManager.getCloneGenealogyElementRetriever();
		this.linkRetriever = dbManager.getCloneGenealogyLinkElementRetriever();
	}

	@Override
	protected DBCloneGenealogyInfo createElement(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long startCombinedRevisionId = rs.getLong(++column);
		final long endCombinedRevisionId = rs.getLong(++column);

		final Map<Long, DBCloneGenealogyElementInfo> elements = elementRetriever
				.retrieveWithIds(id);
		final List<Long> elementIds = new ArrayList<Long>();
		for (final DBCloneGenealogyElementInfo element : elements.values()) {
			elementIds.add(element.getSubElementId());
		}

		final Map<Long, DBCloneGenealogyLinkElementInfo> links = linkRetriever
				.retrieveWithIds(id);
		final List<Long> linkIds = new ArrayList<Long>();
		for (final DBCloneGenealogyLinkElementInfo link : links.values()) {
			linkIds.add(link.getSubElementId());
		}

		return new DBCloneGenealogyInfo(id, startCombinedRevisionId,
				endCombinedRevisionId, elementIds, linkIds);
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
