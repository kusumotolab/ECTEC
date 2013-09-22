package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.IfStatement;

/**
 * A crd creator for if statements
 * 
 * @author k-hotta
 * 
 */
public class IfStatementCRDCreator extends AbstractCRDCreator<IfStatement> {

	public IfStatementCRDCreator(IfStatement node, List<CRD> ancestors) {
		super(node, ancestors, BlockType.IF);
	}

	/**
	 * get the anchor (the conditional expression)
	 */
	@Override
	protected String getAnchor() {
		return node.getExpression().toString();
	}

}
