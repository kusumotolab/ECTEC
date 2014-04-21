package jp.ac.osaka_u.ist.sdl.ectec.main.repositoryregisterer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalFileFormatException;

/**
 * 
 * @author k-hotta
 * 
 */
public class CSVReader {

	/**
	 * the file path of the target
	 */
	private final String filePath;

	public CSVReader(final String filePath) {
		this.filePath = filePath;
	}

	public Map<Long, DBRepositoryInfo> read() throws Exception {
		final Map<Long, DBRepositoryInfo> result = new TreeMap<Long, DBRepositoryInfo>();

		final BufferedReader br = new BufferedReader(new FileReader(new File(
				filePath)));

		try {
			String line;
			while ((line = br.readLine()) != null) {
				final String[] splitLine = line.split(",");

				if (splitLine.length != 3) {
					throw new IllegalFileFormatException(
							"the given file has an illegal format at + " + line
									+ ".");
				}

				try {
					final long id = Long.parseLong(splitLine[0]);
					final String name = splitLine[1];
					final String url = splitLine[2];

					final DBRepositoryInfo repository = new DBRepositoryInfo(
							id, name, url);
					result.put(repository.getId(), repository);

				} catch (Exception e) {
					throw new IllegalFileFormatException(
							"the given file has an illegal format at " + line
									+ ".\n" + e.toString());
				}
			}
		} finally {
			br.close();
		}

		return Collections.unmodifiableMap(result);
	}
}
