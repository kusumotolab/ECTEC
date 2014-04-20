package jp.ac.osaka_u.ist.sdl.ectec.main.clonelinker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.PropertiesReader;
import jp.ac.osaka_u.ist.sdl.ectec.main.AbstractSettings;
import jp.ac.osaka_u.ist.sdl.ectec.settings.IDStringReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A class that has settings for detecting clone set links
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkDetectorMainSettings extends AbstractSettings {

	/**
	 * the list of combined commit ids
	 */
	private List<Long> combinedCommitIds;

	/**
	 * get the list of combined commit ids
	 * 
	 * @return
	 */
	public final List<Long> getCombinedCommitIds() {
		return Collections.unmodifiableList(combinedCommitIds);
	}

	@Override
	protected Options addParticularOptions(Options options) {
		{
			final Option cc = new Option("cc", "commits", true,
					"combined commit ids");
			cc.setArgs(1);
			cc.setRequired(false);
			options.addOption(cc);
		}

		return options;
	}

	@Override
	protected void initializeParticularSettings(CommandLine cmd,
			PropertiesReader propReader) throws Exception {
		combinedCommitIds = new ArrayList<Long>();
		if (cmd.hasOption("cc")) {
			final List<Long> specifiedCombinedCommitIds = IDStringReader
					.read(cmd.getOptionValue("cc"));
			logger.info(specifiedCombinedCommitIds.size()
					+ " combined revisions were specified");
			combinedCommitIds.addAll(specifiedCombinedCommitIds);
		} else {
			logger.info("-cc doesn't specified");
			logger.info("targets all the combined commits registered into the db");
		}
	}

}
