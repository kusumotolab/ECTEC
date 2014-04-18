package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.crd;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.NormalizedStringCreator;
import jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer.StringCreateVisitor;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;

/**
 * A crd creator for else statements
 * 
 * @author k-hotta
 * 
 */
public class ElseStatementCRDCreator extends AbstractBlockAnalyzer<Statement> {

	public ElseStatementCRDCreator(Statement node, DBCrdInfo parent,
			StringCreateVisitor visitor) {
		super(node, parent, BlockType.ELSE, visitor);
	}

	/**
	 * get the anchor (the predicates of the outer if statements connected into
	 * a string)
	 */
	@Override
	protected String getAnchor() {
		return getAnchor(node);
	}

	public static String getAnchor(final Statement node) {
		return detectElsePredicates(node);
	}

	@Override
	protected String getNormalizedAnchor() {
		return detectNormalizedElsePredicates(node);
	}

	private static String detectElsePredicates(Statement elseStatement) {
		List<String> predicates = new LinkedList<String>();
		detectPredicates(elseStatement.getParent(), predicates);

		return convert(predicates);
	}

	private static void detectPredicates(ASTNode node, List<String> predicates) {
		if (!(node instanceof IfStatement)) {
			return;
		}

		detectPredicates(node.getParent(), predicates);

		IfStatement ifStatement = (IfStatement) node;
		predicates.add(ifStatement.getExpression().toString());
	}

	private static String convert(final List<String> predicates) {
		final StringBuilder builder = new StringBuilder();
		if (!predicates.isEmpty()) {
			builder.append("!(");
			boolean isFirstPredicate = true;
			for (String predicate : predicates) {
				if (!isFirstPredicate) {
					builder.append(" || ");
				} else {
					isFirstPredicate = false;
				}
				builder.append("!(");
				builder.append(predicate);
				builder.append(")");
			}
			builder.append(")");
		}

		return builder.toString();
	}

	private String detectNormalizedElsePredicates(Statement elseStatement) {
		final NormalizedStringCreator anchorNormalizer = new NormalizedStringCreator();

		final List<IfStatement> parentIfs = new ArrayList<IfStatement>();
		ASTNode parent = node.getParent();
		while (parent instanceof IfStatement) {
			final IfStatement parentIf = (IfStatement) parent;
			parentIfs.add(parentIf);
			parent = parent.getParent();
		}

		if (!parentIfs.isEmpty()) {
			anchorNormalizer.getBuffer().append("!(");

			boolean isFirstPredicate = true;
			for (final IfStatement parentIf : parentIfs) {
				if (!isFirstPredicate) {
					anchorNormalizer.getBuffer().append(" || ");
				} else {
					isFirstPredicate = false;
				}
				anchorNormalizer.getBuffer().append("!(");
				parentIf.getExpression().accept(anchorNormalizer);
				anchorNormalizer.getBuffer().append(")");
			}

			anchorNormalizer.getBuffer().append(")");
		}

		return anchorNormalizer.getString();
	}

}
