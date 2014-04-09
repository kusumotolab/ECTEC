package jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever;

import java.sql.ResultSet;
import java.sql.SQLException;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;

/**
 * A class for retrieving repositories
 * 
 * @author k-hotta
 * 
 */
public class RepositoryRetriever extends
		AbstractUniqueElementRetriever<DBRepositoryInfo> {

	public RepositoryRetriever(final DBConnectionManager dbManager) {
		super(dbManager);
	}

	@Override
	protected DBRepositoryInfo createElement(ResultSet rs) throws SQLException {
		int column = 0;
		final long id = rs.getLong(++column);
		final String name = rs.getString(++column);
		final String url = rs.getString(++column);

		return new DBRepositoryInfo(id, name, url);
	}

	@Override
	protected String getTableName() {
		return "REPOSITORY";
	}

	@Override
	protected String getIdColumnName() {
		return "REPOSITORY_ID";
	}

}
