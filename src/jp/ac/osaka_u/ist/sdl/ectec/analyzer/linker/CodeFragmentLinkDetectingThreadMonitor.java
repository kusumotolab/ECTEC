package jp.ac.osaka_u.ist.sdl.ectec.analyzer.linker;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.RevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CodeFragmentLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A monitor class for code clone link threads
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkDetectingThreadMonitor {

	/**
	 * a map having detected links
	 */
	private final ConcurrentMap<Long, CodeFragmentLinkInfo> detectedLinks;

	/**
	 * the registerer for links of code fragments
	 */
	private final CodeFragmentLinkRegisterer fragmentLinkRegisterer;

	/**
	 * the map between revision id and code fragments included in the revision
	 */
	private final ConcurrentMap<Long, Map<Long, CodeFragmentInfo>> codeFragments;

	/**
	 * the map between revision id and crds included in the revision
	 */
	private final ConcurrentMap<Long, Map<Long, CRD>> crds;

	/**
	 * already processed revisions
	 */
	private final ConcurrentMap<Long, RevisionInfo> processedRevisions;

	/**
	 * the map whose keys are revision ids and whose values are ids of previous
	 * revisions
	 */
	private final ConcurrentMap<Long, Long> revisionsMap;

	/**
	 * the threshold for elements <br>
	 * if the number of stored elements exceeds this threshold, then this
	 * monitor interrupts the other threads and register elements into db with
	 * the registered elements removed from the map
	 */
	private final int maxElementsCount;

	public CodeFragmentLinkDetectingThreadMonitor(
			final ConcurrentMap<Long, CodeFragmentLinkInfo> detectedLinks,
			final CodeFragmentLinkRegisterer fragmentLinkRegisterer,
			final ConcurrentMap<Long, Map<Long, CodeFragmentInfo>> codeFragments,
			final ConcurrentMap<Long, Map<Long, CRD>> crds,
			final ConcurrentMap<Long, RevisionInfo> processedRevisions,
			final ConcurrentMap<Long, Long> revisionsMap,
			final int maxElementsCount) {
		this.detectedLinks = detectedLinks;
		this.fragmentLinkRegisterer = fragmentLinkRegisterer;
		this.codeFragments = codeFragments;
		this.crds = crds;
		this.processedRevisions = processedRevisions;
		this.revisionsMap = revisionsMap;
		this.maxElementsCount = maxElementsCount;
	}

	public void monitor() throws Exception {
		int numberOfLinks = 0;

		while (true) {
			try {
				Thread.sleep(Constants.MONITORING_INTERVAL);

				// checking the number of detected links
				if (detectedLinks.size() >= maxElementsCount) {
					final Collection<CodeFragmentLinkInfo> currentElements = detectedLinks
							.values();
					fragmentLinkRegisterer.register(currentElements);
					MessagePrinter
							.println("\t"
									+ currentElements.size()
									+ " links of fragments have been registered into db");
					numberOfLinks += currentElements.size();

					for (final CodeFragmentLinkInfo link : currentElements) {
						detectedLinks.remove(link.getId());
					}
				}

				// checking processed revisions
				for (final Map.Entry<Long, RevisionInfo> entry : processedRevisions
						.entrySet()) {
					if (processedRevisions.containsKey(revisionsMap.get(entry
							.getKey()))) {
						final long removeRevisionId = revisionsMap.get(entry
								.getKey());
						codeFragments.remove(removeRevisionId);
						crds.remove(removeRevisionId);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			// break this loop if all the other threads have died
			if (Thread.activeCount() == 2) {
				break;
			}

			MessagePrinter.println();

			MessagePrinter.println("\tall threads have finished their work");
			MessagePrinter
					.println("\tregistering all the remaining elements into db ");
			fragmentLinkRegisterer.register(detectedLinks.values());

			numberOfLinks += detectedLinks.size();

			MessagePrinter.println("\t\tOK");

			MessagePrinter.println();

			MessagePrinter.println("the numbers of detected elements are ... ");
			MessagePrinter.println("\tLinks: " + numberOfLinks);
		}

	}
}
