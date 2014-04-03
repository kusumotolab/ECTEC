package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;

/**
 * A class that represents a registerer for repositories
 * 
 * @author k-hotta
 * 
 */
public class RepositoryRegisterer extends
		AbstractElementRegisterer<DBRepositoryInfo> {

	public RepositoryRegisterer(DBConnectionManager dbManager, int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into REPOSITORY values (?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt,
			DBRepositoryInfo element) throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setString(++column, element.getUrl());
	}

}
