package jp.ac.osaka_u.ist.sdl.ectec.analyzer.filedetector;

/**
 * An enum that represents types of changes on files
 * 
 * @author k-hotta
 * 
 */
public enum ChangeTypeOnFile {

	ADD(new String[] { "a", "add" }),

	CHANGE(new String[] { "m", "modify" }),

	DELETE(new String[] { "d", "delete", "r", "replace" });

	/**
	 * strings that represent each type of change
	 */
	private final String[] strs;

	private ChangeTypeOnFile(final String[] strs) {
		this.strs = strs;
	}

	/**
	 * judge whether the specified string corresponds to this type
	 * 
	 * @param targetStr
	 * @return
	 */
	public boolean correspond(final String targetStr) {
		for (final String str : strs) {
			if (str.equalsIgnoreCase(targetStr)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * get the type of change from a string
	 * 
	 * @param str
	 * @return
	 */
	public static ChangeTypeOnFile getCorrespondingChangeType(final String str) {
		if (ADD.correspond(str)) {
			return ADD;
		} else if (CHANGE.correspond(str)) {
			return CHANGE;
		} else if (DELETE.correspond(str)) {
			return DELETE;
		}

		return null;
	}

	/**
	 * get the type of change from a character
	 * 
	 * @param ch
	 * @return
	 */
	public static ChangeTypeOnFile getCorrespondingChangeType(final char ch) {
		final String chAsStr = new String(new char[] { ch });
		return getCorrespondingChangeType(chAsStr);
	}

}
