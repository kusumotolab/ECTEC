package jp.ac.osaka_u.ist.sdl.ectec.analyzer;

import static org.junit.Assert.*;

import java.io.File;

import jp.ac.osaka_u.ist.sdl.ectec.settings.Language;
import jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrintLevel;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AnalyzerSettingsTest {

	@Test
	public void test1() {
		final String[] args = new String[] {};
		boolean catchException = false;
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
		} catch (Exception e) {
			catchException = true;
		}

		assertTrue(catchException);
	}

	@Test
	public void test2() {
		final String[] args = new String[] { "-r", "test", "-d", "test.db" };
		boolean catchException = false;
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
		} catch (Exception e) {
			catchException = true;
		}

		assertFalse(catchException);
	}

	@Test
	public void test3() {
		final String[] args = new String[] { "-r", "test", "-d", "test.db" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getLanguage() == Language.JAVA);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test4() {
		final String[] args = new String[] { "-r", "test", "-d", "test.db" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getAdditionalPath() == null);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test5() {
		final String[] args = new String[] { "-r", "test", "-d", "test.db" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getThreads() == 1);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test6() {
		final String[] args = new String[] { "-r", "test", "-d", "test.db" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getUserName() == null);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test7() {
		final String[] args = new String[] { "-r", "test", "-d", "test.db" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getPasswd() == null);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test8() {
		final String[] args = new String[] { "-r", "test", "-d", "test.db" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getStartRevisionIdentifier() == null);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test9() {
		final String[] args = new String[] { "-r", "test", "-d", "test.db" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getEndRevisionIdentifier() == null);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test10() {
		final String[] args = new String[] { "-r", "test", "-d", "test.db" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getVerboseLevel() == MessagePrintLevel.VERBOSE);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test11() {
		final String[] args = new String[] { "-r", "test", "-d", "test.db" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getVersionControlSystem() == VersionControlSystem.SVN);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test12() {
		final String[] args = new String[] {
				"-r",
				"test",
				"-d",
				"test.db",
				"-p",
				"test-resources" + File.separator + "properties"
						+ File.separator + "test.properties" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getAdditionalPath().equals("trunk/"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test13() {
		final String[] args = new String[] {
				"-r",
				"test",
				"-d",
				"test.db",
				"-p",
				"test-resources" + File.separator + "properties"
						+ File.separator + "test.properties" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getPasswd().equals("hogehoge"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test14() {
		final String[] args = new String[] {
				"-r",
				"test",
				"-d",
				"test.db",
				"-p",
				"test-resources" + File.separator + "properties"
						+ File.separator + "test.properties" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getThreads() == 100);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void test15() {
		final String[] args = new String[] { "-r", "test", "-d", "test.db", "-a", "hoge" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getAdditionalPath().equals("hoge"));
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void test16() {
		final String[] args = new String[] { "-r", "test", "-d", "test.db", "-th", "24" };
		try {
			final AnalyzerSettings settings = AnalyzerSettings.parseArgs(args);
			assertTrue(settings.getThreads() == 24);
		} catch (Exception e) {
			fail();
		}
	}

}
