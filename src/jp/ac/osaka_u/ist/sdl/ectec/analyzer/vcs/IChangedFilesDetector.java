package jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs;

import java.util.Map;

/**
 * An interface for detecting added/deleted/changed files
 * 
 * @author k-hotta
 * 
 */
public interface IChangedFilesDetector {

	/**
	 * detects added/deleted/changed files
	 * 
	 * @param revisionIdentifier
	 * @return
	 * @throws Exception
	 */
	public Map<String, Character> detectChangedFiles(
			final String revisionIdentifier) throws Exception;

}
