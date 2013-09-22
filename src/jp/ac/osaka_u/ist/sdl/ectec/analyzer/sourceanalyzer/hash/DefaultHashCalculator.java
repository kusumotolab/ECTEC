package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash;

/**
 * A default hash calculator which uses String.hashCode()
 * 
 * @author k-hotta
 * 
 */
public class DefaultHashCalculator implements IHashCalculator {

	@Override
	public long getHashValue(final String str) {
		return (long) str.hashCode();
	}

}
