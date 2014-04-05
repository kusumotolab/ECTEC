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
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;

/**
 * A class for retrieving fragment genealogies
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentGenealogyRetriever
		extends
		AbstractNonuniqueElementRetriever<DBCodeFragmentGenealogyInfo, CodeFragmentGenealogyRowData> {

	public CodeFragmentGenealogyRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected CodeFragmentGenealogyRowData makeRowInstance(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long startCombinedRevisionId = rs.getLong(++column);
		final long endCombinedRevisionId = rs.getLong(++column);
		final long codeFragmentId = rs.getLong(++column);
		final long codeFragmentLinkId = rs.getLong(++column);

		return new CodeFragmentGenealogyRowData(id, startCombinedRevisionId,
				endCombinedRevisionId, codeFragmentId, codeFragmentLinkId);
	}

	@Override
	protected DBCodeFragmentGenealogyInfo createElement(
			Collection<CodeFragmentGenealogyRowData> rows) {
		CodeFragmentGenealogyRowData aRow = null;
		final Set<Long> codeFragmentIds = new TreeSet<Long>();
		final Set<Long> codeFragmentLinkIds = new TreeSet<Long>();

		for (final CodeFragmentGenealogyRowData row : rows) {
			if (aRow == null) {
				aRow = row;
			}

			codeFragmentIds.add(row.getCodeFragmentId());
			codeFragmentLinkIds.add(row.getCodeFragmentLinkId());
		}

		if (aRow == null) {
			return null;
		}

		final long id = aRow.getId();
		final long startCombinedRevisionId = aRow.getStartCombinedRevisionId();
		final long endCombinedRevisionId = aRow.getEndCombinedRevisionId();
		final List<Long> listOfCodeFragmentIds = new ArrayList<Long>(
				codeFragmentIds);
		final List<Long> listOfCodeFragmentLinkIds = new ArrayList<Long>(
				codeFragmentLinkIds);

		return new DBCodeFragmentGenealogyInfo(id, startCombinedRevisionId,
				endCombinedRevisionId, listOfCodeFragmentIds,
				listOfCodeFragmentLinkIds);
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
