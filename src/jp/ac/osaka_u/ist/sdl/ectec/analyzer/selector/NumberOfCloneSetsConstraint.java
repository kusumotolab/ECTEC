package jp.ac.osaka_u.ist.sdl.ectec.analyzer.selector;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;

/**
 * A constraint for nubmer of clone sets to choose genealogies
 * 
 * @author k-hotta
 * 
 */
public class NumberOfCloneSetsConstraint implements IConstraint {

	/**
	 * the lower threshold
	 */
	private int lowerThreshold;

	/**
	 * the higher threshold
	 */
	private int higherThreshold;

	public NumberOfCloneSetsConstraint() {
		lowerThreshold = 1;
		higherThreshold = Integer.MAX_VALUE;
	}

	/**
	 * set the lower threshold
	 * 
	 * @param lowerThreshold
	 *            must be >= 1, set by 1 instead of the given value if the given
	 *            value is lower than 1
	 */
	public void setLowerThreshold(final int lowerThreshold) {
		this.lowerThreshold = Math.max(1, lowerThreshold);
	}

	public void resetLowerThreshold() {
		this.lowerThreshold = 1;
	}

	/**
	 * set the higher threshold
	 * 
	 * @param higherThreshold
	 *            must be >= 1, set by 1 instead of the given value if the given
	 *            value is lower than 1
	 */
	public void setUpperThreshold(final int higherThreshold) {
		this.higherThreshold = Math.max(1, higherThreshold);
	}

	public void resetHigherThreshold() {
		this.higherThreshold = Integer.MAX_VALUE;
	}

	@Override
	public boolean satisfy(DBCloneGenealogyInfo genealogy) {
		final int numberOfCloneSets = genealogy.getElements().size();
		return numberOfCloneSets >= lowerThreshold
				&& numberOfCloneSets <= higherThreshold;
	}

}
