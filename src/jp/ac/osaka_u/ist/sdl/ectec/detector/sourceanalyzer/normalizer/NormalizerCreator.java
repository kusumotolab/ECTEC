package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer;

import jp.ac.osaka_u.ist.sdl.ectec.settings.StringNormalizeMode;

/**
 * An interface to create instances of hash calculators
 * 
 * @author k-hotta
 * 
 */
public class NormalizerCreator {

	private final StringNormalizeMode mode;

	public NormalizerCreator(final StringNormalizeMode mode) {
		this.mode = mode;
	}

	public StringCreateVisitor createNewCalculator() {
		switch (mode) {
		case EXACT:
			return new StringCreateVisitor();
		case IDENTIFIER_NORMALIZED:
			return new NormalizedStringCreator();
		case SUBBLOCK_NORMALIZED:
			return new SubblockNormalizedBlockVisitor();
		default:
			return null;
		}
	}

}
