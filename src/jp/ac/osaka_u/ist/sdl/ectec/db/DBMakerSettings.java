package jp.ac.osaka_u.ist.sdl.ectec.db;

import jp.ac.osaka_u.ist.sdl.ectec.AbstractSettings;
import jp.ac.osaka_u.ist.sdl.ectec.PropertiesReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class DBMakerSettings extends AbstractSettings {

	private boolean overwrite;
	
	public final boolean isOverwrite() {
		return overwrite;
	}
	
	@Override
	protected Options addParticularOptions(Options options) {
		{
			final Option ow = new Option("ow", "overwrite", true, "overwrite db if exists");
			ow.setArgs(1);
			ow.setRequired(false);
			options.addOption(ow);
		}
		
		return options;
	}
	
	@Override
	protected void initializeParticularSettings(CommandLine cmd,
			PropertiesReader propReader) throws Exception {
		overwrite = false;
		if (cmd.hasOption("ow")) {
			final String owStr = cmd.getOptionValue("ow");
			if (owStr.equalsIgnoreCase("y") || owStr.equalsIgnoreCase("yes")) {
				overwrite = true;
			}
		} else {
			final String owProp = propReader.getProperty(DB_OVERWRITE);
			if (owProp != null) {
				if (owProp.equalsIgnoreCase("y") || owProp.equalsIgnoreCase("yes")) {
					overwrite = true;
				}
			}
		}
		
		if (overwrite) {
			logger.info("overwrite is allowed");
		} else {
			logger.info("overwrite is prohibited");
		}
	}
	
}
