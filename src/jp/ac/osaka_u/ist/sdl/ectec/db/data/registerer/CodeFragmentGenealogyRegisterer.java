package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;

/**
 * A class that represents a registerer for genealogies of code fragments
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentGenealogyRegisterer extends
		AbstractNonuniqueElementRegisterer<DBCodeFragmentGenealogyInfo> {

	public CodeFragmentGenealogyRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

	@Override
	protected String createPreparedStatementQueue() {
		return "insert into CODE_FRAGMENT_GENEALOGY values (?,?,?,?,?)";
	}

	@Override
	protected int makePreparedStatements(PreparedStatement pstmt,
			DBCodeFragmentGenealogyInfo element) throws SQLException {
		final long elementId = element.getId();
		final long startCombinedRevisionId = element
				.getStartCombinedRevisionId();
		final long endCombinedRevisionId = element.getEndCombinedRevisionId();
		final Collection<Long> fragmentIds = element.getElements();
		final Collection<Long> fragmentLinks = element.getLinks();

		for (final long fragmentId : fragmentIds) {
			for (final long fragmentLink : fragmentLinks) {
				int column = 0;
				pstmt.setLong(++column, elementId);
				pstmt.setLong(++column, startCombinedRevisionId);
				pstmt.setLong(++column, endCombinedRevisionId);
				pstmt.setLong(++column, fragmentId);
				pstmt.setLong(++column, fragmentLink);

				pstmt.addBatch();
			}
		}

		return fragmentIds.size() * fragmentLinks.size();
	}

}
