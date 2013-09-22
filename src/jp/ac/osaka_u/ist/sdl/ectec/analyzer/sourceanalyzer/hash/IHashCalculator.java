package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * An interface that represents how to calculate a hash value from a code
 * fragment
 * 
 * @author k-hotta
 * 
 */
public interface IHashCalculator {

	/**
	 * get a hash value calculated from the given node as a long value
	 * 
	 * @param node
	 * @return
	 */
	public long getHashValue(final ASTNode node);

}
