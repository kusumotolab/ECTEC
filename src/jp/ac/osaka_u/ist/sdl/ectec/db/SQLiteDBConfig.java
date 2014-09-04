package jp.ac.osaka_u.ist.sdl.ectec.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLiteDBConfig implements IDBConfig {

	private final static String DRIVER = "org.sqlite.JDBC";

	private final static String HEADER = "jdbc:sqlite:";

	private final String dbPath;

	public SQLiteDBConfig(final String dbPath) {
		this.dbPath = dbPath;
	}

	@Override
	public Connection init() throws Exception {
		Class.forName(DRIVER);
		return DriverManager.getConnection(HEADER + dbPath);
	}

}
