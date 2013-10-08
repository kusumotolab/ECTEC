package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;

/**
 * A class for retrieving clone genealogies
 * 
 * @author k-hotta
 * 
 */
public class CloneGenealogyRetriever extends
		RangedElementRetriever<DBCloneGenealogyInfo> {

	public CloneGenealogyRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBCloneGenealogyInfo createElement(ResultSet rs)
			throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long startRevisionId = rs.getLong(++column);
		final long endRevisionId = rs.getLong(++column);
		final String clonesStr = rs.getString(++column);
		final String cloneLinksStr = rs.getString(++column);
		final int numberOfChanges = rs.getInt(++column);
		final int numberOfAdditions = rs.getInt(++column);
		final int numberOfDeletions = rs.getInt(++column);
		final boolean dead = (rs.getInt(++column) == 1) ? true : false;

		final List<Long> clones = new ArrayList<Long>();
		StringUtils.convertStringToCollection(clones, clonesStr);
		final List<Long> cloneLinks = new ArrayList<Long>();
		StringUtils.convertStringToCollection(cloneLinks, cloneLinksStr);

		return new DBCloneGenealogyInfo(id, startRevisionId, endRevisionId,
				clones, cloneLinks, numberOfChanges, numberOfAdditions,
				numberOfDeletions, dead);
	}

	@Override
	protected String getStartRevisionIdColumnName() {
		return "START_REVISION_ID";
	}

	@Override
	protected String getEndRevisionIdColumnName() {
		return "END_REVISION_ID";
	}

	@Override
	protected String getTableName() {
		return "CLONE_GENEALOGY";
	}

	@Override
	protected String getIdColumnName() {
		return "CLONE_GENEALOGY_ID";
	}

}
