package jp.ac.osaka_u.ist.sdl.ectec.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;

/**
 * A class that represents a registerer for links of clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkRegisterer extends
		AbstractElementRegisterer<CloneSetLinkInfo> {

	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public CloneSetLinkRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into CLONE_SET_LINK values(?,?,?,?,?,?,?,?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt,
			CloneSetLinkInfo element) throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setLong(++column, element.getBeforeElementId());
		pstmt.setLong(++column, element.getAfterElementId());
		pstmt.setLong(++column, element.getBeforeRevisionId());
		pstmt.setLong(++column, element.getAfterRevisionId());
		pstmt.setInt(++column, element.getNumberOfChangedElements());
		pstmt.setInt(++column, element.getNumberOfAddedElements());
		pstmt.setInt(++column, element.getNumberOfDeletedElements());
		pstmt.setInt(++column, element.getNumberOfCoChangedElements());
		pstmt.setString(++column,
				StringUtils.convertListToString(element.getCodeFragmentLinks()));
	}
}
