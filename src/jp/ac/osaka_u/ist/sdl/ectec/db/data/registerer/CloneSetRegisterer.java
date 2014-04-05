package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;

/**
 * A class that represents a registerer for clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetRegisterer extends
		AbstractNonuniqueElementRegisterer<DBCloneSetInfo> {

	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public CloneSetRegisterer(DBConnectionManager dbManager, int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into CLONE_SET values (?,?,?)";
	}

	@Override
	protected int makePreparedStatements(PreparedStatement pstmt,
			DBCloneSetInfo element) throws SQLException {
		final long elementId = element.getId();
		final long ownerCombinedRevisionId = element.getCombinedRevisionId();
		final Collection<Long> codeFragments = element.getElements();

		for (final long codeFragment : codeFragments) {
			int column = 0;
			pstmt.setLong(++column, elementId);
			pstmt.setLong(++column, ownerCombinedRevisionId);
			pstmt.setLong(++column, codeFragment);
			pstmt.addBatch();
		}

		return codeFragments.size();
	}

}
