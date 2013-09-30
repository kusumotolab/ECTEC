package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.EnhancedForStatement;

/**
 * A crd creator for enhanced for statements
 * 
 * @author k-hotta
 * 
 */
public class EnhancedForStatementCRDCreator extends
		AbstractBlockAnalyzer<EnhancedForStatement> {

	public EnhancedForStatementCRDCreator(EnhancedForStatement node,
			CRD parent, IHashCalculator visitor) {
		super(node, parent, BlockType.ENHANCED_FOR, visitor);
	}

	/**
	 * get the anchor (the conditional expression)
	 */
	@Override
	protected String getAnchor() {
		return node.getExpression().toString();
	}

}
