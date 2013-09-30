package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.hash.IHashCalculator;
import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.DoStatement;

/**
 * A crd creator for do statements
 * 
 * @author k-hotta
 * 
 */
public class DoStatementCRDCreator extends AbstractBlockAnalyzer<DoStatement> {

	public DoStatementCRDCreator(DoStatement node, CRD parent,
			IHashCalculator visitor) {
		super(node, parent, BlockType.DO, visitor);
	}

	/**
	 * get the anchor (the conditional expression)
	 */
	@Override
	protected String getAnchor() {
		return node.getExpression().toString();
	}

}
