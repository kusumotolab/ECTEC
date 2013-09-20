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
	 * the connection between the db
	 */
	private Connection connection;

	/**
	 * the constructor
	 * 
	 * @param dbPath
	 * @throws Exception
	 */
	public DBConnectionManager(final String dbPath) throws Exception {
		Class.forName("org.sqlite.JDBC");
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
		this.connection.setAutoCommit(false);
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
