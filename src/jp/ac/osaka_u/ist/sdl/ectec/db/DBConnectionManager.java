package jp.ac.osaka_u.ist.sdl.ectec.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.IDManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CRDRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CloneGenealogyRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CloneSetLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CloneSetRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentGenealogyRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CombinedCommitRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CombinedRevisionRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CommitRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.FileRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.RepositoryRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.RevisionRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CRDRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneGenealogyElementRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneGenealogyLinkElementRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneGenealogyRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetLinkFragmentLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentGenealogyElementRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentGenealogyLinkElementRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentGenealogyRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CombinedCommitRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CombinedRevisionRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CommitRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.FileRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.RepositoryRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.RevisionRetriever;

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

	private final RepositoryRegisterer repositoryRegisterer;

	private final RevisionRegisterer revisionRegisterer;

	private final CommitRegisterer commitRegisterer;

	private final CombinedRevisionRegisterer combinedRevisionRegisterer;

	private final CombinedCommitRegisterer combinedCommitRegisterer;

	private final FileRegisterer fileRegisterer;

	private final CodeFragmentRegisterer fragmentRegisterer;

	private final CloneSetRegisterer cloneRegisterer;

	private final CodeFragmentLinkRegisterer fragmentLinkRegisterer;

	private final CloneSetLinkRegisterer cloneLinkRegisterer;

	private final CloneGenealogyRegisterer cloneGenealogyRegisterer;

	private final CodeFragmentGenealogyRegisterer fragmentGenealogyRegisterer;

	private final CRDRegisterer crdRegisterer;

	private final RepositoryRetriever repositoryRetriever;

	private final RevisionRetriever revisionRetriever;

	private final CommitRetriever commitRetriever;

	private final CombinedRevisionRetriever combinedRevisionRetriever;

	private final CombinedCommitRetriever combinedCommitRetriever;

	private final FileRetriever fileRetriever;

	private final CodeFragmentRetriever fragmentRetriever;

	private final CloneSetRetriever cloneRetriever;

	private final CodeFragmentLinkRetriever fragmentLinkRetriever;

	private final CloneSetLinkRetriever cloneLinkRetriever;

	private final CloneGenealogyRetriever cloneGenealogyRetriever;

	private final CodeFragmentGenealogyRetriever fragmentGenealogyRetriever;

	private final CRDRetriever crdRetriever;

	private final CloneGenealogyElementRetriever cloneGenealogyElementRetriever;

	private final CloneGenealogyLinkElementRetriever cloneGenealogyLinkElementRetriever;

	private final CloneSetLinkFragmentLinkRetriever cloneLinkFragmentLinkRetriever;

	private final CodeFragmentGenealogyElementRetriever fragmentGenealogyElementRetriever;

	private final CodeFragmentGenealogyLinkElementRetriever fragmentGenealogyLinkElementRetriever;

	// 決まり文句 (ドライバクラス)
		final private static String PostgresJDBCDriver = "org.postgresql.Driver";

		// 下記の変数を正しく設定する
		// DBNAME, DBDIR, USER, PASS, JDBCDriver, DBURL


			// PostgreSQL 用デフォルト
			// Eclipse で PostgreSQL を使いたいときは，次の手順で，WebContent\WEB-INF\lib にインポートしておく．
			//     WebContent\WEB-INF\lib を右クリック．「一般」→「ファイルシステム」
			//     その後インポートすべきファイルとして，次のファイルを指定
			//       C:\Program Files\psqlJDBC\postgresql-8.3-603.jdbc4.jar
			final private static String JDBCDriver = PostgresJDBCDriver;
			final private static String user = "sa";
			final private static String pass = "";

	/**
	 * the constructor
	 *
	 * @param dbPath
	 * @throws Exception
	 */
	public DBConnectionManager(final String dbPath, final int maxBatchCount)
			throws Exception {
		//Class.forName("org.sqlite.JDBC");
		//this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
		try {
			// JDBC Driver Loading
			Class.forName(JDBCDriver).newInstance();
			System.setProperty("jdbc.driver",JDBCDriver);
		}

		catch (Exception e) {
			// Error Message and Error Code
			System.out.print(e.toString());
			if (e instanceof SQLException) {
				System.out.println("Error Code:" + ((SQLException)e).getErrorCode());
			}
			// Print Stack Trace
			e.printStackTrace();
		}
		try {
			// Connection
			if ( user.isEmpty() && pass.isEmpty() ) {
				this.connection = DriverManager.getConnection("jdbc:postgresql://localhost/" + dbPath);
			}
			else {
				Properties prop = new Properties();
				prop.put("user", user);
				prop.put("password", pass);
				this.connection = DriverManager.getConnection("jdbc:postgresql://localhost/" + dbPath,prop);
			}
		}
		catch (Exception e) {
			// Error Message and Error Code
			System.out.print(e.toString());
			if (e instanceof SQLException) {
				System.out.println("Error Code:" + ((SQLException)e).getErrorCode());
			}
			// Print Stack Trace
			e.printStackTrace();
			if (this.connection != null) {
				this.connection.rollback();
				this.connection.close();
			}
		}


		this.connection.setAutoCommit(false);

		this.cloneGenealogyElementRetriever = new CloneGenealogyElementRetriever(
				this);
		this.cloneGenealogyLinkElementRetriever = new CloneGenealogyLinkElementRetriever(
				this);
		this.cloneLinkFragmentLinkRetriever = new CloneSetLinkFragmentLinkRetriever(
				this);
		this.fragmentGenealogyElementRetriever = new CodeFragmentGenealogyElementRetriever(
				this);
		this.fragmentGenealogyLinkElementRetriever = new CodeFragmentGenealogyLinkElementRetriever(
				this);

		this.repositoryRegisterer = new RepositoryRegisterer(this,
				maxBatchCount);
		this.revisionRegisterer = new RevisionRegisterer(this, maxBatchCount);
		this.commitRegisterer = new CommitRegisterer(this, maxBatchCount);
		this.combinedRevisionRegisterer = new CombinedRevisionRegisterer(this,
				maxBatchCount);
		this.combinedCommitRegisterer = new CombinedCommitRegisterer(this,
				maxBatchCount);
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
		this.fragmentGenealogyRegisterer = new CodeFragmentGenealogyRegisterer(
				this, maxBatchCount);
		this.crdRegisterer = new CRDRegisterer(this, maxBatchCount);
		this.repositoryRetriever = new RepositoryRetriever(this);
		this.revisionRetriever = new RevisionRetriever(this);
		this.commitRetriever = new CommitRetriever(this);
		this.combinedRevisionRetriever = new CombinedRevisionRetriever(this);
		this.combinedCommitRetriever = new CombinedCommitRetriever(this);
		this.fileRetriever = new FileRetriever(this);
		this.fragmentRetriever = new CodeFragmentRetriever(this);
		this.cloneRetriever = new CloneSetRetriever(this);
		this.fragmentLinkRetriever = new CodeFragmentLinkRetriever(this);
		this.cloneLinkRetriever = new CloneSetLinkRetriever(this);
		this.cloneGenealogyRetriever = new CloneGenealogyRetriever(this);
		this.fragmentGenealogyRetriever = new CodeFragmentGenealogyRetriever(
				this);
		this.crdRetriever = new CRDRetriever(this);
	}

	public final RepositoryRegisterer getRepositoryRegisterer() {
		return repositoryRegisterer;
	}

	public final RevisionRegisterer getRevisionRegisterer() {
		return revisionRegisterer;
	}

	public final CommitRegisterer getCommitRegisterer() {
		return commitRegisterer;
	}

	public final CombinedRevisionRegisterer getCombinedRevisionRegisterer() {
		return combinedRevisionRegisterer;
	}

	public final CombinedCommitRegisterer getCombinedCommitRegisterer() {
		return combinedCommitRegisterer;
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

	public final CodeFragmentGenealogyRegisterer getFragmentGenealogyRegisterer() {
		return fragmentGenealogyRegisterer;
	}

	public final CRDRegisterer getCrdRegisterer() {
		return crdRegisterer;
	}

	public final RepositoryRetriever getRepositoryRetriever() {
		return repositoryRetriever;
	}

	public final RevisionRetriever getRevisionRetriever() {
		return revisionRetriever;
	}

	public final CommitRetriever getCommitRetriever() {
		return commitRetriever;
	}

	public final CombinedRevisionRetriever getCombinedRevisionRetriever() {
		return combinedRevisionRetriever;
	}

	public final CombinedCommitRetriever getCombinedCommitRetriever() {
		return combinedCommitRetriever;
	}

	public final FileRetriever getFileRetriever() {
		return fileRetriever;
	}

	public final CodeFragmentRetriever getFragmentRetriever() {
		return fragmentRetriever;
	}

	public final CloneSetRetriever getCloneRetriever() {
		return cloneRetriever;
	}

	public final CodeFragmentLinkRetriever getFragmentLinkRetriever() {
		return fragmentLinkRetriever;
	}

	public final CloneSetLinkRetriever getCloneLinkRetriever() {
		return cloneLinkRetriever;
	}

	public final CloneGenealogyRetriever getCloneGenealogyRetriever() {
		return cloneGenealogyRetriever;
	}

	public final CodeFragmentGenealogyRetriever getFragmentGenealogyRetriever() {
		return fragmentGenealogyRetriever;
	}

	public final CRDRetriever getCrdRetriever() {
		return crdRetriever;
	}

	public final CloneGenealogyElementRetriever getCloneGenealogyElementRetriever() {
		return cloneGenealogyElementRetriever;
	}

	public final CloneGenealogyLinkElementRetriever getCloneGenealogyLinkElementRetriever() {
		return cloneGenealogyLinkElementRetriever;
	}

	public final CloneSetLinkFragmentLinkRetriever getCloneLinkFragmentLinkRetriever() {
		return cloneLinkFragmentLinkRetriever;
	}

	public final CodeFragmentGenealogyElementRetriever getFragmentGenealogyElementRetriever() {
		return fragmentGenealogyElementRetriever;
	}

	public final CodeFragmentGenealogyLinkElementRetriever getFragmentGenealogyLinkElementRetriever() {
		return fragmentGenealogyLinkElementRetriever;
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

	public void initializeElementCounters(final short header) throws Exception {
		if (header < 0) {
			initializeElementCountersWithMaximumIds();
		} else {
			initializeElementCountersWithHeader(header);
		}
	}

	public void initializeElementCountersWithMaximumIds() throws Exception {
		final long maxRepository = repositoryRetriever.getMaximumId();
		DBRepositoryInfo.resetCount(maxRepository + 1);

		final long maxRevision = revisionRetriever.getMaximumId();
		DBRevisionInfo.resetCount(maxRevision + 1);

		final long maxCommit = commitRetriever.getMaximumId();
		DBCommitInfo.resetCount(maxCommit + 1);

		final long maxCombinedRevision = combinedRevisionRetriever
				.getMaximumId();
		DBCombinedRevisionInfo.resetCount(maxCombinedRevision + 1);

		final long maxCombinedCommit = combinedCommitRetriever.getMaximumId();
		DBCombinedCommitInfo.resetCount(maxCombinedCommit + 1);

		final long maxFile = fileRetriever.getMaximumId();
		DBFileInfo.resetCount(maxFile + 1);

		final long maxCrd = crdRetriever.getMaximumId();
		DBCrdInfo.resetCount(maxCrd + 1);

		final long maxFragment = fragmentRetriever.getMaximumId();
		DBCodeFragmentInfo.resetCount(maxFragment + 1);

		final long maxClone = cloneRetriever.getMaximumId();
		DBCloneSetInfo.resetCount(maxClone + 1);

		final long maxFragmentLink = fragmentLinkRetriever.getMaximumId();
		DBCodeFragmentLinkInfo.resetCount(maxFragmentLink + 1);

		final long maxCloneLink = cloneLinkRetriever.getMaximumId();
		DBCloneSetLinkInfo.resetCount(maxCloneLink + 1);

		final long maxCloneGenealogy = cloneGenealogyRetriever.getMaximumId();
		DBCloneGenealogyInfo.resetCount(maxCloneGenealogy + 1);

		final long maxFragmentGenealogy = fragmentGenealogyRetriever
				.getMaximumId();
		DBCodeFragmentGenealogyInfo.resetCount(maxFragmentGenealogy + 1);
	}

	public void initializeElementCountersWithHeader(final short header) {
		final long minimumId = IDManager.issuanceMinimumId(header);

		DBRepositoryInfo.resetCount(minimumId);
		DBRevisionInfo.resetCount(minimumId);
		DBCommitInfo.resetCount(minimumId);
		DBCombinedRevisionInfo.resetCount(minimumId);
		DBCombinedCommitInfo.resetCount(minimumId);
		DBFileInfo.resetCount(minimumId);
		DBCrdInfo.resetCount(minimumId);
		DBCodeFragmentInfo.resetCount(minimumId);
		DBCloneSetInfo.resetCount(minimumId);
		DBCodeFragmentLinkInfo.resetCount(minimumId);
		DBCloneSetLinkInfo.resetCount(minimumId);
		DBCloneGenealogyInfo.resetCount(minimumId);
		DBCodeFragmentGenealogyInfo.resetCount(minimumId);
	}

}
