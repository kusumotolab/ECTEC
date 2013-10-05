package jp.ac.osaka_u.ist.sdl.ectec.analyzer.clonedetector;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.data.CloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.data.registerer.CloneSetRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A monitor class for clone detecting threads
 * 
 * @author k-hotta
 * 
 */
public class BlockBasedCloneDetectingThreadMonitor {

	/**
	 * a map having detected clones
	 */
	private final ConcurrentMap<Long, CloneSetInfo> detectedClones;

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

	public BlockBasedCloneDetectingThreadMonitor(
			final ConcurrentMap<Long, CloneSetInfo> detectedClones,
			final CloneSetRegisterer registerer, final int maxElementsCount) {
		this.detectedClones = detectedClones;
		this.registerer = registerer;
		this.maxElementsCount = maxElementsCount;
	}

	public void monitor() throws Exception {
		int numberOfClones = 0;

		while (true) {
			try {
				Thread.sleep(Constants.MONITORING_INTERVAL);

				if (detectedClones.size() >= maxElementsCount) {
					final Set<CloneSetInfo> currentClones = new HashSet<CloneSetInfo>();
					currentClones.addAll(detectedClones.values());
					registerer.register(currentClones);
					MessagePrinter.println("\t" + currentClones.size()
							+ " clone sets have been registered into db");
					numberOfClones += currentClones.size();

					for (final CloneSetInfo clone : currentClones) {
						detectedClones.remove(clone);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (Thread.activeCount() == 2) {
				break;
			}
		}

		MessagePrinter.println();

		MessagePrinter.println("\tall threads have finished their work");
		MessagePrinter
				.println("\tregistering all the remaining clone sets into db ");
		registerer.register(detectedClones.values());

		numberOfClones += detectedClones.size();

		MessagePrinter.println("\t\tOK");

		MessagePrinter.println();

		MessagePrinter.println("the numbers of detected elements are ... ");
		MessagePrinter.println("\tClone Sets: " + numberOfClones);
	}

}
