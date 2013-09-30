package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.normalizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * A class to detect descriminators of blocks ( = normalized anchors)
 * 
 * @author k-hotta
 * 
 */
public class DescriminatorDetector extends StringCreateVisitor {

	private String normalizedAnchor;

	private void setNormalizedAnchor(final String str) {
		this.normalizedAnchor = str;
	}

	public String getNormalizedAnchor() {
		return normalizedAnchor;
	}

	/**
	 * processing class declarations
	 */
	@Override
	public boolean visit(TypeDeclaration node) {
		if (node.isInterface()) {
			return false;
		}

		// modifiers
		boolean isFirstModifier = true;
		for (Object obj : node.modifiers()) {
			if (!isFirstModifier) {
				getBuffer().append(" ");
			} else {
				isFirstModifier = false;
			}
			ASTNode modifier = (ASTNode) obj;
			modifier.accept(this);
		}

		// class name
		getBuffer().append(" class $");

		// type arguments
		if (!node.typeParameters().isEmpty()) {
			getBuffer().append("<");
			boolean isFirstTypeParameter = true;
			for (Object obj : node.typeParameters()) {
				if (!isFirstTypeParameter) {
					getBuffer().append(", ");
				} else {
					isFirstTypeParameter = false;
				}
				ASTNode typeParameter = (ASTNode) obj;
				typeParameter.accept(this);
				// this.getBuilder().append(obj.toString());
			}
			getBuffer().append(">");
		}
		getBuffer().append(" ");

		// parent class
		if (node.getSuperclassType() != null) {
			getBuffer().append("extends ");
			node.getSuperclassType().accept(this);
			// this.getBuilder().append(node.getSuperclassType().toString());
			getBuffer().append(" ");
		}

		// implementing interfaces
		if (!node.superInterfaceTypes().isEmpty()) {
			getBuffer().append("implements ");
			boolean isFirstSuperInterface = true;
			for (Object obj : node.superInterfaceTypes()) {
				if (!isFirstSuperInterface) {
					getBuffer().append(", ");
				} else {
					isFirstSuperInterface = false;
				}
				ASTNode superInterface = (ASTNode) obj;
				superInterface.accept(this);
				// this.getBuilder().append(obj.toString());
			}
			getBuffer().append(" ");
		}

		final String normalizedAnchor = getStringWhiteSpacesRemoved();

		// body
		getBuffer().append("{\n");
		for (Object obj : node.bodyDeclarations()) {
			BodyDeclaration body = (BodyDeclaration) obj;
			body.accept(this);
		}
		getBuffer().append("}\n");

		setNormalizedAnchor(normalizedAnchor);

		return false;
	}

	/**
	 * processing method declarations
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		// modifiers
		for (Object obj : node.modifiers()) {
			((ASTNode) obj).accept(this);
		}

		// type arguments
		if (!node.typeParameters().isEmpty()) {
			getBuffer().append("<");
			boolean isFirstTypeParameter = true;
			for (Object obj : node.typeParameters()) {
				if (!isFirstTypeParameter) {
					getBuffer().append(",");
				} else {
					isFirstTypeParameter = false;
				}
				((ASTNode) obj).accept(this);
			}
			getBuffer().append("> ");
		}

		// return values
		if (!node.isConstructor()) {
			if (node.getReturnType2() != null) {
				node.getReturnType2().accept(this);
			} else {
				getBuffer().append("void");
			}
			getBuffer().append(" ");
		}

		// name
		getBuffer().append("$");

		// arguments
		getBuffer().append("(");
		boolean isFirstParameter = true;
		for (Object obj : node.parameters()) {
			if (!isFirstParameter) {
				getBuffer().append(",");
			} else {
				isFirstParameter = false;
			}
			((ASTNode) obj).accept(this);
		}
		getBuffer().append(")");
		for (int i = 0; i < node.getExtraDimensions(); i++) {
			getBuffer().append("[]");
		}

		// throwns exceptions
		if (!node.thrownExceptions().isEmpty()) {
			getBuffer().append(" throws ");
			boolean isFirstException = true;
			for (Object obj : node.thrownExceptions()) {
				if (!isFirstException) {
					getBuffer().append(", ");
				} else {
					isFirstException = false;
				}

				getBuffer().append(obj.toString());
			}
			getBuffer().append(" ");
		}

		final String normalizedAnchor = getStringWhiteSpacesRemoved();

		// body
		if (node.getBody() == null) {
			getBuffer().append(";\n");
		} else {
			node.getBody().accept(this);
		}

		setNormalizedAnchor(normalizedAnchor);

		return false;
	}

	/**
	 * for statements
	 */
	@Override
	public boolean visit(ForStatement node) {
		getBuffer().append("for (");

		// initializer
		boolean isFirstInitializer = true;
		for (Object obj : node.initializers()) {
			if (!isFirstInitializer) {
				getBuffer().append(", ");
			} else {
				isFirstInitializer = false;
			}
			ASTNode initializer = (ASTNode) obj;
			initializer.accept(this);
			// getBuilder().append(obj.toString());
		}
		getBuffer().append("; ");

		// conditional predicate
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			// getBuilder().append(node.getExpression().toString());
		}
		getBuffer().append("; ");

		// updater
		boolean isFirstUpdater = true;
		for (Object obj : node.updaters()) {
			if (!isFirstUpdater) {
				getBuffer().append(", ");
			} else {
				isFirstUpdater = false;
			}
			ASTNode updater = (ASTNode) obj;
			updater.accept(this);
			// getBuilder().append(obj.toString());
		}

		getBuffer().append(")");

		final String normalizedAnchor = getStringWhiteSpacesRemoved();

		// body
		node.getBody().accept(this);

		setNormalizedAnchor(normalizedAnchor);

		return false;
	}

	/**
	 * for-each statements
	 */
	@Override
	public boolean visit(EnhancedForStatement node) {
		getBuffer().append("for (");
		node.getParameter().accept(this);
		getBuffer().append(" : ");
		node.getExpression().accept(this);
		getBuffer().append(") ");

		final String normalizedAnchor = getStringWhiteSpacesRemoved();

		node.getBody().accept(this);

		setNormalizedAnchor(normalizedAnchor);

		return false;
	}

	/**
	 * while statements
	 */
	@Override
	public boolean visit(WhileStatement node) {
		getBuffer().append("while (");
		node.getExpression().accept(this);
		getBuffer().append(") ");

		final String normalizedAnchor = getStringWhiteSpacesRemoved();

		node.getBody().accept(this);

		setNormalizedAnchor(normalizedAnchor);

		return false;
	}

	/**
	 * do-while statements
	 */
	@Override
	public boolean visit(DoStatement node) {
		getBuffer().append("do ");

		node.getBody().accept(this);
		final int bodyLength = getBuffer().toString().length();

		getBuffer().append(" while (");
		node.getExpression().accept(this);
		getBuffer().append(");");

		final String discriminator = getBuffer().toString().substring(
				bodyLength);

		getBuffer().append("\n");

		setNormalizedAnchor(discriminator);

		return false;
	}

	/**
	 * if statements
	 */
	@Override
	public boolean visit(IfStatement node) {
		getBuffer().append("if (");
		node.getExpression().accept(this);
		getBuffer().append(") ");

		final String normalizedAnchor = getStringWhiteSpacesRemoved();

		node.getThenStatement().accept(this);

		if (node.getElseStatement() != null) {
			node.getElseStatement().accept(this);
		}

		setNormalizedAnchor(normalizedAnchor);

		return false;
	}

	/**
	 * else statements
	 * 
	 * @param node
	 */
	public void visitElse(Statement node) {
		final List<String> predicates = new ArrayList<String>();
		ASTNode parent = node.getParent();
		while (parent instanceof IfStatement) {
			final IfStatement parentIf = (IfStatement) parent;
			predicates.add(getConditonalPredicate(parentIf));
		}

		StringBuilder builder = new StringBuilder();
		if (!predicates.isEmpty()) {
			builder.append("(");
			boolean isFirstPredicate = true;
			for (String predicate : predicates) {
				if (!isFirstPredicate) {
					builder.append(" && ");
				} else {
					isFirstPredicate = false;
				}
				builder.append("!(");
				builder.append(predicate);
				builder.append(")");
			}
			builder.append(")");
		}

		node.accept(this);

		setNormalizedAnchor(builder.toString());
	}

	public String getConditonalPredicate(IfStatement node) {
		final DescriminatorDetector newVisitor = new DescriminatorDetector();
		node.accept(newVisitor);

		return newVisitor.getNormalizedAnchor();
	}

	/**
	 * switch statements
	 */
	@Override
	public boolean visit(SwitchStatement node) {
		getBuffer().append("switch (");
		node.getExpression().accept(this);
		getBuffer().append(") ");

		final String normalizedAnchor = getStringWhiteSpacesRemoved();

		getBuffer().append("{\n");
		for (Iterator it = node.statements().iterator(); it.hasNext();) {
			Statement s = (Statement) it.next();
			s.accept(this);
		}
		getBuffer().append("}\n");

		setNormalizedAnchor(normalizedAnchor);

		return false;
	}

	/**
	 * try statements
	 */
	@Override
	public boolean visit(TryStatement node) {
		getBuffer().append("try ");

		node.getBody().accept(this);

		for (Object obj : node.catchClauses()) {
			CatchClause catchClause = (CatchClause) obj;
			catchClause.accept(this);
		}

		if (node.getFinally() != null) {
			final ASTNode finallyNode = node.getFinally();
			finallyNode.accept(this);
		}

		setNormalizedAnchor(getTryAnchor(node));

		return false;
	}

	private String getTryAnchor(final TryStatement node) {
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
	 * finally blocks
	 * 
	 * @param node
	 */
	public void visitFinally(Block node) {
		final TryStatement parentTry = (TryStatement) node.getParent();
		final String tryAnchor = getTryAnchor(parentTry);

		node.accept(this);

		setNormalizedAnchor(tryAnchor);
	}

	/**
	 * catch clauses
	 */
	@Override
	public boolean visit(CatchClause node) {
		getBuffer().append("catch (");
		node.getException().accept(this);
		getBuffer().append(") ");

		final String normalizedAnchor = getStringWhiteSpacesRemoved();

		node.getBody().accept(this);

		setNormalizedAnchor(normalizedAnchor);

		return false;
	}

	/**
	 * synchronized statements
	 */
	@Override
	public boolean visit(SynchronizedStatement node) {
		getBuffer().append("synchronoized (");

		node.getExpression().accept(this);
		getBuffer().append(") ");

		final String normalizedAnchor = getStringWhiteSpacesRemoved();

		node.getBody().accept(this);

		setNormalizedAnchor(normalizedAnchor);

		return false;
	}

}
