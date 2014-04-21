package jp.ac.osaka_u.ist.sdl.ectec.main.fragmentdetector;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.LoggingManager;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CRDRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CodeFragmentRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;

import org.apache.log4j.Logger;

/**
 * A monitor class for code fragment detecting threads
 * 
 * @author k-hotta
 * 
 */
public class CodeFragmentDetectingThreadMonitor {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggingManager
			.getLogger(CodeFragmentDetectingThreadMonitor.class.getName());

	/**
	 * the logger for errors
	 */
	private final static Logger eLogger = LoggingManager.getLogger("error");

	/**
	 * a map having detected crds
	 */
	private final ConcurrentMap<Long, DBCrdInfo> detectedCrds;

	/**
	 * a map having detected fragments
	 */
	private final ConcurrentMap<Long, DBCodeFragmentInfo> detectedFragments;

	/**
	 * the threshold for elements <br>
	 * if the number of stored elements exceeds this threshold, then this
	 * monitor interrupts the other threads and register elements into db with
	 * the registered elements removed from the map
	 */
	private final int maxElementsCount;

	/**
	 * the registerer for crds
	 */
	private final CRDRegisterer crdRegisterer;

	/**
	 * the registerer for code fragments
	 */
	private final CodeFragmentRegisterer fragmentRegisterer;

	/**
	 * the array of the threads to be monitored
	 */
	private final Thread[] threads;

	public CodeFragmentDetectingThreadMonitor(
			final ConcurrentMap<Long, DBCrdInfo> detectedCrds,
			final ConcurrentMap<Long, DBCodeFragmentInfo> detectedFragments,
			final int maximumElementsCount, final CRDRegisterer crdRegisterer,
			final CodeFragmentRegisterer fragmentRegisterer,
			final Thread[] threads) {
		this.detectedCrds = detectedCrds;
		this.detectedFragments = detectedFragments;
		this.maxElementsCount = maximumElementsCount;
		this.crdRegisterer = crdRegisterer;
		this.fragmentRegisterer = fragmentRegisterer;
		this.threads = threads;
	}

	public void monitor() throws Exception {
		long numberOfCrds = 0;
		long numberOfFragments = 0;

		while (true) {

			try {
				Thread.sleep(Constants.MONITORING_INTERVAL);

				if (detectedCrds.size() >= maxElementsCount) {
					synchronized (detectedCrds) {
						final Set<DBCrdInfo> currentElements = new TreeSet<DBCrdInfo>();
						currentElements.addAll(detectedCrds.values());
						crdRegisterer.register(currentElements);
						logger.info(currentElements.size()
								+ " CRDs have been registered into db");
						numberOfCrds += currentElements.size();

						for (final DBCrdInfo crd : currentElements) {
							detectedCrds.remove(crd.getId());
						}
					}
				}

				if (detectedFragments.size() >= maxElementsCount) {
					synchronized (detectedFragments) {
						final Set<DBCodeFragmentInfo> currentElements = new TreeSet<DBCodeFragmentInfo>();
						currentElements.addAll(detectedFragments.values());
						fragmentRegisterer.register(currentElements);
						logger.info(currentElements.size()
								+ " fragments have been registered into db");
						numberOfFragments += currentElements.size();

						for (final DBCodeFragmentInfo fragment : currentElements) {
							detectedFragments.remove(fragment.getId());
						}
					}
				}

			} catch (Exception e) {
				eLogger.warn("something is wrong in RevisionDetectThreadMonitor\n"
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
		crdRegisterer.register(detectedCrds.values());
		fragmentRegisterer.register(detectedFragments.values());

		numberOfCrds += detectedCrds.size();
		numberOfFragments += detectedFragments.size();

		logger.info("the numbers of detected elements are ... ");
		logger.info("CRD: " + numberOfCrds);
		logger.info("Fragment: " + numberOfFragments);
	}

}
