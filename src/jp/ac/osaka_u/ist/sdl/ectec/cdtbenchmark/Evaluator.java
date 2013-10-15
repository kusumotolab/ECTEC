package jp.ac.osaka_u.ist.sdl.ectec.cdtbenchmark;

import java.util.Collection;
import java.util.Map;

public class Evaluator {

	public static double calcRecall(
			final Collection<CloneReference> references,
			final Collection<CloneCandidate> candidates,
			final Map<CloneCandidate, CloneReference> overThresholdMapping) {
		return ((double) overThresholdMapping.size())
				/ ((double) references.size());
	}

	public static double calcPrecision(
			final Collection<CloneReference> references,
			final Collection<CloneCandidate> candidates,
			final Map<CloneCandidate, CloneReference> overThresholdMapping) {
		return ((double) overThresholdMapping.size())
				/ ((double) candidates.size());
	}

	public static double calcRejected(
			final Map<CloneCandidate, CloneReference> overThresholdMapping,
			final Map<CloneCandidate, CloneReference> underThresholdMapping) {
		final int oracled = overThresholdMapping.size()
				+ underThresholdMapping.size();
		return ((double) underThresholdMapping.size()) / ((double) oracled);
	}

}
