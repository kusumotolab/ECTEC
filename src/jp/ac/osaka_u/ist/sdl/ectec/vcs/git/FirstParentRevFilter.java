package jp.ac.osaka_u.ist.sdl.ectec.vcs.git;

import java.io.IOException;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevFlag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

public class FirstParentRevFilter extends RevFilter {
	private final RevWalk rw;
	private final RevFlag firstParent;
	private final RevFlag laterParent;

	/**
	 * Create a new filter.
	 * <p>
	 * As a side effect, allocates two flags on the walk. These flags may be
	 * disposed with {@link #dispose()}.
	 *
	 * @param rw
	 *            RevWalk to filter; must have at least 2 flags free.
	 */
	public FirstParentRevFilter(RevWalk rw) {
		this.rw = rw;
		firstParent = rw.newFlag("FIRST_PARENT"); //$NON-NLS-1$
		laterParent = rw.newFlag("LATER_PARENT"); //$NON-NLS-1$
	}

	@Override
	public boolean include(RevWalk walker, RevCommit cmit)
			throws StopWalkException, MissingObjectException,
			IncorrectObjectTypeException, IOException {
		boolean first = cmit.has(firstParent);
		boolean later = cmit.has(laterParent);
		// Include any commit found along a first-parent path, as well as any commit
		// not found along either a first- or later-parent path, which must be a
		// start commit.
		boolean include = first || (!first && !later);
		for (int i = 0; i < cmit.getParentCount(); i++) {
			RevCommit p = cmit.getParent(i);
			if (i == 0 && include) {
				p.add(firstParent);
			} else {
				p.add(laterParent);
			}
		}
		return include;
	}

	@Override
	public RevFilter clone() {
		return this;
	}

	@Override
	public boolean requiresCommitBody() {
		return false;
	}

	@Override
	public String toString() {
		return "FIRST_PARENT"; //$NON-NLS-1$
	}

	/** Dispose flags allocated by this instance. */
	public void dispose() {
		rw.disposeFlag(firstParent);
		rw.disposeFlag(laterParent);
	}
}
