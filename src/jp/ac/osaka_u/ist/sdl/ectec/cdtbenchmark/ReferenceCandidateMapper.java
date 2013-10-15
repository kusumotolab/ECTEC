package jp.ac.osaka_u.ist.sdl.ectec.cdtbenchmark;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReferenceCandidateMapper {

	private final double threshold;

	public ReferenceCandidateMapper(final double threshold) {
		this.threshold = threshold;
	}

	public Map<CloneCandidate, CloneReference> detectMap(
			final Collection<CloneCandidate> candidates,
			final Collection<CloneReference> references) {
		final Map<CloneCandidate, CloneReference> result = new HashMap<CloneCandidate, CloneReference>();

		for (final CloneReference reference : references) {
			double okMax = -1.0;
			double goodMax = -1.0;
			for (final CloneCandidate candidate : candidates) {
				final double ok = MetricsCalculator
						.calcOK(reference, candidate);
				final double good = MetricsCalculator.calcGood(reference,
						candidate);

				if (good >= threshold && good > goodMax) {
					change(candidate, reference, result);
				} else if (good == goodMax && ok > okMax) {
					change(candidate, reference, result);
				} else if (ok >= threshold && okMax < threshold) {
					change(candidate, reference, result);
				}
			}
		}

		return result;
	}

	private void change(final CloneCandidate candidate,
			final CloneReference reference,
			final Map<CloneCandidate, CloneReference> result) {
		if (result.containsKey(candidate)) {
			result.remove(candidate);
		}
		result.put(candidate, reference);
	}

}
