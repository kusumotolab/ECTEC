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
			RevisionInfo startRevision, RevisionInfo endRevision,
			int startLine, int endLine, int size, TypeDeclaration node) {
		super(id, ownerFile, crd, startRevision, endRevision, startLine,
				endLine, size, BlockType.CLASS, node);
	}

}
