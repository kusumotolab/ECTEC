package jp.ac.osaka_u.ist.sdl.ectec.analyzer.genealogydetector;

import java.util.Collection;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CodeFragmentGenealogyRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CodeFragmentLinkRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.data.retriever.CodeFragmentRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A class to detect and register genealogies of code fragments
 * 
 * @author k-hotta
 * 
 */
public class FragmentGenealogyIdentifier {

	/**
	 * target revisions
	 */
	private final Map<Long, RevisionInfo> targetRevisions;

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
			final Map<Long, RevisionInfo> targetRevisions,
			final int threadsCount,
			final CodeFragmentRetriever elementRetriever,
			final CodeFragmentLinkRetriever linkRetriever,
			final CodeFragmentGenealogyRegisterer registerer) {
		this.targetRevisions = targetRevisions;
		this.threadsCount = threadsCount;
		this.elementRetriever = elementRetriever;
		this.linkRetriever = linkRetriever;
		this.registerer = registerer;
	}

	public void detectAndRegister() throws Exception {
		final ElementChainDetector<CodeFragmentLinkInfo> chainDetector = new ElementChainDetector<CodeFragmentLinkInfo>(
				targetRevisions, linkRetriever, threadsCount);
		final Collection<ElementChain<CodeFragmentLinkInfo>> chains = chainDetector
				.detect();

		final FragmentChainFinalizer finalizer = new FragmentChainFinalizer(
				elementRetriever, linkRetriever);
		final Map<Long, CodeFragmentGenealogyInfo> genealogies = finalizer
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
