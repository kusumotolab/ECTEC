package jp.ac.osaka_u.ist.sdl.ectec.main.genealogydetector;

import java.util.Collection;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CloneGenealogyRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetRetriever;

import org.apache.log4j.Logger;

/**
 * A class to detect and register genealogies of clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneGenealogyIdentifier {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CloneGenealogyIdentifier.class.getName());

	/**
	 * target revisions
	 */
	private final Map<Long, DBCombinedRevisionInfo> targetCombinedRevisions;

	/**
	 * the number of threads
	 */
	private final int threadsCount;

	/**
	 * the retriever to get clone sets from db
	 */
	private final CloneSetRetriever elementRetriever;

	/**
	 * the retriever to get clone set links from db
	 */
	private final CloneSetLinkRetriever elementLinkRetriever;

	/**
	 * the registerer to get clone genealogies into db
	 */
	private final CloneGenealogyRegisterer registerer;

	public CloneGenealogyIdentifier(
			final Map<Long, DBCombinedRevisionInfo> targetCombinedRevisions,
			final int threadsCount, final CloneSetRetriever elementRetriever,
			final CloneSetLinkRetriever elementLinkRetriever,
			final CloneGenealogyRegisterer registerer) {
		this.targetCombinedRevisions = targetCombinedRevisions;
		this.threadsCount = threadsCount;
		this.elementRetriever = elementRetriever;
		this.elementLinkRetriever = elementLinkRetriever;
		this.registerer = registerer;
	}

	public void detectAndRegister() throws Exception {
		final ElementChainDetector<DBCloneSetLinkInfo> chainDetector = new ElementChainDetector<DBCloneSetLinkInfo>(
				targetCombinedRevisions, elementLinkRetriever, threadsCount);
		final Collection<ElementChain<DBCloneSetLinkInfo>> chains = chainDetector
				.detect();

		final CloneChainFinalizer finalizer = new CloneChainFinalizer(
				elementRetriever, elementLinkRetriever);
		final Map<Long, DBCloneGenealogyInfo> genealogies = finalizer
				.finalize(chains);

		logger.info("the number of detected genealogies is "
				+ genealogies.size());
		logger.info("registering detected genealogies ... ");

		registerer.register(genealogies.values());
	}

}
