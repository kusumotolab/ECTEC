package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;

/**
 * A class that represents a registerer for links of clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkRegisterer extends
		AbstractNonuniqueElementRegisterer<DBCloneSetLinkInfo> {

	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public CloneSetLinkRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into CLONE_SET_LINK values(?,?,?,?,?,?)";
	}

	@Override
	protected int makePreparedStatements(PreparedStatement pstmt,
			DBCloneSetLinkInfo element) throws SQLException {
		final long elementId = element.getId();
		final long beforeElementId = element.getBeforeElementId();
		final long afterElementId = element.getAfterElementId();
		final long beforeCombinedRevisionId = element
				.getBeforeCombinedRevisionId();
		final long afterCombinedRevisionId = element
				.getAfterCombinedRevisionId();
		final Collection<Long> fragmentLinks = element.getCodeFragmentLinks();

		for (final long fragmentLink : fragmentLinks) {
			int column = 0;
			pstmt.setLong(++column, elementId);
			pstmt.setLong(++column, beforeElementId);
			pstmt.setLong(++column, afterElementId);
			pstmt.setLong(++column, beforeCombinedRevisionId);
			pstmt.setLong(++column, afterCombinedRevisionId);
			pstmt.setLong(++column, fragmentLink);

			pstmt.addBatch();
		}

		return fragmentLinks.size();
	}

}
