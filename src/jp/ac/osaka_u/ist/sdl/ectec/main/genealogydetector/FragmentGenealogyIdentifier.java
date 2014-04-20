package jp.ac.osaka_u.ist.sdl.ectec.main.genealogydetector;

import java.util.Collection;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentGenealogyRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;

import org.apache.log4j.Logger;

/**
 * A class to detect and register genealogies of code fragments
 * 
 * @author k-hotta
 * 
 */
public class FragmentGenealogyIdentifier {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(FragmentGenealogyIdentifier.class.getName());

	/**
	 * target revisions
	 */
	private final Map<Long, DBCombinedRevisionInfo> targetCombinedRevisions;

	/**
	 * the number of threads
	 */
	private final int threadsCount;

	/**
	 * the retriever to get fragments from db
	 */
	private final CodeFragmentRetriever elementRetriever;

	/**
	 * the retriever to get fragment links from db
	 */
	private final CodeFragmentLinkRetriever linkRetriever;

	/**
	 * the registerer for detected genealogies
	 */
	private final CodeFragmentGenealogyRegisterer registerer;

	public FragmentGenealogyIdentifier(
			final Map<Long, DBCombinedRevisionInfo> targetCombinedRevisions,
			final int threadsCount,
			final CodeFragmentRetriever elementRetriever,
			final CodeFragmentLinkRetriever linkRetriever,
			final CodeFragmentGenealogyRegisterer registerer) {
		this.targetCombinedRevisions = targetCombinedRevisions;
		this.threadsCount = threadsCount;
		this.elementRetriever = elementRetriever;
		this.linkRetriever = linkRetriever;
		this.registerer = registerer;
	}

	public void detectAndRegister() throws Exception {
		final ElementChainDetector<DBCodeFragmentLinkInfo> chainDetector = new ElementChainDetector<DBCodeFragmentLinkInfo>(
				targetCombinedRevisions, linkRetriever, threadsCount);
		final Collection<ElementChain<DBCodeFragmentLinkInfo>> chains = chainDetector
				.detect();

		final FragmentChainFinalizer finalizer = new FragmentChainFinalizer(
				elementRetriever, linkRetriever);
		final Map<Long, DBCodeFragmentGenealogyInfo> genealogies = finalizer
				.finalize(chains);

		logger.info("the number of detected genealogies is "
				+ genealogies.size());
		logger.info("registering detected genealogies ... ");

		registerer.register(genealogies.values());
	}

}
