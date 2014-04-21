package jp.ac.osaka_u.ist.sdl.instantcdt.benchmark;


public class MetricsCalculator {

	public static double calcOverlap(final BenchmarkCloneFragment fragment,
			final BenchmarkCloneFragment anotherFragment) {
		if (!fragment.getOwnerFile().equals(anotherFragment.getOwnerFile())) {
			return 0;
		}

		final int overlappedLines = calcOverlappedLines(fragment,
				anotherFragment);
		final int lines = fragment.getEndLine() - fragment.getStartLine() + 1;
		final int anotherLines = anotherFragment.getEndLine()
				- anotherFragment.getStartLine() + 1;

		final int totalLines = lines + anotherLines - overlappedLines;

		return ((double) overlappedLines) / ((double) totalLines);
	}

	public static int calcOverlappedLines(
			final BenchmarkCloneFragment fragment,
			final BenchmarkCloneFragment anotherFragment) {
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

	public static double calcContain(final BenchmarkCloneFragment fragment,
			final BenchmarkCloneFragment anotherFragment) {
		if (!fragment.getOwnerFile().equals(anotherFragment.getOwnerFile())) {
			return 0;
		}

		final int overlappedLines = calcOverlappedLines(fragment,
				anotherFragment);
		final int lines = fragment.getEndLine() - fragment.getStartLine() + 1;

		return ((double) overlappedLines) / ((double) lines);
	}

	public static double calcGood(final BenchmarkClonePair pair,
			final BenchmarkClonePair anotherPair) {
		final double overlap1 = calcOverlap(pair.getFragment1(),
				anotherPair.getFragment1());
		final double overlap2 = calcOverlap(pair.getFragment2(),
				anotherPair.getFragment2());

		return Math.min(overlap1, overlap2);
	}

	public static double calcOK(final BenchmarkClonePair pair,
			final BenchmarkClonePair anotherPair) {
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
