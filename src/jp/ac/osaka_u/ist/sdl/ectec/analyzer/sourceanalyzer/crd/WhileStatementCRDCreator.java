package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * A crd creator for while statements
 * 
 * @author k-hotta
 * 
 */
public class WhileStatementCRDCreator extends
		AbstractCRDCreator<WhileStatement> {

	public WhileStatementCRDCreator(WhileStatement node, CRD parent) {
		super(node, parent, BlockType.WHILE);
	}

	/**
	 * get the anchor (the conditional expression)
	 */
	@Override
	protected String getAnchor() {
		return node.getExpression().toString();
	}

}
