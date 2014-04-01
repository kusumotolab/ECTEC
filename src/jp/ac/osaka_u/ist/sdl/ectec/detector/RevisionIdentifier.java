package jp.ac.osaka_u.ist.sdl.ectec.detector;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CommitRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.RevisionRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.main.vcs.ITargetRevisionDetector;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A class to identify and register the target revisions
 * 
 * @author k-hotta
 * 
 */
public class RevisionIdentifier {

	/**
	 * the detector of target revisions
	 */
	private final ITargetRevisionDetector detector;

	/**
	 * the registerer of revisions
	 */
	private final RevisionRegisterer revisionRegisterer;

	/**
	 * the registerer of commits
	 */
	private final CommitRegisterer commitRegisterer;

	public RevisionIdentifier(final ITargetRevisionDetector detector,
			final RevisionRegisterer revisionRegisterer,
			final CommitRegisterer commitRegisterer) {
		this.detector = detector;
		this.revisionRegisterer = revisionRegisterer;
		this.commitRegisterer = commitRegisterer;
	}

	/**
	 * detect the target revisions and register them
	 * 
	 * @param language
	 * @param startRevisionIdentifier
	 * @param endRevisionIdentifier
	 * @throws Exception
	 */
	public void detectAndRegister(final Language language,
			final String startRevisionIdentifier,
			final String endRevisionIdentifier) throws Exception {
		detector.detect(language, startRevisionIdentifier,
				endRevisionIdentifier);
		final Map<Long, DBRevisionInfo> targetRevisions = detector
				.getTargetRevisions();

		MessagePrinter.stronglyPrintln("\t" + targetRevisions.size()
				+ " revisions are detected");

		MessagePrinter.stronglyPrintln();

		MessagePrinter.stronglyPrintln("registering target revisions ... ");
		revisionRegisterer.register(targetRevisions.values());
		MessagePrinter.stronglyPrintln("\tOK");

		MessagePrinter.stronglyPrintln();

		final Map<Long, DBCommitInfo> commits = detector.getCommits();

		MessagePrinter.stronglyPrintln("registering target commits ... ");
		commitRegisterer.register(commits.values());
		MessagePrinter.stronglyPrintln("\tOK");
	}
}
