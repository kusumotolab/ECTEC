package jp.ac.osaka_u.ist.sdl.ectec.settings;

/**
 * The level of logging
 * 
 * @author k-hotta
 * 
 */
public enum LoggingLevel {

	SEVERE("SEVERE"), WARNING("WARNING"), INFO("INFO"), CONFIG("CONFIG"), FINE(
			"FINE"), FINER("FINER"), FINEST("FINEST");

	private final String str;

	private LoggingLevel(final String str) {
		this.str = str;
	}

	public final String getStr() {
		return str;
	}

	public final boolean corresponds(final String str) {
		if (this.getStr().equalsIgnoreCase(str)) {
			return true;
		} else {
			return false;
		}
	}

	public static LoggingLevel getCorrespondingLevel(final String str) {
		if (SEVERE.corresponds(str)) {
			return SEVERE;
		} else if (WARNING.corresponds(str)) {
			return WARNING;
		} else if (INFO.corresponds(str)) {
			return INFO;
		} else if (CONFIG.corresponds(str)) {
			return CONFIG;
		} else if (FINE.corresponds(str)) {
			return FINE;
		} else if (FINER.corresponds(str)) {
			return FINER;
		} else if (FINEST.corresponds(str)) {
			return FINEST;
		}

		// the default value
		return INFO;
	}

}
