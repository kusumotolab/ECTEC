package jp.ac.osaka_u.ist.sdl.instantcdt.benchmark;

public class ReferenceCandidateMap {

	private final CloneReference reference;

	private final CloneCandidate candidate;

	private final double ok;

	private final double good;

	public ReferenceCandidateMap(final CloneReference reference,
			final CloneCandidate candidate, final double ok, final double good) {
		this.reference = reference;
		this.candidate = candidate;
		this.ok = ok;
		this.good = good;
	}

	public final CloneReference getReference() {
		return reference;
	}

	public final CloneCandidate getCandidate() {
		return candidate;
	}

	public final double getOk() {
		return ok;
	}

	public final double getGood() {
		return good;
	}

}
