package jp.ac.osaka_u.ist.sdl.ectec.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A class to manage the connection between the db
 * 
 * @author k-hotta
 * 
 */
public final class DBConnectionManager {

	/**
	 * the singleton object
	 */
	private static DBConnectionManager SINGLETON = null;

	/**
	 * the connection between the db
	 */
	private Connection connection;

	/**
	 * the private constructor
	 * 
	 * @param dbPath
	 * @throws Exception
	 */
	private DBConnectionManager(final String dbPath) throws Exception {
		Class.forName("org.sqlite.JDBC");
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
		this.connection.setAutoCommit(false);
	}

	/**
	 * initialize this manager <br>
	 * this method must be called before any operations for this manager <br>
	 * this method works only at once when it is called in the first time <br>
	 * this method will do nothing after the second call or later
	 * 
	 * @param dbPath
	 * @throws Exception
	 */
	public static void createInstance(final String dbPath) throws Exception {
		if (SINGLETON == null) {
			SINGLETON = new DBConnectionManager(dbPath);
		}
	}

	/**
	 * get the instance <br>
	 * this method must be called after this manager is initialized by calling
	 * {@link DBConnectionManager#createInstance(String) createInstance(String)}
	 * 
	 * @return
	 */
	public static DBConnectionManager getInstance() {
		if (SINGLETON == null) {
			System.err.println("[Warning] db connection is not initialized");
		}
		return SINGLETON;
	}

	/**
	 * close the connection
	 */
	public void close() {
		try {
			this.connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * create a statement
	 * 
	 * @return
	 */
	public Statement createStatement() {
		Statement result = null;
		try {
			result = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * create a prepared statement with the specified query
	 * 
	 * @param query
	 * @return
	 */
	public PreparedStatement createPreparedStatement(String query) {
		PreparedStatement result = null;
		try {
			result = connection.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * do commit
	 */
	public synchronized void commit() {
		try {
			connection.commit();
		} catch (SQLException e1) {
			try {
				connection.rollback();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			e1.printStackTrace();
		}
	}

	/**
	 * enables/disables auto commit
	 * 
	 * @param autoCommit
	 */
	public void setAutoCommit(boolean autoCommit) {
		try {
			connection.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * execute the query
	 * 
	 * @param query
	 * @throws Exception
	 */
	public void executeUpdate(final String query) throws Exception {
		final Statement stmt = createStatement();
		stmt.executeUpdate(query);
		stmt.close();

	}

}
