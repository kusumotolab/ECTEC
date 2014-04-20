package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import jp.ac.osaka_u.ist.sdl.ectec.PropertiesReader;
import jp.ac.osaka_u.ist.sdl.ectec.main.AbstractSettings;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalSettingValueException;
import jp.ac.osaka_u.ist.sdl.ectec.settings.CRDSimilarityCalculateMode;
import jp.ac.osaka_u.ist.sdl.ectec.settings.CodeFragmentLinkMode;
import jp.ac.osaka_u.ist.sdl.ectec.settings.IDStringReader;

/**
 * A class that has settings of code fragment linker
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkDetectorMainSettings extends AbstractSettings {

	/**
	 * the list of combined commit ids
	 */
	private List<Long> combinedCommitIds;

	/**
	 * whether detect cross project links or not
	 */
	private boolean detectCrossProjectLinks;

	/**
	 * whether detect links from fragments that are not in any clones in before
	 * revision
	 */
	private boolean onlyFragmentInClonesInBeforeRevision;

	/**
	 * the threshold for similarities between crds
	 */
	private double similarityThreshold;

	/**
	 * the mode to calculate crd similarities
	 */
	private CRDSimilarityCalculateMode crdSimilarityMode;

	/**
	 * the mode to link code fragments
	 */
	private CodeFragmentLinkMode fragmentLinkMode;

	/**
	 * get the list of combined commit ids
	 * 
	 * @return
	 */
	public final List<Long> getCombinedCommitIds() {
		return Collections.unmodifiableList(combinedCommitIds);
	}

	/**
	 * whether detect cross project links or not
	 * 
	 * @return
	 */
	public final boolean isDetectCrossProjectLinks() {
		return detectCrossProjectLinks;
	}

	/**
	 * whether detect links from fragments that are not in any clones in before
	 * revision
	 * 
	 * @return
	 */
	public final boolean isOnlyFragmentInClonesInBeforeRevision() {
		return onlyFragmentInClonesInBeforeRevision;
	}

	/**
	 * get the threshold for similarities between crds
	 * 
	 * @return
	 */
	public final double getSimilarityThreshold() {
		return similarityThreshold;
	}

	/**
	 * get the mode to calculate crd similarities
	 * 
	 * @return
	 */
	public final CRDSimilarityCalculateMode getCrdSimilarityMode() {
		return crdSimilarityMode;
	}

	/**
	 * the mode to link code fragments
	 * 
	 * @return
	 */
	public final CodeFragmentLinkMode getFragmentLinkMode() {
		return fragmentLinkMode;
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

		{
			final Option cp = new Option("cp", "cross_project", true,
					"whether detect cross project links");
			cp.setArgs(1);
			cp.setRequired(false);
			options.addOption(cp);
		}

		{
			final Option bc = new Option("bc", "beforeclone", true,
					"detect links from fragments that are not in any clones in before revision");
			bc.setArgs(1);
			bc.setRequired(false);
			options.addOption(bc);
		}

		{
			final Option st = new Option("st", "similarity-threshold", true,
					"the threshold for similarities of crds");
			st.setArgs(1);
			st.setRequired(false);
			options.addOption(st);
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

		final String cpStr = (cmd.hasOption("cp")) ? cmd.getOptionValue("cp")
				: propReader.getProperty(CROSS_PROJECT_CLONES);
		if (cpStr.equalsIgnoreCase("yes")) {
			detectCrossProjectLinks = true;
		} else if (cpStr.equalsIgnoreCase("no")) {
			detectCrossProjectLinks = false;
		} else {
			throw new IllegalSettingValueException("illegal value " + cpStr
					+ " for -cp");
		}
		logger.info("detect cross project clones: " + detectCrossProjectLinks);

		final String bcStr = (cmd.hasOption("bc")) ? cmd.getOptionValue("bc")
				: propReader.getProperty(LINK_ONLY_BEFORE_CLONE);
		if (bcStr.equalsIgnoreCase("yes")) {
			onlyFragmentInClonesInBeforeRevision = true;
		} else if (bcStr.equalsIgnoreCase("no")) {
			onlyFragmentInClonesInBeforeRevision = false;
		} else {
			throw new IllegalSettingValueException("illegal value " + cpStr
					+ " for -bc");
		}
		logger.info("detect links from fragments that are in any clones in before revision: "
				+ onlyFragmentInClonesInBeforeRevision);

		final String stStr = (cmd.hasOption("st")) ? cmd.getOptionValue("st")
				: propReader.getProperty(SIMILARITY_THRESHOLD);
		try {
			similarityThreshold = Double.parseDouble(stStr);
		} catch (Exception e) {
			throw new IllegalSettingValueException("illegal value " + stStr
					+ " was specified with -st, it must be a double value");
		}
		logger.info("similarity threshold: " + similarityThreshold);

		final String csStr = (cmd.hasOption("cs")) ? cmd.getOptionValue("cs")
				: propReader.getProperty(CRD_SIMILARITY);
		crdSimilarityMode = CRDSimilarityCalculateMode
				.getCorrespondingMode(csStr);
		if (crdSimilarityMode == null) {
			throw new IllegalSettingValueException(
					"unknown similarity calculate mode: " + csStr);
		}
		logger.info("CRD similarity calculate mode: " + crdSimilarityMode);

		final String flStr = (cmd.hasOption("fl")) ? cmd.getOptionValue("fl")
				: propReader.getProperty(FRAGMENT_LINK);
		fragmentLinkMode = CodeFragmentLinkMode.getCorrespondingMode(flStr);
		if (fragmentLinkMode == null) {
			throw new IllegalSettingValueException(
					"unknown fragment link mode: " + fragmentLinkMode);
		}
		logger.info("fragment link mode: " + fragmentLinkMode);
	}

}
