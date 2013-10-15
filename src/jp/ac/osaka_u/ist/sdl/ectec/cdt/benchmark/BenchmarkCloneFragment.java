package jp.ac.osaka_u.ist.sdl.ectec.cdt.benchmark;

public class BenchmarkCloneFragment implements Comparable<BenchmarkCloneFragment> {

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

}
