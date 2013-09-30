package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.SwitchStatement;

/**
 * A crd creator for switch statements
 * 
 * @author k-hotta
 * 
 */
public class SwitchStatementCRDCreator extends
		AbstractBlockAnalyzer<SwitchStatement> {

	public SwitchStatementCRDCreator(SwitchStatement node, CRD parent,
			IHashCalculator visitor) {
		super(node, parent, BlockType.SWITCH, visitor);
	}

	/**
	 * get the anchor (the expression)
	 */
	@Override
	protected String getAnchor() {
		return node.getExpression().toString();
	}

}
