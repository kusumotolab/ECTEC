package jp.ac.osaka_u.ist.sdl.ectec.analyzer.manager;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.FileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RevisionInfo;

/**
 * A class that manages data managers
 * 
 * @author k-hotta
 * 
 */
public class DataManagerManager {
	
	private final DataManager<RepositoryInfo> repositoryManager;

	private final DataManager<RevisionInfo> revisionManager;

	private final DataManager<FileInfo> fileManager;

	private final DataManager<CodeFragmentInfo> fragmentManager;

	private final DataManager<CloneSetInfo> cloneManager;

	private final DataManager<CodeFragmentLinkInfo> fragmentLinkManager;

	private final DataManager<CloneSetLinkInfo> cloneLinkManager;

	private final DataManager<CodeFragmentGenealogyInfo> fragmentGenealogyManager;

	private final DataManager<CloneGenealogyInfo> cloneGenealogyManager;

	private final DataManager<CRD> crdManager;

	public DataManagerManager() {
		this.repositoryManager = new DataManager<RepositoryInfo>();
		this.revisionManager = new DataManager<RevisionInfo>();
		this.fileManager = new DataManager<FileInfo>();
		this.fragmentManager = new DataManager<CodeFragmentInfo>();
		this.cloneManager = new DataManager<CloneSetInfo>();
		this.fragmentLinkManager = new DataManager<CodeFragmentLinkInfo>();
		this.cloneLinkManager = new DataManager<CloneSetLinkInfo>();
		this.fragmentGenealogyManager = new DataManager<CodeFragmentGenealogyInfo>();
		this.cloneGenealogyManager = new DataManager<CloneGenealogyInfo>();
		this.crdManager = new DataManager<CRD>();
	}

	public final DataManager<RepositoryInfo> getRepositoryManager() {
		return repositoryManager;
	}
	
	public final DataManager<RevisionInfo> getRevisionManager() {
		return revisionManager;
	}

	public final DataManager<FileInfo> getFileManager() {
		return fileManager;
	}

	public final DataManager<CodeFragmentInfo> getFragmentManager() {
		return fragmentManager;
	}

	public final DataManager<CloneSetInfo> getCloneManager() {
		return cloneManager;
	}

	public final DataManager<CodeFragmentLinkInfo> getFragmentLinkManager() {
		return fragmentLinkManager;
	}

	public final DataManager<CloneSetLinkInfo> getCloneLinkManager() {
		return cloneLinkManager;
	}

	public final DataManager<CodeFragmentGenealogyInfo> getFragmentGenealogyManager() {
		return fragmentGenealogyManager;
	}

	public final DataManager<CloneGenealogyInfo> getCloneGenealogyManager() {
		return cloneGenealogyManager;
	}

	public final DataManager<CRD> getCrdManager() {
		return crdManager;
	}

	public final void clear() {
		this.revisionManager.clear();
		this.fileManager.clear();
		this.fragmentManager.clear();
		this.cloneManager.clear();
		this.fragmentLinkManager.clear();
		this.cloneLinkManager.clear();
		this.fragmentGenealogyManager.clear();
		this.cloneGenealogyManager.clear();
		this.crdManager.clear();
	}

}
