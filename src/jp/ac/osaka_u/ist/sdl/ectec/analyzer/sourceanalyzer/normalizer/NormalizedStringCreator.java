package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.normalizer;

import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeLiteral;

/**
 * A class to determine the behavior of toString methods
 * 
 * @author k-hotta
 * 
 */
public class NormalizedStringCreator extends StringCreateVisitor {

	@Override
	public boolean visit(Javadoc node) {
		return false;
	}

	@Override
	public boolean visit(SimpleName node) {
		this.buffer.append("$");
		return false;
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		this.buffer.append("$");
		return false;
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		this.buffer.append("$");
		return false;
	}

	@Override
	public boolean visit(NumberLiteral node) {
		this.buffer.append("$");
		return false;
	}

	@Override
	public boolean visit(StringLiteral node) {
		this.buffer.append("$");
		return false;
	}

	@Override
	public boolean visit(TypeLiteral node) {
		this.buffer.append("$");
		return false;
	}

	@Override
	public boolean visit(SimpleType node) {
		this.buffer.append(node.getName().toString());
		return false;
	}

	@Override
	public boolean visit(MarkerAnnotation node) {
		return false;
	}

	@Override
	public boolean visit(NormalAnnotation node) {
		return false;
	}

	@Override
	public boolean visit(SingleMemberAnnotation node) {
		return false;
	}

	@Override
	public boolean visit(Modifier node) {
		return false;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		// if (node.getExpression() != null) {
		// node.getExpression().accept(this);
		// this.buffer.append(".");
		// }

		// if (!node.typeArguments().isEmpty()) {
		// this.buffer.append("<");
		// boolean isFirstTypeArgument = true;
		// for (Object obj : node.typeArguments()) {
		// Type t = (Type) obj;
		// if (!isFirstTypeArgument) {
		// this.buffer.append(",");
		// } else {
		// isFirstTypeArgument = false;
		// }
		// t.accept(this);
		// }
		// this.buffer.append(">");
		// }

		this.buffer.append(node.getName().toString());

		this.buffer.append("(");
		boolean isFirstArgument = true;
		for (Object obj : node.arguments()) {
			Expression e = (Expression) obj;
			if (!isFirstArgument) {
				this.buffer.append(",");
			} else {
				isFirstArgument = false;
			}
			e.accept(this);
		}
		this.buffer.append(")");

		return false;
	}

}
