package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.FileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.ast.ASTCreator;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.RepositoryManagerManager;

import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * A class for concretizing files
 * 
 * @author k-hotta
 * 
 */
public final class FileInfoConcretizer {

	/**
	 * the repository manager
	 */
	private final RepositoryManagerManager repositoryManagerManager;

	public FileInfoConcretizer(
			final RepositoryManagerManager repositoryManagerManager) {
		this.repositoryManagerManager = repositoryManagerManager;
	}

	public FileInfo concretize(final DBFileInfo dbFile,
			final Map<Long, RepositoryInfo> repositories,
			final Map<Long, CombinedRevisionInfo> combinedRevisions) {
		try {
			final long id = dbFile.getId();
			final RepositoryInfo ownerRepository = repositories.get(dbFile
					.getOwnerRepositoryId());
			final CombinedRevisionInfo startCombinedRevision = combinedRevisions
					.get(dbFile.getStartCombinedRevisionId());
			final CombinedRevisionInfo endCombinedRevision = combinedRevisions
					.get(dbFile.getCombinedEndRevisionId());
			final RevisionInfo startOriginalRevision = startCombinedRevision
					.getOriginalRevision(ownerRepository);

			if (ownerRepository == null || startCombinedRevision == null
					|| endCombinedRevision == null
					|| startOriginalRevision == null) {
				return null;
			}

			final AbstractRepositoryManager repositoryManager = repositoryManagerManager
					.getRepositoryManager(dbFile.getOwnerRepositoryId());

			final String src = repositoryManager.getFileContents(
					startOriginalRevision.getIdentifier(), dbFile.getPath());

			final CompilationUnit root = ASTCreator.createAST(src);

			return new FileInfo(id, dbFile.getPath(), ownerRepository,
					startCombinedRevision, endCombinedRevision, root);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public final Map<Long, FileInfo> concretizeAll(
			final Collection<DBFileInfo> dbFiles,
			final Map<Long, RepositoryInfo> repositories,
			final Map<Long, CombinedRevisionInfo> combinedRevisions) {
		final Map<Long, FileInfo> result = new TreeMap<Long, FileInfo>();

		for (final DBFileInfo dbFile : dbFiles) {
			final FileInfo file = concretize(dbFile, repositories,
					combinedRevisions);
			if (file != null) {
				result.put(file.getId(), file);
			}
		}

		return Collections.unmodifiableMap(result);
	}

	public final Map<Long, FileInfo> concretizeAll(
			final Map<Long, DBFileInfo> dbFiles,
			final Map<Long, RepositoryInfo> repositories,
			final Map<Long, CombinedRevisionInfo> combinedRevisions) {
		return concretizeAll(dbFiles.values(), repositories, combinedRevisions);
	}

}
