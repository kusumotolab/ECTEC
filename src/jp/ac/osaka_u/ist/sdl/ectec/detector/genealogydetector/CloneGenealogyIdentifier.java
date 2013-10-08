package jp.ac.osaka_u.ist.sdl.ectec.detector.genealogydetector;

import java.util.Collection;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CloneGenealogyRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CloneSetRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A class to detect and register genealogies of clone sets
 * 
 * @author k-hotta
 * 
 */
public class CloneGenealogyIdentifier {

	/**
	 * target revisions
	 */
	private final Map<Long, DBRevisionInfo> targetRevisions;

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

	/**
	 * the id of the latest revision
	 */
	private final long lastRevisionId;

	public CloneGenealogyIdentifier(
			final Map<Long, DBRevisionInfo> targetRevisions,
			final int threadsCount, final CloneSetRetriever elementRetriever,
			final CloneSetLinkRetriever elementLinkRetriever,
			final CloneGenealogyRegisterer registerer, final long lastRevisionId) {
		this.targetRevisions = targetRevisions;
		this.threadsCount = threadsCount;
		this.elementRetriever = elementRetriever;
		this.elementLinkRetriever = elementLinkRetriever;
		this.registerer = registerer;
		this.lastRevisionId = lastRevisionId;
	}

	public void detectAndRegister() throws Exception {
		final ElementChainDetector<DBCloneSetLinkInfo> chainDetector = new ElementChainDetector<DBCloneSetLinkInfo>(
				targetRevisions, elementLinkRetriever, threadsCount);
		final Collection<ElementChain<DBCloneSetLinkInfo>> chains = chainDetector
				.detect();

		final CloneChainFinalizer finalizer = new CloneChainFinalizer(
				elementRetriever, elementLinkRetriever, lastRevisionId);
		final Map<Long, DBCloneGenealogyInfo> genealogies = finalizer
				.finalize(chains);

		MessagePrinter.println();
		MessagePrinter.println("the number of detected genealogies is "
				+ genealogies.size());
		MessagePrinter.println();
		MessagePrinter.println("registering detected genealogies ... ");

		registerer.register(genealogies.values());

		MessagePrinter.println("\tOK");
	}

}
