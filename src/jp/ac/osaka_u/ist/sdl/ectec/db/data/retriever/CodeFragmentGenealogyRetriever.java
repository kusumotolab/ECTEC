package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyElementInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyLinkElementInfo;

/**
 * A class for retrieving fragment genealogies
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentGenealogyRetriever extends
		AbstractUniqueElementRetriever<DBCodeFragmentGenealogyInfo> {

	private final CodeFragmentGenealogyElementRetriever elementRetriever;

	private final CodeFragmentGenealogyLinkElementRetriever linkRetriever;

	public CodeFragmentGenealogyRetriever(DBConnectionManager dbManager) {
		super(dbManager);
		this.elementRetriever = dbManager
				.getFragmentGenealogyElementRetriever();
		this.linkRetriever = dbManager
				.getFragmentGenealogyLinkElementRetriever();
	}

	@Override
	protected DBCodeFragmentGenealogyInfo createElement(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long startCombinedRevisionId = rs.getLong(++column);
		final long endCombinedRevisionId = rs.getLong(++column);

		final Map<Long, DBCodeFragmentGenealogyElementInfo> elements = elementRetriever
				.retrieveWithIds(id);
		final List<Long> elementIds = new ArrayList<Long>();
		for (final DBCodeFragmentGenealogyElementInfo element : elements
				.values()) {
			elementIds.add(element.getSubElementId());
		}

		final Map<Long, DBCodeFragmentGenealogyLinkElementInfo> links = linkRetriever
				.retrieveWithIds(id);
		final List<Long> linkIds = new ArrayList<Long>();
		for (final DBCodeFragmentGenealogyLinkElementInfo link : links.values()) {
			linkIds.add(link.getSubElementId());
		}

		return new DBCodeFragmentGenealogyInfo(id, startCombinedRevisionId,
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
		return "CODE_FRAGMENT_GENEALOGY";
	}

	@Override
	protected String getIdColumnName() {
		return "CODE_FRAGMENT_GENEALOGY_ID";
	}

	/**
	 * retrieve elements that exist in the specified revision
	 * 
	 * @param revisionId
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, DBCodeFragmentGenealogyInfo> retrieveElementsInSpecifiedRevision(
			final long revisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getStartRevisionIdColumnName() + " <= " + revisionId
				+ " AND " + getEndRevisionIdColumnName() + " >= " + revisionId;

		return retrieve(query);
	}

}
