package jp.ac.osaka_u.ist.sdl.ectec;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * A class to manage logging functions
 * 
 * @author k-hotta
 * 
 */
public class LoggingManager {

	/**
	 * the path of the properties file
	 */
	private static final String DEFAULT_LOGGING_PROPERTIES = "log4j.properties";

	static {
		PropertyConfigurator.configure(DEFAULT_LOGGING_PROPERTIES);
	}
	
	public static Logger getLogger(final String name) {
		return LogManager.getLogger(name);
	}

}
