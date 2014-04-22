package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.PropertiesReader;
import jp.ac.osaka_u.ist.sdl.ectec.main.AbstractSettings;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalSettingValueException;
import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;
import jp.ac.osaka_u.ist.sdl.ectec.settings.IDStringReader;
import jp.ac.osaka_u.ist.sdl.ectec.settings.StringNormalizeMode;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CodeFragmentDetectorMainSettings extends AbstractSettings {

	/**
	 * the list of file ids
	 */
	private List<Long> fileIds;

	/**
	 * the granularity of the analysis
	 */
	private AnalyzeGranularity granularity;

	/**
	 * the mode to calculate hash values for clone detection
	 */
	private StringNormalizeMode cloneHashMode;

	/**
	 * get the list of file ids
	 * 
	 * @return
	 */
	public final List<Long> getFileIds() {
		return Collections.unmodifiableList(fileIds);
	}

	/**
	 * get the granularity of the analysis
	 * 
	 * @return
	 */
	public final AnalyzeGranularity getGranularity() {
		return this.granularity;
	}

	/**
	 * get the mode to calculate hash values for clone detection
	 * 
	 * @return
	 */
	public final StringNormalizeMode getCloneHashMode() {
		return this.cloneHashMode;
	}

	/**
	 * define particular options for each subsystem
	 * 
	 * @param options
	 * @return
	 */
	@Override
	protected Options addParticularOptions(final Options options) {
		{
			final Option f = new Option("f", "file", true, "file id");
			f.setArgs(1);
			f.setRequired(false);
			options.addOption(f);
		}

		{
			final Option g = new Option("g", "granularity", true,
					"granularity of the analysis");
			g.setArgs(1);
			g.setRequired(false);
			options.addOption(g);
		}

		{
			final Option ch = new Option("ch", "clone-hash", true,
					"how to calculate hash values for clone detection");
			ch.setArgs(1);
			ch.setRequired(false);
			options.addOption(ch);
		}

		return options;
	}

	/**
	 * initialize particular settings in each subsystem
	 * 
	 * @param cmd
	 * @param propReader
	 * @throws Exception
	 */
	@Override
	protected void initializeParticularSettings(final CommandLine cmd,
			final PropertiesReader propReader) throws Exception {
		fileIds = new ArrayList<Long>();
		if (cmd.hasOption("f")) {
			final List<Long> specifiedFileIds = IDStringReader.read(cmd
					.getOptionValue("f"));
			logger.info(specifiedFileIds.size() + " files were specified");
			fileIds.addAll(specifiedFileIds);
		} else {
			logger.info("-f doesn't specified");
			logger.info("targets all the files registered into the db");
		}

		final String granularityStr = (cmd.hasOption("g")) ? cmd
				.getOptionValue("g") : propReader.getProperty(GRANULARITY);
		granularity = AnalyzeGranularity
				.getCorrespondingGranularity(granularityStr);
		if (granularity == null) {
			throw new IllegalSettingValueException("unknown granularity: "
					+ granularityStr);
		}
		logger.info("analyze granularity: " + granularity.toString());

		final String cloneHashModeStr = cmd.hasOption("ch") ? cmd
				.getOptionValue("ch") : propReader.getProperty(HASH_FOR_CLONE);
		cloneHashMode = StringNormalizeMode
				.getCorrespondingMode(cloneHashModeStr);
		if (cloneHashMode == null) {
			throw new IllegalSettingValueException(
					"unknown normalize mode for clone detection: "
							+ cloneHashModeStr);
		}
		logger.info("normalize mode for clone detection: "
				+ cloneHashMode.toString());
	}
}
