package jp.ac.osaka_u.ist.sdl.ectec.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.data.CloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;

/**
 * A class that represents a registerer for clone genealogies
 * 
 * @author k-hotta
 * 
 */
public class CloneGenealogyRegisterer extends
		AbstractElementRegisterer<CloneGenealogyInfo> {

	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public CloneGenealogyRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into CLONE_GENEALOGY values(?,?,?,?,?,?,?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt,
			CloneGenealogyInfo element) throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setLong(++column, element.getStartRevisionId());
		pstmt.setLong(++column, element.getEndRevisionId());
		pstmt.setString(++column,
				StringUtils.convertListToString(element.getClones()));
		pstmt.setString(++column,
				StringUtils.convertListToString(element.getCloneLinks()));
		pstmt.setInt(++column, element.getNumberOfChanges());
		pstmt.setInt(++column, element.getNumberOfAdditions());
		pstmt.setInt(++column, element.getNumberOfDeletions());
		pstmt.setInt(++column, (element.isDead()) ? 1 : 0);
	}

}
