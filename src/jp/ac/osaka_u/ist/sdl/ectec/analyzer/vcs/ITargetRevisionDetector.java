package jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

/**
 * An interface that represents a detector of target revisions
 * 
 * @author k-hotta
 * 
 */
public interface ITargetRevisionDetector {

	public List<RevisionInfo> detectTargetRevisions(final Language language,
			final String startRevisionIdentifier,
			final String endRevisionIdentifier) throws Exception;

}
