package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import java.util.List;

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
		AbstractCRDCreator<SwitchStatement> {

	public SwitchStatementCRDCreator(SwitchStatement node, List<CRD> ancestors) {
		super(node, ancestors, BlockType.SWITCH);
	}

	/**
	 * get the anchor (the expression)
	 */
	@Override
	protected String getAnchor() {
		return node.getExpression().toString();
	}

}
