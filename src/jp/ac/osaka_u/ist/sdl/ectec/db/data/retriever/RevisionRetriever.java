package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;

/**
 * A class for retrieving revisions from db
 * 
 * @author k-hotta
 * 
 */
public class RevisionRetriever extends AbstractUniqueElementRetriever<DBRevisionInfo> {

	public RevisionRetriever(DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBRevisionInfo createElement(ResultSet rs) throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final String identifier = rs.getString(++column);
		final long repositoryId = rs.getLong(++column);

		return new DBRevisionInfo(id, identifier, repositoryId);
	}

	@Override
	protected String getTableName() {
		return "REVISION";
	}

	@Override
	protected String getIdColumnName() {
		return "REVISION_ID";
	}

	private String getIdentifierColumnName() {
		return "REVISION_IDENTIFIER";
	}

	/**
	 * retrieve elements having the given identifier
	 * 
	 * @param identifier
	 * @return
	 * @throws SQLException
	 */
	public SortedMap<Long, DBRevisionInfo> retrieveWithIdentifier(
			final String identifier) throws SQLException {
		final StringBuilder builder = new StringBuilder();
		builder.append("select * from " + getTableName() + " where "
				+ getIdentifierColumnName() + " = '" + identifier + "'");
		return retrieve(builder.toString());
	}
}
