package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import java.util.Properties;

import jp.ac.osaka_u.ist.sdl.ectec.PropertiesKeys;
import jp.ac.osaka_u.ist.sdl.ectec.PropertiesReader;
import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;
import jp.ac.osaka_u.ist.sdl.ectec.settings.CRDSimilarityCalculateMode;
import jp.ac.osaka_u.ist.sdl.ectec.settings.StringNormalizeMode;
import jp.ac.osaka_u.ist.sdl.ectec.settings.CodeFragmentLinkMode;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrintLevel;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

/**
 * A class that reads a given properties file and loads needed configurations
 * from it
 * 
 * @author k-hotta
 * 
 */
final class DefaultAnalyzerSettingsLoader implements PropertiesKeys {

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
	private final String startRevisionIdentifier;

	/**
	 * the identifier of the end revision
	 */
	private final String endRevisionIdentifier;

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

	private DefaultAnalyzerSettingsLoader(final String additionalPath,
			final Language language, final int threads, final String userName,
			final String passwd, final String startRevisionIdentifier,
			final String endRevisionIdentifier,
			final MessagePrintLevel verboseLevel,
			final VersionControlSystem versionControlSystem,
			final boolean overwriteDb, final int maxBatchCount,
			final StringNormalizeMode cloneHashMode,
			final CRDSimilarityCalculateMode crdSimilarityMode,
			final CodeFragmentLinkMode fragmentLinkMode,
			final double similarityThreshold,
			final AnalyzeGranularity granularity) {
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
		this.fragmentLinkMode = fragmentLinkMode;
		this.similarityThreshold = similarityThreshold;
		this.granularity = granularity;
	}

	/*
	 * getters follow
	 */

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

	/*
	 * end of getters
	 */

	/**
	 * load default values of configurations from the given properties file
	 * 
	 * @param propertiesFilePath
	 * @return
	 * @throws Exception
	 */
	final static DefaultAnalyzerSettingsLoader load(
			final String propertiesFilePath) throws Exception {
		final PropertiesReader reader = new PropertiesReader(propertiesFilePath);
		final Properties prop = reader.read();

		return load(prop);
	}

	/**
	 * load default values of configurations from the default properties file
	 * 
	 * @return
	 * @throws Exception
	 */
	final static DefaultAnalyzerSettingsLoader load() throws Exception {
		final PropertiesReader reader = new PropertiesReader();
		final Properties prop = reader.read();

		return load(prop);
	}

	/**
	 * load values of properties
	 * 
	 * @param prop
	 * @return
	 * @throws Exception
	 */
	private static DefaultAnalyzerSettingsLoader load(final Properties prop)
			throws Exception {
		final String additionalPath = (prop.getProperty(ADDITIONAL_PATH)
				.equalsIgnoreCase("none")) ? null : prop
				.getProperty(ADDITIONAL_PATH);

		final Language language = Language.getCorrespondingLanguage(prop
				.getProperty(LANGUAGE));

		final int threads = Integer.parseInt(prop.getProperty(THREADS));

		final String userName = (prop.getProperty(USER_NAME)
				.equalsIgnoreCase("none")) ? null : prop.getProperty(USER_NAME);

		final String passwd = (prop.getProperty(PASSWD)
				.equalsIgnoreCase("none")) ? null : prop.getProperty(PASSWD);

		final String startRevisionIdentifier = (prop
				.getProperty(START_REVISION_IDENTIFIER)
				.equalsIgnoreCase("none")) ? null : prop
				.getProperty(START_REVISION_IDENTIFIER);

		final String endRevisionIdentifier = (prop
				.getProperty(END_REVISION_IDENTIFIER).equalsIgnoreCase("none")) ? null
				: prop.getProperty(END_REVISION_IDENTIFIER);

		final MessagePrintLevel verboseLevel = MessagePrintLevel
				.getCorrespondingLevel(prop.getProperty(VERBOSE_LEVEL));

		final VersionControlSystem versionControlSystem = VersionControlSystem
				.getCorrespondingVersionControlSystem(prop
						.getProperty(VERSION_CONTROL_SYSTEM));

		final boolean overwriteDb = (prop.getProperty(OVERWRITE_DB)
				.equalsIgnoreCase("yes")) ? true : false;

		final int maxBatchCount = Integer.parseInt(prop.getProperty(MAX_BATCH));

		final StringNormalizeMode cloneHashMode = StringNormalizeMode
				.getCorrespondingMode(prop.getProperty(HASH_FOR_CLONE));

		final CRDSimilarityCalculateMode crdSimilarityMode = CRDSimilarityCalculateMode
				.getCorrespondingMode(prop.getProperty(CRD_SIMILARITY));

		final CodeFragmentLinkMode fragmentLinkMode = CodeFragmentLinkMode
				.getCorrespondingMode(prop.getProperty(FRAGMENT_LINK));

		final double similarityThreshold = Double.parseDouble(prop
				.getProperty(SIMILARITY_THRESHOLD));

		final AnalyzeGranularity granularity = AnalyzeGranularity
				.getCorrespondingGranularity(prop.getProperty(GRANULARITY));

		return new DefaultAnalyzerSettingsLoader(additionalPath, language,
				threads, userName, passwd, startRevisionIdentifier,
				endRevisionIdentifier, verboseLevel, versionControlSystem,
				overwriteDb, maxBatchCount, cloneHashMode, crdSimilarityMode,
				fragmentLinkMode, similarityThreshold, granularity);
	}
}
