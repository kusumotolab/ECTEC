package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.ForStatement;

/**
 * A crd creator for for statements
 * 
 * @author k-hotta
 * 
 */
public class ForStatementCRDCreator extends AbstractCRDCreator<ForStatement> {

	public ForStatementCRDCreator(ForStatement node, List<CRD> ancestors) {
		super(node, ancestors, BlockType.FOR);
	}

	/**
	 * get the anchor (the conditional expression)
	 */
	@Override
	protected String getAnchor() {
		return (node.getExpression() == null) ? " " : node.getExpression()
				.toString();
	}

}
