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
			collection.add(Long.parseLong(element));
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

}
