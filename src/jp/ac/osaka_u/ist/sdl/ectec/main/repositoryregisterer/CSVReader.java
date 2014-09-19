package jp.ac.osaka_u.ist.sdl.ectec.main.repositoryregisterer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalFileFormatException;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

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

				if (splitLine.length < 5 || splitLine.length > 7) {
					throw new IllegalFileFormatException(
							"the given file has an illegal format at + " + line
									+ ".");
				}

				try {
					final long id = Long.parseLong(splitLine[0]);
					final String name = splitLine[1];
					String url = splitLine[2];
					if (url.endsWith("/")) {
						url = url.substring(0, url.length() - 1);
					}

					String additionalUrlStr = splitLine[3];
					if (additionalUrlStr != null) {
						if (!additionalUrlStr.startsWith("/")) {
							additionalUrlStr = "/" + additionalUrlStr;
						}
						if (additionalUrlStr.endsWith("/")) {
							additionalUrlStr = additionalUrlStr.substring(0,
									additionalUrlStr.length() - 1);
						}
					}

					final String additionalUrl = (additionalUrlStr.isEmpty() || additionalUrlStr
							.equalsIgnoreCase("null")) ? null
							: additionalUrlStr;
					final VersionControlSystem managingVcs = VersionControlSystem
							.getCorrespondingVersionControlSystem(splitLine[4]
									.trim());
					final String userNameStr = (splitLine.length > 5) ? splitLine[5]
							.trim() : "";
					final String userName = (userNameStr.isEmpty()) ? null
							: userNameStr;
					final String passwdStr = (splitLine.length > 6) ? splitLine[6]
							.trim() : "";
					final String passwd = (passwdStr.isEmpty()) ? null
							: passwdStr;

					if (managingVcs == null) {
						throw new IllegalStateException(splitLine[3]
								+ ": cannot find such a version control system");
					}

					final DBRepositoryInfo repository = new DBRepositoryInfo(
							id, name, url, additionalUrl, managingVcs,
							userName, passwd);
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
