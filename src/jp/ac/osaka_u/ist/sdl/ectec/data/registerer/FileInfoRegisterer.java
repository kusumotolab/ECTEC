package jp.ac.osaka_u.ist.sdl.ectec.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.data.FileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;

/**
 * A class that represents a registerer for files
 * 
 * @author k-hotta
 * 
 */
public class FileInfoRegisterer extends AbstractElementRegisterer<FileInfo> {

	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public FileInfoRegisterer(DBConnectionManager dbManager, int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into FILE values (?,?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt, FileInfo element)
			throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setString(++column, element.getPath());
		pstmt.setLong(++column, element.getStartRevisionId());
		pstmt.setLong(++column, element.getEndRevisionId());
	}

}
