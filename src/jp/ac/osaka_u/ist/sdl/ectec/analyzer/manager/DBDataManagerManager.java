package jp.ac.osaka_u.ist.sdl.ectec.analyzer.manager;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;

/**
 * A class that manages managers for raw data retrieved from the db
 * 
 * @author k-hotta
 * 
 */
public class DBDataManagerManager {

	private final DBDataManager<DBRepositoryInfo> dbRepositoryManager;

	private final DBDataManager<DBRevisionInfo> dbRevisionManager;

	private final DBDataManager<DBFileInfo> dbFileManager;

	private final DBDataManager<DBCodeFragmentInfo> dbFragmentManager;

	private final DBDataManager<DBCloneSetInfo> dbCloneManager;

	private final DBDataManager<DBCodeFragmentLinkInfo> dbFragmentLinkManager;

	private final DBDataManager<DBCloneSetLinkInfo> dbCloneLinkManager;

	private final DBDataManager<DBCodeFragmentGenealogyInfo> dbFragmentGenealogyManager;

	private final DBDataManager<DBCloneGenealogyInfo> dbCloneGenealogyManager;

	private final DBDataManager<DBCrdInfo> dbCrdManager;

	public DBDataManagerManager() {
		this.dbRepositoryManager = new DBDataManager<DBRepositoryInfo>();
		this.dbRevisionManager = new DBDataManager<DBRevisionInfo>();
		this.dbFileManager = new DBDataManager<DBFileInfo>();
		this.dbFragmentManager = new DBDataManager<DBCodeFragmentInfo>();
		this.dbCloneManager = new DBDataManager<DBCloneSetInfo>();
		this.dbFragmentLinkManager = new DBDataManager<DBCodeFragmentLinkInfo>();
		this.dbCloneLinkManager = new DBDataManager<DBCloneSetLinkInfo>();
		this.dbFragmentGenealogyManager = new DBDataManager<DBCodeFragmentGenealogyInfo>();
		this.dbCloneGenealogyManager = new DBDataManager<DBCloneGenealogyInfo>();
		this.dbCrdManager = new DBDataManager<DBCrdInfo>();
	}

	public final DBDataManager<DBRepositoryInfo> getDbRepositoryManager() {
		return dbRepositoryManager;
	}

	public final DBDataManager<DBRevisionInfo> getDbRevisionManager() {
		return dbRevisionManager;
	}

	public final DBDataManager<DBFileInfo> getDbFileManager() {
		return dbFileManager;
	}

	public final DBDataManager<DBCodeFragmentInfo> getDbFragmentManager() {
		return dbFragmentManager;
	}

	public final DBDataManager<DBCloneSetInfo> getDbCloneManager() {
		return dbCloneManager;
	}

	public final DBDataManager<DBCodeFragmentLinkInfo> getDbFragmentLinkManager() {
		return dbFragmentLinkManager;
	}

	public final DBDataManager<DBCloneSetLinkInfo> getDbCloneLinkManager() {
		return dbCloneLinkManager;
	}

	public final DBDataManager<DBCodeFragmentGenealogyInfo> getDbFragmentGenealogyManager() {
		return dbFragmentGenealogyManager;
	}

	public final DBDataManager<DBCloneGenealogyInfo> getDbCloneGenealogyManager() {
		return dbCloneGenealogyManager;
	}

	public final DBDataManager<DBCrdInfo> getDbCrdManager() {
		return dbCrdManager;
	}

	public final void clear() {
		this.dbRevisionManager.clear();
		this.dbFileManager.clear();
		this.dbFragmentManager.clear();
		this.dbCloneManager.clear();
		this.dbFragmentLinkManager.clear();
		this.dbCloneLinkManager.clear();
		this.dbFragmentGenealogyManager.clear();
		this.dbCloneGenealogyManager.clear();
		this.dbCrdManager.clear();
	}

}
