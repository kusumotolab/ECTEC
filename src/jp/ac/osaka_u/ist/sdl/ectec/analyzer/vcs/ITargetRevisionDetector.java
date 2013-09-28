package jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

/**
 * An interface that represents a detector of target revisions
 * 
 * @author k-hotta
 * 
 */
public interface ITargetRevisionDetector {

	public Map<RevisionInfo, RevisionInfo> detectTargetRevisions(
			final Language language, final String startRevisionIdentifier,
			final String endRevisionIdentifier) throws Exception;

}
