package jp.ac.osaka_u.ist.sdl.ectec.settings;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.similarity.ICRDSimilarityCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.similarity.LevenshteinBasedCRDSimilarityCalculator;

/**
 * An enum that represents how to calculate crd similarities
 * 
 * @author k-hotta
 * 
 */
public enum CRDSimilarityCalculateMode {

	/**
	 * calculate crd similarities with levenshtein distances
	 */
	LEVENSHTEIN(new String[] { "l", "levenshtein", "d", "default" },
			new LevenshteinBasedCRDSimilarityCalculator());

	private final String[] correspondingStrs;

	private final ICRDSimilarityCalculator calculator;

	private CRDSimilarityCalculateMode(final String[] correspondingStrs,
			final ICRDSimilarityCalculator calculator) {
		this.correspondingStrs = correspondingStrs;
		this.calculator = calculator;
	}

	public final ICRDSimilarityCalculator getCalculator() {
		return this.calculator;
	}

	public final boolean correspond(final String str) {
		for (final String correspondingStr : correspondingStrs) {
			if (correspondingStr.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

	public static final CRDSimilarityCalculateMode getCorrespondingMode(
			final String str) {
		if (LEVENSHTEIN.correspond(str)) {
			return LEVENSHTEIN;
		} else {
			return null;
		}
	}

}
