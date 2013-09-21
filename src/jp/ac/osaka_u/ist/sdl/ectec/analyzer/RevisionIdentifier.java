package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.ITargetRevisionDetector;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
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
	private final RevisionRegisterer registerer;

	public RevisionIdentifier(final ITargetRevisionDetector detector,
			final RevisionRegisterer registerer) {
		this.detector = detector;
		this.registerer = registerer;
	}

	/**
	 * detect the target revisions and register them
	 * 
	 * @param language
	 * @param startRevisionIdentifier
	 * @param endRevisionIdentifier
	 * @return
	 * @throws Exception
	 */
	public List<RevisionInfo> detectAndRegister(final Language language,
			final String startRevisionIdentifier,
			final String endRevisionIdentifier) throws Exception {
		final List<RevisionInfo> targetRevisions = detector
				.detectTargetRevisions(language, startRevisionIdentifier,
						endRevisionIdentifier);
		
		MessagePrinter.stronglyPrintln("\t" + targetRevisions.size() + " revisions are detected");

		MessagePrinter.stronglyPrintln();
		
		MessagePrinter.stronglyPrintln("registering target revisions ... ");
		registerer.register(targetRevisions);
		MessagePrinter.stronglyPrintln("\tOK");
		
		return targetRevisions;
	}
}
