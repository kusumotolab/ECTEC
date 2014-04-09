package jp.ac.osaka_u.ist.sdl.ectec;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * A class for managing logging functions
 * 
 * @author k-hotta
 * 
 */
public class ECTECLogger {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger("ECTEC");

	/**
	 * the logger for errors
	 */
	private static Logger eLogger = Logger.getLogger("ECTEC_ERROR");

	public static void initialize(final Level level) {
		if (level == null) {
			return;
		}

		final StreamHandler standardHandler = new StreamHandler() {
			{
				setOutputStream(System.out);
			}
		};
		standardHandler.setLevel(level);
		standardHandler.setFormatter(new StandardLoggerFormatter());
		logger.addHandler(standardHandler);
		logger.setLevel(level);

		final ConsoleHandler errorHandler = new ConsoleHandler();
		errorHandler.setFormatter(new ErrorLoggerFormatter());
		errorHandler.setLevel(Level.WARNING);
		eLogger.addHandler(errorHandler);
		eLogger.setLevel(Level.WARNING);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static Logger getELogger() {
		return eLogger;
	}

}
