package jp.ac.osaka_u.ist.sdl.ectec.cdt.benchmark;

public class BenchmarkCloneFragment implements
		Comparable<BenchmarkCloneFragment> {

	private final String ownerFile;

	private final int startLine;

	private final int endLine;

	public BenchmarkCloneFragment(final String ownerFile, final int startLine,
			final int endLine) {
		this.ownerFile = ownerFile;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	public final String getOwnerFile() {
		return ownerFile;
	}

	public final int getStartLine() {
		return startLine;
	}

	public final int getEndLine() {
		return endLine;
	}

	public final boolean subsume(final BenchmarkCloneFragment anotherFragment) {
		if (!ownerFile.equals(anotherFragment.getOwnerFile())) {
			return false;
		}

		return (this.startLine <= anotherFragment.getStartLine() && this.endLine >= anotherFragment
				.getEndLine());
	}

	@Override
	public int compareTo(final BenchmarkCloneFragment another) {
		final int compareWithPath = this.ownerFile.compareTo(another
				.getOwnerFile());
		if (compareWithPath != 0) {
			return compareWithPath;
		}

		final int compareWithStart = ((Integer) this.startLine)
				.compareTo(another.getStartLine());
		if (compareWithStart != 0) {
			return compareWithStart;
		}

		final int compareWithEnd = ((Integer) this.endLine).compareTo(another
				.getEndLine());
		if (compareWithEnd != 0) {
			return compareWithEnd;
		}

		return 0;

	}

	@Override
	public String toString() {
		return ownerFile + ": " + startLine + "-" + endLine;
	}

}
