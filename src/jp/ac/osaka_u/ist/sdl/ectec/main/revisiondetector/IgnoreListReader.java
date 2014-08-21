package jp.ac.osaka_u.ist.sdl.ectec.main.revisiondetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalSettingValueException;

/**
 * A class to read files listing revisions to be ignored
 * 
 * @author k-hotta
 * 
 */
public class IgnoreListReader {

	public static Map<Long, Set<String>> read(final File file)
			throws IllegalSettingValueException {
		final Map<Long, Set<String>> result = new TreeMap<Long, Set<String>>();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));

			String line;
			while ((line = br.readLine()) != null) {
				final String[] splitLine = line.split(":");
				final long repositoryId = Long.parseLong(splitLine[0]);
				final Set<String> ignoredRevisionIdentifiers = new HashSet<String>();

				if (splitLine[1].contains("-")) {
					// just for SVN repository
					// here it is assumed that the identifiers are numerical
					final String[] secondSplitLine = splitLine[1].split("-");
					final long before = Long.parseLong(secondSplitLine[0]);
					final long after = Long.parseLong(secondSplitLine[1]);

					for (long i = before; i <= after; i++) {
						ignoredRevisionIdentifiers.add(Long.toString(i));
					}
				} else {
					ignoredRevisionIdentifiers.add(splitLine[1]);
				}

				if (result.containsKey(repositoryId)) {
					result.get(repositoryId).addAll(ignoredRevisionIdentifiers);
				} else {
					result.put(repositoryId, ignoredRevisionIdentifiers);
				}
			}

		} catch (Exception e) {
			throw new IllegalSettingValueException(
					"an error occured when parsing the ignore list\n"
							+ e.getLocalizedMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

		return Collections.unmodifiableMap(result);
	}
}
