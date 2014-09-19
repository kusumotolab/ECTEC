package jp.ac.osaka_u.ist.sdl.ectec.main.clonedetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.AbstractSettings;
import jp.ac.osaka_u.ist.sdl.ectec.PropertiesReader;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalSettingValueException;
import jp.ac.osaka_u.ist.sdl.ectec.settings.IDStringReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * the settings for clone detector
 * 
 * @author k-hotta
 * 
 */
public class CloneDetectorMainSettings extends AbstractSettings {

	/**
	 * the list of combined revision ids
	 */
	private List<Long> combinedRevisionIds;

	/**
	 * whether detect cross project clones or not
	 */
	private boolean detectCrossProjectClones;

	/**
	 * the threshold of clone size
	 */
	private int cloneSizeThreshold;

	/**
	 * get the list of combined revision ids
	 * 
	 * @return
	 */
	public final List<Long> getCombinedRevisionIds() {
		return Collections.unmodifiableList(combinedRevisionIds);
	}

	/**
	 * get whether detect cross project clones or not
	 * 
	 * @return
	 */
	public final boolean isDetectCrossProjectClones() {
		return detectCrossProjectClones;
	}

	/**
	 * get the threshold of clone size
	 * 
	 * @return
	 */
	public final int getCloneSizeThreshold() {
		return cloneSizeThreshold;
	}

	@Override
	protected Options addParticularOptions(Options options) {
		{
			final Option cr = new Option("cr", "revisions", true,
					"combined revision ids");
			cr.setArgs(1);
			cr.setRequired(false);
			options.addOption(cr);
		}

		{
			final Option cp = new Option("cp", "cross_project", true,
					"whether detect cross project clones");
			cp.setArgs(1);
			cp.setRequired(false);
			options.addOption(cp);
		}

		{
			final Option cst = new Option("cst", "clone-size-threshold", true,
					"the threshold for sizes of clones");
			cst.setArgs(1);
			cst.setRequired(false);
			options.addOption(cst);
		}

		return options;
	}

	@Override
	protected void initializeParticularSettings(CommandLine cmd,
			PropertiesReader propReader) throws Exception {
		combinedRevisionIds = new ArrayList<Long>();
		if (cmd.hasOption("cr")) {
			final List<Long> specifiedCombinedRevisionIds = IDStringReader
					.read(cmd.getOptionValue("cr"));
			logger.info(specifiedCombinedRevisionIds.size()
					+ " combined revisions were specified");
			combinedRevisionIds.addAll(specifiedCombinedRevisionIds);
		} else {
			logger.info("-cr doesn't specified");
			logger.info("targets all the combined revisions registered into the db");
		}

		final String cpStr = (cmd.hasOption("cp")) ? cmd
				.getOptionValue("cp") : propReader
				.getProperty(CROSS_PROJECT_CLONES);
		if (cpStr.equalsIgnoreCase("yes")) {
			detectCrossProjectClones = true;
		} else if (cpStr.equalsIgnoreCase("no")) {
			detectCrossProjectClones = false;
		} else {
			throw new IllegalSettingValueException("illegal value " + cpStr
					+ " for -cp");
		}
		logger.info("detect cross project clones: " + detectCrossProjectClones);

		final String cstStr = (cmd.hasOption("cst")) ? cmd
				.getOptionValue("cst") : propReader
				.getProperty(CLONE_SIZE_THRESHOLD);
		try {
			cloneSizeThreshold = Integer.parseInt(cstStr);
		} catch (Exception e) {
			throw new IllegalSettingValueException("illegal value "
					+ " for -cst, it must be an integer value");
		}
		logger.info("clone size threshold: " + cloneSizeThreshold);
	}
}
