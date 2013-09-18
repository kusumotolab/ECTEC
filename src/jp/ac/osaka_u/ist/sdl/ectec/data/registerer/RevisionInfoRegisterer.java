package jp.ac.osaka_u.ist.sdl.ectec.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;

/**
 * A class that represents a registerer for revisions
 * 
 * @author k-hotta
 * 
 */
public class RevisionInfoRegisterer extends
		AbstractElementRegisterer<RevisionInfo> {

	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public RevisionInfoRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into REVISION values (?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt, RevisionInfo element)
			throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setString(++column, element.getIdentifier());
	}

}
