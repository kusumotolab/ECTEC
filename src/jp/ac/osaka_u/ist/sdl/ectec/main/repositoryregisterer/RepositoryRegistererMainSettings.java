package jp.ac.osaka_u.ist.sdl.ectec.main.repositoryregisterer;

import java.io.File;

import jp.ac.osaka_u.ist.sdl.ectec.AbstractSettings;
import jp.ac.osaka_u.ist.sdl.ectec.PropertiesReader;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalSettingValueException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A class to have settings for repository registerer
 * 
 * @author k-hotta
 * 
 */
public class RepositoryRegistererMainSettings extends AbstractSettings {

	/**
	 * the path of the input file having the list of repositories
	 */
	private String filePath;

	/**
	 * get the path
	 * 
	 * @return
	 */
	public final String getFilePath() {
		return this.filePath;
	}

	@Override
	protected Options addParticularOptions(Options options) {

		{
			final Option i = new Option("i", "input", true,
					"the path of the input file");
			i.setArgs(1);
			i.setRequired(true);
			options.addOption(i);
		}

		return options;
	}

	@Override
	protected void initializeParticularSettings(CommandLine cmd,
			PropertiesReader propReader) throws Exception {
		filePath = cmd.getOptionValue("i");
		final File givenFile = new File(filePath);
		if (!givenFile.exists()) {
			throw new IllegalSettingValueException(filePath + " doesn't exist.");
		}
		logger.info("the path of input file: " + filePath);
	}

}
