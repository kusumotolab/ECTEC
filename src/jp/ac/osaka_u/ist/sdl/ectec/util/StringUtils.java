package jp.ac.osaka_u.ist.sdl.ectec.util;

import java.util.Collection;

/**
 * A class equipped with utility methods for string
 * 
 * @author k-hotta
 * 
 */
public class StringUtils {

	/**
	 * convert the given collection into a string in which each element is
	 * connected with the given separator
	 * 
	 * @param collection
	 * @param separator
	 * @return
	 */
	public static String convertListToString(final Collection<?> collection,
			final String separator) {
		final StringBuilder builder = new StringBuilder();

		for (final Object obj : collection) {
			builder.append(obj.toString() + separator);
		}

		if (builder.length() > 0) {
			builder.delete(builder.length() - separator.length(),
					builder.length());
		}

		return builder.toString();
	}

	/**
	 * convert the given collection into a string in which each element is
	 * connected with ","
	 * 
	 * @param collection
	 * @return
	 */
	public static String convertListToString(final Collection<?> collection) {
		return convertListToString(collection, ",");
	}

	/**
	 * convert the given string into a collection of long values <br>
	 * the result will be stored in the collection specified as the first
	 * argument
	 * 
	 * @param collection
	 * @param str
	 * @param separator
	 */
	public static void convertStringToCollection(
			final Collection<Long> collection, final String str,
			final String separator) {
		final String[] splitStr = str.split(separator);
		for (final String element : splitStr) {
			if (!element.isEmpty()) {
				collection.add(Long.parseLong(element));
			}
		}
	}

	/**
	 * convert the given string into a collection of integers <br>
	 * the result will be stored in the collection specified as the first
	 * argument
	 * 
	 * @param collection
	 * @param str
	 */
	public static void convertStringToCollection(
			final Collection<Long> collection, final String str) {
		convertStringToCollection(collection, str, ",");
	}

	/**
	 * calculate the Levenshtein distance between given two strings
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static int calcLevenshteinDistance(String str1, String str2) {
		final int len1 = str1.length();
		final int len2 = str2.length();
		final int[][] d = new int[len1 + 1][len2 + 1];

		for (int i = 0; i < len1 + 1; i++) {
			d[i][0] = i;
		}
		for (int j = 0; j < len2 + 1; j++) {
			d[0][j] = j;
		}

		for (int i = 1; i < len1 + 1; i++) {
			for (int j = 1; j < len2 + 1; j++) {
				int cost;
				if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
					cost = 0;
				} else {
					cost = 1;
				}
				d[i][j] = MathUtils.min(d[i - 1][j] + 1, d[i][j - 1] + 1,
						d[i - 1][j - 1] + cost);
			}
		}
		return d[len1][len2];
	}

	/**
	 * calculate Levenshtein distance based similarity between the given two
	 * strings
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static double calcLebenshteinDistanceBasedSimilarity(String str1,
			String str2) {
		final int ld = calcLevenshteinDistance(str1, str2);

		return ((double) 1) - (((double) ld) * 2)
				/ (((double) str1.length()) + ((double) str2.length()));
	}

}
