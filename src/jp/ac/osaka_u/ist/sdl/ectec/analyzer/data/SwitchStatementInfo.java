package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;

import org.eclipse.jdt.core.dom.SwitchStatement;

/**
 * A class that represents switch statements
 * 
 * @author k-hotta
 * 
 */
public class SwitchStatementInfo extends BlockInfo<SwitchStatement> {

	public SwitchStatementInfo(long id, FileInfo ownerFile, CRD crd,
			CombinedRevisionInfo startCombinedRevision,
			CombinedRevisionInfo endCombinedRevision, int startLine,
			int endLine, int size, SwitchStatement node) {
		super(id, ownerFile, crd, startCombinedRevision, endCombinedRevision,
				startLine, endLine, size, BlockType.SWITCH, node);
	}

}
