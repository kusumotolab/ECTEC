package jp.ac.osaka_u.ist.sdl.ectec.db.data;

/**
 * A class that represents an element of the table of link elements of fragment
 * genealogies
 * 
 * @author k-hotta
 * 
 */
public class DBCodeFragmentGenealogyLinkElementInfo extends
		AbstractDBSubTableElementInfo {

	public DBCodeFragmentGenealogyLinkElementInfo(long mainElementId,
			long subElementId) {
		super(mainElementId, subElementId);
	}

}
