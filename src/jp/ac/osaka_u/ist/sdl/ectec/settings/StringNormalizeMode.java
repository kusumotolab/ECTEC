package jp.ac.osaka_u.ist.sdl.ectec.settings;


/**
 * An enum that represents how to calculate hash values from blocks for clone
 * detection
 * 
 * @author k-hotta
 * 
 */
public enum StringNormalizeMode {

	/**
	 * calculate hash values without any normalizations <br>
	 * (except for white spaces, tabs, and new line characters)
	 */
	EXACT(new String[] { "e", "exact" }),

	/**
	 * calculate hash values with identifiers are normalized
	 */
	IDENTIFIER_NORMALIZED(new String[] { "d", "default", "w", "weak" }),

	/**
	 * calculate hash values with identifiers and sub-blocks are normalized
	 */
	SUBBLOCK_NORMALIZED(new String[] { "s", "strong", "subtree", "subblock" });

	/**
	 * an array of strings which are used to choose this mode
	 */
	private final String[] correspondingStrs;

	private StringNormalizeMode(final String[] correspondingStrs) {
		this.correspondingStrs = correspondingStrs;
	}

	public final boolean correspond(final String str) {
		for (final String tmp : correspondingStrs) {
			if (tmp.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

	public static final StringNormalizeMode getCorrespondingMode(
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
