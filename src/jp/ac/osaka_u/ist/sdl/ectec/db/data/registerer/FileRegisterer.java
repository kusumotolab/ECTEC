package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;

/**
 * A class that represents a registerer for files
 * 
 * @author k-hotta
 * 
 */
public class FileRegisterer extends AbstractUniqueElementRegisterer<DBFileInfo> {

	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public FileRegisterer(DBConnectionManager dbManager, int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into FILE values (?,?,?,?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt, DBFileInfo element)
			throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setLong(++column, element.getOwnerRepositoryId());
		pstmt.setString(++column, element.getPath());
		pstmt.setLong(++column, element.getStartCombinedRevisionId());
		pstmt.setLong(++column, element.getCombinedEndRevisionId());

		final int deletedAtEndInt = (element.isDeletedAtEnd()) ? 1 : 0;

		pstmt.setInt(++column, deletedAtEndInt);
	}

}
