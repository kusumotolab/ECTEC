package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;

/**
 * A class that represents a registerer for clone genealogies
 * 
 * @author k-hotta
 * 
 */
public class CloneGenealogyRegisterer extends
		AbstractNonuniqueElementRegisterer<DBCloneGenealogyInfo> {

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
		return "insert into CLONE_GENEALOGY values(?,?,?,?,?)";
	}

	@Override
	protected int makePreparedStatements(PreparedStatement pstmt,
			DBCloneGenealogyInfo element) throws SQLException {
		final long elementId = element.getId();
		final long startCombinedRevisionId = element.getStartCombinedRevisionId();
		final long endCombinedRevisionId = element.getEndCombinedRevisionId();
		final Collection<Long> cloneSetIds = element.getElements();
		final Collection<Long> cloneSetLinks = element.getLinks();
		
		for (final long cloneSetId : cloneSetIds) {
			for (final long cloneSetLink : cloneSetLinks) {
				int column = 0;
				pstmt.setLong(++column, elementId);
				pstmt.setLong(++column, startCombinedRevisionId);
				pstmt.setLong(++column, endCombinedRevisionId);
				pstmt.setLong(++column, cloneSetId);
				pstmt.setLong(++column, cloneSetLink);
				
				pstmt.addBatch();
			}
		}
		
		return cloneSetIds.size() * cloneSetLinks.size();
	}

}
