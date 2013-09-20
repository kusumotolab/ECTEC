package jp.ac.osaka_u.ist.sdl.ectec.settings;

/**
 * An enum that represents the level of verbose output of the current state of
 * the processing
 * 
 * @author k-hotta
 * 
 */
public enum MessagePrintLevel {

	// the most strong (verbose) output
	VERBOSE(new String[] { "strong", "s", "verbose", "v", "all", "a" }),

	// print only important information
	LITTLE(new String[] { "little", "l", "weak", "w", "default", "d", "yes",
			"y" }),

	// print nothing
	NONE(new String[] { "nothing", "none", "no", "n" });

	/**
	 * strings that represent the level
	 */
	private final String[] correspondingStrs;

	private MessagePrintLevel(final String[] correspondingStrs) {
		this.correspondingStrs = correspondingStrs;
	}

	/**
	 * get the corresponding strings
	 * 
	 * @return
	 */
	public final String[] getCorrespondingStrs() {
		return this.correspondingStrs;
	}

	/**
	 * judge whether the given string meets any of corresponding strings
	 * 
	 * @param str
	 * @return
	 */
	public final boolean corresponds(final String str) {
		for (final String tmp : correspondingStrs) {
			if (str.equalsIgnoreCase(tmp)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * get the corresponding level with the specified string
	 * 
	 * @param str
	 * @return
	 */
	public static MessagePrintLevel getCorrespondingLevel(final String str) {
		if (VERBOSE.corresponds(str)) {
			return VERBOSE;
		} else if (LITTLE.corresponds(str)) {
			return LITTLE;
		} else if (NONE.corresponds(str)) {
			return NONE;
		}

		return LITTLE;
	}

}
