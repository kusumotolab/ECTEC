package jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer;

import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;

/**
 * A class that represents a registerer for combined revisions
 * 
 * @author k-hotta
 * 
 */
public class CombinedRevisionRegisterer extends
		AbstractElementRegisterer<DBCombinedRevisionInfo> {

	public CombinedRevisionRegisterer(DBConnectionManager dbManager,
			int maxBatchCount) {
		super(dbManager, maxBatchCount);
	}

}
