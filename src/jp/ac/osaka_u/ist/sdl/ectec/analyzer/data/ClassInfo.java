package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * A class that represents classes
 * 
 * @author k-hotta
 * 
 */
public class ClassInfo extends BlockInfo<TypeDeclaration> {

	public ClassInfo(long id, FileInfo ownerFile, CRD crd,
			CombinedRevisionInfo startCombinedRevision,
			CombinedRevisionInfo endCombinedRevision, int startLine,
			int endLine, int size, TypeDeclaration node) {
		super(id, ownerFile, crd, startCombinedRevision, endCombinedRevision,
				startLine, endLine, size, BlockType.CLASS, node);
	}

}
