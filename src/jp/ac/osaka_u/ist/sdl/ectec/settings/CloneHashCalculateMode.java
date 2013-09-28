package jp.ac.osaka_u.ist.sdl.ectec.settings;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash.ExactHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash.IdentifierNormalizedHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash.SubblockNormalizedHashCalculator;

/**
 * An enum that represents how to calculate hash values from blocks for clone
 * detection
 * 
 * @author k-hotta
 * 
 */
public enum CloneHashCalculateMode {

	/**
	 * calculate hash values without any normalizations <br>
	 * (except for white spaces, tabs, and new line characters)
	 */
	EXACT(new String[] { "e", "exact" }, new ExactHashCalculator()),

	/**
	 * calculate hash values with identifiers are normalized
	 */
	IDENTIFIER_NORMALIZED(new String[] { "d", "default", "w", "weak" },
			new IdentifierNormalizedHashCalculator()),

	/**
	 * calculate hash values with identifiers and sub-blocks are normalized
	 */
	SUBBLOCK_NORMALIZED(new String[] { "s", "strong", "strict", "subtree" },
			new SubblockNormalizedHashCalculator());

	/**
	 * an array of strings which are used to choose this mode
	 */
	private final String[] correspondingStrs;

	/**
	 * the hash calculator
	 */
	private final IHashCalculator calculator;

	private CloneHashCalculateMode(final String[] correspondingStrs,
			final IHashCalculator calculator) {
		this.correspondingStrs = correspondingStrs;
		this.calculator = calculator;
	}

	public final IHashCalculator getCalculator() {
		return calculator;
	}

	public final boolean correspond(final String str) {
		for (final String tmp : correspondingStrs) {
			if (tmp.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

	public static final CloneHashCalculateMode getCorrespondingMode(
			final String str) {
		if (EXACT.correspond(str)) {
			return EXACT;
		} else if (IDENTIFIER_NORMALIZED.correspond(str)) {
			return IDENTIFIER_NORMALIZED;
		} else if (SUBBLOCK_NORMALIZED.correspond(str)) {
			return SUBBLOCK_NORMALIZED;
		} else {
			return null;
		}
	}

}
