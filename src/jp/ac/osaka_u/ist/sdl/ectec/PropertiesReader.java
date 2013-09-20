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
public class PropertiesReader {

	/**
	 * the path of properties file to be read
	 */
	protected final String propertiesFilePath;

	/**
	 * the defalut value of the path of properties file to be read
	 */
	private static final String defaultPropertiesFilePath = "ectec.properties";

	/**
	 * initialize this reader with the specified file path
	 * 
	 * @param propertiesFilePath
	 */
	public PropertiesReader(final String propertiesFilePath) {
		this.propertiesFilePath = propertiesFilePath;
	}

	/**
	 * initialize this reader with the default file path <br>
	 * ("ectec.properties" in the current directory)
	 */
	public PropertiesReader() {
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

}
