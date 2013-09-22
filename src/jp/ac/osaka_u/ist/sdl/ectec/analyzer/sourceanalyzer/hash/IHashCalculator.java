package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash;

/**
 * An interface that represents how to calculate a hash value from a code
 * fragment
 * 
 * @author k-hotta
 * 
 */
public interface IHashCalculator {

	/**
	 * get a hash value calcuated from the given string as a long value
	 * 
	 * @param str
	 * @return
	 */
	public long getHashValue(final String str);

}
