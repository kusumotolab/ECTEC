package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.crd;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.NormalizedStringCreator;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.StringCreateVisitor;

import org.eclipse.jdt.core.dom.ForStatement;

/**
 * A crd creator for for statements
 * 
 * @author k-hotta
 * 
 */
public class ForStatementCRDCreator extends AbstractBlockAnalyzer<ForStatement> {

	public ForStatementCRDCreator(ForStatement node, DBCrdInfo parent,
			StringCreateVisitor visitor) {
		super(node, parent, BlockType.FOR, visitor);
	}

	/**
	 * get the anchor (the conditional expression)
	 */
	@Override
	protected String getAnchor() {
		return getAnchor(node);
	}

	public static String getAnchor(final ForStatement node) {
		return (node.getExpression() == null) ? " " : node.getExpression()
				.toString();
	}

	@Override
	protected String getNormalizedAnchor() {
		final NormalizedStringCreator anchorNormalizer = new NormalizedStringCreator();

		if (node.getExpression() != null) {
			node.getExpression().accept(anchorNormalizer);
			return anchorNormalizer.getString();
		} else {
			return " ";
		}
	}

}
