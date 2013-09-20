package jp.ac.osaka_u.ist.sdl.ectec.settings;

/**
 * An enum that represents version control systems
 * 
 * @author k-hotta
 * 
 */
public enum VersionControlSystem {

	SVN("svn"), OTHER("n/a");

	private final String str;

	private VersionControlSystem(final String str) {
		this.str = str;
	}

	public static final VersionControlSystem getCorrespondingVersionControlSystem(
			final String str) {
		if (str.equalsIgnoreCase(SVN.getStr())) {
			return SVN;
		} else {
			return OTHER;
		}
	}

	public final String getStr() {
		return str;
	}

}
