package jp.ac.osaka_u.ist.sdl.ectec.main.filedetector;

import jp.ac.osaka_u.ist.sdl.ectec.PropertiesReader;
import jp.ac.osaka_u.ist.sdl.ectec.main.AbstractSettings;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalSettingValueException;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A class that has settings for file detector
 * 
 * @author k-hotta
 * 
 */
public class FileDetectorMainSettings extends AbstractSettings {

	/**
	 * the type of the version control system
	 */
	private VersionControlSystem vcs;

	/**
	 * the target programming language
	 */
	private Language language;

	/**
	 * get the type of the version control system
	 * 
	 * @return
	 */
	public final VersionControlSystem getVcs() {
		return this.vcs;
	}

	/**
	 * get the target programming language
	 * 
	 * @return
	 */
	public final Language getLanguage() {
		return this.language;
	}

	@Override
	protected Options addParticularOptions(Options options) {

		{
			final Option vc = new Option("vcs", "version-control-system", true,
					"version control system");
			vc.setArgs(1);
			vc.setRequired(false);
			options.addOption(vc);
		}

		{
			final Option l = new Option("l", "language", true, "language");
			l.setArgs(1);
			l.setRequired(false);
			options.addOption(l);
		}

		return options;
	}

	@Override
	protected void initializeParticularSettings(CommandLine cmd,
			PropertiesReader propReader) throws Exception {
		final String vcsStr = (cmd.hasOption("vcs")) ? cmd
				.getOptionValue("vcs") : propReader
				.getProperty(VERSION_CONTROL_SYSTEM);
		vcs = VersionControlSystem.getCorrespondingVersionControlSystem(vcsStr);
		if (vcs == VersionControlSystem.OTHER) {
			throw new IllegalSettingValueException(
					"unknown version control system: " + vcsStr);
		}
		logger.info("the version control system: " + vcs.getStr());

		final String languageStr = (cmd.hasOption("l")) ? cmd
				.getOptionValue("l") : propReader.getProperty(LANGUAGE);
		language = Language.getCorrespondingLanguage(languageStr);
		if (language == Language.OTHER) {
			throw new IllegalSettingValueException("unknown language: "
					+ languageStr);
		}
		logger.info("the target programming language: " + language.getStr());
	}

}
