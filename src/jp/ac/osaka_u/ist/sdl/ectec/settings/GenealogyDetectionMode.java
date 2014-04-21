package jp.ac.osaka_u.ist.sdl.ectec.settings;

/**
 * the mode of genealogy detection
 * 
 * @author k-hotta
 * 
 */
public enum GenealogyDetectionMode {

	FRAGMENT(new String[] { "f", "fragment" }),

	CLONE(new String[] { "c", "clone" });

	private final String[] strs;

	private GenealogyDetectionMode(final String[] strs) {
		this.strs = strs;
	}

	public final String[] getStrs() {
		return strs;
	}

	public final boolean matches(final String str) {
		for (final String tmpStr : strs) {
			if (tmpStr.equalsIgnoreCase(str)) {
				return true;
			}
		}

		return false;
	}

	public static GenealogyDetectionMode getCorrespondingMode(final String str) {
		if (FRAGMENT.matches(str)) {
			return FRAGMENT;
		} else if (CLONE.matches(str)) {
			return CLONE;
		} else {
			return null;
		}
	}

}
