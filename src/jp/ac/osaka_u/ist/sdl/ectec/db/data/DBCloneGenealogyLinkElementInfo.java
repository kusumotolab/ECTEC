package jp.ac.osaka_u.ist.sdl.ectec.db.data;

/**
 * A class that represents an element of the table of link elements of clone
 * genealogies
 * 
 * @author k-hotta
 * 
 */
public class DBCloneGenealogyLinkElementInfo extends
		AbstractDBSubTableElementInfo {

	public DBCloneGenealogyLinkElementInfo(long mainElementId, long subElementId) {
		super(mainElementId, subElementId);
	}

}
