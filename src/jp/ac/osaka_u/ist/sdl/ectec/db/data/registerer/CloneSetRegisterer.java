package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;

/**
 * A class that represents a registerer for clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetRegisterer extends AbstractElementRegisterer<DBCloneSetInfo> {

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
		return "insert into CLONE_SET values (?,?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt, DBCloneSetInfo element)
			throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setLong(++column, element.getCombinedRevisionId());
		pstmt.setString(++column,
				StringUtils.convertListToString(element.getElements()));
		pstmt.setInt(++column, element.getNumberOfElements());
	}
}
