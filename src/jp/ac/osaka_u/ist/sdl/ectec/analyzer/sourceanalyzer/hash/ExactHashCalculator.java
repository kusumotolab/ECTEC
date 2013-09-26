package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * A default hash calculator which uses String.hashCode()
 * 
 * @author k-hotta
 * 
 */
public class ExactHashCalculator implements IHashCalculator {

	@Override
	public long getHashValue(final ASTNode node) {
		return (long) node.toString().hashCode();
	}

}
