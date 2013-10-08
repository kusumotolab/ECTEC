package jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.crd;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.detector.sourceanalyzer.normalizer.StringCreateVisitor;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

/**
 * A crd creator for methods
 * 
 * @author k-hotta
 * 
 */
public class MethodCRDCreator extends AbstractBlockAnalyzer<MethodDeclaration> {

	public MethodCRDCreator(MethodDeclaration node, DBCrdInfo parent,
			StringCreateVisitor visitor) {
		super(node, parent, BlockType.METHOD, visitor);
	}

	/**
	 * get the anchor (the signature of the declared method)
	 */
	@Override
	protected String getAnchor() {
		return detectCanonicalSignature(node);
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

	@Override
	protected String getNormalizedAnchor() {
		return detectCanonicalSignature(node);
	}

}
