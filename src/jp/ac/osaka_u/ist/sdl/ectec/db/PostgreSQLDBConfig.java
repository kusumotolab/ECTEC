package jp.ac.osaka_u.ist.sdl.ectec.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class PostgreSQLDBConfig implements IDBConfig {

	// 決まり文句 (ドライバクラス)
	// 下記の変数を正しく設定する
	// DBNAME, DBDIR, USER, PASS, JDBCDriver, DBURL

	// PostgreSQL 用デフォルト
	// Eclipse で PostgreSQL を使いたいときは，次の手順で，WebContent\WEB-INF\lib にインポートしておく．
	// WebContent\WEB-INF\lib を右クリック．「一般」→「ファイルシステム」
	// その後インポートすべきファイルとして，次のファイルを指定
	// C:\Program Files\psqlJDBC\postgresql-8.3-603.jdbc4.jar
	private static final String DRIVER = "org.postgresql.Driver";

	private static final String HEADER = "jdbc:postgresql://localhost/";

	private final String dbPath;

	private final String userName;

	private final String passwd;

	public PostgreSQLDBConfig(final String dbPath, final String userName,
			final String passwd) {
		this.dbPath = dbPath;
		this.userName = (userName == null) ? "" : userName;
		this.passwd = (passwd == null) ? "" : passwd;
	}

	@Override
	public Connection init() throws Exception {
		Class.forName(DRIVER).newInstance();
		// System.setProperty("jdbc.driver", DRIVER);

		if (userName.isEmpty() && passwd.isEmpty()) {
			return DriverManager.getConnection(HEADER + dbPath);
		} else {
			final Properties info = new Properties();
			info.put("user", userName);
			info.put("password", passwd);

			return DriverManager.getConnection(HEADER + dbPath, info);
		}
	}

}
