package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.normalizer.DescriminatorDetector;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * An abstract class that represents how to calculate a hash value from a code
 * fragment
 * 
 * @author k-hotta
 * 
 */
public abstract class IHashCalculator extends DescriminatorDetector {

	/**
	 * get a hash value calculated from the given node as a long value
	 * 
	 * @param node
	 * @return
	 */
	public abstract long getHashValue(final ASTNode node);

}
