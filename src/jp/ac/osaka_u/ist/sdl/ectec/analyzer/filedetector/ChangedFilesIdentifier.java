package jp.ac.osaka_u.ist.sdl.ectec.analyzer.filedetector;

import java.util.Collection;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.IChangedFilesDetector;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.FileRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

/**
 * A class to detect and register changed files
 * 
 * @author k-hotta
 * 
 */
public class ChangedFilesIdentifier {

	/**
	 * the changed files detector
	 */
	private final IChangedFilesDetector detector;

	/**
	 * the registerer for files
	 */
	private final FileRegisterer registerer;

	/**
	 * the target language
	 */
	private final Language language;

	/**
	 * the constructor
	 * 
	 * @param detector
	 * @param registerer
	 */
	public ChangedFilesIdentifier(final IChangedFilesDetector detector,
			final FileRegisterer registerer, final Language language) {
		this.detector = detector;
		this.registerer = registerer;
		this.language = language;
	}

	/**
	 * detect files that exist in the specified revisions and register them into
	 * the db
	 * 
	 * @param targetRevisions
	 */
	public void detectAndRegister(final Collection<RevisionInfo> targetRevisions) {
		
	}

}
