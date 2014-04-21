package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.NormalizedStringCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.StringCreateVisitor;

import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * A crd creator for while statements
 * 
 * @author k-hotta
 * 
 */
public class WhileStatementCRDCreator extends
		AbstractBlockAnalyzer<WhileStatement> {

	public WhileStatementCRDCreator(WhileStatement node, DBCrdInfo parent,
			StringCreateVisitor visitor) {
		super(node, parent, BlockType.WHILE, visitor);
	}

	/**
	 * get the anchor (the conditional expression)
	 */
	@Override
	protected String getAnchor() {
		return getAnchor(node);
	}

	public static String getAnchor(final WhileStatement node) {
		return node.getExpression().toString();
	}

	@Override
	protected String getNormalizedAnchor() {
		final NormalizedStringCreator anchorNormalizer = new NormalizedStringCreator();
		node.getExpression().accept(anchorNormalizer);
		return anchorNormalizer.getString();
	}

}
