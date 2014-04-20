package jp.ac.osaka_u.ist.sdl.ectec.main.linker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;

import org.apache.log4j.Logger;

/**
 * A monitor class for code clone link threads
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentLinkDetectingThreadMonitor {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CodeFragmentLinkDetectingThreadMonitor.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * a map having detected links
	 */
	private final ConcurrentMap<Long, DBCodeFragmentLinkInfo> detectedLinks;

	/**
	 * the registerer for links of code fragments
	 */
	private final CodeFragmentLinkRegisterer fragmentLinkRegisterer;

	/**
	 * the map between revision id and code fragments included in the revision
	 */
	private final ConcurrentMap<Long, Map<Long, DBCodeFragmentInfo>> codeFragments;

	/**
	 * the map between revision id and crds included in the revision
	 */
	private final ConcurrentMap<Long, Map<Long, DBCrdInfo>> crds;

	/**
	 * already processed combined commits
	 */
	private final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits;

	/**
	 * id of a combined revision and a collection of ids of combined commits
	 * that relates to the revision
	 */
	private final Map<Long, Collection<Long>> combinedRevisionAndRelatedCombinedCommits;

	/**
	 * the threshold for elements <br>
	 * if the number of stored elements exceeds this threshold, then this
	 * monitor interrupts the other threads and register elements into db with
	 * the registered elements removed from the map
	 */
	private final int maxElementsCount;

	private final Thread[] threads;

	public CodeFragmentLinkDetectingThreadMonitor(
			final ConcurrentMap<Long, DBCodeFragmentLinkInfo> detectedLinks,
			final CodeFragmentLinkRegisterer fragmentLinkRegisterer,
			final ConcurrentMap<Long, Map<Long, DBCodeFragmentInfo>> codeFragments,
			final ConcurrentMap<Long, Map<Long, DBCrdInfo>> crds,
			final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits,
			final Map<Long, Collection<Long>> combinedRevisionAndRelatedCombinedCommits,
			final int maxElementsCount, final Thread[] threads) {
		this.detectedLinks = detectedLinks;
		this.fragmentLinkRegisterer = fragmentLinkRegisterer;
		this.codeFragments = codeFragments;
		this.crds = crds;
		this.processedCombinedCommits = processedCombinedCommits;
		this.combinedRevisionAndRelatedCombinedCommits = combinedRevisionAndRelatedCombinedCommits;
		this.maxElementsCount = maxElementsCount;
		this.threads = threads;
	}

	public void monitor() throws Exception {
		int numberOfLinks = 0;

		while (true) {
			try {
				Thread.sleep(Constants.MONITORING_INTERVAL);

				// checking the number of detected links
				synchronized (detectedLinks) {
					if (detectedLinks.size() >= maxElementsCount) {
						final Set<DBCodeFragmentLinkInfo> currentElements = new HashSet<DBCodeFragmentLinkInfo>();
						currentElements.addAll(detectedLinks.values());
						fragmentLinkRegisterer.register(currentElements);
						logger.info(currentElements.size()
								+ " links of fragments have been registered into db");
						numberOfLinks += currentElements.size();

						for (final DBCodeFragmentLinkInfo link : currentElements) {
							detectedLinks.remove(link.getId());
						}
					}
				}

				// remove fragments if they are no longer needed
				synchronized (codeFragments) {
					final Collection<Long> fragmentCombinedRevisionIds = new TreeSet<Long>();
					fragmentCombinedRevisionIds.addAll(codeFragments.keySet());
					for (final long combinedRevisionId : fragmentCombinedRevisionIds) {
						final Collection<Long> relatedCombinedCommits = combinedRevisionAndRelatedCombinedCommits
								.get(combinedRevisionId);
						if (processedCombinedCommits.keySet().containsAll(
								relatedCombinedCommits)) {
							codeFragments.remove(combinedRevisionId);
						}
					}
				}

				// remove crds if they are no longer needed
				synchronized (crds) {
					final Collection<Long> crdCombinedRevisionIds = new TreeSet<Long>();
					crdCombinedRevisionIds.addAll(crds.keySet());
					for (final long combinedRevisionId : crdCombinedRevisionIds) {
						final Collection<Long> relatedCommits = combinedRevisionAndRelatedCombinedCommits
								.get(combinedRevisionId);
						if (processedCombinedCommits.keySet().containsAll(
								relatedCommits)) {
							crds.remove(combinedRevisionId);
						}
					}
				}

			} catch (Exception e) {
				eLogger.warn("something is wrong in the monitoring thread\n"
						+ e.toString());
			}

			// break this loop if all the other threads have died
			boolean allThreadDead = true;
			for (final Thread thread : threads) {
				if (thread.isAlive()) {
					allThreadDead = false;
					break;
				}
			}

			if (allThreadDead) {
				break;
			}

		}

		logger.info("all threads have finished their work");
		logger.info("registering all the remaining elements into db ");
		fragmentLinkRegisterer.register(detectedLinks.values());

		numberOfLinks += detectedLinks.size();

		logger.info("the numbers of detected elements are ... ");
		logger.info("Links: " + numberOfLinks);

	}
}
