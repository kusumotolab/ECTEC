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
		final Map<CloneCandidate, ReferenceCandidateMap> map = new HashMap<CloneCandidate, ReferenceCandidateMap>();

		for (final CloneReference reference : references) {
			double okMax = -1.0;
			double goodMax = -1.0;
			for (final CloneCandidate candidate : candidates) {
				final double ok = MetricsCalculator
						.calcOK(reference, candidate);
				final double good = MetricsCalculator.calcGood(reference,
						candidate);

				if (good > goodMax) {
					change(candidate, reference, good, ok, map, result);
				} else if (good == goodMax && ok > okMax) {
					change(candidate, reference, good, ok, map, result);
				}
			}
		}

		return result;
	}

	private void change(final CloneCandidate candidate,
			final CloneReference reference, final double good, final double ok,
			final Map<CloneCandidate, ReferenceCandidateMap> map,
			final List<ReferenceCandidateMap> result) {
		if (map.containsKey(candidate)) {
			final ReferenceCandidateMap toBeRemoved = map.get(candidate);
			map.remove(candidate);
			result.remove(toBeRemoved);
		}

		final ReferenceCandidateMap newMapping = new ReferenceCandidateMap(
				reference, candidate, ok, good);
		map.put(candidate, newMapping);
		result.add(newMapping);
	}

}
