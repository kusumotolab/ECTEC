package jp.ac.osaka_u.ist.sdl.ectec.main.linker.similarity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBFileInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CombinedRevisionRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.FileRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.RevisionRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.util.StringUtils;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.AbstractRepositoryManager;
import jp.ac.osaka_u.ist.sdl.ectec.vcs.RepositoryManagerManager;

public class ContentBasedCRDSimilarityCalculator implements
		ICRDSimilarityCalculator {

	private RepositoryManagerManager repositoryManagerManager;

	private FileRetriever fileRetriever;

	private RevisionRetriever revisionRetriever;

	private CombinedRevisionRetriever combinedRevisionRetriever;

	public void setup(final RepositoryManagerManager repositoryManagerManager,
			final FileRetriever fileRetriever,
			final RevisionRetriever revisionRetriever,
			final CombinedRevisionRetriever combinedRevisionRetriever) {
		this.repositoryManagerManager = repositoryManagerManager;
		this.fileRetriever = fileRetriever;
		this.revisionRetriever = revisionRetriever;
		this.combinedRevisionRetriever = combinedRevisionRetriever;
	}

	@Override
	public double calcSimilarity(DBCrdInfo crd, DBCodeFragmentInfo fragment,
			DBCrdInfo anotherCrd, DBCodeFragmentInfo anotherFragment) {
		try {
			final Map<Long, DBFileInfo> files = fileRetriever
					.retrieveWithIds(fragment.getOwnerFileId(),
							anotherFragment.getOwnerFileId());

			final Map<Long, DBCombinedRevisionInfo> combinedRevisions = combinedRevisionRetriever
					.retrieveWithIds(fragment.getStartCombinedRevisionId(),
							anotherFragment.getStartCombinedRevisionId());

			final Set<Long> revisionIds = new HashSet<Long>();
			for (final Map.Entry<Long, DBCombinedRevisionInfo> entry : combinedRevisions
					.entrySet()) {
				revisionIds.addAll(entry.getValue().getOriginalRevisions());
			}

			final Map<Long, DBRevisionInfo> revisions = revisionRetriever
					.retrieveWithIds(revisionIds);

			final String fragmentContent = getFragmentContent(
					files.get(fragment.getOwnerFileId()),
					fragment,
					combinedRevisions.get(fragment.getStartCombinedRevisionId()),
					revisions);
			final String anotherFragmentContent = getFragmentContent(
					files.get(anotherFragment.getOwnerFileId()),
					anotherFragment, combinedRevisions.get(anotherFragment
							.getStartCombinedRevisionId()), revisions);

			return StringUtils.calcLebenshteinDistanceBasedSimilarity(
					fragmentContent, anotherFragmentContent);

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private final String getFragmentContent(final DBFileInfo file,
			final DBCodeFragmentInfo fragment,
			DBCombinedRevisionInfo combinedRevision,
			final Map<Long, DBRevisionInfo> revisions) throws Exception {
		final AbstractRepositoryManager repositoryManager = repositoryManagerManager
				.getRepositoryManager(file.getOwnerRepositoryId());

		DBRevisionInfo revision = null;
		for (final long revisionId : combinedRevision.getOriginalRevisions()) {
			final DBRevisionInfo tmpRevision = revisions.get(revisionId);
			if (tmpRevision.getRepositoryId() == file.getOwnerRepositoryId()) {
				revision = tmpRevision;
				break;
			}
		}

		if (revision == null) {
			return null;
		}

		final String src = repositoryManager.getFileContents(
				revision.getIdentifier(), file.getPath());

		return cutSrc(fragment, src);
	}

	private String cutSrc(final DBCodeFragmentInfo fragment, final String src)
			throws IOException {
		final StringBuilder builder = new StringBuilder();
		int lineCount = 1;

		final BufferedReader br = new BufferedReader(new StringReader(src));
		String line;

		while ((line = br.readLine()) != null) {
			if (lineCount < fragment.getStartLine()) {
				continue;
			}
			if (lineCount > fragment.getEndLine()) {
				break;
			}

			builder.append(line + "\n");
			lineCount++;
		}

		br.close();

		return builder.toString();
	}
}
