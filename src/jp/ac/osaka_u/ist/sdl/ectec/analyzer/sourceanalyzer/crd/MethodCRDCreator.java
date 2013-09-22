package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

/**
 * A crd creator for methods
 * 
 * @author k-hotta
 * 
 */
public class MethodCRDCreator extends AbstractCRDCreator<MethodDeclaration> {

	public MethodCRDCreator(MethodDeclaration node, List<CRD> ancestors) {
		super(node, ancestors, BlockType.METHOD);
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

}
