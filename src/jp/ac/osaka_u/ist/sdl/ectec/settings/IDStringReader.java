package jp.ac.osaka_u.ist.sdl.ectec.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.main.IllegalSettingValueException;

import org.apache.log4j.Logger;

public class IDStringReader {

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	private static final String regex = "^(\\d+-\\d+|\\d+)(,(\\d+-\\d+|\\d+))*$";

	private static final String regex2 = "\\d+-\\d+|\\d+";

	private static final String regex3 = "(\\d+)-(\\d+)";

	private static final Pattern pattern = Pattern.compile(regex);

	private static final Pattern pattern2 = Pattern.compile(regex2);

	private static final Pattern pattern3 = Pattern.compile(regex3);

	public static List<Long> read(final String str) throws Exception {
		final Matcher m = pattern.matcher(str);

		if (!m.matches()) {
			throw new IllegalSettingValueException("cannot read " + str);
		}

		final SortedSet<Long> sortedIds = new TreeSet<Long>();

		final Matcher m2 = pattern2.matcher(str);
		while (m2.find()) {
			final String tmp = m2.group();

			final Matcher m3 = pattern3.matcher(tmp);

			try {
				if (m3.matches()) {
					final long beforeValue = Long.parseLong(m3.group(1));
					final long afterValue = Long.parseLong(m3.group(2));
					for (long l = beforeValue; l <= afterValue; l++) {
						sortedIds.add(l);
					}
				} else {
					final long value = Long.parseLong(tmp);
					sortedIds.add(value);
				}
			} catch (Exception e) {
				eLogger.warn("cannot read " + tmp);
			}

		}

		final List<Long> result = new ArrayList<Long>();
		result.addAll(sortedIds);

		return Collections.unmodifiableList(result);
	}
}
