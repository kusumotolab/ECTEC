package jp.ac.osaka_u.ist.sdl.ectec.analyzer.concretizer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.FileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.ast.ASTCreator;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.IRepositoryManager;

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
	private final IRepositoryManager repositoryManager;

	public FileInfoConcretizer(final IRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	public FileInfo concretize(final DBFileInfo dbFile,
			final Map<Long, RevisionInfo> revisions) {
		try {
			final long id = dbFile.getId();
			final RevisionInfo startRevision = revisions.get(dbFile
					.getStartCombinedRevisionId());
			final RevisionInfo endRevision = revisions.get(dbFile
					.getCombinedEndRevisionId());

			if (startRevision == null || endRevision == null) {
				return null;
			}

			final String src = repositoryManager.getFileContents(
					startRevision.getIdentifier(), dbFile.getPath());

			final CompilationUnit root = ASTCreator.createAST(src);

			return new FileInfo(id, dbFile.getPath(), startRevision,
					endRevision, root);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public final Map<Long, FileInfo> concretizeAll(
			final Collection<DBFileInfo> dbFiles,
			final Map<Long, RevisionInfo> revisions) {
		final Map<Long, FileInfo> result = new TreeMap<Long, FileInfo>();

		for (final DBFileInfo dbFile : dbFiles) {
			final FileInfo file = concretize(dbFile, revisions);
			if (file != null) {
				result.put(file.getId(), file);
			}
		}

		return Collections.unmodifiableMap(result);
	}

	public final Map<Long, FileInfo> concretizeAll(
			final Map<Long, DBFileInfo> dbFiles,
			final Map<Long, RevisionInfo> revisions) {
		return concretizeAll(dbFiles.values(), revisions);
	}

}
