package jp.ac.osaka_u.ist.sdl.ectec.main.revisiondetector;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.AbstractSettings;
import jp.ac.osaka_u.ist.sdl.ectec.PropertiesReader;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalSettingValueException;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A class that has settings for revision detector
 * 
 * @author k-hotta
 * 
 */
public class RevisionDetectorMainSettings extends AbstractSettings {

	/**
	 * the target programming language
	 */
	private Language language;

	/**
	 * the set of ids of the target repositories
	 */
	private Set<Long> repositoryIds;

	/**
	 * the map between repository ids and the set of revisions to be ignored
	 */
	private ConcurrentMap<Long, Set<String>> ignoredRevisions;

	/**
	 * get the target programming language
	 * 
	 * @return
	 */
	public final Language getLanguage() {
		return this.language;
	}

	/**
	 * get the set of ids of the target repositories
	 * 
	 * @return
	 */
	public final Set<Long> getRepositoryIds() {
		return Collections.unmodifiableSet(this.repositoryIds);
	}

	/**
	 * get the map between repository id and revisions to be ignored
	 * 
	 * @return
	 */
	public final ConcurrentMap<Long, Set<String>> getIgnoredRevisions() {
		return ignoredRevisions;
	}

	@Override
	protected Options addParticularOptions(Options options) {

		{
			final Option l = new Option("l", "language", true, "language");
			l.setArgs(1);
			l.setRequired(false);
			options.addOption(l);
		}

		{
			final Option r = new Option("r", "repository", true,
					"repository id");
			r.setArgs(1);
			r.setRequired(false);
			options.addOption(r);
		}

		{

			final Option igl = new Option("igl", "ignore list", true,
					"list of revisions to be ignored");
			igl.setArgs(1);
			igl.setRequired(false);
			options.addOption(igl);
		}

		return options;
	}

	@Override
	protected void initializeParticularSettings(CommandLine cmd,
			PropertiesReader propReader) throws Exception {
		final String languageStr = (cmd.hasOption("l")) ? cmd
				.getOptionValue("l") : propReader.getProperty(LANGUAGE);
		language = Language.getCorrespondingLanguage(languageStr);
		if (language == Language.OTHER) {
			throw new IllegalSettingValueException("unknown language: "
					+ languageStr);
		}
		logger.info("the target programming language: " + language.getStr());

		repositoryIds = new TreeSet<Long>();

		if (cmd.hasOption("r")) {
			final String[] split = cmd.getOptionValue("r").split(",");
			for (final String repositoryIdStr : split) {
				try {
					final long repositoryId = Long.parseLong(repositoryIdStr);
					repositoryIds.add(repositoryId);
				} catch (Exception e) {
					eLogger.warn("illegal value is specified for -r and will be ignored: "
							+ repositoryIdStr);
				}
			}

			if (repositoryIds.isEmpty()) {
				throw new IllegalSettingValueException(
						"no valid repository id was specified in -r: "
								+ cmd.getOptionValue("r"));
			}
			logger.info(repositoryIds.size() + " repositories were specified");
		} else {
			logger.info("-r doesn't specified");
			logger.info("targets all the repositories registered into the db");
		}

		ignoredRevisions = new ConcurrentHashMap<Long, Set<String>>();
		if (cmd.hasOption("igl")) {
			final String ignoreListPath = cmd.getOptionValue("igl");
			final File ignoreListFile = new File(ignoreListPath);
			if (!ignoreListFile.exists()) {
				throw new IllegalSettingValueException("cannot find "
						+ ignoreListPath);
			}

			ignoredRevisions.putAll(IgnoreListReader.read(ignoreListFile));
			logger.info("loaded ignored list: " + ignoreListPath);
		} else {
			logger.info("-igl doesn't specified");
			logger.info("targets all the revisions");
		}

	}
}
