package jp.ac.osaka_u.ist.sdl.ectec.settings;

/**
 * An enum that represents the granularity of the analysis
 * 
 * @author k-hotta
 * 
 */
public enum AnalyzeGranularity {

	CLASS(new String[] { "c", "class", "f", "file" }),

	METHOD(new String[] { "m", "method" }),

	CLASS_METHOD(new String[] { "cm", "class_method", "larger_than_method" }),

	ALL(new String[] { "d", "default", "a", "all", "fine-grained" });

	private final String[] correspondingStrs;

	private AnalyzeGranularity(final String[] correspondingStrs) {
		this.correspondingStrs = correspondingStrs;
	}

	public final boolean correspond(final String str) {
		for (final String correspondingStr : correspondingStrs) {
			if (correspondingStr.equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

	public static AnalyzeGranularity getCorrespondingGranularity(
			final String str) {
		if (CLASS.correspond(str)) {
			return CLASS;
		} else if (METHOD.correspond(str)) {
			return METHOD;
		} else if (CLASS_METHOD.correspond(str)) {
			return CLASS_METHOD;
		} else if (ALL.correspond(str)) {
			return ALL;
		} else {
			return null;
		}
	}

}
