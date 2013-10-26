package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.FileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.manager.DBDataManager;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.manager.DBDataManagerManager;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.manager.DataManager;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.manager.DataManagerManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CRDRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneGenealogyRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.FileRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.RevisionRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.detector.vcs.IRepositoryManager;

/**
 * A class for concretizing elements
 * 
 * @author k-hotta
 * 
 */
public class Concretizer {

	/**
	 * the manager for data managers <br>
	 * all the concretized elements are stored into the corresponding manager
	 */
	private final DataManagerManager dataManagerManager;

	/**
	 * the manager for db data managers
	 */
	private final DBDataManagerManager dbDataManagerManager;

	/**
	 * the manager for db connection
	 */
	private final DBConnectionManager dbManager;

	/**
	 * the manager of repository
	 */
	private final IRepositoryManager repositoryManager;

	/**
	 * true if a code fragment corresponds to a block
	 */
	private final boolean isBlockMode;

	public Concretizer(final DataManagerManager dataManagerManager,
			final DBDataManagerManager dbDataManagerManager,
			final DBConnectionManager dbManager,
			final IRepositoryManager repositoryManager,
			final boolean isBlockMode) {
		this.dataManagerManager = dataManagerManager;
		this.dbDataManagerManager = dbDataManagerManager;
		this.dbManager = dbManager;
		this.repositoryManager = repositoryManager;
		this.isBlockMode = isBlockMode;
	}

	public CloneGenealogyInfo concretizeCloneGenealogy(final long genealogyId)
			throws NotConcretizedException {
		try {
			// check whether the target has been already concretized
			if (dataManagerManager.getCloneGenealogyManager().contains(
					genealogyId)) {
				return dataManagerManager.getCloneGenealogyManager()
						.getElement(genealogyId);
			}

			// get or retrieve db clone genealogy
			final DBCloneGenealogyInfo dbGenealogy = (dbDataManagerManager
					.getDbCloneGenealogyManager().contains(genealogyId)) ? dbDataManagerManager
					.getDbCloneGenealogyManager().getElement(genealogyId)
					: retrieveDbCloneGenealogy(genealogyId);

			// fail to retrieve
			if (dbGenealogy == null) {
				throw new NotConcretizedException(
						"cannot retrieve db genealogy " + genealogyId);
			}

			dbDataManagerManager.getDbCloneGenealogyManager().add(dbGenealogy);

			// retrieve necessary db elements
			// they are stored in the managers
			retrieveNecessaryElements(dbGenealogy);

			// concretize the genealogy and related elements
			// with storing them in the managers
			return concretizeAndRegister(dbGenealogy);

		} catch (Exception e) {
			// something is wrong!!
			e.printStackTrace();
			throw new NotConcretizedException(e);
		}
	}

	/**
	 * retrieve the db clone genealogy having the given id
	 * 
	 * @param genealogyId
	 * @return
	 * @throws Exception
	 */
	private DBCloneGenealogyInfo retrieveDbCloneGenealogy(long genealogyId)
			throws Exception {
		final CloneGenealogyRetriever retriever = dbManager
				.getCloneGenealogyRetriever();
		final Map<Long, DBCloneGenealogyInfo> result = retriever
				.retrieveWithIds(genealogyId);

		return result.get(genealogyId);
	}

	/**
	 * retrieve db elements
	 * 
	 * @param dbGenealogy
	 * @throws SQLException
	 */
	private void retrieveNecessaryElements(
			final DBCloneGenealogyInfo dbGenealogy) throws SQLException {
		// retrieve db clone links
		final List<Long> cloneLinkIds = dbGenealogy.getLinks();
		final Map<Long, DBCloneSetLinkInfo> dbCloneLinks = retrieveCloneSetLinks(cloneLinkIds);
		dbDataManagerManager.getDbCloneLinkManager().addAll(
				dbCloneLinks.values());

		// retrieve db clones
		final List<Long> cloneIds = dbGenealogy.getElements();
		final Map<Long, DBCloneSetInfo> dbClones = retrieveCloneSets(cloneIds);
		dbDataManagerManager.getDbCloneManager().addAll(dbClones.values());

		// retrieve code fragment links
		final Set<Long> fragmentLinkIds = new HashSet<Long>();
		for (final Map.Entry<Long, DBCloneSetLinkInfo> entry : dbCloneLinks
				.entrySet()) {
			final DBCloneSetLinkInfo cloneLink = entry.getValue();
			fragmentLinkIds.addAll(cloneLink.getCodeFragmentLinks());
		}
		final Map<Long, DBCodeFragmentLinkInfo> dbFragmentLinks = retrieveFragmentLinks(fragmentLinkIds);
		dbDataManagerManager.getDbFragmentLinkManager().addAll(
				dbFragmentLinks.values());

		// retrieve code fragments
		final Set<Long> fragmentIds = new HashSet<Long>();
		for (final Map.Entry<Long, DBCloneSetInfo> entry : dbClones.entrySet()) {
			final DBCloneSetInfo clone = entry.getValue();
			fragmentIds.addAll(clone.getElements());
		}
		final Map<Long, DBCodeFragmentInfo> dbFragments = retrieveFragments(fragmentIds);
		dbDataManagerManager.getDbFragmentManager()
				.addAll(dbFragments.values());

		// retrieve files
		final Set<Long> fileIds = new HashSet<Long>();
		for (final Map.Entry<Long, DBCodeFragmentInfo> entry : dbFragments
				.entrySet()) {
			final DBCodeFragmentInfo fragment = entry.getValue();
			fileIds.add(fragment.getOwnerFileId());
		}
		final Map<Long, DBFileInfo> dbFiles = retrieveFiles(fileIds);
		dbDataManagerManager.getDbFileManager().addAll(dbFiles.values());

		// retrieve crds
		final Set<Long> crdIds = new HashSet<Long>();
		for (final Map.Entry<Long, DBCodeFragmentInfo> entry : dbFragments
				.entrySet()) {
			final DBCodeFragmentInfo fragment = entry.getValue();
			crdIds.add(fragment.getCrdId());
		}
		final Map<Long, DBCrdInfo> dbCrds = retrieveCrds(crdIds);
		dbDataManagerManager.getDbCrdManager().addAll(dbCrds.values());

		// retrieve revisions
		final Set<Long> revisionIds = new HashSet<Long>();
		for (final Map.Entry<Long, DBCloneSetInfo> entry : dbClones.entrySet()) {
			final DBCloneSetInfo clone = entry.getValue();
			revisionIds.add(clone.getRevisionId());
		}
		for (final Map.Entry<Long, DBFileInfo> entry : dbFiles.entrySet()) {
			final DBFileInfo file = entry.getValue();
			revisionIds.add(file.getStartRevisionId());
			revisionIds.add(file.getEndRevisionId());
		}
		final Map<Long, DBRevisionInfo> dbRevisions = retrieveRevisions(revisionIds);
		dbDataManagerManager.getDbRevisionManager()
				.addAll(dbRevisions.values());
	}

	/**
	 * retrieve clone set links that are not stored in the manager
	 * 
	 * @param cloneLinkIds
	 * @throws SQLException
	 */
	private Map<Long, DBCloneSetLinkInfo> retrieveCloneSetLinks(
			final Collection<Long> cloneLinkIds) throws SQLException {
		final List<Long> cloneLinkIdsToBeRetrieved = new ArrayList<Long>();
		final DBDataManager<DBCloneSetLinkInfo> dbCloneLinkManager = dbDataManagerManager
				.getDbCloneLinkManager();
		for (final long cloneLinkId : cloneLinkIds) {
			if (!dbCloneLinkManager.contains(cloneLinkId)) {
				cloneLinkIdsToBeRetrieved.add(cloneLinkId);
			}
		}
		final CloneSetLinkRetriever retriever = dbManager
				.getCloneLinkRetriever();

		return retriever.retrieveWithIds(cloneLinkIdsToBeRetrieved);
	}

	/**
	 * retrieve clone sets that are not stored in the manager
	 * 
	 * @param cloneIds
	 * @throws SQLException
	 */
	private Map<Long, DBCloneSetInfo> retrieveCloneSets(
			final Collection<Long> cloneIds) throws SQLException {
		final List<Long> cloneIdsToBeRetrieved = new ArrayList<Long>();
		final DBDataManager<DBCloneSetInfo> dbCloneManager = dbDataManagerManager
				.getDbCloneManager();
		for (final long cloneId : cloneIds) {
			if (!dbCloneManager.contains(cloneId)) {
				cloneIdsToBeRetrieved.add(cloneId);
			}
		}
		final CloneSetRetriever retriever = dbManager.getCloneRetriever();

		return retriever.retrieveWithIds(cloneIdsToBeRetrieved);
	}

	/**
	 * retrieve code fragment links that are not stored in the manager
	 * 
	 * @param fragmentLinkIds
	 * @return
	 * @throws SQLException
	 */
	private Map<Long, DBCodeFragmentLinkInfo> retrieveFragmentLinks(
			final Collection<Long> fragmentLinkIds) throws SQLException {
		final List<Long> fragmentLinkIdsToBeRetrieved = new ArrayList<Long>();
		final DBDataManager<DBCodeFragmentLinkInfo> dbFragmentLinkManager = dbDataManagerManager
				.getDbFragmentLinkManager();
		for (final long fragmentLinkId : fragmentLinkIds) {
			if (!dbFragmentLinkManager.contains(fragmentLinkId)) {
				fragmentLinkIdsToBeRetrieved.add(fragmentLinkId);
			}
		}
		final CodeFragmentLinkRetriever retriever = dbManager
				.getFragmentLinkRetriever();

		return retriever.retrieveWithIds(fragmentLinkIdsToBeRetrieved);
	}

	/**
	 * retrieve code fragments that are not stored in the manager
	 * 
	 * @param fragmentIds
	 * @return
	 * @throws SQLException
	 */
	private Map<Long, DBCodeFragmentInfo> retrieveFragments(
			final Collection<Long> fragmentIds) throws SQLException {
		final List<Long> fragmentIdsToBeRetrieved = new ArrayList<Long>();
		final DBDataManager<DBCodeFragmentInfo> dbFragmentManager = dbDataManagerManager
				.getDbFragmentManager();
		for (final long fragmentId : fragmentIds) {
			if (!dbFragmentManager.contains(fragmentId)) {
				fragmentIdsToBeRetrieved.add(fragmentId);
			}
		}
		final CodeFragmentRetriever retriever = dbManager
				.getFragmentRetriever();

		return retriever.retrieveWithIds(fragmentIdsToBeRetrieved);
	}

	/**
	 * retrieve files that are not stored in the manager
	 * 
	 * @param fileIds
	 * @return
	 * @throws SQLException
	 */
	private Map<Long, DBFileInfo> retrieveFiles(final Collection<Long> fileIds)
			throws SQLException {
		final List<Long> fileIdsToBeRetrieved = new ArrayList<Long>();
		final DBDataManager<DBFileInfo> dbFileManager = dbDataManagerManager
				.getDbFileManager();
		for (final long fileId : fileIds) {
			if (!dbFileManager.contains(fileId)) {
				fileIdsToBeRetrieved.add(fileId);
			}
		}
		final FileRetriever retriever = dbManager.getFileRetriever();

		return retriever.retrieveWithIds(fileIdsToBeRetrieved);
	}

	/**
	 * retrieve crds that are not stored in the manager
	 * 
	 * @param crdIds
	 * @return
	 * @throws SQLException
	 */
	private Map<Long, DBCrdInfo> retrieveCrds(final Collection<Long> crdIds)
			throws SQLException {
		final Set<Long> crdIdsToBeRetrieved = new HashSet<Long>();
		final DBDataManager<DBCrdInfo> dbCrdManager = dbDataManagerManager
				.getDbCrdManager();

		for (final long crdId : crdIds) {
			if (!dbCrdManager.contains(crdId)) {
				crdIdsToBeRetrieved.add(crdId);
			}
		}
		final CRDRetriever retriever = dbManager.getCrdRetriever();
		final Map<Long, DBCrdInfo> retrievedCrds = retriever
				.retrieveWithIds(crdIdsToBeRetrieved);

		for (final DBCrdInfo crd : retrievedCrds.values()) {
			crdIdsToBeRetrieved.addAll(crd.getAncestors());
		}

		return retriever.retrieveWithIds(crdIdsToBeRetrieved);
	}

	/**
	 * retrieve revisions that are not stored in the manager
	 * 
	 * @param crdIds
	 * @return
	 * @throws SQLException
	 */
	private Map<Long, DBRevisionInfo> retrieveRevisions(
			final Collection<Long> revisionIds) throws SQLException {
		final List<Long> revisionIdsToBeRetrieved = new ArrayList<Long>();
		final DBDataManager<DBRevisionInfo> dbRevisionManager = dbDataManagerManager
				.getDbRevisionManager();
		for (final long revisionId : revisionIds) {
			if (!dbRevisionManager.contains(revisionId)) {
				revisionIdsToBeRetrieved.add(revisionId);
			}
		}
		final RevisionRetriever retriever = dbManager.getRevisionRetriever();

		return retriever.retrieveWithIds(revisionIdsToBeRetrieved);
	}

	/**
	 * perform concretization and register the results
	 * 
	 * @param dbGenealogy
	 * @return
	 */
	private CloneGenealogyInfo concretizeAndRegister(
			final DBCloneGenealogyInfo dbGenealogy) {
		final Map<Long, DBCloneSetLinkInfo> dbCloneLinks = getDbCloneLinks(dbGenealogy);
		final Map<Long, DBCloneSetInfo> dbClones = getDbClones(dbGenealogy);
		final Map<Long, DBCodeFragmentInfo> dbFragments = getDbFragments(dbClones);
		final Map<Long, DBCodeFragmentLinkInfo> dbFragmentLinks = getDbFragmentLinks(dbCloneLinks);
		final Map<Long, DBCrdInfo> dbCrds = getDbCrds(dbFragments);
		final Map<Long, DBFileInfo> dbFiles = getDbFiles(dbFragments);
		final Map<Long, DBRevisionInfo> dbRevisions = getDbRevisions(dbClones, dbFiles);

		// concretize revisions
		final Map<Long, RevisionInfo> concretizedRevisions = getConcretizedRevisions(dbRevisions);

		// concretize files
		final Map<Long, FileInfo> concretizedFiles = getConcretizedFiles(
				dbFiles, concretizedRevisions);

		// concretize crds
		final Map<Long, CRD> concretizedCrds = getConcretizedCrds(dbCrds);

		// concretize code fragments
		final Map<Long, CodeFragmentInfo> concretizedFragments = getConcretizedFragments(
				dbFragments, concretizedRevisions, concretizedFiles,
				concretizedCrds);

		// concretize fragment links
		final Map<Long, CodeFragmentLinkInfo> concretizedFragmentLinks = getConcretizedFragmentLinks(
				dbFragmentLinks, concretizedRevisions, concretizedFragments);

		// concretize clones
		final Map<Long, CloneSetInfo> concretizedClones = getConcretizedClones(
				dbClones, concretizedRevisions, concretizedFragments);

		// concretize clone links
		final Map<Long, CloneSetLinkInfo> concretizedCloneLinks = getConcretizedCloneLinks(
				dbCloneLinks, concretizedRevisions, concretizedFragmentLinks,
				concretizedClones);

		// concretize clone genealogy
		final CloneGenealogyInfoConcretizer cloneGenealogyConcretizer = new CloneGenealogyInfoConcretizer();
		final CloneGenealogyInfo concretizedGenealogy = cloneGenealogyConcretizer
				.concretize(dbGenealogy, concretizedRevisions,
						concretizedClones, concretizedCloneLinks);
		dataManagerManager.getCloneGenealogyManager().add(concretizedGenealogy);

		return concretizedGenealogy;
	}

	/**
	 * get db clone set links that correspond to the given genealogy
	 * 
	 * @param dbGenealogy
	 * @return
	 */
	private Map<Long, DBCloneSetLinkInfo> getDbCloneLinks(
			final DBCloneGenealogyInfo dbGenealogy) {
		final Map<Long, DBCloneSetLinkInfo> dbCloneLinks = new TreeMap<Long, DBCloneSetLinkInfo>();
		for (final long cloneLinkId : dbGenealogy.getLinks()) {
			dbCloneLinks.put(cloneLinkId, dbDataManagerManager
					.getDbCloneLinkManager().getElement(cloneLinkId));
		}
		return dbCloneLinks;
	}

	/**
	 * get db clones that correspond to the given genealogy
	 * 
	 * @param dbGenealogy
	 * @return
	 */
	private Map<Long, DBCloneSetInfo> getDbClones(
			final DBCloneGenealogyInfo dbGenealogy) {
		final Map<Long, DBCloneSetInfo> dbClones = new TreeMap<Long, DBCloneSetInfo>();
		for (final long cloneId : dbGenealogy.getElements()) {
			dbClones.put(cloneId, dbDataManagerManager.getDbCloneManager()
					.getElement(cloneId));
		}
		return dbClones;
	}

	/**
	 * get db fragments that correspond to one of the given clones
	 * 
	 * @param dbClones
	 * @return
	 */
	private Map<Long, DBCodeFragmentInfo> getDbFragments(
			final Map<Long, DBCloneSetInfo> dbClones) {
		final Map<Long, DBCodeFragmentInfo> dbFragments = new TreeMap<Long, DBCodeFragmentInfo>();
		for (final Map.Entry<Long, DBCloneSetInfo> entry : dbClones.entrySet()) {
			final List<Long> fragmentIds = entry.getValue().getElements();
			for (final long fragmentId : fragmentIds) {
				if (!dbFragments.containsKey(fragmentId)) {
					dbFragments.put(fragmentId, dbDataManagerManager
							.getDbFragmentManager().getElement(fragmentId));
				}
			}
		}
		return dbFragments;
	}

	/**
	 * get db fragment links that correspond to one of the given clone links
	 * 
	 * @param dbCloneLinks
	 * @return
	 */
	private Map<Long, DBCodeFragmentLinkInfo> getDbFragmentLinks(
			final Map<Long, DBCloneSetLinkInfo> dbCloneLinks) {
		final Map<Long, DBCodeFragmentLinkInfo> dbFragmentLinks = new TreeMap<Long, DBCodeFragmentLinkInfo>();
		for (final Map.Entry<Long, DBCloneSetLinkInfo> entry : dbCloneLinks
				.entrySet()) {
			for (final long fragmentLinkId : entry.getValue()
					.getCodeFragmentLinks()) {
				if (!dbFragmentLinks.containsKey(fragmentLinkId)) {
					dbFragmentLinks.put(fragmentLinkId,
							dbDataManagerManager.getDbFragmentLinkManager()
									.getElement(fragmentLinkId));
				}
			}
		}
		return dbFragmentLinks;
	}

	/**
	 * get db crds that correspond to one of the given code fragments
	 * 
	 * @param dbFragments
	 * @return
	 */
	private Map<Long, DBCrdInfo> getDbCrds(
			final Map<Long, DBCodeFragmentInfo> dbFragments) {
		final Map<Long, DBCrdInfo> dbCrds = new TreeMap<Long, DBCrdInfo>();
		for (final Map.Entry<Long, DBCodeFragmentInfo> entry : dbFragments
				.entrySet()) {
			final long crdId = entry.getValue().getCrdId();
			if (!dbCrds.containsKey(crdId)) {
				final DBCrdInfo crd = dbDataManagerManager.getDbCrdManager()
						.getElement(crdId);
				dbCrds.put(crdId, crd);

				for (final long ancestor : crd.getAncestors()) {
					if (!dbCrds.containsKey(ancestor)) {
						dbCrds.put(ancestor, dbDataManagerManager
								.getDbCrdManager().getElement(ancestor));
					}
				}
			}
		}
		return dbCrds;
	}

	/**
	 * get db files that correspond to one of the given code fragments
	 * 
	 * @param dbFragments
	 * @return
	 */
	private Map<Long, DBFileInfo> getDbFiles(
			final Map<Long, DBCodeFragmentInfo> dbFragments) {
		final Map<Long, DBFileInfo> dbFiles = new TreeMap<Long, DBFileInfo>();
		for (final Map.Entry<Long, DBCodeFragmentInfo> entry : dbFragments
				.entrySet()) {
			final long fileId = entry.getValue().getOwnerFileId();
			if (!dbFiles.containsKey(fileId)) {
				dbFiles.put(fileId, dbDataManagerManager.getDbFileManager()
						.getElement(fileId));
			}
		}
		return dbFiles;
	}

	/**
	 * get db revisions that correspond to one of the given clones
	 * 
	 * @param dbClones
	 * @return
	 */
	private Map<Long, DBRevisionInfo> getDbRevisions(
			final Map<Long, DBCloneSetInfo> dbClones,
			final Map<Long, DBFileInfo> dbFiles) {
		final Map<Long, DBRevisionInfo> dbRevisions = new TreeMap<Long, DBRevisionInfo>();
		for (final Map.Entry<Long, DBCloneSetInfo> entry : dbClones.entrySet()) {
			final long revisionId = entry.getValue().getRevisionId();
			if (!dbRevisions.containsKey(revisionId)) {
				dbRevisions.put(revisionId, dbDataManagerManager
						.getDbRevisionManager().getElement(revisionId));
			}
		}
		for (final Map.Entry<Long, DBFileInfo> entry : dbFiles.entrySet()) {
			final long startRevisionId = entry.getValue().getStartRevisionId();
			final long endRevisionId = entry.getValue().getEndRevisionId();
			if (!dbRevisions.containsKey(startRevisionId)) {
				dbRevisions.put(startRevisionId, dbDataManagerManager
						.getDbRevisionManager().getElement(startRevisionId));
			}
			if (!dbRevisions.containsKey(endRevisionId)) {
				dbRevisions.put(endRevisionId, dbDataManagerManager
						.getDbRevisionManager().getElement(endRevisionId));
			}
		}
		return dbRevisions;
	}

	/**
	 * perform concretization for the given revisions
	 * 
	 * @param dbRevisions
	 * @return
	 */
	private Map<Long, RevisionInfo> getConcretizedRevisions(
			final Map<Long, DBRevisionInfo> dbRevisions) {
		final DataManager<RevisionInfo> revisionManager = dataManagerManager
				.getRevisionManager();
		final RevisionInfoConcretizer revisionConcretizer = new RevisionInfoConcretizer();
		final Map<Long, RevisionInfo> concretizedRevisions = new TreeMap<Long, RevisionInfo>();
		for (final Map.Entry<Long, DBRevisionInfo> entry : dbRevisions
				.entrySet()) {
			final long revisionId = entry.getKey();
			if (revisionManager.contains(revisionId)) {
				concretizedRevisions.put(revisionId,
						revisionManager.getElement(revisionId));
			} else {
				final RevisionInfo concretizedRevision = revisionConcretizer
						.concretize(entry.getValue());
				revisionManager.add(concretizedRevision);
				concretizedRevisions.put(entry.getKey(), concretizedRevision);
			}
		}
		return concretizedRevisions;
	}

	/**
	 * perform concretization for the given files
	 * 
	 * @param dbFiles
	 * @param concretizedRevisions
	 * @return
	 */
	private Map<Long, FileInfo> getConcretizedFiles(
			final Map<Long, DBFileInfo> dbFiles,
			final Map<Long, RevisionInfo> concretizedRevisions) {
		final DataManager<FileInfo> fileManager = dataManagerManager
				.getFileManager();
		final Map<Long, FileInfo> concretizedFiles = new TreeMap<Long, FileInfo>();

		final FileInfoConcretizer fileConcretizer = new FileInfoConcretizer(
				repositoryManager);
		for (final Map.Entry<Long, DBFileInfo> entry : dbFiles.entrySet()) {
			final long fileId = entry.getKey();
			if (fileManager.contains(fileId)) {
				concretizedFiles.put(fileId, fileManager.getElement(fileId));
			} else {
				final FileInfo concretizedFile = fileConcretizer.concretize(
						entry.getValue(), concretizedRevisions);
				concretizedFiles.put(fileId, concretizedFile);
				fileManager.add(concretizedFile);
			}
		}
		return concretizedFiles;
	}

	/**
	 * perform concretization for the given crds
	 * 
	 * @param dbCrds
	 * @return
	 */
	private Map<Long, CRD> getConcretizedCrds(final Map<Long, DBCrdInfo> dbCrds) {
		final DataManager<CRD> crdManager = dataManagerManager.getCrdManager();
		final CRDConcretizer crdConcretizer = new CRDConcretizer();
		final Map<Long, CRD> concretizedCrds = new TreeMap<Long, CRD>();
		for (final Map.Entry<Long, DBCrdInfo> entry : dbCrds.entrySet()) {
			final long crdId = entry.getKey();
			if (crdManager.contains(crdId)) {
				concretizedCrds.put(crdId, crdManager.getElement(crdId));
			} else {
				final Map<Long, CRD> alreadyConcretized = new TreeMap<Long, CRD>();
				alreadyConcretized.putAll(crdManager.getElements());
				final Map<Long, CRD> concretized = crdConcretizer
						.concretizeAll(entry.getValue(), dbCrds,
								alreadyConcretized);
				concretizedCrds.putAll(concretized);
				crdManager.addAll(concretized.values());
			}
		}
		return concretizedCrds;
	}

	/**
	 * perform concretization for the given fragments
	 * 
	 * @param dbFragments
	 * @param concretizedRevisions
	 * @param concretizedFiles
	 * @param concretizedCrds
	 * @return
	 */
	private Map<Long, CodeFragmentInfo> getConcretizedFragments(
			final Map<Long, DBCodeFragmentInfo> dbFragments,
			final Map<Long, RevisionInfo> concretizedRevisions,
			final Map<Long, FileInfo> concretizedFiles,
			final Map<Long, CRD> concretizedCrds) {
		final DataManager<CodeFragmentInfo> fragmentManager = dataManagerManager
				.getFragmentManager();

		final Map<Long, CodeFragmentInfo> concretizedFragments = new TreeMap<Long, CodeFragmentInfo>();
		if (isBlockMode) {
			final BlockInfoConcretizer blockConcretizer = new BlockInfoConcretizer();

			for (final Map.Entry<Long, DBCodeFragmentInfo> entry : dbFragments
					.entrySet()) {
				final long fragmentId = entry.getKey();
				final DBCodeFragmentInfo dbFragment = entry.getValue();

				if (fragmentManager.contains(fragmentId)) {
					concretizedFragments.put(fragmentId,
							fragmentManager.getElement(fragmentId));
				} else {
					final CodeFragmentInfo concretizedFragment = blockConcretizer
							.concretize(dbFragment, concretizedRevisions,
									concretizedFiles, concretizedCrds);
					concretizedFragments.put(fragmentId, concretizedFragment);
					fragmentManager.add(concretizedFragment);
				}
			}
		} else {
			final CodeFragmentInfoConcretizer fragmentConcretizer = new CodeFragmentInfoConcretizer();

			for (final Map.Entry<Long, DBCodeFragmentInfo> entry : dbFragments
					.entrySet()) {
				final long fragmentId = entry.getKey();
				final DBCodeFragmentInfo dbFragment = entry.getValue();

				if (fragmentManager.contains(fragmentId)) {
					concretizedFragments.put(fragmentId,
							fragmentManager.getElement(fragmentId));
				} else {
					final CodeFragmentInfo concretizedFragment = fragmentConcretizer
							.concretize(dbFragment, concretizedRevisions,
									concretizedFiles, concretizedCrds);
					concretizedFragments.put(fragmentId, concretizedFragment);
					fragmentManager.add(concretizedFragment);
				}
			}
		}
		return concretizedFragments;
	}

	/**
	 * perform concretization for the given fragment links
	 * 
	 * @param dbFragmentLinks
	 * @param concretizedRevisions
	 * @param concretizedFragments
	 * @return
	 */
	private Map<Long, CodeFragmentLinkInfo> getConcretizedFragmentLinks(
			final Map<Long, DBCodeFragmentLinkInfo> dbFragmentLinks,
			final Map<Long, RevisionInfo> concretizedRevisions,
			final Map<Long, CodeFragmentInfo> concretizedFragments) {
		final DataManager<CodeFragmentLinkInfo> fragmentLinkManager = dataManagerManager
				.getFragmentLinkManager();
		final CodeFragmentLinkConcretizer fragmentLinkConcretizer = new CodeFragmentLinkConcretizer();
		final Map<Long, CodeFragmentLinkInfo> concretizedFragmentLinks = new TreeMap<Long, CodeFragmentLinkInfo>();

		for (final Map.Entry<Long, DBCodeFragmentLinkInfo> entry : dbFragmentLinks
				.entrySet()) {
			final long fragmentLinkId = entry.getKey();
			final DBCodeFragmentLinkInfo dbFragmentLink = entry.getValue();

			if (fragmentLinkManager.contains(fragmentLinkId)) {
				concretizedFragmentLinks.put(fragmentLinkId,
						fragmentLinkManager.getElement(fragmentLinkId));
			} else {
				final CodeFragmentLinkInfo concretizedFragmentLink = fragmentLinkConcretizer
						.concretize(dbFragmentLink, concretizedRevisions,
								concretizedFragments);
				concretizedFragmentLinks.put(fragmentLinkId,
						concretizedFragmentLink);
				fragmentLinkManager.add(concretizedFragmentLink);
			}
		}
		return concretizedFragmentLinks;
	}

	/**
	 * perform concretization for the given clones
	 * 
	 * @param dbClones
	 * @param concretizedRevisions
	 * @param concretizedFragments
	 * @return
	 */
	private Map<Long, CloneSetInfo> getConcretizedClones(
			final Map<Long, DBCloneSetInfo> dbClones,
			final Map<Long, RevisionInfo> concretizedRevisions,
			final Map<Long, CodeFragmentInfo> concretizedFragments) {
		final DataManager<CloneSetInfo> cloneManager = dataManagerManager
				.getCloneManager();
		final CloneSetInfoConcretizer cloneConcretizer = new CloneSetInfoConcretizer();

		final Map<Long, CloneSetInfo> concretizedClones = new TreeMap<Long, CloneSetInfo>();

		for (final Map.Entry<Long, DBCloneSetInfo> entry : dbClones.entrySet()) {
			final long cloneId = entry.getKey();
			final DBCloneSetInfo dbClone = entry.getValue();

			if (cloneManager.contains(cloneId)) {
				concretizedClones
						.put(cloneId, cloneManager.getElement(cloneId));
			} else {
				final CloneSetInfo concretizedClone = cloneConcretizer
						.concretize(dbClone, concretizedRevisions,
								concretizedFragments);
				concretizedClones.put(cloneId, concretizedClone);
				cloneManager.add(concretizedClone);
			}
		}
		return concretizedClones;
	}

	/**
	 * perform concretization for the given clone links
	 * 
	 * @param dbCloneLinks
	 * @param concretizedRevisions
	 * @param concretizedFragmentLinks
	 * @param concretizedClones
	 * @return
	 */
	private Map<Long, CloneSetLinkInfo> getConcretizedCloneLinks(
			final Map<Long, DBCloneSetLinkInfo> dbCloneLinks,
			final Map<Long, RevisionInfo> concretizedRevisions,
			final Map<Long, CodeFragmentLinkInfo> concretizedFragmentLinks,
			final Map<Long, CloneSetInfo> concretizedClones) {
		final DataManager<CloneSetLinkInfo> cloneLinkManager = dataManagerManager
				.getCloneLinkManager();
		final CloneSetLinkInfoConcretizer cloneLinkConcretizer = new CloneSetLinkInfoConcretizer();
		final Map<Long, CloneSetLinkInfo> concretizedCloneLinks = new TreeMap<Long, CloneSetLinkInfo>();

		for (final Map.Entry<Long, DBCloneSetLinkInfo> entry : dbCloneLinks
				.entrySet()) {
			final long cloneLinkId = entry.getKey();
			final DBCloneSetLinkInfo dbCloneLink = entry.getValue();

			if (cloneLinkManager.contains(cloneLinkId)) {
				concretizedCloneLinks.put(cloneLinkId,
						cloneLinkManager.getElement(cloneLinkId));
			} else {
				final CloneSetLinkInfo concretizedCloneLink = cloneLinkConcretizer
						.concretize(dbCloneLink, concretizedRevisions,
								concretizedClones, concretizedFragmentLinks);
				concretizedCloneLinks.put(cloneLinkId, concretizedCloneLink);
				cloneLinkManager.add(concretizedCloneLink);
			}
		}
		return concretizedCloneLinks;
	}

}
