package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.similarity;

import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;

/**
 * A class that calculates crd similarities with Levenshtein distances
 * 
 * @author k-hotta
 * 
 */
public class LevenshteinBasedCRDSimilarityCalculator implements
		ICRDSimilarityCalculator {

	@Override
	public double calcSimilarity(CRD crd, CRD anotherCrd) {
		return StringUtils.calcLebenshteinDistanceBasedSimilarity(
				crd.getFullText(), anotherCrd.getFullText());
	}

}
