package jp.ac.osaka_u.ist.sdl.ectec.analyzer.vcs.svn;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;

import org.junit.BeforeClass;
import org.junit.Test;

public class SVNChangedFilesDetectorTest {

	private static SVNRepositoryManager manager;

	private static SVNChangedFilesDetector detector;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		manager = new SVNRepositoryManager(
				"file:///G:/repositories/repository-C20R", null, null, null);
		detector = new SVNChangedFilesDetector(manager);
	}

	@Test
	public void test() {
		try {
			final Map<String, Character> result = detector.detectChangedFiles(
					"2", Language.JAVA);
			assertTrue(result.size() > 0);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test2() {
		try {
			final Map<String, Character> result = detector.detectChangedFiles(
					"17", Language.JAVA);
			assertTrue(result.size() == 36);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test3() {
		try {
			final int result1 = runThread();
			final int result2 = runThread();
			final int result3 = runThread();
			
			assertTrue(result1 == result2 && result2 == result3);
		} catch (Exception e) {
			fail();
		}
	}

	private int runThread() {
		final Thread[] threads = new Thread[5];
		final ConcurrentMap<String, Character> result = new ConcurrentHashMap<String, Character>();
		threads[0] = new Thread(new TestThread(new SVNChangedFilesDetector(
				manager), "10", result));
		threads[1] = new Thread(new TestThread(new SVNChangedFilesDetector(
				manager), "17", result));
		threads[2] = new Thread(new TestThread(new SVNChangedFilesDetector(
				manager), "6", result));
		threads[3] = new Thread(new TestThread(new SVNChangedFilesDetector(
				manager), "24", result));
		threads[4] = new Thread(new TestThread(new SVNChangedFilesDetector(
				manager), "40", result));

		for (final Thread thread : threads) {
			thread.start();
		}
		for (final Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result.size();
	}

	class TestThread implements Runnable {

		private SVNChangedFilesDetector myDetector;

		private final String revision;

		private ConcurrentMap<String, Character> result;

		public TestThread(final SVNChangedFilesDetector myDetector,
				final String revision,
				final ConcurrentMap<String, Character> result) {
			this.myDetector = myDetector;
			this.revision = revision;
			this.result = result;
		}

		@Override
		public void run() {
			try {
				result.putAll(myDetector.detectChangedFiles(revision,
						Language.JAVA));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
