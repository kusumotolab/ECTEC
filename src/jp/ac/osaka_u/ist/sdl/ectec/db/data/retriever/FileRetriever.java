package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;

/**
 * A class for retrieving files from db
 * 
 * @author k-hotta
 * 
 */
public class FileRetriever extends AbstractUniqueElementRetriever<DBFileInfo> {

	public FileRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBFileInfo createElement(ResultSet rs) throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final long repositoryId = rs.getLong(++column);
		final String path = rs.getString(++column);
		final long startCombinedRevisionId = rs.getLong(++column);
		final long endCombinedRevisionId = rs.getLong(++column);
		final int addedAtStartInt = rs.getInt(++column);
		final int deletedAtEndInt = rs.getInt(++column);

		final boolean addedAtStart = (addedAtStartInt == 1);
		final boolean deletedAtEnd = (deletedAtEndInt == 1);

		return new DBFileInfo(id, repositoryId, path, startCombinedRevisionId,
				endCombinedRevisionId, addedAtStart, deletedAtEnd);
	}

	protected String getStartRevisionIdColumnName() {
		return "START_COMBINED_REVISION_ID";
	}

	protected String getEndRevisionIdColumnName() {
		return "END_COMBINED_REVISION_ID";
	}

	@Override
	protected String getTableName() {
		return "FILE";
	}

	@Override
	protected String getIdColumnName() {
		return "FILE_ID";
	}

	/**
	 * retrieve elements that exist in the specified revision
	 * 
	 * @param revisionId
	 * @return
	 * @throws SQLException
	 */
	public synchronized SortedMap<Long, DBFileInfo> retrieveElementsInSpecifiedRevision(
			final long revisionId) throws SQLException {
		final String query = "select * from " + getTableName() + " where "
				+ getStartRevisionIdColumnName() + " <= " + revisionId
				+ " AND " + getEndRevisionIdColumnName() + " >= " + revisionId;

		return retrieve(query);
	}

}
