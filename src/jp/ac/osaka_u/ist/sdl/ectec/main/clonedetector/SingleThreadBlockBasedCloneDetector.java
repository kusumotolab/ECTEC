package jp.ac.osaka_u.ist.sdl.ectec.main.clonedetector;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCloneSetInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.registerer.CloneSetRegisterer;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.retriever.CodeFragmentRetriever;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrinter;

/**
 * A class to detect block based clones with a single thread
 */
public class SingleThreadBlockBasedCloneDetector {

	/**
	 * the target revisions
	 */
	private final DBRevisionInfo[] targetRevisions;

	/**
	 * the retriever for code fragments
	 */
	private final CodeFragmentRetriever fragmentRetriever;

	/**
	 * the registerer for clone sets
	 */
	private final CloneSetRegisterer cloneRegisterer;

	/**
	 * the detected clones
	 */
	private final Map<Long, DBCloneSetInfo> detectedClones;

	/**
	 * the threshold for element
	 */
	private final int maxElementsCount;

	/**
	 * the size threshold
	 */
	private final int cloneSizeThreshold;

	public SingleThreadBlockBasedCloneDetector(
			final DBRevisionInfo[] targetRevisions,
			final CodeFragmentRetriever fragmentRetriever,
			final CloneSetRegisterer cloneRegisterer,
			final int maxElementsCount, final int cloneSizeThreshold) {
		this.targetRevisions = targetRevisions;
		this.fragmentRetriever = fragmentRetriever;
		this.cloneRegisterer = cloneRegisterer;
		this.detectedClones = new TreeMap<Long, DBCloneSetInfo>();
		this.maxElementsCount = maxElementsCount;
		this.cloneSizeThreshold = cloneSizeThreshold;
	}

	/**
	 * detect clones and register them into db
	 * 
	 * @throws Exception
	 */
	public void detectAndRegister() throws Exception {
		int numberOfClones = 0;

		for (int i = 0; i < targetRevisions.length; i++) {
			final DBRevisionInfo targetRevision = targetRevisions[i];

			// detect clones
			MessagePrinter.println("\t[" + i + "/" + targetRevisions.length
					+ "] analyzing revision " + targetRevision.getIdentifier());

			final Map<Long, DBCodeFragmentInfo> codeFragments = fragmentRetriever
					.retrieveElementsInSpecifiedRevision(targetRevision.getId());
			final FragmentComparator detector = new FragmentComparator(
					targetRevision.getId(), cloneSizeThreshold);
			detectedClones.putAll(detector.detectClones(codeFragments));

			// register clones if the number of clones exceeds the threshold
			if (detectedClones.size() >= maxElementsCount) {
				final Set<DBCloneSetInfo> currentClones = new HashSet<DBCloneSetInfo>();
				currentClones.addAll(detectedClones.values());
				cloneRegisterer.register(currentClones);
				MessagePrinter.println("\t" + currentClones.size()
						+ " clone sets have been registered into db");
				numberOfClones += currentClones.size();

				for (final DBCloneSetInfo clone : currentClones) {
					detectedClones.remove(clone.getId());
				}
			}
		}

		MessagePrinter.println();

		MessagePrinter.println("\tall threads have finished their work");
		MessagePrinter
				.println("\tregistering all the remaining clone sets into db ");
		cloneRegisterer.register(detectedClones.values());

		numberOfClones += detectedClones.size();

		MessagePrinter.println("\t\tOK");

		MessagePrinter.println();

		MessagePrinter.println("the numbers of detected elements are ... ");
		MessagePrinter.println("\tClone Sets: " + numberOfClones);
	}
}
