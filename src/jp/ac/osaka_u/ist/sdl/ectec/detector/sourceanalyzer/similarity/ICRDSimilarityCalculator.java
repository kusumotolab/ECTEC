package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.similarity;

import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

/**
 * An interface for calculating similarities between two crds
 * 
 * @author k-hotta
 * 
 */
public interface ICRDSimilarityCalculator {

	/**
	 * calculate hash values between the given two crds <br>
	 * this function must satisfy the symmetric law (calc(a, b) = calc(b, a))
	 * 
	 * @param crd
	 * @param anotherCrd
	 * @return
	 */
	public double calcSimilarity(final CRD crd, final CRD anotherCrd);

}
