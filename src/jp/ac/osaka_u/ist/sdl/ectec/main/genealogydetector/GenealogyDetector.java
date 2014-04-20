package jp.ac.osaka_u.ist.sdl.ectec.main.genealogydetector;

import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.DBConnectionManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;

import org.apache.log4j.Logger;

/**
 * A class that preforms the main process of genealogy detection
 * 
 * @author k-hotta
 * 
 */
public class GenealogyDetector {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(GenealogyDetector.class.getName());

	/**
	 * the settings
	 */
	private final GenealogyDetectorMainSettings settings;

	/**
	 * the manager of db
	 */
	private final DBConnectionManager dbManager;

	public GenealogyDetector(final GenealogyDetectorMainSettings settings,
			final DBConnectionManager dbManager) {
		this.settings = settings;
		this.dbManager = dbManager;
	}

	/**
	 * perform the main process
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception {
		logger.info("retrieving combined revisions ... ");
		final Map<Long, DBCombinedRevisionInfo> combinedRevisions = dbManager
				.getCombinedRevisionRetriever().retrieveAll();
		logger.info(combinedRevisions.size()
				+ " combined revisions have been retrieved");

		switch (settings.getMode()) {

		case FRAGMENT:
			logger.info("detecting fragment genealogies ... ");
			final FragmentGenealogyIdentifier fragmentGenealogyIdentifier = new FragmentGenealogyIdentifier(
					combinedRevisions, settings.getThreads(),
					dbManager.getFragmentRetriever(),
					dbManager.getFragmentLinkRetriever(),
					dbManager.getFragmentGenealogyRegisterer());
			fragmentGenealogyIdentifier.detectAndRegister();
			break;

		case CLONE:
			logger.info("detecting clone genealogies ... ");
			final CloneGenealogyIdentifier cloneGenealogyIdentifier = new CloneGenealogyIdentifier(
					combinedRevisions, settings.getThreads(),
					dbManager.getCloneRetriever(),
					dbManager.getCloneLinkRetriever(),
					dbManager.getCloneGenealogyRegisterer());
			cloneGenealogyIdentifier.detectAndRegister();
			break;

		default:
			break;
		}

		logger.info("complete");
	}
}
