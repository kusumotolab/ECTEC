package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import java.util.List;

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
		AbstractCRDCreator<EnhancedForStatement> {

	public EnhancedForStatementCRDCreator(EnhancedForStatement node,
			List<CRD> ancestors) {
		super(node, ancestors, BlockType.ENHANCED_FOR);
	}

	/**
	 * get the anchor (the conditional expression)
	 */
	@Override
	protected String getAnchor() {
		return node.getExpression().toString();
	}

}
