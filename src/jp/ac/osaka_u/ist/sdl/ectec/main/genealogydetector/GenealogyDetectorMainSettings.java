package jp.ac.osaka_u.ist.sdl.ectec.main.genealogydetector;

import jp.ac.osaka_u.ist.sdl.ectec.AbstractSettings;
import jp.ac.osaka_u.ist.sdl.ectec.PropertiesReader;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalSettingValueException;
import jp.ac.osaka_u.ist.sdl.ectec.settings.GenealogyDetectionMode;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A class that has settings of genealogy detection
 * 
 * @author k-hotta
 * 
 */
public class GenealogyDetectorMainSettings extends AbstractSettings {

	private GenealogyDetectionMode mode;

	public final GenealogyDetectionMode getMode() {
		return mode;
	}

	@Override
	protected Options addParticularOptions(Options options) {
		{
			final Option gm = new Option("gm", "mode", true,
					"genealogy detection mode");
			gm.setArgs(1);
			gm.setRequired(true);
			options.addOption(gm);
		}

		return options;
	}

	@Override
	protected void initializeParticularSettings(CommandLine cmd,
			PropertiesReader propReader) throws Exception {
		final String gmStr = cmd.getOptionValue("gm");
		mode = GenealogyDetectionMode.getCorrespondingMode(gmStr);
		if (mode == null) {
			throw new IllegalSettingValueException("unknown mode: " + gmStr);
		}
		logger.info("genealogy detection mode: " + mode);
	}

}
