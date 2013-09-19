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

}
