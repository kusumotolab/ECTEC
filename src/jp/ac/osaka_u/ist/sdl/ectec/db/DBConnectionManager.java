package jp.ac.osaka_u.ist.sdl.ectec.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CRDRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CloneGenealogyRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CloneSetLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CloneSetRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CodeFragmentLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CodeFragmentRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.FileRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.RevisionRegisterer;

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

	private final RevisionRegisterer revisionRegisterer;

	private final FileRegisterer fileRegisterer;

	private final CodeFragmentRegisterer fragmentRegisterer;

	private final CloneSetRegisterer cloneRegisterer;

	private final CodeFragmentLinkRegisterer fragmentLinkRegisterer;

	private final CloneSetLinkRegisterer cloneLinkRegisterer;

	private final CloneGenealogyRegisterer cloneGenealogyRegisterer;

	private final CRDRegisterer crdRegisterer;

	/**
	 * the constructor
	 * 
	 * @param dbPath
	 * @throws Exception
	 */
	public DBConnectionManager(final String dbPath, final int maxBatchCount)
			throws Exception {
		Class.forName("org.sqlite.JDBC");
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
		this.connection.setAutoCommit(false);
		this.revisionRegisterer = new RevisionRegisterer(this, maxBatchCount);
		this.fileRegisterer = new FileRegisterer(this, maxBatchCount);
		this.fragmentRegisterer = new CodeFragmentRegisterer(this,
				maxBatchCount);
		this.cloneRegisterer = new CloneSetRegisterer(this, maxBatchCount);
		this.fragmentLinkRegisterer = new CodeFragmentLinkRegisterer(this,
				maxBatchCount);
		this.cloneLinkRegisterer = new CloneSetLinkRegisterer(this,
				maxBatchCount);
		this.cloneGenealogyRegisterer = new CloneGenealogyRegisterer(this,
				maxBatchCount);
		this.crdRegisterer = new CRDRegisterer(this, maxBatchCount);
	}

	public final RevisionRegisterer getRevisionRegisterer() {
		return revisionRegisterer;
	}

	public final FileRegisterer getFileRegisterer() {
		return fileRegisterer;
	}

	public final CodeFragmentRegisterer getFragmentRegisterer() {
		return fragmentRegisterer;
	}

	public final CloneSetRegisterer getCloneRegisterer() {
		return cloneRegisterer;
	}

	public final CodeFragmentLinkRegisterer getFragmentLinkRegisterer() {
		return fragmentLinkRegisterer;
	}

	public final CloneSetLinkRegisterer getCloneLinkRegisterer() {
		return cloneLinkRegisterer;
	}

	public final CloneGenealogyRegisterer getCloneGenealogyRegisterer() {
		return cloneGenealogyRegisterer;
	}

	public final CRDRegisterer getCrdRegisterer() {
		return crdRegisterer;
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
