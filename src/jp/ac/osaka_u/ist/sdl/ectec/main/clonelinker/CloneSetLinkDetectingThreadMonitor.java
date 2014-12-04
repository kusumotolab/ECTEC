package jp.ac.osaka_u.ist.sdl.ectec.main.clonelinker;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCombinedCommitInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CloneSetLinkRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;

import org.apache.log4j.Logger;

/**
 * A monitor class for clone set linking threads
 * 
 * @author k-hotta
 * 
 */
public class CloneSetLinkDetectingThreadMonitor {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CloneSetLinkDetectingThreadMonitor.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * a map having detected links of clones
	 */
	private final ConcurrentMap<Long, DBCloneSetLinkInfo> detectedCloneLinks;

	/**
	 * the registerer for clone sets
	 */
	private final CloneSetLinkRegisterer cloneLinkRegisterer;

	/**
	 * the map between revision id and clone sets including in the revision
	 */
	private final ConcurrentMap<Long, Map<Long, DBCloneSetInfo>> cloneSets;

	/**
	 * already processed combined commits
	 */
	private final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits;

	/**
	 * id of a revision and a collection of ids of commits that relates to the
	 * revision
	 */
	private final Map<Long, Collection<Long>> combinedRevisionAndRelatedCombinedCommits;

	/**
	 * the threshold for elements <br>
	 * if the number of stored elements exceeds this threshold, then this
	 * monitor interrupts the other threads and register elements into db with
	 * the registered elements removed from the map
	 */
	private final int maxElementsCount;

	/**
	 * the threads to be monitored
	 */
	private final Thread[] threads;

	public CloneSetLinkDetectingThreadMonitor(
			final ConcurrentMap<Long, DBCloneSetLinkInfo> detectedCloneLinks,
			final CloneSetLinkRegisterer cloneLinkRegisterer,
			final ConcurrentMap<Long, Map<Long, DBCloneSetInfo>> cloneSets,
			final ConcurrentMap<Long, DBCombinedCommitInfo> processedCombinedCommits,
			final Map<Long, Collection<Long>> combinedRevisionAndRelatedCombinedCommits,
			final int maxElementsCount, final Thread[] threads) {
		this.detectedCloneLinks = detectedCloneLinks;
		this.cloneLinkRegisterer = cloneLinkRegisterer;
		this.cloneSets = cloneSets;
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
				synchronized (detectedCloneLinks) {
					if (detectedCloneLinks.size() >= maxElementsCount) {
						final Set<DBCloneSetLinkInfo> currentElements = new HashSet<DBCloneSetLinkInfo>();
						currentElements.addAll(detectedCloneLinks.values());
						cloneLinkRegisterer.register(currentElements);
						logger.info(currentElements.size()
								+ " links of fragments have been registered into db");
						numberOfLinks += currentElements.size();

						for (final DBCloneSetLinkInfo link : currentElements) {
							detectedCloneLinks.remove(link.getId());
						}
					}
				}

				// remove clones if they are no longer needed
				synchronized (cloneSets) {
					final Collection<Long> cloneRevisionIds = new TreeSet<Long>();
					cloneRevisionIds.addAll(cloneSets.keySet());
					for (final long revisionId : cloneRevisionIds) {
						final Collection<Long> relatedCombinedCommits = combinedRevisionAndRelatedCombinedCommits
								.get(revisionId);
						if (processedCombinedCommits.keySet().containsAll(
								relatedCombinedCommits)) {
							cloneSets.remove(revisionId);
						}
					}
				}

			} catch (Exception e) {
				eLogger.warn("something is wrong in the monitoring threaed\n",
						e);
				if (e instanceof SQLException) {
					eLogger.warn("error code: "
							+ ((SQLException) e).getErrorCode());
				}
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
		cloneLinkRegisterer.register(detectedCloneLinks.values());

		numberOfLinks += detectedCloneLinks.size();

		logger.info("the numbers of detected elements are ... ");
		logger.info("Links: " + numberOfLinks);
	}

}
