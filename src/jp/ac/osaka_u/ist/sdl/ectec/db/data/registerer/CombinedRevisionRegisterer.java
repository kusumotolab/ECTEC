package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;

/**
 * A class that represents a registerer for combined revisions
 * 
 * @author k-hotta
 * 
 */
public class CombinedRevisionRegisterer extends
		AbstractNonuniqueElementRegisterer<DBCombinedRevisionInfo> {

	public CombinedRevisionRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQuery() {
		return "insert into COMBINED_REVISION values (?,?)";
	}

	@Override
	protected int fillPreparedStatement(PreparedStatement pstmt,
			DBCombinedRevisionInfo element) throws SQLException {
		final long elementId = element.getId();
		final Collection<Long> originalRevisions = element.getOriginalRevisions();
		
		for (final long originalRevisionId : originalRevisions) {
			int column = 0;
			pstmt.setLong(++column, elementId);
			pstmt.setLong(++column, originalRevisionId);
			
			pstmt.addBatch();
		}
		
		return element.getOriginalRevisions().size();
	}

}
