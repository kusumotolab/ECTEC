package jp.ac.osaka_u.ist.sdl.ectec.db;

/**
 * A class for create
 * 
 * @author k-hotta
 * 
 */
public class DBMaker {

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

	/**
	 * make the db
	 * 
	 * @param overwrite
	 *            if true, the existing db is overwritten by the new one (the
	 *            existing db will be deleted)
	 * @throws Exception
	 */
	public void makeDb(final boolean overwrite) throws Exception {
		dbManager.setAutoCommit(true);

		if (overwrite) {
			dropTables();
		}

		createNewTables();

		createIndexes();

		dbManager.setAutoCommit(false);
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
		dbManager.executeUpdate(getCloneGenealogyTableQuery());
		dbManager.executeUpdate(getCodeFragmentGenealogyTableQuery());
	}

	/**
	 * drop all the tables and vacuum the db
	 */
	public void dropTables() {
		try {
			dbManager.executeUpdate("DROP TABLE REVISION");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE VCS_COMMIT");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE FILE");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE CODE_FRAGMENT");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE CLONE_SET");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE CODE_FRAGMENT_LINK");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE CLONE_SET_LINK");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE CLONE_GENEALOGY");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE CODE_FRAGMENT_GENEALOGY");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("DROP TABLE CRD");
		} catch (Exception e) {
			// e.printStackTrace();
		}

		try {
			dbManager.executeUpdate("VACUUM");
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
		createCloneGenealogyTableIndexes();
		createCodeFragmentGenealogyTableIndexes();
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

		builder.append("create table REPOSITORY(");
		builder.append("REPOSITORY_ID LONG PRIMARY KEY,");
		builder.append("REPOSITORY_URL TEXT UNIQUE");
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
	 * get the query to create the revision table
	 * 
	 * @return
	 */
	private String getRevisionTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table REVISION(");
		builder.append("REVISION_ID LONG PRIMARY KEY,");
		builder.append("REVISION_IDENTIFIER TEXT,");
		builder.append("REPOSITORY_ID LONG,");
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
	 * get the query to create the commit table
	 * 
	 * @return
	 */
	private String getCommitTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table VCS_COMMIT(");
		builder.append("VCS_COMMIT_ID LONG PRIMARY KEY,");
		builder.append("BEFORE_REVISION_ID LONG,");
		builder.append("AFTER_REVISION_ID LONG,");
		builder.append("YEAR INTEGER CHECK(YEAR > 0),");
		builder.append("MONTH INTEGER CHECK(MONTH >= 1 AND MONTH <= 12),");
		builder.append("DAY INTEGER CHECK(DAY >= 1 AND DAY <= 31),");
		builder.append("HOUR INTEGER CHECK(HOUR >= 0 AND HOUR <= 23),");
		builder.append("MINUTE INTEGER CHECK(MINUTE >= 0 AND MINUTE <= 59),");
		builder.append("SECOND INTEGER CHECK(SECOND >= 0 AND SECOND <= 59),");
		builder.append("FOREIGN KEY(BEFORE_REVISION_ID) REFERENCES REVISION(REVISION_ID),");
		builder.append("FOREIGN KEY(AFTER_REVISION_ID) REFERENCES REVISION(REVISION_ID)");
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
	 * get the query to create the combined revision table
	 * 
	 * @return
	 */
	private String getCombinedRevisionTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table COMBINED_REVISION(");
		builder.append("COMBINED_REVISION_ID LONG,");
		builder.append("REVISION_ID LONG,");
		builder.append("PRIMARY KEY(COMBINED_REVISION_ID, REVISION_ID),");
		builder.append("FOREIGN KEY(REVISION_ID) REFERENCES REVISION(REVISION_ID)");
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
	 * get the query to create the combined revision table
	 * 
	 * @return
	 */
	private String getCombinedCommitTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table COMBINED_COMMIT(");
		builder.append("COMBINED_COMMIT_ID LONG PRIMARY KEY,");
		builder.append("BEFORE_COMBINED_REVISION_ID LONG,");
		builder.append("AFTER_COMBINED_REVISION_ID LONG,");
		builder.append("VCS_COMMIT_ID LONG,");
		builder.append("FOREIGN KEY(BEFORE_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		builder.append("FOREIGN KEY(AFTER_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
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
	 * get the query to create the file table
	 * 
	 * @return
	 */
	private String getFileTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table FILE(");
		builder.append("FILE_ID LONG PRIMARY KEY,");
		builder.append("REPOSITORY_ID LONG,");
		builder.append("FILE_PATH TEXT NOT NULL,");
		builder.append("START_COMBINED_REVISION_ID LONG,");
		builder.append("END_COMBINED_REVISION_ID LONG,");
		builder.append("ADDED_COMBINED_COMMIT_ID LONG,");
		builder.append("FOREIGN KEY(REPOSITORY_ID) REFERENCES REPOSITORY(REPOSITORY_ID),");
		builder.append("FOREIGN KEY(START_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		builder.append("FOREIGN KEY(END_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID)");
		builder.append("FOREIGN KEY(ADDED_COMBINED_COMMIT_ID) REFERENCES COMBINED_COMMIT(COMBINED_COMMIT_ID)");
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
	 * get the query to create the table for genealogies of clones
	 * 
	 * @return
	 */
	private String getCrdQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table CRD(");
		builder.append("CRD_ID LONG PRIMARY KEY,");
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
	 * get the query to create the code fragment table
	 * 
	 * @return
	 */
	private String getCodeFragmentTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table CODE_FRAGMENT(");
		builder.append("CODE_FRAGMENT_ID LONG PRIMARY KEY,");
		builder.append("OWNER_FILE_ID LONG,");
		builder.append("CRD_ID LONG,");
		builder.append("START_COMBINED_REVISION_ID LONG,");
		builder.append("END_COMBINED_REVISION_ID LONG,");
		builder.append("HASH LONG,");
		builder.append("HASH_FOR_CLONE LONG,");
		builder.append("START_LINE INTEGER,");
		builder.append("END_LINE INTEGER,");
		builder.append("SIZE INTEGER,");
		builder.append("FOREIGN KEY(OWNER_FILE_ID) REFERENCES FILE(FILE_ID),");
		builder.append("FOREIGN KEY(CRD_ID) REFERENCES CRD(CRD_ID),");
		builder.append("FOREIGN KEY(START_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		builder.append("FOREIGN KEY(END_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID)");
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
	 * get the query to create the clone set table
	 * 
	 * @return
	 */
	private String getCloneSetTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table CLONE_SET(");
		builder.append("CLONE_SET_ID LONG,");
		builder.append("OWNER_REPOSITORY_ID LONG,");
		builder.append("OWNER_COMBINED_REVISION_ID LONG,");
		builder.append("ELEMENT LONG,");
		builder.append("PRIMARY KEY(CLONE_SET_ID, ELEMENT),");
		builder.append("FOREIGN KEY(OWNER_REPOSITORY_ID) REFERENCES REPOSITORY(REPOSITORY_ID),");
		builder.append("FOREIGN KEY(OWNER_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(OWNER_COMBINED_REVISION_ID),");
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
				.executeUpdate("create index OWNER_REPOSITORY_ID_INDEX_CLONE_SET on CLONE_SET(OWNER_REPOSITORY_ID)");
		dbManager
				.executeUpdate("create index OWNER_COMBINED_REVISION_ID_INDEX_CLONE_SET on CLONE_SET(OWNER_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index ELEMENT_INDEX_CLONE_SET on CLONE_SET(ELEMENT)");
		dbManager
				.executeUpdate("create index CLONE_SET_ID_ELEMENT_INDEX_CLONE_SET on CLONE_SET(CLONE_SET_ID,ELEMENT)");
	}

	/**
	 * get the query to create the table for links of code fragments
	 * 
	 * @return
	 */
	private String getCodeFragmentLinkTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table CODE_FRAGMENT_LINK(");
		builder.append("CODE_FRAGMENT_LINK_ID LONG PRIMARY KEY,");
		builder.append("BEFORE_ELEMENT_ID LONG,");
		builder.append("AFTER_ELEMENT_ID LONG,");
		builder.append("BEFORE_COMBINED_REVISION_ID LONG,");
		builder.append("AFTER_COMBINED_REVISION_ID LONG,");
		builder.append("CHANGED INTEGER,");
		builder.append("FOREIGN KEY(BEFORE_ELEMENT_ID) REFERENCES CODE_FRAGMENT(CODE_FRAGMENT_ID),");
		builder.append("FOREIGN KEY(AFTER_ELEMENT_ID) REFERENCES CODE_FRAGMENT(CODE_FRAGMENT_ID),");
		builder.append("FOREIGN KEY(BEFORE_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		builder.append("FOREIGN KEY(AFTER_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID)");
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
		dbManager
				.executeUpdate("create index CHANGED_INDEX_CODE_FRAGMENT_LINK on CODE_FRAGMENT_LINK(CHANGED)");
	}

	/**
	 * get the query to create the table for links of clone sets
	 * 
	 * @return
	 */
	private String getCloneSetLinkTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table CLONE_SET_LINK(");
		builder.append("CLONE_SET_LINK_ID LONG,");
		builder.append("BEFORE_ELEMENT_ID LONG,");
		builder.append("AFTER_ELEMENT_ID LONG,");
		builder.append("BEFORE_COMBINED_REVISION_ID LONG,");
		builder.append("AFTER_COMBINED_REVISION_ID LONG,");
		builder.append("CHANGED_ELEMENTS INTEGER,");
		builder.append("ADDED_ELEMENTS INTEGER,");
		builder.append("DELETED_ELEMENTS INTEGER,");
		builder.append("CO_CHANGED_ELEMENTS INTEGER,");
		builder.append("CODE_FRAGMENT_LINK_ID LONG,");
		builder.append("PRIMARY KEY(CLONE_SET_LINK_ID, CODE_FRAGMENT_LINK_ID),");
		builder.append("FOREIGN KEY(BEFORE_ELEMENT_ID) REFERENCES CLONE_SET(CLONE_SET_ID),");
		builder.append("FOREIGN KEY(AFTER_ELEMENT_ID) REFERENCES CLONE_SET(CLONE_SET_ID),");
		builder.append("FOREIGN KEY(BEFORE_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		builder.append("FOREIGN KEY(AFTER_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		builder.append("FOREIGN KEY(CODE_FRAGMENT_LINK_ID) REFERENCES CODE_FRAGMENT_LINK(CODE_FRAGMENT_LINK_ID)");
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
		dbManager
				.executeUpdate("create index CHANGED_ELEMENTS_INDEX_CLONE_SET_LINK on CLONE_SET_LINK(CHANGED_ELEMENTS)");
		dbManager
				.executeUpdate("create index ADDED_ELEMENTS_INDEX_CLONE_SET_LINK on CLONE_SET_LINK(ADDED_ELEMENTS)");
		dbManager
				.executeUpdate("create index DELETED_ELEMENTS_INDEX_CLONE_SET_LINK on CLONE_SET_LINK(DELETED_ELEMENTS)");
		dbManager
				.executeUpdate("create index CO_CHANGED_ELEMENTS_INDEX_CLONE_SET_LINK on CLONE_SET_LINK(CO_CHANGED_ELEMENTS)");
		dbManager
				.executeUpdate("create index CODE_FRAGMENT_LINK_INDEX_CLONE_SET_LINK on CLONE_SET_LINK(CODE_FRAGMENT_LINK_ID)");
	}

	/**
	 * get the query to create the table for genealogies of clones
	 * 
	 * @return
	 */
	private String getCloneGenealogyTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table CLONE_GENEALOGY(");
		builder.append("CLONE_GENEALOGY_ID LONG,");
		builder.append("START_COMBINED_REVISION_ID LONG,");
		builder.append("END_COMBINED_REVISION_ID LONG,");
		builder.append("CLONE_SET_ID LONG,");
		builder.append("CLONE_SET_LINK_ID LONG,");
		builder.append("CHANGES INTEGER,");
		builder.append("ADDITIONS INTEGER,");
		builder.append("DELETIONS INTEGER,");
		builder.append("DEAD INTEGER,");
		builder.append("PRIMARY KEY(CLONE_GENEALOGY_ID,CLONE_SET_ID,CLONE_SET_LINK_ID),");
		builder.append("FOREIGN KEY(START_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		builder.append("FOREIGN KEY(END_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		builder.append("FOREIGN KEY(CLONE_SET_ID) REFERENCES CLONE_SET(CLONE_SET_ID),");
		builder.append("FOREIGN KEY(CLONE_SET_LINK_ID) REFERENCES CLONE_SET_LINK(CLONE_SET_LINK_ID)");
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
				.executeUpdate("create index CLONE_SET_ID_INDEX_CLONE_GENEALOGY on CLONE_GENEALOGY(CLONE_SET_ID)");
		dbManager
				.executeUpdate("create index CLONE_SET_LINK_ID_INDEX_CLONE_GENEALOGY on CLONE_GENEALOGY(CLONE_SET_LINK_ID)");
		dbManager
				.executeUpdate("create index CHANGES_INDEX_CLONE_GENEALOGY on CLONE_GENEALOGY(CHANGES)");
		dbManager
				.executeUpdate("create index ADDITIONS_INDEX_CLONE_GENEALOGY on CLONE_GENEALOGY(ADDITIONS)");
		dbManager
				.executeUpdate("create index DELETIONS_INDEX_CLONE_GENEALOGY on CLONE_GENEALOGY(DELETIONS)");
		dbManager
				.executeUpdate("create index DEAD_INDEX_CLONE_GENEALOGY on CLONE_GENEALOGY(DEAD)");
		dbManager
				.executeUpdate("create index START_END_REVISION_ID_INDEX_CLONE_GENEALOGY on CLONE_GENEALOGY(START_COMBINED_REVISION_ID,END_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index KEYS_INDEX_CLONE_GENEALOGY on CLONE_GENEALOGY(CLONE_GENEALOGY_ID,CLONE_SET_ID,CLONE_SET_LINK_ID)");
	}

	/**
	 * get the query to create the table for genealogies of code fragments
	 * 
	 * @return
	 */
	private String getCodeFragmentGenealogyTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table CODE_FRAGMENT_GENEALOGY(");
		builder.append("CODE_FRAGMENT_GENEALOGY_ID LONG,");
		builder.append("START_COMBINED_REVISION_ID LONG,");
		builder.append("END_COMBINED_REVISION_ID LONG,");
		builder.append("CODE_FRAGMENT_ID LONG,");
		builder.append("CODE_FRAGMENT_LINK_ID LONG,");
		builder.append("CHANGES INTEGER,");
		builder.append("PRIMARY KEY(CODE_FRAGMENT_GENEALOGY_ID,CODE_FRAGMENT_ID,CODE_FRAGMENT_LINK_ID),");
		builder.append("FOREIGN KEY(START_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		builder.append("FOREIGN KEY(END_COMBINED_REVISION_ID) REFERENCES COMBINED_REVISION(COMBINED_REVISION_ID),");
		builder.append("FOREIGN KEY(CODE_FRAGMENT_ID) REFERENCES CODE_FRAGMENT(CODE_FRAGMENT_ID),");
		builder.append("FOREIGN KEY(CODE_FRAGMENT_LINK_ID) REFERENCES CODE_FRAGMENT_LINK(CODE_FRAGMENT_LINK_ID)");
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
				.executeUpdate("create index CODE_FRAGMENT_ID_INDEX_CODE_FRAGMENT_GENEALOGY on CODE_FRAGMENT_GENEALOGY(CODE_FRAGMENT_ID)");
		dbManager
				.executeUpdate("create index CODE_FRAGMENT_LINK_ID_INDEX_CODE_FRAGMENT_GENEALOGY on CODE_FRAGMENT_GENEALOGY(CODE_FRAGMENT_LINK_ID)");
		dbManager
				.executeUpdate("create index CHANGES_INDEX_CODE_FRAGMENT_GENEALOGY on CODE_FRAGMENT_GENEALOGY(CHANGES)");
		dbManager
				.executeUpdate("create index START_END_REVISION_ID_INDEX_CODE_FRAGMENT_GENEALOGY on CODE_FRAGMENT_GENEALOGY(START_COMBINED_REVISION_ID,END_COMBINED_REVISION_ID)");
		dbManager
				.executeUpdate("create index KEYS_INDEX_CODE_FRAGMENT_GENEALOGY on CODE_FRAGMENT_GENEALOGY(CODE_FRAGMENT_GENEALOGY_ID,CODE_FRAGMENT_ID,CODE_FRAGMENT_LINK_ID)");
	}

}
