package jp.ac.osaka_u.ist.sdl.ectec;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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

	public static void initialize(final String level) {
		if (level == null) {
			return;
		}

		final StringBuilder configBuilder = new StringBuilder();

		configBuilder.append("handlers=java.util.logging.ConsoleHandler\n");
		configBuilder.append(".level=" + level + "\n");
		configBuilder.append("java.util.logging.ConsoleHandler.level=" + level
				+ "\n");
		configBuilder.append("java.util.logging.ConsoleHandler.formatter");
		configBuilder.append("=java.util.logging.SimpleFormatter");

		final String config = configBuilder.toString();

		InputStream inStream = null;
		try {
			inStream = new ByteArrayInputStream(config.getBytes("UTF-8"));

			try {
				LogManager.getLogManager().readConfiguration(inStream);
				logger.config("LogManager was configured");
			} catch (IOException e) {
				logger.warning("An error occurred when configuring LogManager:"
						+ e.toString());
			}

		} catch (UnsupportedEncodingException e) {
			logger.severe("UTF-8 Encoding is not supported: " + e.toString());
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (IOException e) {
				logger.warning("An error occurred when closing the stream of configuration for logging:"
						+ e.toString());
			}
		}
	}

	public synchronized static Logger getLogger() {
		return logger;
	}

}
