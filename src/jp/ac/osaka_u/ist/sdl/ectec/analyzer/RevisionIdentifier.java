package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.ITargetRevisionDetector;
import jp.ac.osaka_u.ist.sdl.ectec.data.Commit;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CommitRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.RevisionRegisterer;
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
		final Map<Long, RevisionInfo> targetRevisions = detector
				.getTargetRevisions();

		MessagePrinter.stronglyPrintln("\t" + targetRevisions.size()
				+ " revisions are detected");

		MessagePrinter.stronglyPrintln();

		MessagePrinter.stronglyPrintln("registering target revisions ... ");
		revisionRegisterer.register(targetRevisions.values());
		MessagePrinter.stronglyPrintln("\tOK");

		MessagePrinter.stronglyPrintln();

		final Map<Long, Commit> commits = detector.getCommits();

		MessagePrinter.stronglyPrintln("registering target commits ... ");
		commitRegisterer.register(commits.values());
		MessagePrinter.stronglyPrintln("\tOK");
	}
}
