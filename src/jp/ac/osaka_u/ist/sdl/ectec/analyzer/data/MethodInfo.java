package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.MethodDeclaration;

/**
 * A class that represents methods
 * 
 * @author k-hotta
 * 
 */
public class MethodInfo extends BlockInfo<MethodDeclaration> {

	public MethodInfo(long id, FileInfo ownerFile, CRD crd,
			CombinedRevisionInfo startCombinedRevision,
			CombinedRevisionInfo endCombinedRevision, int startLine,
			int endLine, int size, MethodDeclaration node) {
		super(id, ownerFile, crd, startCombinedRevision, endCombinedRevision,
				startLine, endLine, size, BlockType.METHOD, node);
	}

}
