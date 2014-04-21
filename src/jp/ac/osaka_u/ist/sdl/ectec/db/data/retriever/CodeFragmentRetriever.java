package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;

/**
 * A class for retrieving code fragments from db
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentRetriever extends
		AbstractUniqueElementRetriever<DBCodeFragmentInfo> {

	public CodeFragmentRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCodeFragmentInfo createElement(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long ownerFileId = rs.getLong(++column);
		final long ownerRepositoryId = rs.getLong(++column);
		final long crdId = rs.getLong(++column);
		final long startCombinedRevisionId = rs.getLong(++column);
		final long endCombinedRevisionId = rs.getLong(++column);
		final long hash = rs.getLong(++column);
		final long hashForClone = rs.getLong(++column);
		final int startLine = rs.getInt(++column);
		final int endLine = rs.getInt(++column);
		final int size = rs.getInt(++column);

		return new DBCodeFragmentInfo(id, ownerFileId, ownerRepositoryId,
				crdId, startCombinedRevisionId, endCombinedRevisionId, hash,
				hashForClone, startLine, endLine, size);
	}

	protected String getStartRevisionIdColumnName() {
		return "START_COMBINED_REVISION_ID";
	}

	protected String getEndRevisionIdColumnName() {
		return "END_COMBINED_REVISION_ID";
	}

	@Override
	protected String getTableName() {
		return "CODE_FRAGMENT";
	}

	@Override
	protected String getIdColumnName() {
		return "CODE_FRAGMENT_ID";
	}

	/**
	 * retrieve elements that exist in the specified revision
	 * 
	 * @param combinedRevisionId
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, DBCodeFragmentInfo> retrieveElementsInSpecifiedCombinedRevision(
			final long combinedRevisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getStartRevisionIdColumnName() + " <= " + combinedRevisionId
				+ " AND " + getEndRevisionIdColumnName() + " >= "
				+ combinedRevisionId;

		return retrieve(query);
	}

}
