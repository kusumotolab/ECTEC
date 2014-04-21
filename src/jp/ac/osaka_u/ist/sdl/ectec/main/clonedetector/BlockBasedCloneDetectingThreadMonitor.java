package jp.ac.osaka_u.ist.sdl.ectec.main.clonedetector;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CloneSetRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;

import org.apache.log4j.Logger;

/**
 * A monitor class for clone detecting threads
 * 
 * @author k-hotta
 * 
 */
public class BlockBasedCloneDetectingThreadMonitor {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(BlockBasedCloneDetectingThreadMonitor.class.getName());

	/**
	 * the logger for errors
	 */
	private static final Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * a map having detected clones
	 */
	private final ConcurrentMap<Long, DBCloneSetInfo> detectedClones;

	/**
	 * the registerer for clone sets
	 */
	private final CloneSetRegisterer registerer;

	/**
	 * the threshold for elements <br>
	 * if the number of stored elements exceeds this threshold, then this
	 * monitor interrupts the other threads and register elements into db with
	 * the registered elements removed from the map
	 */
	private final int maxElementsCount;

	/**
	 * the array of the threads to be monitored
	 */
	private final Thread[] threads;

	public BlockBasedCloneDetectingThreadMonitor(
			final ConcurrentMap<Long, DBCloneSetInfo> detectedClones,
			final CloneSetRegisterer registerer, final int maxElementsCount,
			final Thread[] threads) {
		this.detectedClones = detectedClones;
		this.registerer = registerer;
		this.maxElementsCount = maxElementsCount;
		this.threads = threads;
	}

	public void monitor() throws Exception {
		int numberOfClones = 0;

		while (true) {
			try {
				Thread.sleep(Constants.MONITORING_INTERVAL);

				synchronized (detectedClones) {
					if (detectedClones.size() >= maxElementsCount) {
						final Set<DBCloneSetInfo> currentClones = new HashSet<DBCloneSetInfo>();
						currentClones.addAll(detectedClones.values());
						registerer.register(currentClones);
						logger.info(currentClones.size()
								+ " clone sets have been registered into db");
						numberOfClones += currentClones.size();

						for (final DBCloneSetInfo clone : currentClones) {
							detectedClones.remove(clone.getId());
						}
					}
				}

			} catch (Exception e) {
				eLogger.warn("something is wrong in BlockBasedCloneDetectingThreadMonigor\n"
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
		logger.info("registering all the remaining clone sets into db ");
		registerer.register(detectedClones.values());

		numberOfClones += detectedClones.size();

		logger.info("the numbers of detected elements are ... ");
		logger.info("Clone Sets: " + numberOfClones);
	}

}
