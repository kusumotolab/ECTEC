package jp.ac.osaka_u.ist.sdl.instantcdt.benchmark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class BenchMarkUIMain {

	public static void main(String[] args) throws Exception {
		final String referencePath = args[0];
		final String candidatePath = args[1];
		final String dir = args[2];

		final List<CloneReference> references = loadReferences(referencePath);
		final List<CloneCandidate> candidates = loadCandidates(candidatePath);

		final Map<Integer, CloneReference> referencesMap = new TreeMap<Integer, CloneReference>();
		for (final CloneReference reference : references) {
			referencesMap.put(reference.getId(), reference);
		}

		final Map<Integer, CloneCandidate> candidatesMap = new TreeMap<Integer, CloneCandidate>();
		for (final CloneCandidate candidate : candidates) {
			candidatesMap.put(candidate.getId(), candidate);
		}

		final List<ReferenceCandidateMap> rawMappings = detectRawMappings(
				references, candidates);
		final Map<CloneCandidate, CloneReference> okOverThreshold = detectOKOverThresholdMapping(
				rawMappings, 0.7);

		for (final Map.Entry<CloneCandidate, CloneReference> entry : okOverThreshold
				.entrySet()) {
			final double ok = MetricsCalculator.calcOK(entry.getValue(),
					entry.getKey());
			final double ok2 = MetricsCalculator.calcOK(entry.getKey(),
					entry.getValue());
			System.out.println(ok + ":" + ok2);
		}

		final Set<CloneReference> undetected = new TreeSet<CloneReference>();
		undetected.addAll(references);
		undetected.removeAll(okOverThreshold.values());

		try {
			BenchmarkUIMainFrame frame = new BenchmarkUIMainFrame(
					okOverThreshold, undetected, referencesMap, candidatesMap,
					dir);
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<CloneReference> loadReferences(
			final String referencePath) throws Exception {
		final ReferenceLoader loader = new ReferenceLoader();
		return loader.load(referencePath);
	}

	private static List<CloneCandidate> loadCandidates(
			final String candidatePath) throws Exception {
		final CandidateLoader loader = new CandidateLoader();
		return loader.load(candidatePath);
	}

	private static List<ReferenceCandidateMap> detectRawMappings(
			final List<CloneReference> references,
			final List<CloneCandidate> candidates) {
		final ReferenceCandidateMapper mapper = new ReferenceCandidateMapper();
		return mapper.detectMap(candidates, references);
	}

	private static Map<CloneCandidate, CloneReference> detectOKOverThresholdMapping(
			final List<ReferenceCandidateMap> rawMappings,
			final double threshold) {
		final Map<CloneCandidate, CloneReference> result = new HashMap<CloneCandidate, CloneReference>();
		for (final ReferenceCandidateMap mapping : rawMappings) {
			if (mapping.getOk() > threshold) {
				result.put(mapping.getCandidate(), mapping.getReference());
			}
		}
		return result;
	}

}
