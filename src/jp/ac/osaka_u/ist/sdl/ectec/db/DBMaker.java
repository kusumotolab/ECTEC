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

		dbManager.setAutoCommit(false);
	}

	/**
	 * create new tables
	 * 
	 * @throws Exception
	 */
	public void createNewTables() throws Exception {
		dbManager.executeUpdate(getRevisionTableQuery());
		dbManager.executeUpdate(getFileTableQuery());
		dbManager.executeUpdate(getCodeFragmentTableQuery());
		dbManager.executeUpdate(getCloneSetTableQuery());
		dbManager.executeUpdate(getCodeFragmentLinkTableQuery());
		dbManager.executeUpdate(getCloneSetLinkTableQuery());
		dbManager.executeUpdate(getCloneGenealogyTableQuery());
		dbManager.executeUpdate(getCrdQuery());
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

	/*
	 * definitions of each table follow
	 */

	/**
	 * get the query to create the revision table
	 * 
	 * @return
	 */
	private String getRevisionTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table REVISION(");
		builder.append("REVISION_ID LONG PRIMARY KEY,");
		builder.append("REVISION_IDENTIFIER TEXT UNIQUE");
		builder.append(")");

		return builder.toString();
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
		builder.append("FILE_PATH TEXT,");
		builder.append("START_REVISION_ID LONG,");
		builder.append("END_REVISION_ID LONG");
		builder.append(")");

		return builder.toString();
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
		builder.append("START_REVISION_ID LONG,");
		builder.append("END_REVISION_ID LONG");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * get the query to create the clone set table
	 * 
	 * @return
	 */
	private String getCloneSetTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table CLONE_SET(");
		builder.append("CLONE_SET_ID LONG PRIMARY KEY,");
		builder.append("OWNER_REVISION_ID LONG,");
		builder.append("ELEMENTS TEXT NOT NULL");
		builder.append(")");

		return builder.toString();
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
		builder.append("BEFORE_REVISION_ID LONG,");
		builder.append("AFTER_REVISION_ID LONG,");
		builder.append("CHANGED INTEGER");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * get the query to create the table for links of clone sets
	 * 
	 * @return
	 */
	private String getCloneSetLinkTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table CLONE_SET_LINK(");
		builder.append("CLONE_SET_LINK_ID LONG PRIMARY KEY,");
		builder.append("BEFORE_ELEMENT_ID LONG,");
		builder.append("AFTER_ELEMENT_ID LONG,");
		builder.append("BEFORE_REVISION_ID LONG,");
		builder.append("AFTER_REVISION_ID LONG,");
		builder.append("CHANGED_ELEMENTS INTEGER,");
		builder.append("ADDED_ELEMENTS INTEGER,");
		builder.append("DELETED_ELEMENTS INTEGER,");
		builder.append("CO_CHANGED_ELEMENTS INTEGER,");
		builder.append("CODE_FRAGMENT_LINKS TEXT NOT NULL");
		builder.append(")");

		return builder.toString();
	}

	/**
	 * get the query to create the table for genealogies of clones
	 * 
	 * @return
	 */
	private String getCloneGenealogyTableQuery() {
		final StringBuilder builder = new StringBuilder();

		builder.append("create table CLONE_GENEALOGY(");
		builder.append("CLONE_GENEALOGY_ID LONG PRIMARY KEY,");
		builder.append("START_REVISION_ID LONG,");
		builder.append("END_REVISION_ID LONG,");
		builder.append("CLONES TEXT NOT NULL,");
		builder.append("CLONE_LINKS TEXT NOT NULL,");
		builder.append("CHANGES INTEGER,");
		builder.append("ADDITIONS INTEGER,");
		builder.append("DELETIONS INTEGER,");
		builder.append("DEAD INTEGER");
		builder.append(")");

		return builder.toString();
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
		builder.append("CM INTEGER,");
		builder.append("ANCESTORS TEXT NOT NULL,");
		builder.append("FULL_TEXT TEXT NOT NULL");
		builder.append(")");

		return builder.toString();
	}

}
