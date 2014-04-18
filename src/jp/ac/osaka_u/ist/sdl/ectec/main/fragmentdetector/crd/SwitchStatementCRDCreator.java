package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.NormalizedStringCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.StringCreateVisitor;

import org.eclipse.jdt.core.dom.SwitchStatement;

/**
 * A crd creator for switch statements
 * 
 * @author k-hotta
 * 
 */
public class SwitchStatementCRDCreator extends
		AbstractBlockAnalyzer<SwitchStatement> {

	public SwitchStatementCRDCreator(SwitchStatement node, DBCrdInfo parent,
			StringCreateVisitor visitor) {
		super(node, parent, BlockType.SWITCH, visitor);
	}

	/**
	 * get the anchor (the expression)
	 */
	@Override
	protected String getAnchor() {
		return getAnchor(node);
	}

	public static String getAnchor(final SwitchStatement node) {
		return node.getExpression().toString();
	}

	@Override
	protected String getNormalizedAnchor() {
		final NormalizedStringCreator anchorNormalizer = new NormalizedStringCreator();
		node.getExpression().accept(anchorNormalizer);
		return anchorNormalizer.getString();
	}

}
