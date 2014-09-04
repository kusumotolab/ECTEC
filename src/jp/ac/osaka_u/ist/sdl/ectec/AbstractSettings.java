package jp.ac.osaka_u.ist.sdl.ectec;

import java.io.File;

import jp.ac.osaka_u.ist.sdl.ectec.db.IDBConfig;
import jp.ac.osaka_u.ist.sdl.ectec.db.PostgreSQLDBConfig;
import jp.ac.osaka_u.ist.sdl.ectec.db.SQLiteDBConfig;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalSettingValueException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

/**
 * An abstract class having common functions to keep runtime settings
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractSettings implements PropertiesKeys {

	/**
	 * the logger for settings
	 */
	protected static final Logger logger = LoggingManager.getLogger("settings");

	/**
	 * the logger for errors
	 */
	protected static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * the path of the properties file
	 */
	private String propertyFilePath;

	/**
	 * the path of the db
	 */
	private String dbPath;

	/**
	 * the number of threads
	 */
	private int threads;

	/**
	 * the maximum number of batched statements
	 */
	private int maxBatchCount;

	/**
	 * the header of id
	 */
	private short headerOfId;

	/**
	 * the configuration of db
	 */
	private IDBConfig dbConfig;

	public final String getPropertyFilePath() {
		return this.propertyFilePath;
	}

	public final String getDbPath() {
		return this.dbPath;
	}

	public final int getThreads() {
		return this.threads;
	}

	public final int getMaxBatchCount() {
		return this.maxBatchCount;
	}

	public final short getHeaderOfId() {
		return this.headerOfId;
	}

	public final IDBConfig getDBConfig() {
		return dbConfig;
	}

	/**
	 * parse and load the given arguments
	 * 
	 * @param args
	 * @throws Exception
	 */
	public void load(final String[] args) throws Exception {
		load(args, true);
	}

	/**
	 * parse and load the given arguments
	 * 
	 * @param args
	 * @param dbMustExist
	 * @throws Exception
	 */
	public void load(final String[] args, final boolean dbMustExist)
			throws Exception {
		final Options options = defineOptions();

		final CommandLineParser parser = new PosixParser();
		final CommandLine cmd = parser.parse(options, args);

		// initialize the property file path
		propertyFilePath = (cmd.hasOption("p")) ? cmd.getOptionValue("p")
				: null;

		// load the given or default properties file
		final PropertiesReader propReader = (propertyFilePath == null) ? new PropertiesReader()
				: new PropertiesReader(propertyFilePath);
		logger.info("the loaded property file: "
				+ propReader.getLoadedFileName());

		// initialize other common settings
		dbPath = cmd.getOptionValue("d");

		if (dbPath == null) {
			throw new IllegalSettingValueException("dbPath must not be null.");
		}
		final File dbFile = new File(dbPath);
		if (dbMustExist && !dbFile.exists()) {
			throw new IllegalSettingValueException(dbPath + " doesn't exist.");
		}

		logger.info("the path of the database file: " + dbPath);

		threads = (cmd.hasOption("th")) ? Integer.parseInt(cmd
				.getOptionValue("th")) : Integer.parseInt(propReader
				.getProperty(THREADS));

		if (threads <= 0) {
			throw new IllegalSettingValueException(
					"the number of threads must be more than 0 but the specified value is "
							+ threads);
		}

		logger.info("the number of threads: " + threads);

		maxBatchCount = (cmd.hasOption("mb")) ? Integer.parseInt(cmd
				.getOptionValue("mb")) : Integer.parseInt(propReader
				.getProperty(MAX_BATCH));

		if (maxBatchCount <= 0) {
			throw new IllegalSettingValueException(
					"the maximum number of batched statements must be more than 0 but the specified value is "
							+ maxBatchCount);
		}

		logger.info("the maximum number of batched statements: "
				+ maxBatchCount);

		if (cmd.hasOption("id")) {
			headerOfId = Short.parseShort(cmd.getOptionValue("id"));
			if (headerOfId < 0) {
				throw new IllegalSettingValueException(
						"the header of id must not be a negative value");
			}
		} else {
			headerOfId = -1;
		}

		dbConfig = null;
		final String dbms = propReader.getProperty(DBMS);
		if (dbms != null) {
			if (dbms.equalsIgnoreCase("postgre")
					|| dbms.equalsIgnoreCase("PostgreSQL")
					|| dbms.equalsIgnoreCase("p")) {
				final String dbUserName = propReader.getProperty(DB_USERNAME);
				final String dbPasswd = propReader.getProperty(DB_PASSWD);

				dbConfig = new PostgreSQLDBConfig(this.dbPath, dbUserName,
						dbPasswd);

				String hiddenPasswd = "";
				for (int count = 0; count < dbPasswd.length(); count++) {
					hiddenPasswd = hiddenPasswd + "*";
				}

				logger.info("the specified DBMS: PostgreSQL");
				logger.info("the user name of DB: " + dbUserName);
				logger.info("the password of DB: " + hiddenPasswd);
			}
		}

		if (dbConfig == null) {
			dbConfig = new SQLiteDBConfig(this.dbPath);
			logger.info("the specified DBMS: SQLite");
		}

		// initialize other settings
		initializeParticularSettings(cmd, propReader);
	}

	/**
	 * define options
	 * 
	 * @return
	 */
	protected final Options defineOptions() {
		final Options options = new Options();

		{
			final Option p = new Option("p", "properties", true,
					"properties file");
			p.setArgs(1);
			p.setRequired(false);
			options.addOption(p);
		}

		{
			final Option d = new Option("d", "db", true, "database");
			d.setArgs(1);
			d.setRequired(true);
			options.addOption(d);
		}

		{
			final Option th = new Option("th", "threads", true,
					"the number of maximum threads");
			th.setArgs(1);
			th.setRequired(false);
			options.addOption(th);
		}

		{
			final Option mb = new Option("mb", "max-batch", true,
					"the maximum number of batched statements");
			mb.setArgs(1);
			mb.setRequired(false);
			options.addOption(mb);
		}

		{
			final Option id = new Option("id", "idhead", true, "header of id");
			id.setArgs(1);
			id.setRequired(false);
			options.addOption(id);
		}

		return addParticularOptions(options);
	}

	/**
	 * define particular options for each subsystem <br>
	 * override this method if any particular options are required
	 * 
	 * @param options
	 * @return
	 */
	protected Options addParticularOptions(final Options options) {
		return options;
	}

	/**
	 * initialize particular settings in each subsystem <br>
	 * override this method if any particular options are required
	 * 
	 * @param cmd
	 * @param propReader
	 * @throws Exception
	 */
	protected void initializeParticularSettings(final CommandLine cmd,
			final PropertiesReader propReader) throws Exception {
		return; // do nothing on default
	}

}
