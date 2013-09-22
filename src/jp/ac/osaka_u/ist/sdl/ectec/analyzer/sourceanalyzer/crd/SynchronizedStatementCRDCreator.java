package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.SynchronizedStatement;

/**
 * A crd creator for synchronized statements
 * 
 * @author k-hotta
 * 
 */
public class SynchronizedStatementCRDCreator extends
		AbstractCRDCreator<SynchronizedStatement> {

	public SynchronizedStatementCRDCreator(SynchronizedStatement node,
			List<CRD> ancestors) {
		super(node, ancestors, BlockType.SYNCHRONIZED);
	}

	/**
	 * get the anchor (the expression)
	 */
	@Override
	protected String getAnchor() {
		return node.getExpression().toString();
	}

}
