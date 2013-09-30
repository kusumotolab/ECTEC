package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash;

public class DefaultHashCalculator implements IHashCalculator {

	@Override
	public long calcHashValue(String str) {
		return (long) str.hashCode();
	}

}
