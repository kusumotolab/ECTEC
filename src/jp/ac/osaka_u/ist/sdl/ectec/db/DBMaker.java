package jp.ac.osaka_u.ist.sdl.ectec.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;

import org.apache.log4j.Logger;

/**
 * A class for create
 *
 * @author k-hotta
 *
 */
public class DBMaker {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager.getLogger(DBMaker.class
			.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * the manager for the connection between the db
	 */
	private final DBConnectionManager dbManager;

	/**
	 * the constructor <br>
	 * DBConnectionManager must be initialized before calling this constructor
	 */
	public DBMaker(final DBConnectionManager dbManager) {
		this.dbManager = dbManager;
	}

	public static void main(String[] args) {
		try {
			final DBMakerSettings settings = loadSettings(args);
			final DBMaker maker = preprocess(settings);
			maker.makeDb(settings.isOverwrite());
			logger.info("operations have finished.");
		} catch (Exception e) {
			eLogger.fatal("operations failed.\n" + e.toString());
			e.printStackTrace();
		}
	}

	private static DBMakerSettings loadSettings(final String[] args)
			throws Exception {
		final DBMakerSettings settings = new DBMakerSettings();
		settings.load(args, false);
		return settings;
	}

	private static DBMaker preprocess(final DBMakerSettings settings)
			throws Exception {
		final DBConnectionManager dbManager = new DBConnectionManager(
				settings.getDBConfig(), settings.getMaxBatchCount());
		logger.info("connected to the db");

		return new DBMaker(dbManager);
	}

	/**
	 * make the db
	 *
	 * @param overwrite
	 *            if true, the existing db is overwritten by the new one (the
	 *            existing db will be deleted)
	 * @throws Exception
	 */
	public void makeDb(final boolean overwrite) throws Exception {
		try {
			dbManager.setAutoCommit(true);

			if (!overwrite) {
				logger.info("overwriting is prohibited");
				logger.info("checking whether the db exists");
				final boolean exists = isDBExists();
				if (exists) {
					eLogger.warn("the db has already existed! DBMaker will do nothing.");
					return;
				} else {
					logger.info("confirmed that the db does not exist");
				}
			}

			logger.info("dropping the existing tables if exist");
			dropTables();

			logger.info("dropping the existing indexes if exist");
			dropIndexes();

			logger.info("creating new tables if not exist");
			createNewTables();

			logger.info("creating indexes");
			createIndexes();

			dbManager.setAutoCommit(false);
		} catch (Exception e) {
			dbManager.rollback();
			throw e;
		} finally {
			if (this.dbManager != null) {
				dbManager.close();
			}
		}
	}

	public boolean isDBExists() {
		Statement stmt = null;
		try {
			stmt = dbManager.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM REPOSITORY");
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * create new tables
	 *
	 * @throws Exception
	 */
	public void createNewTables() throws Exception {
		dbManager.executeUpdate(getRepositoryTableQuery());
		dbManager.executeUpdate(getRevisionTableQuery());
		dbManager.executeUpdate(getCommitTableQuery());
		dbManager.executeUpdate(getCombinedRevisionTableQuery());
		dbManager.executeUpdate(getCombinedCommitTableQuery());
		dbManager.executeUpdate(getFileTableQuery());
		dbManager.executeUpdate(getCrdQuery());
		dbManager.executeUpdate(getCodeFragmentTableQuery());
		dbManager.executeUpdate(getCloneSetTableQuery());
		dbManager.executeUpdate(getCodeFragmentLinkTableQuery());
		dbManager.executeUpdate(getCloneSetLinkTableQuery());
		dbManager.executeUpdate(getCloneSetLinkFragmentLinkTableQuery());
		dbManager.executeUpdate(getCloneGenealogyTableQuery());
		dbManager.executeUpdate(getCloneGenealogyEelementTableQuery());
		dbManager.executeUpdate(getCloneGenealogyLinkTableQuery());
		dbManager.executeUpdate(getCodeFragmentGenealogyTableQuery());
		dbManager.executeUpdate(getCodeFragmentGenealogyElementTableQuery());
		dbManager.executeUpdate(getCodeFragmentGenealogyLinkTableQuery());
	}

	/**
	 * drop all the tables and vacuum the db
	 */
	public void dropTables() {
		try {
			dbManager.executeUpdate("DROP TABLE IF EXISTS REVISION");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE IF EXISTS VCS_COMMIT");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE IF EXISTS FILE");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE IF EXISTS CODE_FRAGMENT");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE IF EXISTS CLONE_SET");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE IF EXISTS CODE_FRAGMENT_LINK");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE IF EXISTS CLONE_SET_LINK");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager
					.executeUpdate("DROP TABLE IF EXISTS CLONE_SET_LINK_FRAGMENT_LINK");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE IF EXISTS CLONE_GENEALOGY");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager
					.executeUpdate("DROP TABLE IF EXISTS CLONE_GENEALOGY_ELEMENT");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager
					.executeUpdate("DROP TABLE IF EXISTS CLONE_GENEALOGY_LINK");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager
					.executeUpdate("DROP TABLE IF EXISTS CODE_FRAGMENT_GENEALOGY");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager
					.executeUpdate("DROP TABLE IF EXISTS CODE_FRAGMENT_GENEALOGY_ELEMENT");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager
					.executeUpdate("DROP TABLE IF EXISTS CODE_FRAGMENT_GENEALOGY_LINK");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE IF EXISTS CRD");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			// dbManager.executeUpdate("VACUUM");
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public void createIndexes() throws Exception {
		createRepositoryTableIndexes();
		createRevisionTableIndexes();
		createCommitTableIndexes();
		createCombinedRevisionTableIndexes();
		createCombinedCommitTableIndexes();
		createFileTableIndexes();
		createCrdTableIndexes();
		createCodeFragmentTableIndexes();
		createCloneSetTableIndexes();
		createCodeFragmentLinkTableIndexes();
		createCloneSetLinkTableIndexes();
		createCloneSetLinkFragmentLinkTableIndexes();
		createCloneGenealogyTableIndexes();
		createCloneGenealogyElementTableIndexes();
		createCloneGenealogyLinkTableIndexes();
		createCodeFragmentGenealogyTableIndexes();
		createCodeFragmentGenealogyElementTableIndexes();
		createCodeFragmentGenealogyLinkTableIndexes();
	}

	public void dropIndexes() throws Exception {
		try {
			dropRepositoryTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropRevisionTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCommitTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCombinedRevisionTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCombinedCommitTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropFileTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCrdTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCodeFragmentTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCloneSetTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCodeFragmentLinkTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCloneSetLinkTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCloneSetLinkFragmentLinkTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCloneGenealogyTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCloneGenealogyElementTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCloneGenealogyLinkTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCodeFragmentGenealogyTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCodeFragmentGenealogyElementTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dropCodeFragmentGenealogyLinkTableIndexes();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * definitions of each table follow
	 */

	/**
	 * get the query to create the repository table
	 *
	 * @return
	 */
	private String getRepositoryTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists REPOSITORY(");
		builder.append("REPOSITORY_ID BIGINT PRIMARY KEY,");
		builder.append("REPOSITORY_NAME TEXT UNIQUE,");
		builder.append("REPOSITORY_ROOT_URL TEXT UNIQUE,");
		builder.append("REPOSITORY_ADDITIONAL_URL TEXT,");
		builder.append("VCS TEXT,");
		builder.append("USER_NAME TEXT,");
		builder.append("PASSWORD TEXT");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the repository table
	 *
	 * @throws Exception
	 */
	private void createRepositoryTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index REPOSITORY_ID_INDEX_REPOSITORY on REPOSITORY(REPOSITORY_ID)");
	}

	/**
	 * drop indexes on the repository table
	 *
	 * @throws Exception
	 */
	private void dropRepositoryTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists REPOSITORY_ID_INDEX_REPOSITORY");
	}

	/**
	 * get the query to create the revision table
	 *
	 * @return
	 */
	private String getRevisionTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists REVISION(");
		builder.append("REVISION_ID BIGINT PRIMARY KEY,");
		builder.append("REVISION_IDENTIFIER TEXT,");
		builder.append("REPOSITORY_ID BIGINT,");
		builder.append("FOREIGN KEY(REPOSITORY_ID) REFERENCES REPOSITORY(REPOSITORY_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the revision table
	 *
	 * @throws Exception
	 */
	private void createRevisionTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index REVISION_ID_INDEX_REVISION on REVISION(REVISION_ID)");
		dbManager
				.executeUpdate("create index REPOSITORY_ID_INDEX_REVISION on REVISION(REPOSITORY_ID)");
	}

	/**
	 * drop indexes on the revision table
	 *
	 * @throws Exception
	 */
	private void dropRevisionTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists REVISION_ID_INDEX_REVISION");
		dbManager
				.executeUpdate("drop index if exists REPOSITORY_ID_INDEX_REVISION");
	}

	/**
	 * get the query to create the commit table
	 *
	 * @return
	 */
	private String getCommitTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists VCS_COMMIT(");
		builder.append("VCS_COMMIT_ID BIGINT PRIMARY KEY,");
		builder.append("REPOSITORY_ID BIGINT,");
		builder.append("BEFORE_REVISION_ID BIGINT,");
		builder.append("AFTER_REVISION_ID BIGINT,");
		builder.append("BEFORE_REVISION_IDENTIFIER TEXT NOT NULL,");
		builder.append("AFTER_REVISION_IDENTIFIER TEXT NOT NULL,");
		builder.append("COMMITTER_NAME TEXT,");
		builder.append("COMMITTER_ADDRESS TEXT,");
		builder.append("YEAR INTEGER CHECK(YEAR > 0),");
		builder.append("MONTH INTEGER CHECK(MONTH >= 1 AND MONTH <= 12),");
		builder.append("DAY INTEGER CHECK(DAY >= 1 AND DAY <= 31),");
		builder.append("HOUR INTEGER CHECK(HOUR >= 0 AND HOUR <= 23),");
		builder.append("MINUTE INTEGER CHECK(MINUTE >= 0 AND MINUTE <= 59),");
		builder.append("SECOND INTEGER CHECK(SECOND >= 0 AND SECOND <= 59),");
		builder.append("FOREIGN KEY(REPOSITORY_ID) REFERENCES REPOSITORY(REPOSITORY_ID)");
		// builder.append("FOREIGN KEY(BEFORE_REVISION_ID) REFERENCES REVISION(REVISION_ID),");
		// builder.append("FOREIGN KEY(AFTER_REVISION_ID) REFERENCES REVISION(REVISION_ID)");
		// builder.append("FOREIGN KEY(BEFORE_REVISION_IDENTIFIER) REFERENCES REVISION(REVISION_IDENTIFIER),");
		// builder.append("FOREIGN KEY(AFTER_REVISION_IDENTIFIER) REFERENCES REVISION(REVISION_IDENTIFIER)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the commit table
	 *
	 * @throws Exception
	 */
	private void createCommitTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index VCS_COMMIT_ID_INDEX_VCS_COMMIT on VCS_COMMIT(VCS_COMMIT_ID)");
		dbManager
				.executeUpdate("create index REPOSITORY_ID_INDEX_VCS_COMMIT on VCS_COMMIT(REPOSITORY_ID)");
		dbManager
				.executeUpdate("create index BEFORE_REVISION_ID_INDEX_VCS_COMMIT on VCS_COMMIT(BEFORE_REVISION_ID)");
		dbManager
				.executeUpdate("create index AFTER_REVISION_ID_INDEX_VCS_COMMIT on VCS_COMMIT(AFTER_REVISION_ID)");
		dbManager
				.executeUpdate("create index YEAR_INDEX_VCS_COMMIT on VCS_COMMIT(YEAR)");
		dbManager
				.executeUpdate("create index MONTH_INDEX_VCS_COMMIT on VCS_COMMIT(MONTH)");
		dbManager
				.executeUpdate("create index DAY_INDEX_VCS_COMMIT on VCS_COMMIT(DAY)");
		dbManager
				.executeUpdate("create index HOUR_INDEX_VCS_COMMIT on VCS_COMMIT(HOUR)");
		dbManager
				.executeUpdate("create index MINUTE_INDEX_VCS_COMMIT on VCS_COMMIT(MINUTE)");
		dbManager
				.executeUpdate("create index SECOND_INDEX_VCS_COMMIT on VCS_COMMIT(SECOND)");
		dbManager
				.executeUpdate("create index DATE_INDEX_VCS_COMMIT on VCS_COMMIT(YEAR,MONTH,DAY,HOUR,MINUTE,SECOND)");
	}

	/**
	 * drop indexes on the commit table
	 *
	 * @throws Exception
	 */
	private void dropCommitTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists VCS_COMMIT_ID_INDEX_VCS_COMMIT");
		dbManager
				.executeUpdate("drop index if exists REPOSITORY_ID_INDEX_VCS_COMMIT");
		dbManager
				.executeUpdate("drop index if exists BEFORE_REVISION_ID_INDEX_VCS_COMMIT");
		dbManager
				.executeUpdate("drop index if exists AFTER_REVISION_ID_INDEX_VCS_COMMIT");
		dbManager.executeUpdate("drop index if exists YEAR_INDEX_VCS_COMMIT");
		dbManager.executeUpdate("drop index if exists MONTH_INDEX_VCS_COMMIT");
		dbManager.executeUpdate("drop index if exists DAY_INDEX_VCS_COMMIT");
		dbManager.executeUpdate("drop index if exists HOUR_INDEX_VCS_COMMIT");
		dbManager.executeUpdate("drop index if exists MINUTE_INDEX_VCS_COMMIT");
		dbManager.executeUpdate("drop index if exists SECOND_INDEX_VCS_COMMIT");
		dbManager.executeUpdate("drop index if exists DATE_INDEX_VCS_COMMIT");
	}

	/**
	 * get the query to create the combined revision table
	 *
	 * @return
	 */
	private String getCombinedRevisionTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists COMBINED_REVISION(");
		builder.append("COMBINED_REVISION_ID BIGINT,");
		builder.append("REVISION_ID BIGINT,");
		builder.append("PRIMARY KEY(COMBINED_REVISION_ID, REVISION_ID)");
		// builder.append("FOREIGN KEY(REVISION_ID) REFERENCES REVISION(REVISION_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the revision table
	 *
	 * @throws Exception
	 */
	private void createCombinedRevisionTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index COMBINED_REVISION_ID_INDEX_COMBINED_REVISION on COMBINED_REVISION(COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index REVISION_ID_INDEX_COMBINED_REVISION on COMBINED_REVISION(REVISION_ID)");
		dbManager
				.executeUpdate("create index COMBINED_REVISION_ID_REVISION_ID_INDEX_COMBINED_REVISION on COMBINED_REVISION(COMBINED_REVISION_ID,REVISION_ID)");
	}

	/**
	 * drop indexes on the revision table
	 *
	 * @throws Exception
	 */
	private void dropCombinedRevisionTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists COMBINED_REVISION_ID_INDEX_COMBINED_REVISION");
		dbManager
				.executeUpdate("drop index if exists REVISION_ID_INDEX_COMBINED_REVISION");
		dbManager
				.executeUpdate("drop index if exists COMBINED_REVISION_ID_REVISION_ID_INDEX_COMBINED_REVISION");
	}

	/**
	 * get the query to create the combined revision table
	 *
	 * @return
	 */
	private String getCombinedCommitTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists COMBINED_COMMIT(");
		builder.append("COMBINED_COMMIT_ID BIGINT PRIMARY KEY,");
		builder.append("BEFORE_COMBINED_REVISION_ID BIGINT,");
		builder.append("AFTER_COMBINED_REVISION_ID BIGINT,");
		builder.append("VCS_COMMIT_ID BIGINT,");
		// builder.append("FOREIGN KEY(BEFORE_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		// builder.append("FOREIGN KEY(AFTER_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		builder.append("FOREIGN KEY(VCS_COMMIT_ID) REFERENCES VCS_COMMIT(VCS_COMMIT_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the revision table
	 *
	 * @throws Exception
	 */
	private void createCombinedCommitTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index COMBINED_COMMIT_ID_INDEX_COMBINED_COMMIT on COMBINED_COMMIT(COMBINED_COMMIT_ID)");
		dbManager
				.executeUpdate("create index BEFORE_COMBINED_REVISION_ID_INDEX_COMBINED_COMMIT on COMBINED_COMMIT(BEFORE_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index AFTER_COMBINED_REVISION_ID_INDEX_COMBINED_COMMIT on COMBINED_COMMIT(AFTER_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index VCS_COMMIT_ID_INDEX_COMBINED_COMMIT on COMBINED_COMMIT(VCS_COMMIT_ID)");
	}

	/**
	 * drop indexes on the combined commit table
	 *
	 * @throws Exception
	 */
	private void dropCombinedCommitTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists COMBINED_COMMIT_ID_INDEX_COMBINED_COMMIT");
		dbManager
				.executeUpdate("drop index if exists BEFORE_COMBINED_REVISION_ID_INDEX_COMBINED_COMMIT");
		dbManager
				.executeUpdate("drop index if exists AFTER_COMBINED_REVISION_ID_INDEX_COMBINED_COMMIT");
		dbManager
				.executeUpdate("drop index if exists VCS_COMMIT_ID_INDEX_COMBINED_COMMIT");
	}

	/**
	 * get the query to create the file table
	 *
	 * @return
	 */
	private String getFileTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists FILE(");
		builder.append("FILE_ID BIGINT PRIMARY KEY,");
		builder.append("REPOSITORY_ID BIGINT,");
		builder.append("FILE_PATH TEXT NOT NULL,");
		builder.append("START_COMBINED_REVISION_ID BIGINT,");
		builder.append("END_COMBINED_REVISION_ID BIGINT,");
		builder.append("ADDED_AT_START INTEGER,");
		builder.append("DELETED_AT_END INTEGER,");
		builder.append("FOREIGN KEY(REPOSITORY_ID) REFERENCES REPOSITORY(REPOSITORY_ID)");
		// builder.append("FOREIGN KEY(START_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		// builder.append("FOREIGN KEY(END_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the file table
	 *
	 * @throws Exception
	 */
	private void createFileTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index FILE_ID_INDEX_FILE on FILE(FILE_ID)");
		dbManager
				.executeUpdate("create index REPOSITORY_ID_INDEX_FILE on FILE(REPOSITORY_ID)");
		dbManager
				.executeUpdate("create index START_COMBINED_REVISION_ID_INDEX_FILE on FILE(START_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index END_COMBINED_REVISION_ID_INDEX_FILE on FILE(END_COMBINED_REVISION_ID)");
	}

	/**
	 * drop indexes on the file table
	 *
	 * @throws Exception
	 */
	private void dropFileTableIndexes() throws Exception {
		dbManager.executeUpdate("drop index if exists FILE_ID_INDEX_FILE");
		dbManager
				.executeUpdate("drop index if exists REPOSITORY_ID_INDEX_FILE");
		dbManager
				.executeUpdate("drop index if exists START_COMBINED_REVISION_ID_INDEX_FILE");
		dbManager
				.executeUpdate("drop index if exists END_COMBINED_REVISION_ID_INDEX_FILE");
	}

	/**
	 * get the query to create the table for crds
	 *
	 * @return
	 */
	private String getCrdQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists CRD(");
		builder.append("CRD_ID BIGINT PRIMARY KEY,");
		builder.append("TYPE TEXT NOT NULL,");
		builder.append("HEAD TEXT NOT NULL,");
		builder.append("ANCHOR TEXT NOT NULL,");
		builder.append("NORMALIZED_ANCHOR TEXT NOT NULL,");
		builder.append("CM INTEGER,");
		builder.append("ANCESTORS TEXT NOT NULL,");
		builder.append("FULL_TEXT TEXT NOT NULL");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the crd table
	 *
	 * @throws Exception
	 */
	private void createCrdTableIndexes() throws Exception {
		dbManager.executeUpdate("create index CRD_ID_INDEX_CRD on CRD(CRD_ID)");
		dbManager.executeUpdate("create index CM_INDEX_CRD on CRD(CM)");
	}

	/**
	 * drop indexes on the crd table
	 *
	 * @throws Exception
	 */
	private void dropCrdTableIndexes() throws Exception {
		dbManager.executeUpdate("drop index if exists CRD_ID_INDEX_CRD");
		dbManager.executeUpdate("drop index if exists CM_INDEX_CRD");
	}

	/**
	 * get the query to create the code fragment table
	 *
	 * @return
	 */
	private String getCodeFragmentTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists CODE_FRAGMENT(");
		builder.append("CODE_FRAGMENT_ID BIGINT PRIMARY KEY,");
		builder.append("OWNER_FILE_ID BIGINT,");
		builder.append("OWNER_REPOSITORY_ID BIGINT,");
		builder.append("CRD_ID BIGINT,");
		builder.append("START_COMBINED_REVISION_ID BIGINT,");
		builder.append("END_COMBINED_REVISION_ID BIGINT,");
		builder.append("HASH BIGINT,");
		builder.append("HASH_FOR_CLONE BIGINT,");
		builder.append("START_LINE INTEGER,");
		builder.append("END_LINE INTEGER,");
		builder.append("SIZE INTEGER,");
		builder.append("FILE_ADDED_AT_START INTEGER,");
		builder.append("FILE_DELETED_AT_END INTEGER,");
		builder.append("FOREIGN KEY(OWNER_FILE_ID) REFERENCES FILE(FILE_ID),");
		builder.append("FOREIGN KEY(OWNER_REPOSITORY_ID) REFERENCES REPOSITORY(REPOSITORY_ID),");
		builder.append("FOREIGN KEY(CRD_ID) REFERENCES CRD(CRD_ID)");
		// builder.append("FOREIGN KEY(START_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		// builder.append("FOREIGN KEY(END_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the code fragment table
	 *
	 * @throws Exception
	 */
	private void createCodeFragmentTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index CODE_FRAGMENT_ID_INDEX_CODE_FRAGMENT on CODE_FRAGMENT(CODE_FRAGMENT_ID)");
		dbManager
				.executeUpdate("create index START_COMBINED_REVISION_ID_INDEX_CODE_FRAGMENT on CODE_FRAGMENT(START_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index END_COMBINED_REVISION_ID_INDEX_CODE_FRAGMENT on CODE_FRAGMENT(END_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index HASH_INDEX_CODE_FRAGMENT on CODE_FRAGMENT(HASH)");
		dbManager
				.executeUpdate("create index HASH_FOR_CLONE_INDEX_CODE_FRAGMENT on CODE_FRAGMENT(HASH_FOR_CLONE)");
		dbManager
				.executeUpdate("create index START_LINE_INDEX_CODE_FRAGMENT on CODE_FRAGMENT(START_LINE)");
		dbManager
				.executeUpdate("create index END_LINE_INDEX_CODE_FRAGMENT on CODE_FRAGMENT(END_LINE)");
		dbManager
				.executeUpdate("create index SIZE_INDEX_CODE_FRAGMENT on CODE_FRAGMENT(SIZE)");
		dbManager
				.executeUpdate("create index START_END_REVISION_ID_INDEX_CODE_FRAGMENT on CODE_FRAGMENT(START_COMBINED_REVISION_ID,END_COMBINED_REVISION_ID)");
	}

	/**
	 * drop indexes on the code fragment table
	 *
	 * @throws Exception
	 */
	private void dropCodeFragmentTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists CODE_FRAGMENT_ID_INDEX_CODE_FRAGMENT");
		dbManager
				.executeUpdate("drop index if exists START_COMBINED_REVISION_ID_INDEX_CODE_FRAGMENT");
		dbManager
				.executeUpdate("drop index if exists END_COMBINED_REVISION_ID_INDEX_CODE_FRAGMENT");
		dbManager
				.executeUpdate("drop index if exists HASH_INDEX_CODE_FRAGMENT");
		dbManager
				.executeUpdate("drop index if exists HASH_FOR_CLONE_INDEX_CODE_FRAGMENT");
		dbManager
				.executeUpdate("drop index if exists START_LINE_INDEX_CODE_FRAGMENT");
		dbManager
				.executeUpdate("drop index if exists END_LINE_INDEX_CODE_FRAGMENT");
		dbManager
				.executeUpdate("drop index if exists SIZE_INDEX_CODE_FRAGMENT");
		dbManager
				.executeUpdate("drop index if exists START_END_REVISION_ID_INDEX_CODE_FRAGMENT");
	}

	/**
	 * get the query to create the clone set table
	 *
	 * @return
	 */
	private String getCloneSetTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists CLONE_SET(");
		builder.append("CLONE_SET_ID BIGINT,");
		builder.append("OWNER_COMBINED_REVISION_ID BIGINT,");
		builder.append("ELEMENT BIGINT,");
		builder.append("PRIMARY KEY(CLONE_SET_ID, ELEMENT),");
		// builder.append("FOREIGN KEY(OWNER_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(OWNER_COMBINED_REVISION_ID),");
		builder.append("FOREIGN KEY(ELEMENT) REFERENCES CODE_FRAGMENT(CODE_FRAGMENT_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the clone set table
	 *
	 * @throws Exception
	 */
	private void createCloneSetTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index CLONE_SET_ID_INDEX_CLONE_SET on CLONE_SET(CLONE_SET_ID)");
		dbManager
				.executeUpdate("create index OWNER_COMBINED_REVISION_ID_INDEX_CLONE_SET on CLONE_SET(OWNER_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index ELEMENT_INDEX_CLONE_SET on CLONE_SET(ELEMENT)");
		dbManager
				.executeUpdate("create index CLONE_SET_ID_ELEMENT_INDEX_CLONE_SET on CLONE_SET(CLONE_SET_ID,ELEMENT)");
	}

	/**
	 * drop indexes on the clone set table
	 *
	 * @throws Exception
	 */
	private void dropCloneSetTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists CLONE_SET_ID_INDEX_CLONE_SET");
		dbManager
				.executeUpdate("drop index if exists OWNER_COMBINED_REVISION_ID_INDEX_CLONE_SET");
		dbManager.executeUpdate("drop index if exists ELEMENT_INDEX_CLONE_SET");
		dbManager
				.executeUpdate("drop index if exists CLONE_SET_ID_ELEMENT_INDEX_CLONE_SET");
	}

	/**
	 * get the query to create the table for links of code fragments
	 *
	 * @return
	 */
	private String getCodeFragmentLinkTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table CODE_FRAGMENT_LINK(");
		builder.append("CODE_FRAGMENT_LINK_ID BIGINT PRIMARY KEY,");
		builder.append("BEFORE_ELEMENT_ID BIGINT,");
		builder.append("AFTER_ELEMENT_ID BIGINT,");
		builder.append("BEFORE_COMBINED_REVISION_ID BIGINT,");
		builder.append("AFTER_COMBINED_REVISION_ID BIGINT,");
		builder.append("CHANGED INTEGER");
		// builder.append("FOREIGN KEY(BEFORE_ELEMENT_ID) REFERENCES CODE_FRAGMENT(CODE_FRAGMENT_ID),");
		// builder.append("FOREIGN KEY(AFTER_ELEMENT_ID) REFERENCES CODE_FRAGMENT(CODE_FRAGMENT_ID),");
		// builder.append("FOREIGN KEY(BEFORE_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		// builder.append("FOREIGN KEY(AFTER_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the fragment link table
	 *
	 * @throws Exception
	 */
	private void createCodeFragmentLinkTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index CODE_FRAGMENT_LINK_ID_INDEX_CODE_FRAGMENT_LINK on CODE_FRAGMENT_LINK(CODE_FRAGMENT_LINK_ID)");
		dbManager
				.executeUpdate("create index BEFORE_ELEMENT_ID_INDEX_CODE_FRAGMENT_LINK on CODE_FRAGMENT_LINK(BEFORE_ELEMENT_ID)");
		dbManager
				.executeUpdate("create index AFTER_ELEMENT_ID_INDEX_CODE_FRAGMENT_LINK on CODE_FRAGMENT_LINK(AFTER_ELEMENT_ID)");
		dbManager
				.executeUpdate("create index BEFORE_COMBINED_REVISION_ID_INDEX_CODE_FRAGMENT_LINK on CODE_FRAGMENT_LINK(BEFORE_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index AFTER_COMBINED_REVISION_ID_INDEX_CODE_FRAGMENT_LINK on CODE_FRAGMENT_LINK(AFTER_COMBINED_REVISION_ID)");
	}

	/**
	 * drop indexes on the fragment link table
	 *
	 * @throws Exception
	 */
	private void dropCodeFragmentLinkTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists CODE_FRAGMENT_LINK_ID_INDEX_CODE_FRAGMENT_LINK");
		dbManager
				.executeUpdate("drop index if exists BEFORE_ELEMENT_ID_INDEX_CODE_FRAGMENT_LINK");
		dbManager
				.executeUpdate("drop index if exists AFTER_ELEMENT_ID_INDEX_CODE_FRAGMENT_LINK");
		dbManager
				.executeUpdate("drop index if exists BEFORE_COMBINED_REVISION_ID_INDEX_CODE_FRAGMENT_LINK");
		dbManager
				.executeUpdate("drop index if exists AFTER_COMBINED_REVISION_ID_INDEX_CODE_FRAGMENT_LINK");
	}

	/**
	 * get the query to create the table for links of clone sets
	 *
	 * @return
	 */
	private String getCloneSetLinkTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists CLONE_SET_LINK(");
		builder.append("CLONE_SET_LINK_ID BIGINT PRIMARY KEY,");
		builder.append("BEFORE_ELEMENT_ID BIGINT,");
		builder.append("AFTER_ELEMENT_ID BIGINT,");
		builder.append("BEFORE_COMBINED_REVISION_ID BIGINT,");
		builder.append("AFTER_COMBINED_REVISION_ID BIGINT");
		// builder.append("FOREIGN KEY(BEFORE_ELEMENT_ID) REFERENCES CLONE_SET(CLONE_SET_ID),");
		// builder.append("FOREIGN KEY(AFTER_ELEMENT_ID) REFERENCES CLONE_SET(CLONE_SET_ID),");
		// builder.append("FOREIGN KEY(BEFORE_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		// builder.append("FOREIGN KEY(AFTER_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the clone link table
	 *
	 * @throws Exception
	 */
	private void createCloneSetLinkTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index CLONE_SET_LINK_ID_INDEX_CLONE_SET_LINK on CLONE_SET_LINK(CLONE_SET_LINK_ID)");
		dbManager
				.executeUpdate("create index BEFORE_ELEMENT_ID_INDEX_CLONE_SET_LINK on CLONE_SET_LINK(BEFORE_ELEMENT_ID)");
		dbManager
				.executeUpdate("create index AFTER_ELEMENT_ID_INDEX_CLONE_SET_LINK on CLONE_SET_LINK(AFTER_ELEMENT_ID)");
		dbManager
				.executeUpdate("create index BEFORE_COMBINED_REVISION_ID_INDEX_CLONE_SET_LINK on CLONE_SET_LINK(BEFORE_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index AFTER_COMBINED_REVISION_ID_INDEX_CLONE_SET_LINK on CLONE_SET_LINK(AFTER_COMBINED_REVISION_ID)");
	}

	/**
	 * drop indexes on the clone link table
	 *
	 * @throws Exception
	 */
	private void dropCloneSetLinkTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists CLONE_SET_LINK_ID_INDEX_CLONE_SET_LINK");
		dbManager
				.executeUpdate("drop index if exists BEFORE_ELEMENT_ID_INDEX_CLONE_SET_LINK");
		dbManager
				.executeUpdate("drop index if exists AFTER_ELEMENT_ID_INDEX_CLONE_SET_LINK");
		dbManager
				.executeUpdate("drop index if exists BEFORE_COMBINED_REVISION_ID_INDEX_CLONE_SET_LINK");
		dbManager
				.executeUpdate("drop index if exists AFTER_COMBINED_REVISION_ID_INDEX_CLONE_SET_LINK");
	}

	/**
	 * get the query to create the table for links of clone sets
	 *
	 * @return
	 */
	private String getCloneSetLinkFragmentLinkTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists CLONE_SET_LINK_FRAGMENT_LINK(");
		builder.append("CLONE_SET_LINK_ID BIGINT,");
		builder.append("CODE_FRAGMENT_LINK_ID BIGINT,");
		builder.append("PRIMARY KEY(CLONE_SET_LINK_ID, CODE_FRAGMENT_LINK_ID),");
		builder.append("FOREIGN KEY(CLONE_SET_LINK_ID) REFERENCES CLONE_SET_LINK(CLONE_SET_LINK_ID),");
		builder.append("FOREIGN KEY(CODE_FRAGMENT_LINK_ID) REFERENCES CODE_FRAGMENT_LINK(CODE_FRAGMENT_LINK_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the clone link table
	 *
	 * @throws Exception
	 */
	private void createCloneSetLinkFragmentLinkTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index CLONE_SET_LINK_ID_INDEX_CLONE_SET_LINK_FRAGMENT_LINK on CLONE_SET_LINK_FRAGMENT_LINK(CLONE_SET_LINK_ID)");
		dbManager
				.executeUpdate("create index CODE_FRAGMENT_LINK_INDEX_CLONE_SET_LINK_FRAGMENT_LINK on CLONE_SET_LINK_FRAGMENT_LINK(CODE_FRAGMENT_LINK_ID)");
		dbManager
				.executeUpdate("create index KEY_INDEX_CLONE_SET_LINK_FRAGMENT_LINK on CLONE_SET_LINK_FRAGMENT_LINK(CLONE_SET_LINK_ID, CODE_FRAGMENT_LINK_ID)");
	}

	/**
	 * drop indexes on the clone link table
	 *
	 * @throws Exception
	 */
	private void dropCloneSetLinkFragmentLinkTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists CLONE_SET_LINK_ID_INDEX_CLONE_SET_LINK_FRAGMENT_LINK");
		dbManager
				.executeUpdate("drop index if exists CODE_FRAGMENT_LINK_INDEX_CLONE_SET_LINK_FRAGMENT_LINK");
		dbManager
				.executeUpdate("drop index if exists KEY_INDEX_CLONE_SET_LINK_FRAGMENT_LINK");
	}

	/**
	 * get the query to create the table for genealogies of clones
	 *
	 * @return
	 */
	private String getCloneGenealogyTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists CLONE_GENEALOGY(");
		builder.append("CLONE_GENEALOGY_ID BIGINT PRIMARY KEY,");
		builder.append("START_COMBINED_REVISION_ID BIGINT,");
		builder.append("END_COMBINED_REVISION_ID BIGINT");
		// builder.append("FOREIGN KEY(START_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		// builder.append("FOREIGN KEY(END_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the clone genealogy table
	 *
	 * @throws Exception
	 */
	private void createCloneGenealogyTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index CLONE_GENEALOGY_ID_INDEX_CLONE_GENEALOGY on CLONE_GENEALOGY(CLONE_GENEALOGY_ID)");
		dbManager
				.executeUpdate("create index START_COMBINED_REVISION_ID_INDEX_CLONE_GENEALOGY on CLONE_GENEALOGY(START_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index END_COMBINED_REVISION_ID_INDEX_CLONE_GENEALOGY on CLONE_GENEALOGY(END_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index START_END_REVISION_ID_INDEX_CLONE_GENEALOGY on CLONE_GENEALOGY(START_COMBINED_REVISION_ID,END_COMBINED_REVISION_ID)");
	}

	/**
	 * drop indexes on the clone genealogy table
	 *
	 * @throws Exception
	 */
	private void dropCloneGenealogyTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists CLONE_GENEALOGY_ID_INDEX_CLONE_GENEALOGY");
		dbManager
				.executeUpdate("drop index if exists START_COMBINED_REVISION_ID_INDEX_CLONE_GENEALOGY");
		dbManager
				.executeUpdate("drop index if exists END_COMBINED_REVISION_ID_INDEX_CLONE_GENEALOGY");
		dbManager
				.executeUpdate("drop index if exists START_END_REVISION_ID_INDEX_CLONE_GENEALOGY");
	}

	/**
	 * get the query to create the table for elements in genealogies of clones
	 *
	 * @return
	 */
	private String getCloneGenealogyEelementTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists CLONE_GENEALOGY_ELEMENT(");
		builder.append("CLONE_GENEALOGY_ID BIGINT,");
		builder.append("CLONE_SET_ID BIGINT,");
		builder.append("PRIMARY KEY(CLONE_GENEALOGY_ID, CLONE_SET_ID),");
		builder.append("FOREIGN KEY(CLONE_GENEALOGY_ID) REFERENCES CLONE_GENEALOGY(CLONE_GENEALOGY_ID)");
		// builder.append("FOREIGN KEY(CLONE_SET_ID) REFERENCES CLONE_SET(CLONE_SET_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the clone genealogy element table
	 *
	 * @throws Exception
	 */
	private void createCloneGenealogyElementTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index CLONE_GENEALOGY_ID_INDEX_CLONE_GENEALOGY_ELEMENT on CLONE_GENEALOGY_ELEMENT(CLONE_GENEALOGY_ID)");
		dbManager
				.executeUpdate("create index CLONE_SET_ID_INDEX_CLONE_GENEALOGY_ELEMENT on CLONE_GENEALOGY_ELEMENT(CLONE_SET_ID)");
		dbManager
				.executeUpdate("create index KEY_INDEX_CLONE_GENEALOGY_ELEMENT on CLONE_GENEALOGY_ELEMENT(CLONE_GENEALOGY_ID, CLONE_SET_ID)");
	}

	/**
	 * drop indexes on the clone genealogy element table
	 *
	 * @throws Exception
	 */
	private void dropCloneGenealogyElementTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists CLONE_GENEALOGY_ID_INDEX_CLONE_GENEALOGY_ELEMENT");
		dbManager
				.executeUpdate("drop index if exists CLONE_SET_ID_INDEX_CLONE_GENEALOGY_ELEMENT");
		dbManager
				.executeUpdate("drop index if exists KEY_INDEX_CLONE_GENEALOGY_ELEMENT");
	}

	/**
	 * get the query to create the table for links in genealogies of clones
	 *
	 * @return
	 */
	private String getCloneGenealogyLinkTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists CLONE_GENEALOGY_LINK_ELEMENT(");
		builder.append("CLONE_GENEALOGY_ID BIGINT,");
		builder.append("CLONE_SET_LINK_ID BIGINT,");
		builder.append("PRIMARY KEY(CLONE_GENEALOGY_ID, CLONE_SET_LINK_ID),");
		builder.append("FOREIGN KEY(CLONE_GENEALOGY_ID) REFERENCES CLONE_GENEALOGY(CLONE_GENEALOGY_ID),");
		builder.append("FOREIGN KEY(CLONE_SET_LINK_ID) REFERENCES CLONE_SET_LINK(CLONE_SET_LINK_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the clone genealogy element table
	 *
	 * @throws Exception
	 */
	private void createCloneGenealogyLinkTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index CLONE_GENEALOGY_ID_INDEX_CLONE_GENEALOGY_LINK_ELEMENT on CLONE_GENEALOGY_LINK_ELEMENT(CLONE_GENEALOGY_ID)");
		dbManager
				.executeUpdate("create index CLONE_SET_LINK_ID_INDEX_CLONE_GENEALOGY_LINK_ELEMENT on CLONE_GENEALOGY_LINK_ELEMENT(CLONE_SET_LINK_ID)");
		dbManager
				.executeUpdate("create index KEY_INDEX_CLONE_GENEALOGY_LINK_ELEMENT on CLONE_GENEALOGY_LINK_ELEMENT(CLONE_GENEALOGY_ID, CLONE_SET_LINK_ID)");
	}

	/**
	 * drop indexes on the clone genealogy element table
	 *
	 * @throws Exception
	 */
	private void dropCloneGenealogyLinkTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists CLONE_GENEALOGY_ID_INDEX_CLONE_GENEALOGY_LINK_ELEMENT");
		dbManager
				.executeUpdate("drop index if exists CLONE_SET_LINK_ID_INDEX_CLONE_GENEALOGY_LINK_ELEMENT");
		dbManager
				.executeUpdate("drop index if exists KEY_INDEX_CLONE_GENEALOGY_LINK_ELEMENT");
	}

	/**
	 * get the query to create the table for genealogies of code fragments
	 *
	 * @return
	 */
	private String getCodeFragmentGenealogyTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists CODE_FRAGMENT_GENEALOGY(");
		builder.append("CODE_FRAGMENT_GENEALOGY_ID BIGINT PRIMARY KEY,");
		builder.append("START_COMBINED_REVISION_ID BIGINT,");
		builder.append("END_COMBINED_REVISION_ID BIGINT");
		// builder.append("FOREIGN KEY(START_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		// builder.append("FOREIGN KEY(END_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the fragment genealogy table
	 *
	 * @throws Exception
	 */
	private void createCodeFragmentGenealogyTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index CODE_FRAGMENT_GENEALOGY_ID_INDEX_CODE_FRAGMENT_GENEALOGY on CODE_FRAGMENT_GENEALOGY(CODE_FRAGMENT_GENEALOGY_ID)");
		dbManager
				.executeUpdate("create index START_COMBINED_REVISION_ID_INDEX_CODE_FRAGMENT_GENEALOGY on CODE_FRAGMENT_GENEALOGY(START_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index END_COMBINED_REVISION_ID_INDEX_CODE_FRAGMENT_GENEALOGY on CODE_FRAGMENT_GENEALOGY(END_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index START_END_REVISION_ID_INDEX_CODE_FRAGMENT_GENEALOGY on CODE_FRAGMENT_GENEALOGY(START_COMBINED_REVISION_ID,END_COMBINED_REVISION_ID)");
	}

	/**
	 * drop indexes on the fragment genealogy table
	 *
	 * @throws Exception
	 */
	private void dropCodeFragmentGenealogyTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists CODE_FRAGMENT_GENEALOGY_ID_INDEX_CODE_FRAGMENT_GENEALOGY");
		dbManager
				.executeUpdate("drop index if exists START_COMBINED_REVISION_ID_INDEX_CODE_FRAGMENT_GENEALOGY");
		dbManager
				.executeUpdate("drop index if exists END_COMBINED_REVISION_ID_INDEX_CODE_FRAGMENT_GENEALOGY");
		dbManager
				.executeUpdate("drop index if exists START_END_REVISION_ID_INDEX_CODE_FRAGMENT_GENEALOGY");
	}

	/**
	 * get the query to create the table for elements in genealogies of code
	 * fragments
	 *
	 * @return
	 */
	private String getCodeFragmentGenealogyElementTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists CODE_FRAGMENT_GENEALOGY_ELEMENT(");
		builder.append("CODE_FRAGMENT_GENEALOGY_ID BIGINT,");
		builder.append("CODE_FRAGMENT_ID BIGINT,");
		builder.append("PRIMARY KEY(CODE_FRAGMENT_GENEALOGY_ID, CODE_FRAGMENT_ID),");
		builder.append("FOREIGN KEY(CODE_FRAGMENT_GENEALOGY_ID) REFERENCES CODE_FRAGMENT_GENEALOGY(CODE_FRAGMENT_GENEALOGY_ID),");
		builder.append("FOREIGN KEY(CODE_FRAGMENT_ID) REFERENCES CODE_FRAGMENT(CODE_FRAGMENT_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the fragment genealogy table
	 *
	 * @throws Exception
	 */
	private void createCodeFragmentGenealogyElementTableIndexes()
			throws Exception {
		dbManager
				.executeUpdate("create index CODE_FRAGMENT_GENEALOGY_ID_INDEX_CODE_FRAGMENT_GENEALOGY_ELEMENT on CODE_FRAGMENT_GENEALOGY_ELEMENT(CODE_FRAGMENT_GENEALOGY_ID)");
		dbManager
				.executeUpdate("create index CODE_FRAGMENT_ID_INDEX_CODE_FRAGMENT_GENEALOGY_ELEMENT on CODE_FRAGMENT_GENEALOGY_ELEMENT(CODE_FRAGMENT_ID)");
		dbManager
				.executeUpdate("create index KEY_INDEX_CODE_FRAGMENT_GENEALOGY_ELEMENT on CODE_FRAGMENT_GENEALOGY_ELEMENT(CODE_FRAGMENT_GENEALOGY_ID, CODE_FRAGMENT_ID)");
	}

	/**
	 * drop indexes on the fragment genealogy table
	 *
	 * @throws Exception
	 */
	private void dropCodeFragmentGenealogyElementTableIndexes()
			throws Exception {
		dbManager
				.executeUpdate("drop index if exists CODE_FRAGMENT_GENEALOGY_ID_INDEX_CODE_FRAGMENT_GENEALOGY_ELEMENT");
		dbManager
				.executeUpdate("drop index if exists CODE_FRAGMENT_ID_INDEX_CODE_FRAGMENT_GENEALOGY_ELEMENT");
		dbManager
				.executeUpdate("drop index if exists KEY_INDEX_CODE_FRAGMENT_GENEALOGY_ELEMENT");
	}

	/**
	 * get the query to create the table for elements in genealogies of code
	 * fragments
	 *
	 * @return
	 */
	private String getCodeFragmentGenealogyLinkTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table if not exists CODE_FRAGMENT_GENEALOGY_LINK_ELEMENT(");
		builder.append("CODE_FRAGMENT_GENEALOGY_ID BIGINT,");
		builder.append("CODE_FRAGMENT_LINK_ID BIGINT,");
		builder.append("PRIMARY KEY(CODE_FRAGMENT_GENEALOGY_ID, CODE_FRAGMENT_LINK_ID),");
		builder.append("FOREIGN KEY(CODE_FRAGMENT_GENEALOGY_ID) REFERENCES CODE_FRAGMENT_GENEALOGY(CODE_FRAGMENT_GENEALOGY_ID),");
		builder.append("FOREIGN KEY(CODE_FRAGMENT_LINK_ID) REFERENCES CODE_FRAGMENT_LINK(CODE_FRAGMENT_LINK_ID)");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * create indexes on the fragment genealogy table
	 *
	 * @throws Exception
	 */
	private void createCodeFragmentGenealogyLinkTableIndexes() throws Exception {
		dbManager
				.executeUpdate("create index CODE_FRAGMENT_GENEALOGY_ID_INDEX_CODE_FRAGMENT_GENEALOGY_LINK_ELEMENT on CODE_FRAGMENT_GENEALOGY_LINK_ELEMENT(CODE_FRAGMENT_GENEALOGY_ID)");
		dbManager
				.executeUpdate("create index CODE_FRAGMENT_LINK_ID_INDEX_CODE_FRAGMENT_GENEALOGY_LINK_ELEMENT on CODE_FRAGMENT_GENEALOGY_LINK_ELEMENT(CODE_FRAGMENT_LINK_ID)");
		dbManager
				.executeUpdate("create index KEY_INDEX_CODE_FRAGMENT_GENEALOGY_LINK_ELEMENT on CODE_FRAGMENT_GENEALOGY_LINK_ELEMENT(CODE_FRAGMENT_GENEALOGY_ID, CODE_FRAGMENT_LINK_ID)");
	}

	private void dropCodeFragmentGenealogyLinkTableIndexes() throws Exception {
		dbManager
				.executeUpdate("drop index if exists CODE_FRAGMENT_GENEALOGY_ID_INDEX_CODE_FRAGMENT_GENEALOGY_LINK_ELEMENT");
		dbManager
				.executeUpdate("drop index if exists CODE_FRAGMENT_LINK_ID_INDEX_CODE_FRAGMENT_GENEALOGY_LINK_ELEMENT");
		dbManager
				.executeUpdate("drop index if exists KEY_INDEX_CODE_FRAGMENT_GENEALOGY_LINK_ELEMENT");
	}

}
