package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.DoStatement;

/**
 * A crd creator for do statements
 * 
 * @author k-hotta
 * 
 */
public class DoStatementCRDCreator extends AbstractCRDCreator<DoStatement> {

	public DoStatementCRDCreator(DoStatement node, List<CRD> ancestors) {
		super(node, ancestors, BlockType.DO);
	}

	/**
	 * get the anchor (the conditional expression)
	 */
	@Override
	protected String getAnchor() {
		return node.getExpression().toString();
	}

}
