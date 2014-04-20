package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;

/**
 * A class for retrieving links of code fragments from db
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkRetriever extends
		AbstractUniqueElementRetriever<DBCodeFragmentLinkInfo> implements
		ILinkElementRetriever<DBCodeFragmentLinkInfo> {

	public CodeFragmentLinkRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCodeFragmentLinkInfo createElement(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long beforeElementId = rs.getLong(++column);
		final long afterElementId = rs.getLong(++column);
		final long beforeCombinedRevisionId = rs.getLong(++column);
		final long afterCombinedRevisionId = rs.getLong(++column);
		final int changed = rs.getInt(++column);

		final boolean changedBool = (changed == 1);

		return new DBCodeFragmentLinkInfo(id, beforeElementId, afterElementId,
				beforeCombinedRevisionId, afterCombinedRevisionId, changedBool);
	}

	protected String getBeforeRevisionIdColumnName() {
		return "BEFORE_COMBINED_REVISION_ID";
	}

	protected String getAfterRevisionIdColumnName() {
		return "AFTER_COMBINED_REVISION_ID";
	}

	@Override
	protected String getTableName() {
		return "CODE_FRAGMENT_LINK";
	}

	@Override
	protected String getIdColumnName() {
		return "CODE_FRAGMENT_LINK_ID";
	}

	/**
	 * retrieve elements by specifying their before revision
	 * 
	 * @param beforeRevisionId
	 * @return
	 * @throws SQLException
	 */
	@Override
	public synchronized SortedMap<Long, DBCodeFragmentLinkInfo> retrieveElementsWithBeforeCombinedRevision(
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
	public synchronized SortedMap<Long, DBCodeFragmentLinkInfo> retrieveElementsWithAfterCombinedRevision(
			final long afterRevisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getAfterRevisionIdColumnName() + " = " + afterRevisionId;

		return retrieve(query);
	}

}
