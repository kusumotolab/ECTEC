package jp.ac.osaka_u.ist.sdl.ectec.settings;

import jp.ac.osaka_u.ist.sdl.ectec.detector.linker.ICodeFragmentLinker;
import jp.ac.osaka_u.ist.sdl.ectec.detector.linker.MultipleCodeFragmentLinker;
import jp.ac.osaka_u.ist.sdl.ectec.detector.linker.SingleCodeFragmentLinker;

/**
 * An enum that represents how to detect pairs of code fragments
 * 
 * @author k-hotta
 * 
 */
public enum CodeFragmentLinkMode {

	/**
	 * detect 1 by 1 links
	 */
	SINGLE(new String[] { "s", "single" }, new SingleCodeFragmentLinker()),

	/**
	 * detect n by m links
	 */
	MULTIPLE(new String[] { "m", "multiple", "d", "default" },
			new MultipleCodeFragmentLinker());

	private final String[] correspondingStrs;

	private final ICodeFragmentLinker linker;

	private CodeFragmentLinkMode(final String[] correspondingStrs,
			final ICodeFragmentLinker linker) {
		this.correspondingStrs = correspondingStrs;
		this.linker = linker;
	}

	public final ICodeFragmentLinker getLinker() {
		return linker;
	}

	public final boolean correspond(final String str) {
		for (final String correspondingStr : correspondingStrs) {
			if (correspondingStr.equalsIgnoreCase(str)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * get the mode that corresponds to the given str
	 * 
	 * @param str
	 * @return
	 */
	public static final CodeFragmentLinkMode getCorrespondingMode(
			final String str) {
		if (SINGLE.correspond(str)) {
			return SINGLE;
		} else if (MULTIPLE.correspond(str)) {
			return MULTIPLE;
		} else {
			return null;
		}
	}
}
