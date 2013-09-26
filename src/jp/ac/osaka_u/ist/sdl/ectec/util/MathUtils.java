package jp.ac.osaka_u.ist.sdl.ectec.util;

/**
 * A class equipped with utility methods for operations of mathmatics
 * 
 * @author k-hotta
 * 
 */
public class MathUtils {

	/**
	 * get the minimum value among the given arguments
	 * 
	 * @param args
	 *            values
	 * @return the minimum value
	 */
	public static int min(int... args) {
		if (args.length == 0) {
			return -1;
		}

		int result = args[0];
		for (final int tmp : args) {
			if (tmp < result) {
				result = tmp;
			}
		}

		return result;
	}

}
