package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.IfStatement;

/**
 * A crd creator for if statements
 * 
 * @author k-hotta
 * 
 */
public class IfStatementCRDCreator extends AbstractBlockAnalyzer<IfStatement> {

	public IfStatementCRDCreator(IfStatement node, CRD parent,
			IHashCalculator visitor) {
		super(node, parent, BlockType.IF, visitor);
	}

	/**
	 * get the anchor (the conditional expression)
	 */
	@Override
	protected String getAnchor() {
		return node.getExpression().toString();
	}

}
