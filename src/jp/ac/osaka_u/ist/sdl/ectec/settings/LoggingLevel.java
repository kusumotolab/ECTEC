package jp.ac.osaka_u.ist.sdl.ectec.settings;

import java.util.logging.Level;

/**
 * The level of logging
 * 
 * @author k-hotta
 * 
 */
public enum LoggingLevel {

	INFO("INFO", Level.INFO), CONFIG("CONFIG", Level.CONFIG), FINE("FINE",
			Level.FINE), FINER("FINER", Level.FINER), FINEST("FINEST",
			Level.FINEST);

	private final String str;

	private final Level level;

	private LoggingLevel(final String str, final Level level) {
		this.str = str;
		this.level = level;
	}

	public final String getStr() {
		return str;
	}

	public final Level getLevel() {
		return level;
	}

	public final boolean corresponds(final String str) {
		if (this.getStr().equalsIgnoreCase(str)) {
			return true;
		} else {
			return false;
		}
	}

	public static LoggingLevel getCorrespondingLevel(final String str) {
		if (INFO.corresponds(str)) {
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
