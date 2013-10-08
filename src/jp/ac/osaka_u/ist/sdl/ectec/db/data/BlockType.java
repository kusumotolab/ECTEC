package jp.ac.osaka_u.ist.sdl.ectec.db.data;

import jp.ac.osaka_u.ist.sdl.ectec.settings.AnalyzeGranularity;

/**
 * An enum that represents types of blocks
 * 
 * @author k-hotta
 * 
 */
public enum BlockType {

	CLASS("class", new AnalyzeGranularity[] { AnalyzeGranularity.ALL,
			AnalyzeGranularity.CLASS, AnalyzeGranularity.CLASS_METHOD }),

	METHOD("method", new AnalyzeGranularity[] { AnalyzeGranularity.ALL,
			AnalyzeGranularity.METHOD, AnalyzeGranularity.CLASS_METHOD }),

	CATCH("catch", new AnalyzeGranularity[] { AnalyzeGranularity.ALL }),

	DO("do", new AnalyzeGranularity[] { AnalyzeGranularity.ALL }),

	ELSE("else", new AnalyzeGranularity[] { AnalyzeGranularity.ALL }),

	ENHANCED_FOR("for", new AnalyzeGranularity[] { AnalyzeGranularity.ALL }),

	FINALLY("finally", new AnalyzeGranularity[] { AnalyzeGranularity.ALL }),

	FOR("for", new AnalyzeGranularity[] { AnalyzeGranularity.ALL }),

	IF("if", new AnalyzeGranularity[] { AnalyzeGranularity.ALL }),

	SWITCH("switch", new AnalyzeGranularity[] { AnalyzeGranularity.ALL }),

	SYNCHRONIZED("synchronized",
			new AnalyzeGranularity[] { AnalyzeGranularity.ALL }),

	TRY("try", new AnalyzeGranularity[] { AnalyzeGranularity.ALL }),

	WHILE("while", new AnalyzeGranularity[] { AnalyzeGranularity.ALL });

	/**
	 * the head string
	 */
	private final String head;

	private final AnalyzeGranularity[] correspondingGranularities;

	private BlockType(final String head,
			final AnalyzeGranularity[] correspondingGranularities) {
		this.head = head;
		this.correspondingGranularities = correspondingGranularities;
	}

	/**
	 * get the head string
	 * 
	 * @return
	 */
	public final String getHead() {
		return head;
	}

	public final boolean isInterested(final AnalyzeGranularity granularity) {
		for (final AnalyzeGranularity correspondingGranularity : correspondingGranularities) {
			if (granularity == correspondingGranularity) {
				return true;
			}
		}

		return false;
	}
}
