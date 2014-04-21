package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;

/**
 * A class for retrieving links of clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkRetriever extends
		AbstractUniqueElementRetriever<DBCloneSetLinkInfo> implements
		ILinkElementRetriever<DBCloneSetLinkInfo> {

	private final CloneSetLinkFragmentLinkRetriever fragmentLinkRetriever;

	public CloneSetLinkRetriever(DBConnectionManager dbManager) {
		super(dbManager);
		this.fragmentLinkRetriever = dbManager
				.getCloneLinkFragmentLinkRetriever();
	}

	@Override
	protected DBCloneSetLinkInfo createElement(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long beforeElementId = rs.getLong(++column);
		final long afterElementId = rs.getLong(++column);
		final long beforeCombinedRevisionId = rs.getLong(++column);
		final long afterCombinedRevisionId = rs.getLong(++column);

		final Map<Long, DBCloneSetLinkFragmentLinkInfo> fragmentLinks = fragmentLinkRetriever
				.retrieveWithIds(id);
		final List<Long> fragmentLinkIds = new ArrayList<Long>();
		for (final DBCloneSetLinkFragmentLinkInfo fragmentLink : fragmentLinks
				.values()) {
			fragmentLinkIds.add(fragmentLink.getSubElementId());
		}

		return new DBCloneSetLinkInfo(id, beforeElementId, afterElementId,
				beforeCombinedRevisionId, afterCombinedRevisionId,
				fragmentLinkIds);
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
