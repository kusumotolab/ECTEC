package jp.ac.osaka_u.ist.sdl.ectec;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * A class to read a property file
 * 
 * @author k-hotta
 * 
 */
public class PropertiesReader implements PropertiesKeys {

	/**
	 * the path of properties file to be read
	 */
	private final String propertiesFilePath;

	/**
	 * the defalut value of the path of properties file to be read
	 */
	private static final String defaultPropertiesFilePath = "ectec.properties";

	/**
	 * loaded properties
	 */
	private final Properties prop;

	/**
	 * initialize this reader with the specified file path
	 * 
	 * @param propertiesFilePath
	 */
	public PropertiesReader(final String propertiesFilePath) throws Exception {
		this.propertiesFilePath = propertiesFilePath;
		this.prop = read();
	}

	/**
	 * initialize this reader with the default file path <br>
	 * ("ectec.properties" in the current directory)
	 */
	public PropertiesReader() throws Exception {
		this(defaultPropertiesFilePath);
	}

	/**
	 * read the properties file
	 * 
	 * @return
	 * @throws Exception
	 */
	public final Properties read() throws Exception {
		final Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new BufferedInputStream(new FileInputStream(
					propertiesFilePath));
			prop.load(input);

		} catch (Exception e) {
			throw e; // do nothing here
		} finally {
			if (input != null) {
				input.close();
			}
		}

		return prop;
	}

	/**
	 * get the value of the property having the given key
	 * 
	 * @param key
	 * @return
	 */
	public final String getProperty(final String key) {
		return prop.getProperty(key);
	}

	/**
	 * get the name of the loaded properties file
	 * 
	 * @return
	 */
	public final String getLoadedFileName() {
		return this.propertiesFilePath;
	}

}
