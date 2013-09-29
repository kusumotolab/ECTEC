package jp.ac.osaka_u.ist.sdl.ectec.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;

/**
 * A class that represents a registerer for genealogies of code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentGenealogyRegisterer extends
		AbstractElementRegisterer<CodeFragmentGenealogyInfo> {

	public CodeFragmentGenealogyRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into CODE_FRAGMENT_GENEALOGY values (?,?,?,?,?)";
	}

	@Override
	protected void setAttributes(PreparedStatement pstmt,
			CodeFragmentGenealogyInfo element) throws SQLException {
		int column = 0;
		pstmt.setLong(++column, element.getId());
		pstmt.setLong(++column, element.getStartRevisionId());
		pstmt.setLong(++column, element.getEndRevisionId());
		pstmt.setString(++column,
				StringUtils.convertListToString(element.getElements()));
		pstmt.setString(++column,
				StringUtils.convertListToString(element.getLinks()));
	}
}
