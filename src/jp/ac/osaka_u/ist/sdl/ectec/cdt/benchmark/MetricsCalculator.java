package jp.ac.osaka_u.ist.sdl.ectec.cdt.benchmark;

import jp.ac.osaka_u.ist.sdl.ectec.cdt.CloneFragment;
import jp.ac.osaka_u.ist.sdl.ectec.cdt.ClonePair;

public class MetricsCalculator {

	public static double calcOverlap(final CloneFragment fragment,
			final CloneFragment anotherFragment) {
		if (fragment.getOwnerFile().equals(anotherFragment.getOwnerFile())) {
			return 0;
		}

		final int overlappedLines = calcOverlappedLines(fragment,
				anotherFragment);
		final int lines = fragment.getEndLine() - fragment.getStartLine() + 1;
		final int anotherLines = anotherFragment.getEndLine()
				- anotherFragment.getStartLine() + 1;

		return ((double) overlappedLines)
				/ (((double) lines) + ((double) anotherLines));
	}

	public static int calcOverlappedLines(final CloneFragment fragment,
			final CloneFragment anotherFragment) {
		int result = 0;

		final int anotherStart = anotherFragment.getStartLine();
		final int anotherEnd = anotherFragment.getEndLine();

		for (int i = fragment.getStartLine(); i <= fragment.getEndLine(); i++) {
			if (anotherStart <= i && i <= anotherEnd) {
				result++;
			}
		}

		return result;
	}

	public static double calcContain(final CloneFragment fragment,
			final CloneFragment anotherFragment) {
		if (fragment.getOwnerFile().equals(anotherFragment.getOwnerFile())) {
			return 0;
		}

		final int overlappedLines = calcOverlappedLines(fragment,
				anotherFragment);
		final int lines = fragment.getEndLine() - fragment.getStartLine() + 1;

		return ((double) overlappedLines) / ((double) lines);
	}

	public static double calcGood(final ClonePair pair,
			final ClonePair anotherPair) {
		final double overlap1 = calcOverlap(pair.getFragment1(),
				anotherPair.getFragment1());
		final double overlap2 = calcOverlap(pair.getFragment2(),
				anotherPair.getFragment2());

		return Math.min(overlap1, overlap2);
	}

	public static double calcOK(final ClonePair pair,
			final ClonePair anotherPair) {
		final double contain11 = calcContain(pair.getFragment1(),
				anotherPair.getFragment1());
		final double contain12 = calcContain(anotherPair.getFragment1(),
				pair.getFragment1());
		final double contain1 = Math.max(contain11, contain12);

		final double contain21 = calcContain(pair.getFragment2(),
				anotherPair.getFragment2());
		final double contain22 = calcContain(anotherPair.getFragment2(),
				pair.getFragment2());
		final double contain2 = Math.max(contain21, contain22);

		return Math.min(contain1, contain2);
	}

}
