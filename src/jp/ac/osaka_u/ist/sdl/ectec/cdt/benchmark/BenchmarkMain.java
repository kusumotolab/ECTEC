package jp.ac.osaka_u.ist.sdl.ectec.cdt.benchmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BenchmarkMain {

	public static void main(String[] args) throws Exception {
		final String referencePath = args[0];
		final String candidatePath = args[1];
		final String outputPath = args[2];

		final List<CloneReference> references = loadReferences(referencePath);
		final List<CloneCandidate> candidates = loadCandidates(candidatePath);

		final List<ReferenceCandidateMap> rawMappings = detectRawMappings(
				references, candidates);
		final Map<CloneCandidate, CloneReference> goodOverThreshold = detectGoodOverThresholdMapping(
				rawMappings, 0.7);
		final Map<CloneCandidate, CloneReference> goodUnderThreshold = detectGoodUnderThresholdMapping(
				rawMappings, 0.7);
		final Map<CloneCandidate, CloneReference> okOverThreshold = detectOKOverThresholdMapping(
				rawMappings, 0.7);
		final Map<CloneCandidate, CloneReference> okUnderThreshold = detectOKUnderThresholdMapping(
				rawMappings, 0.7);

		final PrintWriter pw = new PrintWriter(new BufferedWriter(
				new FileWriter(new File(outputPath))));
		
		pw.println(" ,#REF,#CAND,#REF_DETECTED,#ORACLED,RECALL,PRECISION,REJECTED");
		
		pw.print("good,");
		pw.print(references.size() + ",");
		pw.print(candidates.size() + ",");
		pw.print(goodOverThreshold.size() + ",");
		pw.print((goodOverThreshold.size() + goodUnderThreshold.size()) + ",");
		pw.print(Evaluator.calcRecall(references, candidates, goodOverThreshold) + ",");
		pw.print(Evaluator.calcPrecision(references, candidates, goodOverThreshold) + ",");
		pw.println(Evaluator.calcRejected(goodOverThreshold, goodUnderThreshold));
		
		pw.print("ok,");
		pw.print(references.size() + ",");
		pw.print(candidates.size() + ",");
		pw.print(okOverThreshold.size() + ",");
		pw.print((okOverThreshold.size() + okUnderThreshold.size()) + ",");
		pw.print(Evaluator.calcRecall(references, candidates, okOverThreshold) + ",");
		pw.print(Evaluator.calcPrecision(references, candidates, okOverThreshold) + ",");
		pw.println(Evaluator.calcRejected(okOverThreshold, okUnderThreshold));
		
		pw.close();
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

	private static Map<CloneCandidate, CloneReference> detectGoodOverThresholdMapping(
			final List<ReferenceCandidateMap> rawMappings,
			final double threshold) {
		final Map<CloneCandidate, CloneReference> result = new HashMap<CloneCandidate, CloneReference>();
		for (final ReferenceCandidateMap mapping : rawMappings) {
			if (mapping.getGood() >= threshold) {
				result.put(mapping.getCandidate(), mapping.getReference());
			}
		}
		return result;
	}

	private static Map<CloneCandidate, CloneReference> detectGoodUnderThresholdMapping(
			final List<ReferenceCandidateMap> rawMappings,
			final double threshold) {
		final Map<CloneCandidate, CloneReference> result = new HashMap<CloneCandidate, CloneReference>();
		for (final ReferenceCandidateMap mapping : rawMappings) {
			if (mapping.getGood() < threshold) {
				result.put(mapping.getCandidate(), mapping.getReference());
			}
		}
		return result;
	}

	private static Map<CloneCandidate, CloneReference> detectOKOverThresholdMapping(
			final List<ReferenceCandidateMap> rawMappings,
			final double threshold) {
		final Map<CloneCandidate, CloneReference> result = new HashMap<CloneCandidate, CloneReference>();
		for (final ReferenceCandidateMap mapping : rawMappings) {
			if (mapping.getOk() >= threshold) {
				result.put(mapping.getCandidate(), mapping.getReference());
			}
		}
		return result;
	}

	private static Map<CloneCandidate, CloneReference> detectOKUnderThresholdMapping(
			final List<ReferenceCandidateMap> rawMappings,
			final double threshold) {
		final Map<CloneCandidate, CloneReference> result = new HashMap<CloneCandidate, CloneReference>();
		for (final ReferenceCandidateMap mapping : rawMappings) {
			if (mapping.getOk() < threshold) {
				result.put(mapping.getCandidate(), mapping.getReference());
			}
		}
		return result;
	}

}
