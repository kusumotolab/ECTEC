package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector.normalizer;

import java.util.LinkedList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * A class for detecting strings of blocks whose subblocks are normalized
 * 
 * @author k-hotta
 * 
 */
public class SubblockNormalizedBlockVisitor extends NormalizedStringCreator {

	/**
	 * true if it is the first call of any visit method
	 */
	private boolean firstVisit = true;

	private void appendRepresentativeString(final String head,
			final String discriminator) {
		final StringBuilder builder = new StringBuilder();
		builder.append(head + " ");
		builder.append(discriminator);
		builder.append("\n");

		buffer.append(builder.toString());
	}

	/**
	 * processing type declarations
	 */
	@Override
	public boolean visit(TypeDeclaration node) {
		if (firstVisit) {
			firstVisit = false;
			return super.visit(node);
		}

		final StringBuilder builder = new StringBuilder();
		detectFullyQualifiedName(node, builder);
		builder.insert(0, "CLASS ");
		builder.append("\n");

		buffer.append(builder.toString());

		return false;
	}

	private void detectFullyQualifiedName(ASTNode node, StringBuilder builder) {
		if (node instanceof TypeDeclaration) {
			detectFullyQualifiedName((TypeDeclaration) node, builder);
		} else if (node instanceof CompilationUnit) {
			detectFullyQualifiedName((CompilationUnit) node, builder);
		} else {
			detectFullyQualifiedName(node.getParent(), builder);
		}
	}

	private void detectFullyQualifiedName(TypeDeclaration node,
			StringBuilder builder) {
		builder.insert(0, node.getName());
		detectFullyQualifiedName(node.getParent(), builder);
	}

	private void detectFullyQualifiedName(CompilationUnit node,
			StringBuilder builder) {
		if (node.getPackage() != null) {
			builder.insert(0, node.getPackage().getName() + ".");
		}
	}

	/**
	 * processing method declarations
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		if (firstVisit) {
			firstVisit = false;
			return super.visit(node);
		}

		appendRepresentativeString("METHOD", detectCanonicalSignature(node));

		return false;
	}

	private String detectCanonicalSignature(MethodDeclaration node) {
		StringBuilder builder = new StringBuilder();

		builder.append(node.getName().toString());

		builder.append("(");
		{
			boolean isFirstParam = true;
			for (Object obj : node.parameters()) {
				SingleVariableDeclaration param = (SingleVariableDeclaration) obj;
				if (!isFirstParam) {
					builder.append(", ");
				} else {
					isFirstParam = false;
				}
				builder.append(param.getType().toString());
			}
		}
		builder.append(")");

		return builder.toString();
	}

	/**
	 * processing for statements
	 */
	@Override
	public boolean visit(ForStatement node) {
		if (firstVisit) {
			firstVisit = false;
			return super.visit(node);
		}

		appendRepresentativeString("FOR ", node.getExpression().toString());

		return false;
	}

	/**
	 * processing for-each statements
	 */
	@Override
	public boolean visit(EnhancedForStatement node) {
		if (firstVisit) {
			firstVisit = false;
			return super.visit(node);
		}

		appendRepresentativeString("FOR ", node.getExpression().toString());

		return false;
	}

	/**
	 * processing while statements
	 */
	@Override
	public boolean visit(WhileStatement node) {
		if (firstVisit) {
			firstVisit = false;
			return super.visit(node);
		}

		appendRepresentativeString("WHILE ", node.getExpression().toString());

		return false;
	}

	/**
	 * processing do-while statements
	 */
	@Override
	public boolean visit(DoStatement node) {
		if (firstVisit) {
			firstVisit = false;
			return super.visit(node);
		}

		appendRepresentativeString("DO ", node.getExpression().toString());

		return false;
	}

	/**
	 * processing if statements
	 */
	@Override
	public boolean visit(IfStatement node) {
		if (firstVisit) {
			firstVisit = false;
			return super.visit(node);
		}

		appendRepresentativeString("IF ", node.getExpression().toString());

		return false;
	}

	/**
	 * processing else blocks and finally blocks
	 */
	@Override
	public boolean visit(Block node) {
		if (firstVisit) {
			firstVisit = false;
			return super.visit(node);
		}

		ASTNode parent = node.getParent();
		if (parent instanceof IfStatement) {
			return processElseBlock(node);
		} else if (parent instanceof TryStatement) {
			return processFinallyBlock(node);
		} else {
			return super.visit(node);
		}
	}

	/**
	 * processing else blocks
	 * 
	 * @param node
	 * @return
	 */
	private boolean processElseBlock(Block node) {
		final IfStatement parentIf = (IfStatement) node.getParent();
		final Statement elseStatement = parentIf.getElseStatement();

		if (elseStatement != node) {
			return super.visit(node);
		}

		appendRepresentativeString("ELSE ", detectElsePredicates(elseStatement));

		return false;
	}

	private String detectElsePredicates(Statement elseStatement) {
		List<String> predicates = new LinkedList<String>();
		detectPredicates(elseStatement.getParent(), predicates);

		return convert(predicates);
	}

	private void detectPredicates(ASTNode node, List<String> predicates) {
		if (!(node instanceof IfStatement)) {
			return;
		}

		detectPredicates(node.getParent(), predicates);

		IfStatement ifStatement = (IfStatement) node;
		predicates.add(ifStatement.getExpression().toString());
	}

	private String convert(final List<String> predicates) {
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

	/**
	 * processing finally blocks
	 * 
	 * @param node
	 * @return
	 */
	private boolean processFinallyBlock(Block node) {
		final TryStatement parentTry = (TryStatement) node.getParent();
		final Block finallyBlock = parentTry.getFinally();

		if (finallyBlock != node) {
			return super.visit(node);
		}

		appendRepresentativeString("FINALLY ", getTryAnchor(parentTry));

		return false;
	}

	/**
	 * processing switch statements
	 */
	@Override
	public boolean visit(SwitchStatement node) {
		if (firstVisit) {
			firstVisit = false;
			return super.visit(node);
		}

		appendRepresentativeString("SWITCH ", node.getExpression().toString());

		return false;
	}

	/**
	 * processing try statements
	 */
	@Override
	public boolean visit(TryStatement node) {
		if (firstVisit) {
			firstVisit = false;
			return super.visit(node);
		}

		appendRepresentativeString("TRY ", getTryAnchor(node));

		return false;
	}

	private String getTryAnchor(TryStatement node) {
		final StringBuilder builder = new StringBuilder();

		@SuppressWarnings("rawtypes")
		List catchClauses = node.catchClauses();

		boolean catchAnyException = false;

		for (Object obj : catchClauses) {
			final CatchClause catchClause = (CatchClause) obj;
			final String caughtExceptionType = catchClause.getException()
					.getType().toString();
			builder.append(caughtExceptionType + Constants.PREDICATE_DIVIDER);
			catchAnyException = true;
		}

		if (catchAnyException) {
			builder.delete(
					builder.length() - Constants.PREDICATE_DIVIDER.length(),
					builder.length());
		}

		return builder.toString();
	}

	/**
	 * processing catch clauses
	 */
	@Override
	public boolean visit(CatchClause node) {
		if (firstVisit) {
			firstVisit = false;
			return super.visit(node);
		}

		appendRepresentativeString("CATCH ", node.getException().getType()
				.toString());

		return false;
	}

	/**
	 * processing synchronized statements
	 */
	@Override
	public boolean visit(SynchronizedStatement node) {
		if (firstVisit) {
			firstVisit = false;
			return super.visit(node);
		}

		appendRepresentativeString("SYNCHRONIZED ", node.getExpression()
				.toString());

		return false;
	}

}
