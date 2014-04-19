package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;

/**
 * A class that represents a registerer for code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentRegisterer extends
		AbstractUniqueElementRegisterer<DBCodeFragmentInfo> {

	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public CodeFragmentRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into CODE_FRAGMENT values (?,?,?,?,?,?,?,?,?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt,
			DBCodeFragmentInfo element) throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setLong(++column, element.getOwnerFileId());
		pstmt.setLong(++column, element.getOwnerRepositoryId());
		pstmt.setLong(++column, element.getCrdId());
		pstmt.setLong(++column, element.getStartCombinedRevisionId());
		pstmt.setLong(++column, element.getEndCombinedRevisionId());
		pstmt.setLong(++column, element.getHash());
		pstmt.setLong(++column, element.getHashForClone());
		pstmt.setInt(++column, element.getStartLine());
		pstmt.setInt(++column, element.getEndLine());
		pstmt.setInt(++column, element.getSize());
	}

}
