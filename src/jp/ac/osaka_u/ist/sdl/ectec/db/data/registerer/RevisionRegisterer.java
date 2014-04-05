package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;

/**
 * A class that represents a registerer for revisions
 * 
 * @author k-hotta
 * 
 */
public class RevisionRegisterer extends
		AbstractUniqueElementRegisterer<DBRevisionInfo> {

	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public RevisionRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into REVISION values (?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt, DBRevisionInfo element)
			throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setString(++column, element.getIdentifier());
		pstmt.setLong(++column, element.getRepositoryId());
	}

}
