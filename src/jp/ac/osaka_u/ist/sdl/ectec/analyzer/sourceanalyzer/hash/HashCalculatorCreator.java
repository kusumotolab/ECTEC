package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash;

import jp.ac.osaka_u.ist.sdl.ectec.settings.StringNormalizeMode;

/**
 * An interface to create instances of hash calculators
 * 
 * @author k-hotta
 * 
 */
public class HashCalculatorCreator {

	private final StringNormalizeMode mode;

	public HashCalculatorCreator(final StringNormalizeMode mode) {
		this.mode = mode;
	}

	public IHashCalculator createNewCalculator() {
		switch (mode) {
		case EXACT:
			return new ExactHashCalculator();
		case IDENTIFIER_NORMALIZED:
			return new IdentifierNormalizedHashCalculator();
		case SUBBLOCK_NORMALIZED:
			return new SubblockNormalizedHashCalculator();
		default:
			return null;
		}
	}

}
