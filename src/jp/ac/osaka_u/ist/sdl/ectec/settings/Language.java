package jp.ac.osaka_u.ist.sdl.ectec.settings;

/**
 * An enum that represents target programming languages
 * 
 * @author k-hotta
 * 
 */
public enum Language {

	JAVA("java", new String[] { ".java" }),

	OTHER("n/a", new String[] {});

	/**
	 * the string representation of this language
	 */
	private final String str;

	/**
	 * the suffixes for this language
	 */
	private final String[] suffixes;

	private Language(final String str, final String[] suffixes) {
		this.str = str;
		this.suffixes = suffixes;
	}

	public final String getStr() {
		return str;
	}

	/**
	 * get the corresponding language for the given string
	 * 
	 * @param str
	 * @return
	 */
	public static final Language getCorrespondingLanguage(final String str) {
		if (str.equalsIgnoreCase(JAVA.getStr())) {
			return JAVA;
		} else {
			return OTHER;
		}
	}

	/**
	 * check whether the specified file is a target source file
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isTarget(final String fileName) {
		for (final String suffix : suffixes) {
			if (fileName.endsWith(suffix)) {
				return true;
			}
		}

		return false;
	}

}
