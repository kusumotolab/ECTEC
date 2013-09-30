package jp.ac.osaka_u.ist.sdl.ectec.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;

/**
 * A class that represents a registerer for crd
 * 
 * @author k-hotta
 * 
 */
public class CRDRegisterer extends AbstractElementRegisterer<CRD> {

	/**
	 * the constructor
	 * 
	 * @param dbManager
	 * @param maxBatchCount
	 */
	public CRDRegisterer(DBConnectionManager dbManager, int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into CRD values(?,?,?,?,?,?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt, CRD element)
			throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setString(++column, element.getType().toString());
		pstmt.setString(++column, element.getHead());
		pstmt.setString(++column, element.getAnchor());
		pstmt.setString(++column, element.getNormalizedAnchor());
		pstmt.setInt(++column, element.getCm());
		pstmt.setString(++column,
				StringUtils.convertListToString(element.getAncestors()));
		pstmt.setString(++column, element.getFullText());
	}

}
