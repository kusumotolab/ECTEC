package jp.ac.osaka_u.ist.sdl.ectec.main;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.PropertiesKeys;
import jp.ac.osaka_u.ist.sdl.ectec.PropertiesReader;
import jp.ac.osaka_u.ist.sdl.ectec.settings.LoggingLevel;

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
	protected static final Logger logger = LoggingManager
			.getLogger("settings");

	/**
	 * the path of the properties file
	 */
	private String propertyFilePath;

	/**
	 * the level of logging
	 */
	private LoggingLevel loggingLevel;

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

	public final LoggingLevel getLoggingLevel() {
		return this.loggingLevel;
	}

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

	/**
	 * parse and load the given arguments
	 * 
	 * @param args
	 */
	public void load(final String[] args) throws Exception {
		final Options options = defineOptions();

		final CommandLineParser parser = new PosixParser();
		final CommandLine cmd = parser.parse(options, args);

		// initialize the property file path
		propertyFilePath = (cmd.hasOption("p")) ? cmd.getOptionValue("p")
				: null;

		// load the given or default properties file
		final PropertiesReader propReader = (propertyFilePath == null) ? new PropertiesReader()
				: new PropertiesReader(propertyFilePath);

		// initialize the logger
		loggingLevel = (cmd.hasOption("v")) ? LoggingLevel
				.getCorrespondingLevel(cmd.getOptionValue("v")) : LoggingLevel
				.getCorrespondingLevel(propReader.getProperty(VERBOSE_LEVEL));

		logger.info("the logger was initialized as " + loggingLevel.getStr());
		logger.info("the loaded property file: " + propertyFilePath);

		// initialize other common settings
		dbPath = cmd.getOptionValue("d");
		logger.info("the path of the database file: " + dbPath);

		threads = (cmd.hasOption("th")) ? Integer.parseInt(cmd
				.getOptionValue("th")) : Integer.parseInt(propReader
				.getProperty(THREADS));
		logger.info("the number of threads: " + threads);

		maxBatchCount = (cmd.hasOption("mb")) ? Integer.parseInt(cmd
				.getOptionValue("mb")) : Integer.parseInt(propReader
				.getProperty(MAX_BATCH));
		logger.info("the maximum number of batched statements: "
				+ maxBatchCount);

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
			final Option v = new Option("v", "verbose", true, "verbose output");
			v.setArgs(1);
			v.setRequired(false);
			options.addOption(v);
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

		return addParticularOptions(options);
	}

	/**
	 * define particular options for each subsystem
	 * 
	 * @param options
	 * @return
	 */
	protected abstract Options addParticularOptions(final Options options);

	/**
	 * initialize particular settings in each subsystem
	 * 
	 * @param cmd
	 * @param propReader
	 * @throws Exception
	 */
	protected abstract void initializeParticularSettings(final CommandLine cmd,
			final PropertiesReader propReader) throws Exception;

}
