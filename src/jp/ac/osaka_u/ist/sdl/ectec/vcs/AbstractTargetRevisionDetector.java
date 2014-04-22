package jp.ac.osaka_u.ist.sdl.ectec.vcs;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

/**
 * A target revision detector for a SVN repository
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractTargetRevisionDetector<M extends AbstractRepositoryManager> {

	/**
	 * the repository manager
	 */
	protected final M manager;

	/**
	 * detected target revisions
	 */
	private final Map<Long, DBRevisionInfo> targetRevisions;

	/**
	 * detected commits
	 */
	private final Map<Long, DBCommitInfo> commits;

	public AbstractTargetRevisionDetector(final M manager) {
		this.manager = manager;
		this.targetRevisions = new TreeMap<Long, DBRevisionInfo>();
		this.commits = new TreeMap<Long, DBCommitInfo>();
	}

	public void detect(final Language language) throws Exception {
		final Map<String, Date> revisions = detectRevisionsAfterTargetCommits(language);

		final SortedSet<TemporaryRevision> temporaryRevisions = new TreeSet<TemporaryRevision>();
		for (final Map.Entry<String, Date> entry : revisions.entrySet()) {
			temporaryRevisions.add(new TemporaryRevision(entry.getKey(), entry
					.getValue()));
		}

		final long repositoryId = manager.getRepositoryId();

		DBRevisionInfo previousRevision = new DBRevisionInfo(-1, "INITIAL",
				repositoryId);
		for (final TemporaryRevision tmpRevision : temporaryRevisions) {
			final DBRevisionInfo newRevision = new DBRevisionInfo(
					tmpRevision.getIdentifer(), repositoryId);
			targetRevisions.put(newRevision.getId(), newRevision);

			final DBCommitInfo commit = new DBCommitInfo(repositoryId,
					previousRevision.getId(), newRevision.getId(),
					previousRevision.getIdentifier(),
					newRevision.getIdentifier(), tmpRevision.getDate());
			commits.put(commit.getId(), commit);

			previousRevision = newRevision;
		}

	}

	protected abstract Map<String, Date> detectRevisionsAfterTargetCommits(
			final Language language) throws Exception;

	public Map<Long, DBRevisionInfo> getTargetRevisions() {
		return Collections.unmodifiableMap(targetRevisions);
	}

	public Map<Long, DBCommitInfo> getCommits() {
		return Collections.unmodifiableMap(commits);
	}

	private class TemporaryRevision implements Comparable<TemporaryRevision> {

		private final String identifier;

		private final Date date;

		private TemporaryRevision(final String identifier, final Date date) {
			this.identifier = identifier;
			this.date = date;
		}

		private final String getIdentifer() {
			return identifier;
		}

		private final Date getDate() {
			return date;
		}

		@Override
		public int compareTo(TemporaryRevision another) {
			final int basedOnDate = this.date.compareTo(another.getDate());

			if (basedOnDate != 0) {
				return basedOnDate;
			}

			return this.identifier.compareTo(another.getIdentifer());
		}

	}

}
