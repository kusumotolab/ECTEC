package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;
import jp.ac.osaka_u.ist.sdl.ectec.settings.CRDSimilarityCalculateMode;
import jp.ac.osaka_u.ist.sdl.ectec.settings.StringNormalizeMode;
import jp.ac.osaka_u.ist.sdl.ectec.settings.CodeFragmentLinkMode;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrintLevel;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 * A class to store settings of the analyzer
 * 
 * @author k-hotta
 * 
 */
final class AnalyzerSettings {

	/**
	 * the path of the repository to be analyzed
	 */
	private final String repositoryPath;

	/**
	 * the path of the db file that stores the results of the analysis
	 */
	private final String dbPath;

	/**
	 * the additional path for analyzing a part of the repository
	 */
	private final String additionalPath;

	/**
	 * the target programming language
	 */
	private final Language language;

	/**
	 * the number of maximum threads
	 */
	private final int threads;

	/**
	 * the user name used to access the repository
	 */
	private final String userName;

	/**
	 * the password used to access the repository
	 */
	private final String passwd;

	/**
	 * the identifier of the start revision
	 */
	private String startRevisionIdentifier;

	/**
	 * the identifier of the end revision
	 */
	private String endRevisionIdentifier;

	/**
	 * the level of verbose output
	 */
	private final MessagePrintLevel verboseLevel;

	/**
	 * the version control system that manages the target repository
	 */
	private final VersionControlSystem versionControlSystem;

	/**
	 * whether overwrite the db if it has already existed
	 */
	private final boolean overwriteDb;

	/**
	 * the maximum number of statements that are batched
	 */
	private final int maxBatchCount;

	/**
	 * the mode to calculate hash values for clone detection
	 */
	private final StringNormalizeMode cloneHashMode;

	/**
	 * the mode to calculate crd similarities
	 */
	private final CRDSimilarityCalculateMode crdSimilarityMode;

	/**
	 * the mode to link code fragments
	 */
	private final CodeFragmentLinkMode fragmentLinkMode;

	/**
	 * the threshold for similarities between crds
	 */
	private final double similarityThreshold;

	/**
	 * the granularity of the analysis
	 */
	private final AnalyzeGranularity granularity;

	/**
	 * the path of the properties file
	 */
	private final String propertiesFilePath;

	private AnalyzerSettings(final String repositoryPath, final String dbPath,
			final String additionalPath, final Language language,
			final int threads, final String userName, final String passwd,
			final String startRevisionIdentifier,
			final String endRevisionIdentifier,
			final MessagePrintLevel verboseLevel,
			final VersionControlSystem versionControlSystem,
			final boolean overwriteDb, final int maxBatchCount,
			final StringNormalizeMode cloneHashMode,
			final CRDSimilarityCalculateMode crdSimilarityMode,
			final CodeFragmentLinkMode fragmentLinkMode,
			final double similarityThreshold,
			final AnalyzeGranularity granularity,
			final String propertiesFilePath) {
		this.repositoryPath = repositoryPath;
		this.dbPath = dbPath;
		this.additionalPath = additionalPath;
		this.language = language;
		this.threads = threads;
		this.userName = userName;
		this.passwd = passwd;
		this.startRevisionIdentifier = startRevisionIdentifier;
		this.endRevisionIdentifier = endRevisionIdentifier;
		this.verboseLevel = verboseLevel;
		this.versionControlSystem = versionControlSystem;
		this.overwriteDb = overwriteDb;
		this.maxBatchCount = maxBatchCount;
		this.cloneHashMode = cloneHashMode;
		this.crdSimilarityMode = crdSimilarityMode;
		this.propertiesFilePath = propertiesFilePath;
		this.similarityThreshold = similarityThreshold;
		this.granularity = granularity;
		this.fragmentLinkMode = fragmentLinkMode;
	}

	/*
	 * getters follow
	 */

	final String getRepositoryPath() {
		return repositoryPath;
	}

	final String getDbPath() {
		return dbPath;
	}

	final String getAdditionalPath() {
		return additionalPath;
	}

	final Language getLanguage() {
		return language;
	}

	final int getThreads() {
		return threads;
	}

	final String getUserName() {
		return userName;
	}

	final String getPasswd() {
		return passwd;
	}

	final String getStartRevisionIdentifier() {
		return startRevisionIdentifier;
	}

	final String getEndRevisionIdentifier() {
		return endRevisionIdentifier;
	}

	final MessagePrintLevel getVerboseLevel() {
		return verboseLevel;
	}

	final VersionControlSystem getVersionControlSystem() {
		return versionControlSystem;
	}

	final boolean isOverwriteDb() {
		return overwriteDb;
	}

	final int getMaxBatchCount() {
		return maxBatchCount;
	}

	final String getPropertiesFilePath() {
		return propertiesFilePath;
	}

	final StringNormalizeMode getCloneHashCalculateMode() {
		return cloneHashMode;
	}

	final CRDSimilarityCalculateMode getCrdSimilarityMode() {
		return crdSimilarityMode;
	}

	final CodeFragmentLinkMode getFragmentLinkMode() {
		return fragmentLinkMode;
	}

	final double getSimilarityThreshold() {
		return similarityThreshold;
	}

	final AnalyzeGranularity getGranularity() {
		return granularity;
	}

	final void setStartRevisionIdentifier(final String startRevisionIdentifier) {
		this.startRevisionIdentifier = startRevisionIdentifier;
	}

	final void setEndRevisionIdentifier(final String endRevisionIdentifier) {
		this.endRevisionIdentifier = endRevisionIdentifier;
	}

	static AnalyzerSettings parseArgs(final String[] args) throws Exception {
		final Options options = defineOptions();

		final CommandLineParser parser = new PosixParser();
		final CommandLine cmd = parser.parse(options, args);

		final String propertiesFilePath = (cmd.hasOption("p")) ? cmd
				.getOptionValue("p") : null;
		final DefaultAnalyzerSettingsLoader defaultLoader = (propertiesFilePath == null) ? DefaultAnalyzerSettingsLoader
				.load() : DefaultAnalyzerSettingsLoader
				.load(propertiesFilePath);

		final String repositoryPath = cmd.getOptionValue("r");

		final String dbPath = cmd.getOptionValue("d");

		final String additionalPath = (cmd.hasOption("a")) ? cmd
				.getOptionValue("a") : defaultLoader.getAdditionalPath();

		final Language language = (cmd.hasOption("l")) ? Language
				.getCorrespondingLanguage(cmd.getOptionValue("l"))
				: defaultLoader.getLanguage();

		final int threads = (cmd.hasOption("th")) ? Integer.parseInt(cmd
				.getOptionValue("th")) : defaultLoader.getThreads();

		final String userName = (cmd.hasOption("u")) ? cmd.getOptionValue("u")
				: defaultLoader.getUserName();

		final String passwd = (cmd.hasOption("pw")) ? cmd.getOptionValue("pw")
				: defaultLoader.getPasswd();

		final String startRevisionIdentifier = (cmd.hasOption("s")) ? cmd
				.getOptionValue("s") : defaultLoader
				.getStartRevisionIdentifier();

		final String endRevisionIdentifier = (cmd.hasOption("e")) ? cmd
				.getOptionValue("e") : defaultLoader.getEndRevisionIdentifier();

		final MessagePrintLevel verboseLevel = (cmd.hasOption("v")) ? MessagePrintLevel
				.getCorrespondingLevel(cmd.getOptionValue("v")) : defaultLoader
				.getVerboseLevel();

		final VersionControlSystem versionControlSystem = (cmd.hasOption("vc")) ? VersionControlSystem
				.getCorrespondingVersionControlSystem(cmd.getOptionValue("vc"))
				: defaultLoader.getVersionControlSystem();

		final boolean overwriteDb = (cmd.hasOption("ow")) ? ((cmd
				.getOptionValue("ow").equalsIgnoreCase("yes") ? true : false))
				: defaultLoader.isOverwriteDb();

		final int maxBatchCount = (cmd.hasOption("mb")) ? Integer.parseInt(cmd
				.getOptionValue("mb")) : defaultLoader.getMaxBatchCount();

		final StringNormalizeMode cloneHashMode = (cmd.hasOption("ch")) ? StringNormalizeMode
				.getCorrespondingMode(cmd.getOptionValue("ch")) : defaultLoader
				.getCloneHashCalculateMode();

		final CRDSimilarityCalculateMode crdSimilarityMode = (cmd
				.hasOption("cs")) ? CRDSimilarityCalculateMode
				.getCorrespondingMode(cmd.getOptionValue("cs")) : defaultLoader
				.getCrdSimilarityMode();

		final CodeFragmentLinkMode fragmentLinkMode = (cmd.hasOption("fl")) ? CodeFragmentLinkMode
				.getCorrespondingMode(cmd.getOptionValue("fl")) : defaultLoader
				.getFragmentLinkMode();

		final double similarityThreshold = (cmd.hasOption("st")) ? Double
				.parseDouble(cmd.getOptionValue("st")) : defaultLoader
				.getSimilarityThreshold();

		final AnalyzeGranularity granularity = (cmd.hasOption("g")) ? AnalyzeGranularity
				.getCorrespondingGranularity(cmd.getOptionValue("g"))
				: defaultLoader.getGranularity();

		return new AnalyzerSettings(repositoryPath, dbPath, additionalPath,
				language, threads, userName, passwd, startRevisionIdentifier,
				endRevisionIdentifier, verboseLevel, versionControlSystem,
				overwriteDb, maxBatchCount, cloneHashMode, crdSimilarityMode,
				fragmentLinkMode, similarityThreshold, granularity,
				propertiesFilePath);
	}

	/**
	 * define options
	 * 
	 * @return
	 */
	private static Options defineOptions() {
		final Options options = new Options();

		{
			final Option r = new Option("r", "repository", true, "repository");
			r.setArgs(1);
			r.setRequired(true);
			options.addOption(r);
		}

		{
			final Option d = new Option("d", "db", true, "database");
			d.setArgs(1);
			d.setRequired(true);
			options.addOption(d);
		}

		{
			final Option a = new Option("a", "additional", true,
					"additional path");
			a.setArgs(1);
			a.setRequired(false);
			options.addOption(a);
		}

		{
			final Option l = new Option("l", "language", true, "language");
			l.setArgs(1);
			l.setRequired(false);
			options.addOption(l);
		}

		{
			final Option th = new Option("th", "threads", true,
					"the number of maximum threads");
			th.setArgs(1);
			th.setRequired(false);
			options.addOption(th);
		}

		{
			final Option u = new Option("u", "user", true, "user name");
			u.setArgs(1);
			u.setRequired(false);
			options.addOption(u);
		}

		{
			final Option pw = new Option("pw", "password", true, "password");
			pw.setArgs(1);
			pw.setRequired(false);
			options.addOption(pw);
		}

		{
			final Option s = new Option("s", "start", true, "start revision");
			s.setArgs(1);
			s.setRequired(false);
			options.addOption(s);
		}

		{
			final Option e = new Option("e", "end", true, "end revision");
			e.setArgs(1);
			e.setRequired(false);
			options.addOption(e);
		}

		{
			final Option v = new Option("v", "verbose", true, "verbose output");
			v.setArgs(1);
			v.setRequired(false);
			options.addOption(v);
		}

		{
			final Option vc = new Option("vc", "version-control", true,
					"version control system");
			vc.setArgs(1);
			vc.setRequired(false);
			options.addOption(vc);
		}

		{
			final Option ow = new Option("ow", "overwrite", true,
					"overwrite db");
			ow.setArgs(1);
			ow.setRequired(false);
			options.addOption(ow);
		}

		{
			final Option mb = new Option("mb", "max-batch", true,
					"the maximum number of batched statements");
			mb.setArgs(1);
			mb.setRequired(false);
			options.addOption(mb);
		}

		{
			final Option ch = new Option("ch", "clone-hash", true,
					"how to calculate hash values for clone detection");
			ch.setArgs(1);
			ch.setRequired(false);
			options.addOption(ch);
		}

		{
			final Option cs = new Option("cs", "crd-similarity", true,
					"how to calculate crd similarity");
			cs.setArgs(1);
			cs.setRequired(false);
			options.addOption(cs);
		}

		{
			final Option fl = new Option("fl", "fragment-link", true,
					"how to link code fragments");
			fl.setArgs(1);
			fl.setRequired(false);
			options.addOption(fl);
		}

		{
			final Option st = new Option("st", "similarity-threshold", true,
					"the threshold for similarities of crds");
			st.setArgs(1);
			st.setRequired(false);
			options.addOption(st);
		}

		{
			final Option g = new Option("g", "granularity", true,
					"granularity of the analysis");
			g.setArgs(1);
			g.setRequired(false);
			options.addOption(g);
		}

		{
			final Option p = new Option("p", "properties", true,
					"properties file");
			p.setArgs(1);
			p.setRequired(false);
			options.addOption(p);
		}

		return options;
	}
}
