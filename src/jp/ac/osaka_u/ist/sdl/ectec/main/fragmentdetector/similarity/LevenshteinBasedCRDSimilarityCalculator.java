package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.similarity;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
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
	public double calcSimilarity(DBCrdInfo crd, DBCrdInfo anotherCrd) {
		return StringUtils.calcLebenshteinDistanceBasedSimilarity(
				crd.getFullText(), anotherCrd.getFullText());
	}

}
