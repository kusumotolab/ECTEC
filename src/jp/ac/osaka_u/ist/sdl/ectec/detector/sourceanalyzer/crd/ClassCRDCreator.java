package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.crd;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.StringCreateVisitor;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * A crd creator for classes
 * 
 * @author k-hotta
 * 
 */
public class ClassCRDCreator extends AbstractBlockAnalyzer<TypeDeclaration> {

	public ClassCRDCreator(TypeDeclaration node, CRD parent,
			StringCreateVisitor visitor) {
		super(node, parent, BlockType.CLASS, visitor);
	}

	/**
	 * get the anchor (the fully qualified name)
	 */
	@Override
	protected String getAnchor() {
		return getFullyQualifiedName(node);
	}

	private final String getFullyQualifiedName(ASTNode node) {
		final StringBuilder builder = new StringBuilder();
		detectFullyQualifiedName(node, builder);
		return builder.toString();
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

	@Override
	protected String getNormalizedAnchor() {
		return getFullyQualifiedName(node);
	}

}
