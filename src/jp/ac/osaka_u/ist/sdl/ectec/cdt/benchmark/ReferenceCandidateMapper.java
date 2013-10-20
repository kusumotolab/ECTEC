package jp.ac.osaka_u.ist.sdl.ectec.cdt.benchmark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReferenceCandidateMapper {

	public List<ReferenceCandidateMap> detectMap(
			final Collection<CloneCandidate> candidates,
			final Collection<CloneReference> references) {
		final List<ReferenceCandidateMap> result = new ArrayList<ReferenceCandidateMap>();
		final Map<CloneCandidate, CloneReference> bestReferenceOf = new HashMap<CloneCandidate, CloneReference>();

		for (final CloneReference reference : references) {
			double okMax = -1.0;
			double goodMax = -1.0;
			for (final CloneCandidate candidate : candidates) {
				final double ok = MetricsCalculator
						.calcOK(reference, candidate);
				final double good = MetricsCalculator.calcGood(reference,
						candidate);

				if (good == 0 && ok == 0) {
					continue;
				}

				if (good > goodMax) {
					bestReferenceOf.put(candidate, reference);
					goodMax = good;
					okMax = ok;
				} else if (good == goodMax && ok > okMax) {
					bestReferenceOf.put(candidate, reference);
					goodMax = good;
					okMax = ok;
				}
			}
		}

		for (Map.Entry<CloneCandidate, CloneReference> entry : bestReferenceOf
				.entrySet()) {
			final CloneReference reference = entry.getValue();
			final CloneCandidate candidate = entry.getKey();
			final ReferenceCandidateMap newMapping = new ReferenceCandidateMap(
					reference, candidate, MetricsCalculator.calcOK(reference,
							candidate), MetricsCalculator.calcGood(reference,
							candidate));
			result.add(newMapping);
		}

		return result;
	}

}
